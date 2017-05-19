package me.ilich.vel.model.realm

import io.realm.RealmObject
import me.ilich.vel.MpsSpeed

open class RealmSpeedSummary(
        open var speedLast: MpsSpeed = 0f,
        open var speedAvg: MpsSpeed = 0f,
        open var speedMax: MpsSpeed = 0f
) : RealmObject()