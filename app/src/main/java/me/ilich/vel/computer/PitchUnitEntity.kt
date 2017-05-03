package me.ilich.vel.computer

import android.content.Context
import me.ilich.vel.PitchDegree
import me.ilich.vel.R

sealed class PitchUnitEntity() {

    abstract val title: String
    abstract fun value(v: PitchDegree): Float

    class Degree(context: Context) : PitchUnitEntity() {

        override val title: String = context.getString(R.string.pitch_unit_degree)

        override fun value(v: PitchDegree) = v

    }

    class Percent(context: Context) : PitchUnitEntity() {

        override val title: String = context.getString(R.string.pitch_unit_percent)

        override fun value(v: PitchDegree): Float {
            val alpha = Math.toRadians(v.toDouble())
            return Math.sin(alpha).toFloat() * 100f
        }

    }

}