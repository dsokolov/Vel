package me.ilich.vel.computer

import android.Manifest
import android.app.Activity
import android.content.Context
import android.hardware.SensorManager
import android.os.Bundle
import com.nvanbenschoten.rxsensor.RxSensorManager
import com.tbruyelle.rxpermissions.RxPermissions
import io.realm.Realm
import me.ilich.vel.firstOrCreate
import me.ilich.vel.model.RealmCalibration
import me.ilich.vel.sources.*
import me.ilich.vel.transactionObservable
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit

class ComputerModel(val activity: Activity) {

    companion object {

        private val PERMISSIONS = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION
        )

    }

    private lateinit var rxPermissions: RxPermissions
    private lateinit var rxSensor: RxSensorManager
    private lateinit var realm: Realm

    fun onCreate(savedInstanceState: Bundle?) {
        rxPermissions = RxPermissions(activity)
        realm = Realm.getDefaultInstance()
        val sensorManager = activity.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        rxSensor = RxSensorManager(sensorManager)
    }

    fun onDestroy() {
        realm.close()
    }

    fun permissionsObservable(): Observable<Boolean> =
            rxPermissions.request(*PERMISSIONS)

    fun calibratedOrientationObservable(): Observable<OrientationData> {
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
        val orientationObservable = orientationObservable(rxSensor)
        val result = Observable.
                combineLatest(orientationObservable, calibrationObservable) { orientation, calibration ->
                    OrientationData(
                            roll = orientation.roll,
                            pitch = orientation.pitch - calibration,
                            yaw = orientation.yaw
                    )
                }.
                subscribeOn(Schedulers.computation())
        return result
    }

    fun uncalibratedOrientationObservable(): Observable<OrientationData> =
            orientationObservable(rxSensor)

    fun timeObservable(): Observable<Date> =
            Observable.interval(0L, 500L, TimeUnit.MILLISECONDS).
                    map { Date() }.
                    subscribeOn(Schedulers.computation())

    fun locationObservable(): Observable<LocationData> =
            gpsObservable(activity)

    fun calibrate(orientation: OrientationData): Observable<Unit> =
            realm.transactionObservable { realm ->
                val r = realm.firstOrCreate(RealmCalibration::class.java)
                r.pitch = orientation.pitch
            }.observeOn(AndroidSchedulers.mainThread())

    fun accelerationObservable(): Observable<AccelerationData> =
            accelerationObservable(rxSensor)

}