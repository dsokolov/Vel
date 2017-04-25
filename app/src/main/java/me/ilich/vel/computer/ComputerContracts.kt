package me.ilich.vel.computer

import android.os.Bundle
import me.ilich.vel.model.sources.AccelerationEntity
import me.ilich.vel.model.sources.LocationEntity
import me.ilich.vel.model.sources.OrientationEntity
import rx.Observable
import java.util.*

interface ComputerContracts {

    fun onCreate(savedInstanceState: Bundle?)
    fun onDestroy()

    interface View : ComputerContracts {
        fun updatePermissionsError(visible: Boolean)
        fun updateTime(time: String)
        fun updateAngel(angel: String)
        fun updateSpeed(speed: String)
        fun updateAcceleration(acceleration: String)
        fun userCalibrate(): Observable<Unit>
    }

    interface Interactor : ComputerContracts {
        fun permissions(): Observable<Boolean>
        fun time(): Observable<Date>
        fun calibratedOrientation(): Observable<OrientationEntity>
        fun uncalibratedOrientation(): Observable<OrientationEntity>
        fun location(): Observable<LocationEntity>
        fun acceleration(): Observable<AccelerationEntity>
        fun calibrate(orientation: OrientationEntity): Observable<Unit>
    }

}