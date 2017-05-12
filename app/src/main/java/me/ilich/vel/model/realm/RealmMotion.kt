package me.ilich.vel.model.realm

import me.ilich.vel.Acceleration
import me.ilich.vel.MpsSpeed

open class RealmMotion(
        open var gpsSpeed: MpsSpeed? = null,
        open var accelerationX: Acceleration? = null,
        open var accelerationY: Acceleration? = null,
        open var accelerationZ: Acceleration? = null
)

