package me.ilich.vel.computer

import me.ilich.vel.MpsSpeed
import me.ilich.vel.R

sealed class SpeedUnitEntity {

    abstract val titleResId: Int
    abstract fun convert(speed: MpsSpeed): Float

    class MetersPerSecond : SpeedUnitEntity() {

        override val titleResId = R.string.computer_speed_mps
        override fun convert(speed: MpsSpeed): Float = speed

    }

    class KilometersPerHour : SpeedUnitEntity() {

        companion object {
            private val MPS_TO_KMPH = 3.6f
        }

        override val titleResId = R.string.computer_speed_kmph
        override fun convert(speed: MpsSpeed): Float = speed * MPS_TO_KMPH

    }

    class MilesPerHour : SpeedUnitEntity() {

        companion object {
            private val MPS_TO_MiPH = 2.236936f
        }

        override val titleResId = R.string.computer_speed_miph
        override fun convert(speed: MpsSpeed): Float = speed * MPS_TO_MiPH

    }

}