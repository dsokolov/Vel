package me.ilich.vel.computer

import android.os.Bundle
import me.ilich.vel.model.BatteryStatus
import me.ilich.vel.model.GpsStatus
import me.ilich.vel.model.Theme
import me.ilich.vel.model.sources.AccelerationEntity
import me.ilich.vel.model.sources.LocationEntity
import me.ilich.vel.model.sources.OrientationEntity
import me.ilich.vel.viper.Contracts
import rx.Observable
import java.util.*

object ComputerContracts {

    interface View {
        fun onCreate(savedInstanceState: Bundle?)
        fun onDestroy()
        fun updateGpsStatus(gpsStatus: GpsStatus)
        fun updateBatteryStatus(batteryStatus: BatteryStatus)
        fun updateTime(time: String)
        fun updateSpeedCurrent(speed: String)
        fun updateSpeedMax(speed: String)
        fun updateSpeedAvg(speed: String)
        fun updateSpeedUnit(unitResIt: Int)
        fun userToSettings(): Observable<Unit>
        fun userToAbout(): Observable<Unit>
        fun userMenu(): Observable<Unit>
        fun userLocation(): Observable<Unit>
        fun menuShow()
        fun menuHide()
        fun isMenuVisible(): Boolean
        fun configureLogger()
        fun updateTheme(theme: Theme)
    }

    interface Interactor : Contracts.Interactor {
        fun permissions(): Observable<Boolean>
        fun time(): Observable<Date>
        fun calibratedOrientation(): Observable<OrientationEntity>
        fun uncalibratedOrientation(): Observable<OrientationEntity>
        fun speedUnitObservable(): Observable<SpeedUnitEntity>
        fun pitchUnitObservable(): Observable<PitchUnitEntity>
        fun location(): Observable<LocationEntity>
        fun acceleration(): Observable<AccelerationEntity>
        fun calibrate(orientation: OrientationEntity): Observable<Unit>
        fun themeObservable(): Observable<Theme>
    }

    interface Presenter : Contracts.Presenter {
    }

    interface Router : Contracts.Router {
        fun settings()
        fun about()
    }

}