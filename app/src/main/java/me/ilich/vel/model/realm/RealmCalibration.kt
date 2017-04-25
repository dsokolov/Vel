package me.ilich.vel.model.realm

import io.realm.RealmObject
import me.ilich.vel.PitchDegree

open class RealmCalibration(
        open var pitch: PitchDegree = 0f
): RealmObject()