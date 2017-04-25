package me.ilich.vel.computer

import android.Manifest
import android.app.Activity
import android.content.Context
import android.hardware.SensorManager
import android.os.Bundle
import com.nvanbenschoten.rxsensor.RxSensorManager
import com.tbruyelle.rxpermissions.RxPermissions
import io.realm.Realm
import me.ilich.vel.model.realm.RealmCalibration
import me.ilich.vel.model.realm.firstOrCreate
import me.ilich.vel.model.realm.transactionObservable
import me.ilich.vel.model.sources.*
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit

class ComputerInteractor(val activity: Activity) : ComputerContracts.Interactor {

    companion object {

        private val PERMISSIONS = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION
        )

    }

    private lateinit var rxPermissions: RxPermissions
    private lateinit var rxSensor: RxSensorManager
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        rxPermissions = RxPermissions(activity)
        realm = Realm.getDefaultInstance()
        val sensorManager = activity.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        rxSensor = RxSensorManager(sensorManager)
    }

    override fun onDestroy() {
        realm.close()
    }

    override fun permissions(): Observable<Boolean> = rxPermissions.request(*PERMISSIONS)

    override fun time(): Observable<Date> =
            Observable.interval(0L, 500L, TimeUnit.MILLISECONDS).
                    map { Date() }.
                    subscribeOn(Schedulers.computation())

    override fun calibratedOrientation(): Observable<OrientationEntity> {
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
                    OrientationEntity(
                            roll = orientation.roll,
                            pitch = orientation.pitch - calibration,
                            yaw = orientation.yaw
                    )
                }.
                subscribeOn(Schedulers.computation())
        return result
    }

    override fun uncalibratedOrientation(): Observable<OrientationEntity> =
            orientationObservable(rxSensor)

    override fun location(): Observable<LocationEntity> =
            gpsObservable(activity)

    override fun calibrate(orientation: OrientationEntity): Observable<Unit> =
            realm.transactionObservable { realm ->
                val r = realm.firstOrCreate(RealmCalibration::class.java)
                r.pitch = orientation.pitch
            }.observeOn(AndroidSchedulers.mainThread())

    override fun acceleration(): Observable<AccelerationEntity> =
            accelerationObservable(rxSensor)

}