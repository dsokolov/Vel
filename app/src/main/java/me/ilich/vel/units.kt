package me.ilich.vel

import android.content.Context
import android.support.annotation.AttrRes
import android.support.annotation.ColorInt
import android.support.v4.content.ContextCompat
import android.util.TypedValue

typealias MpsSpeed = Float
typealias KmphSpeed = Float
typealias Latitude = Double
typealias Longitude = Double
typealias RollDegree = Float
typealias PitchDegree = Float
typealias YawDegree = Float
typealias Acceleration = Float

@ColorInt fun Context.getColorByAttrId(@AttrRes attrId: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(R.attr.velColorWarning, typedValue, true)
    val colorId = typedValue.data
    val color = ContextCompat.getColor(this, colorId)
    return color

}