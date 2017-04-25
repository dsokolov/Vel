package me.ilich.vel.computer

import android.os.Bundle
import me.ilich.vel.model.sources.AccelerationEntity
import me.ilich.vel.model.sources.LocationEntity
import me.ilich.vel.model.sources.OrientationEntity
import rx.Observable
import java.util.*

interface ComputerContracts {

    interface View {
        fun onCreate(savedInstanceState: Bundle?)
        fun onDestroy()
        fun onBackPressed(): Boolean
        fun updatePermissionsError(visible: Boolean)
        fun updateTime(time: String)
        fun updateAngelValue(angel: String)
        fun updateAngleStateAscend()
        fun updateAngleStateDescend()
        fun updateAngleStateFlat()
        fun updateSpeed(speed: String)
        fun updateAcceleration(acceleration: String)
        fun userCalibrate(): Observable<Unit>
        fun userToSettings(): Observable<Unit>
        fun userToAbout(): Observable<Unit>
    }

    interface Interactor {
        fun onCreate(savedInstanceState: Bundle?)
        fun onDestroy()
        fun permissions(): Observable<Boolean>
        fun time(): Observable<Date>
        fun calibratedOrientation(): Observable<OrientationEntity>
        fun uncalibratedOrientation(): Observable<OrientationEntity>
        fun location(): Observable<LocationEntity>
        fun acceleration(): Observable<AccelerationEntity>
        fun calibrate(orientation: OrientationEntity): Observable<Unit>
    }

    interface Router {
        fun settings()
        fun about()
    }

}