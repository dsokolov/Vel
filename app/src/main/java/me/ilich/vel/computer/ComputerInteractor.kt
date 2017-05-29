package me.ilich.vel.computer

import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import com.f2prateek.rx.preferences.RxSharedPreferences
import com.tbruyelle.rxpermissions.RxPermissions
import me.ilich.vel.MpsSpeed
import me.ilich.vel.R
import me.ilich.vel.model.BatteryStatus
import me.ilich.vel.model.Theme
import me.ilich.vel.model.realm.RealmMotion
import me.ilich.vel.model.realm.RealmSpeedSummary
import me.ilich.vel.model.sources.LocationEntity
import me.ilich.vel.model.sources.locationObservable
import me.ilich.vel.model.sources.themeObservable
import me.ilich.vel.realm.RealmWrapper
import rx.Observable
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
    private val realmWrapper = RealmWrapper()
    private lateinit var preferences: RxSharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        rxPermissions = RxPermissions(activity)
        realmWrapper.init().subscribe()
        preferences = RxSharedPreferences.create(PreferenceManager.getDefaultSharedPreferences(activity))
    }

    override fun onDestroy() {
        realmWrapper.close().subscribe()
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

    override fun speedCurrent(): Observable<MpsSpeed> =
            realmWrapper.realm()
                    .flatMap { realm ->
                        realm.where(RealmSpeedSummary::class.java)
                                .findAllAsync()
                                .asObservable()
                    }
                    .map { it.firstOrNull()?.speedLast ?: 0f }

    override fun speedMax(): Observable<MpsSpeed> =
            realmWrapper.realm()
                    .flatMap { realm ->
                        realm.where(RealmSpeedSummary::class.java)
                                .findAllAsync()
                                .asObservable()
                    }
                    .map { it.firstOrNull()?.speedMax ?: 0f }

    override fun speedAvg(): Observable<MpsSpeed> =
            realmWrapper.realm()
                    .flatMap { realm ->
                        realm.where(RealmSpeedSummary::class.java)
                                .findAllAsync()
                                .asObservable()
                    }
                    .map { it.firstOrNull()?.speedAvg ?: 0f }

    override fun speedReset(): Observable<Unit> =
            realmWrapper.exec { realm ->
                realm.executeTransaction { realm ->
                    realm.delete(RealmSpeedSummary::class.java)
                    realm.delete(RealmMotion::class.java)
                }
            }

}