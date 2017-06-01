package me.ilich.vel.computer

import android.os.Bundle
import me.ilich.vel.MpsSpeed
import me.ilich.vel.model.BatteryStatus
import me.ilich.vel.model.Theme
import me.ilich.vel.model.sources.LocationEntity
import rx.Observable
import rx.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit

class ComputerStubInteractor(val activity: ComputerActivity) : ComputerContracts.Interactor {

    override fun onCreate(savedInstanceState: Bundle?) {}

    override fun onDestroy() {}

    override fun permissions(): Observable<Boolean> = Observable.just(true)

    override fun time(): Observable<Date> = Observable.interval(0L, 1L, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .map { Date() }

    override fun speedUnitObservable(): Observable<SpeedUnitEntity> = Observable.interval(0L, 12L, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .map {
                when (it % 3L) {
                    0L -> SpeedUnitEntity.KilometersPerHour()
                    1L -> SpeedUnitEntity.MetersPerSecond()
                    else -> SpeedUnitEntity.MilesPerHour()
                }
            }

    override fun location(): Observable<LocationEntity> = Observable.interval(0L, 500L, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .map {
                val speed = (it % 100).toFloat()
                LocationEntity(0.0, 0.0, speed, it, 0f, "STUB", 123f)
            }

    override fun themeObservable(): Observable<Theme> = me.ilich.vel.model.sources.themeObservable(activity)

/*    override fun batteryStatus(): Observable<BatteryStatus> = Observable.interval(0L, 100L, TimeUnit.MILLISECONDS).
            map {
                val percents = it % 100
                BatteryStatus(percents.toInt(), false)
            }*/

    override fun speedCurrent(): Observable<MpsSpeed> = Observable.just(60f)

    override fun speedMax(): Observable<MpsSpeed> = Observable.just(120f)

    override fun speedAvg(): Observable<MpsSpeed> = Observable.just(45f)

    override fun speedReset(): Observable<Unit> {
        TODO("implement speedReset")
    }

}