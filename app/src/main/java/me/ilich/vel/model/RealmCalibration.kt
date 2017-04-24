package me.ilich.vel.model

import io.realm.RealmObject
import me.ilich.vel.PitchDegree

open class RealmCalibration(
        open var pitch: PitchDegree = 0f
): RealmObject()