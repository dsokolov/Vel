package me.ilich.vel.computer

import android.Manifest
import android.app.Activity
import android.content.Context
import android.hardware.SensorManager
import android.os.Bundle
import android.preference.PreferenceManager
import com.f2prateek.rx.preferences.RxSharedPreferences
import com.nvanbenschoten.rxsensor.RxSensorManager
import com.tbruyelle.rxpermissions.RxPermissions
import io.realm.Realm
import me.ilich.vel.R
import me.ilich.vel.model.Theme
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
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

    }

    private lateinit var rxPermissions: RxPermissions
    private lateinit var rxSensor: RxSensorManager
    private lateinit var realm: Realm
    private lateinit var preferences: RxSharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        rxPermissions = RxPermissions(activity)
        realm = Realm.getDefaultInstance()
        val sensorManager = activity.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        rxSensor = RxSensorManager(sensorManager)
        preferences = RxSharedPreferences.create(PreferenceManager.getDefaultSharedPreferences(activity))
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
            locationObservable(activity)

    override fun calibrate(orientation: OrientationEntity): Observable<Unit> =
            realm.transactionObservable { realm ->
                val r = realm.firstOrCreate(RealmCalibration::class.java)
                r.pitch = orientation.pitch
            }.observeOn(AndroidSchedulers.mainThread())

    override fun acceleration(): Observable<AccelerationEntity> =
            accelerationObservable(rxSensor)

    override fun speedUnitObservable(): Observable<SpeedUnitEntity> {
        val key = activity.getString(R.string.preference_speed_key)
        val default = activity.getString(R.string.preference_speed_default)
        val kmph = activity.getString(R.string.preference_speed_value_kmph)
        val mps = activity.getString(R.string.preference_speed_value_mps)
        val miph = activity.getString(R.string.preference_speed_value_miph)
        return preferences.getString(key, default)
                .asObservable()
                .flatMap {
                    when (it) {
                        kmph -> Observable.just(SpeedUnitEntity.KilometersPerHour())
                        mps -> Observable.just(SpeedUnitEntity.MetersPerSecond())
                        miph -> Observable.just(SpeedUnitEntity.MilesPerHour())
                        else -> Observable.error(RuntimeException("unknown unit $it"))
                    }
                }
                .subscribeOn(Schedulers.computation())
    }

    override fun pitchUnitObservable(): Observable<PitchUnitEntity> {
        val key = activity.getString(R.string.preference_pitch_key)
        val default = activity.getString(R.string.preference_pitch_default)
        val degree = activity.getString(R.string.preference_pitch_value_degree)
        val percent = activity.getString(R.string.preference_pitch_value_percent)
        return preferences.getString(key, default)
                .asObservable()
                .flatMap {
                    when (it) {
                        degree -> Observable.just(PitchUnitEntity.Degree())
                        percent -> Observable.just(PitchUnitEntity.Percent())
                        else -> Observable.error(RuntimeException("unknown unit $it"))
                    }
                }
                .subscribeOn(Schedulers.computation())
    }

    override fun themeObservable(): Observable<Theme> = themeObservable(activity)

}