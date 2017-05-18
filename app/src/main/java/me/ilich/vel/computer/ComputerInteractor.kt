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
import me.ilich.vel.model.BatteryStatus
import me.ilich.vel.model.Theme
import me.ilich.vel.model.sources.LocationEntity
import me.ilich.vel.model.sources.locationObservable
import me.ilich.vel.model.sources.themeObservable
import rx.Observable
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

    override fun location(): Observable<LocationEntity> =
            locationObservable(activity)

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

    override fun themeObservable(): Observable<Theme> = themeObservable(activity)

    override fun batteryStatus(): Observable<BatteryStatus> {
        TODO("implement batteryStatus")
    }

}