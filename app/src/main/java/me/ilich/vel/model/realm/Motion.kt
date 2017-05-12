package me.ilich.vel.model.realm

import me.ilich.vel.Acceleration
import me.ilich.vel.MpsSpeed

data class Motion(
        val speed: MpsSpeed,
        val bearing: Float,
        val accuracy: Float,
        val locationProvider: String,
        val accelerationX: Acceleration,
        val accelerationY: Acceleration,
        val accelerationZ: Acceleration
)