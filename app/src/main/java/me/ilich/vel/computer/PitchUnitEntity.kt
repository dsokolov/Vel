package me.ilich.vel.computer

import me.ilich.vel.PitchDegree
import me.ilich.vel.R

sealed class PitchUnitEntity() {

    abstract val titleResId: Int
    abstract fun value(v: PitchDegree): Float

    class Degree : PitchUnitEntity() {

        override val titleResId = R.string.preference_pitch_entry_degree

        override fun value(v: PitchDegree) = v

    }

    class Percent : PitchUnitEntity() {

        override val titleResId = R.string.preference_pitch_entry_percent

        override fun value(v: PitchDegree): Float {
            val alpha = Math.toRadians(v.toDouble())
            return Math.tan(alpha).toFloat() * 100f
        }

    }

}