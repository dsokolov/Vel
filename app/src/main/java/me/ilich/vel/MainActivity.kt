package me.ilich.vel

import android.Manifest
import android.content.Context
import android.hardware.SensorManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.jakewharton.rxbinding.view.RxView
import com.nvanbenschoten.rxsensor.RxSensorManager
import com.tbruyelle.rxpermissions.RxPermissions
import io.realm.Realm
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    companion object {

        private val PERMISSIONS = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION
        )

        private val SDF = SimpleDateFormat("HH:mm", Locale.getDefault())

    }

    private lateinit var timeTextView: TextView
    private lateinit var speedTextView: TextView
    private lateinit var errorTextView: TextView
    private lateinit var angelTextView: TextView

    private lateinit var rxPermissions: RxPermissions
    private lateinit var sensorManager: SensorManager
    private lateinit var rxSensor: RxSensorManager

    private val onCreateSubscription = CompositeSubscription()
    private val onStartSubscription = CompositeSubscription()

    private var permissionsSubscription: Subscription? = null
    private var locationSubscription: Subscription? = null
    private var orientationSubscription: Subscription? = null

    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main)
        timeTextView = findViewById(R.id.time) as TextView
        speedTextView = findViewById(R.id.speed) as TextView
        errorTextView = findViewById(R.id.error) as TextView
        angelTextView = findViewById(R.id.angel) as TextView

        realm = Realm.getDefaultInstance()

        rxPermissions = RxPermissions(this)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        rxSensor = RxSensorManager(sensorManager)

        onCreateSubscription.add(
                RxView.longClicks(angelTextView).
                        flatMap {
                            orientationObservable(sensorManager, rxSensor).first()
                        }.
                        flatMap { orientation ->
                            when (orientation) {
                                is OrientationData.Values ->
                                    realm.transactionObservable { realm ->
                                        val r = realm.firstOrCreate(RealmCalibration::class.java)
                                        r.pitch = orientation.pitch
                                    }
                                else -> Observable.fromCallable { }
                            }
                        }.
                        subscribe()
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        onCreateSubscription.unsubscribe()
        realm.close()
    }

    override fun onStart() {
        super.onStart()
        onStartSubscription.add(
                timeObservable().
                        observeOn(AndroidSchedulers.mainThread()).
                        subscribe {
                            timeTextView.text = it
                        }
        )
        permissionsSubscription = rxPermissions.request(*PERMISSIONS).subscribe {
            if (it) {
                errorTextView.visibility = View.GONE
                subscribeSpeed()
            } else {
                errorTextView.visibility = View.VISIBLE
                errorTextView.text = "GPS disabled"
            }
        }
        subscribeAngel()
    }

    override fun onStop() {
        super.onStop()
        permissionsSubscription?.unsubscribe()
        locationSubscription?.unsubscribe()
        orientationSubscription?.unsubscribe()
        onStartSubscription.unsubscribe()
    }

    private fun subscribeSpeed() {
        locationSubscription = gpsObservable(this).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe {
                    when (it) {
                        is LocationData.Values -> {
                            val speedKmph = it.speed * 0.36
                            speedTextView.text = String.format("%.2f", speedKmph)
                        }
                        else -> {
                        }
                    }
                }
    }

    private fun subscribeAngel() {
        val calibrationObservable = realm.where(RealmCalibration::class.java).
                findAllAsync().
                asObservable().
                map {
                    if (it.isEmpty()) {
                        0f
                    } else {
                        it.first().pitch
                    }
                }.
                subscribeOn(AndroidSchedulers.mainThread())
        val orientationObservabe = orientationObservable(sensorManager, rxSensor)
        orientationSubscription = Observable.
                combineLatest(orientationObservabe, calibrationObservable) { orientation, caliberation ->
                    when (orientation) {
                        is OrientationData.Values -> {
                            OrientationData.Values(
                                    roll = orientation.roll,
                                    pitch = orientation.pitch - caliberation,
                                    yaw = orientation.yaw
                            )
                        }
                        else -> orientation
                    }
                }.
                subscribeOn(Schedulers.computation()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe {
                    when (it) {
                        is OrientationData.Values -> {
                            if (-1 < it.pitch && it.pitch < 1) {
                                angelTextView.text = "0"
                            } else {
                                angelTextView.text = String.format("%.0f", it.pitch)
                            }
                        }
                        else -> {
                        }
                    }
                }
    }

    private fun timeObservable() = Observable.interval(0L, 500L, TimeUnit.MILLISECONDS).
            map { Date() }.
            map { SDF.format(it) }.
            subscribeOn(Schedulers.computation())

}