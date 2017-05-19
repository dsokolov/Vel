package me.ilich.vel.computer

import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.preference.PreferenceManager
import com.f2prateek.rx.preferences.RxSharedPreferences
import com.tbruyelle.rxpermissions.RxPermissions
import io.realm.Realm
import me.ilich.vel.MpsSpeed
import me.ilich.vel.R
import me.ilich.vel.model.BatteryStatus
import me.ilich.vel.model.Theme
import me.ilich.vel.model.realm.RealmMotion
import me.ilich.vel.model.realm.RealmSpeedSummary
import me.ilich.vel.model.realm.transactionObservable
import me.ilich.vel.model.sources.LocationEntity
import me.ilich.vel.model.sources.locationObservable
import me.ilich.vel.model.sources.themeObservable
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
    private lateinit var realm: Realm
    private lateinit var preferences: RxSharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        rxPermissions = RxPermissions(activity)
        realm = Realm.getDefaultInstance()
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

    override fun speedCurrent(): Observable<MpsSpeed> =
            realm.where(RealmSpeedSummary::class.java)
                    .findAllAsync()
                    .asObservable()
                    .map { it.firstOrNull()?.speedLast ?: 0f }
                    .subscribeOn(AndroidSchedulers.mainThread())

    override fun speedMax(): Observable<MpsSpeed> =
            realm.where(RealmSpeedSummary::class.java)
                    .findAllAsync()
                    .asObservable()
                    .map { it.firstOrNull()?.speedMax ?: 0f }
                    .subscribeOn(AndroidSchedulers.mainThread())

    override fun speedAvg(): Observable<MpsSpeed> =
            realm.where(RealmSpeedSummary::class.java)
                    .findAllAsync()
                    .asObservable()
                    .map { it.firstOrNull()?.speedAvg ?: 0f }
                    .subscribeOn(AndroidSchedulers.mainThread())

    override fun speedReset(): Observable<Unit> =
            realm.transactionObservable { realm ->
                realm.delete(RealmSpeedSummary::class.java)
                realm.delete(RealmMotion::class.java)
            }
}