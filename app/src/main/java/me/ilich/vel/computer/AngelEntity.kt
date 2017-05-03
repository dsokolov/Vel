package me.ilich.vel.computer

data class AngelEntity(
        val angel: String,
        val unit: PitchUnitEntity,
        val state: State
) {
    enum class State {
        DESCEND, FLAT, ASCEND
    }
}

