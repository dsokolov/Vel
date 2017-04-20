package me.ilich.vel.computer

import android.Manifest
import android.content.Context
import android.hardware.SensorManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.nvanbenschoten.rxsensor.RxSensorManager
import com.tbruyelle.rxpermissions.RxPermissions
import io.realm.Realm
import me.ilich.vel.*
import me.ilich.vel.sources.LocationData
import me.ilich.vel.sources.OrientationData
import me.ilich.vel.sources.gpsObservable
import me.ilich.vel.sources.orientationObservable
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.util.*
import java.util.concurrent.TimeUnit

class ComputerActivity : AppCompatActivity() {

    companion object {

        private val PERMISSIONS = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION
        )

    }

    private val view = ComputerView(
            onCalibratePress = {
                it.flatMap {
                    orientationObservable(sensorManager, rxSensor).first()
                }.flatMap { orientation ->
                    when (orientation) {
                        is OrientationData.Values ->
                            realm.transactionObservable { realm ->
                                val r = realm.firstOrCreate(RealmCalibration::class.java)
                                r.pitch = orientation.pitch
                            }
                        else -> Observable.fromCallable { }
                    }
                }.subscribe()
            }
    )


    private lateinit var rxPermissions: RxPermissions
    private lateinit var sensorManager: SensorManager
    private lateinit var rxSensor: RxSensorManager


    private val onStartSubscription = CompositeSubscription()

    private var permissionsSubscription: Subscription? = null
    private var locationSubscription: Subscription? = null
    private var orientationSubscription: Subscription? = null

    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view.onCreate(this, savedInstanceState)

        realm = Realm.getDefaultInstance()

        rxPermissions = RxPermissions(this)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        rxSensor = RxSensorManager(sensorManager)
    }

    override fun onDestroy() {
        super.onDestroy()
        view.onDestroy()
        realm.close()
    }

    override fun onStart() {
        super.onStart()
        onStartSubscription.add(
                timeObservable().
                        observeOn(AndroidSchedulers.mainThread()).
                        subscribe {
                            view.showTime(it)
                        }
        )
        permissionsSubscription = rxPermissions.request(*PERMISSIONS).subscribe {
            if (it) {
                view.hideError()
                subscribeSpeed()
            } else {
                view.showError("GPS disabled")
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
                            view.showSpeed(it.speed)
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
                            view.showAngel(it)
                        }
                        else -> {
                        }
                    }
                }
    }

    private fun timeObservable() = Observable.interval(0L, 500L, TimeUnit.MILLISECONDS).
            map { Date() }.
            subscribeOn(Schedulers.computation())

}