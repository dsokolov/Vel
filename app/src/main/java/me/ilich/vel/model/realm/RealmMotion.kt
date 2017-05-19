package me.ilich.vel.model.realm

import io.realm.RealmObject
import me.ilich.vel.Acceleration
import me.ilich.vel.MpsSpeed
import java.util.*

open class RealmMotion(
        open var date: Date = Date(),
        open var gpsSpeed: MpsSpeed = 0f,
        open var accelerationX: Acceleration? = null,
        open var accelerationY: Acceleration? = null,
        open var accelerationZ: Acceleration? = null
) : RealmObject()