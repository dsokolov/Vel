package me.ilich.vel.computer

import me.ilich.vel.model.sources.AccelerationEntity
import me.ilich.vel.model.sources.LocationEntity
import me.ilich.vel.model.sources.OrientationEntity
import me.ilich.vel.viper.Contracts
import rx.Observable
import java.util.*

object ComputerContracts {

    interface View : Contracts.View {
        fun updatePermissionsError(visible: Boolean)
        fun updateTime(time: String)
        fun updateAngelValue(angel: String)
        fun updateAngelUnit(unitResIt: Int)
        fun updateAngleStateAscend()
        fun updateAngleStateDescend()
        fun updateAngleStateFlat()
        fun updateSpeed(speed: String)
        fun updateSpeedUnit(unitResIt: Int)
        fun userCalibrate(): Observable<Unit>
        fun userToSettings(): Observable<Unit>
        fun userToAbout(): Observable<Unit>
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
    }

    interface Presenter : Contracts.Presenter {
    }

    interface Router : Contracts.Router {
        fun settings()
        fun about()
    }

}