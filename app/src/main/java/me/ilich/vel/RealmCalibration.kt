package me.ilich.vel

import io.realm.RealmObject

open class RealmCalibration(
        open var pitch: PitchDegree = 0f
): RealmObject()