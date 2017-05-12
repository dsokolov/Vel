package me.ilich.vel

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.SensorManager
import android.os.Binder
import android.os.IBinder
import com.nvanbenschoten.rxsensor.RxSensorManager
import me.ilich.vel.model.realm.Motion
import me.ilich.vel.model.sources.accelerationObservable
import me.ilich.vel.model.sources.locationObservable
import rx.Observable
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.util.concurrent.TimeUnit

class VelService : Service() {

    private val binder = object : Binder() {

    }

    private val log = logger(javaClass)
    private val subs = CompositeSubscription()

    override fun onCreate() {
        super.onCreate()
        val rxSensorManager = RxSensorManager(getSystemService(Context.SENSOR_SERVICE) as SensorManager)
        subs.addAll(
                Observable.combineLatest(
                        locationObservable(this),
                        accelerationObservable(rxSensorManager)
                ) { location, acceleration ->
                    val motion = Motion(
                            speed = location.speed,
                            bearing = location.bearing,
                            accuracy = location.accuracy,
                            accelerationX = acceleration.x,
                            accelerationY = acceleration.y,
                            accelerationZ = acceleration.z,
                            locationProvider = location.provider
                    )
                    motion
                }
                        .throttleFirst(100L, TimeUnit.MILLISECONDS)
                        .subscribeOn(Schedulers.computation())
                        .subscribe {
                            log.debug(it.toString())
                        }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        subs.unsubscribe()
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

}