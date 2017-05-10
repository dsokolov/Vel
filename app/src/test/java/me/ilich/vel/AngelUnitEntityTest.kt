package me.ilich.vel

import junit.framework.Assert.assertEquals
import me.ilich.vel.computer.PitchUnitEntity
import org.junit.Test

class AngelUnitEntityTest {

    @Test fun degree() {
        val e = PitchUnitEntity.Degree()
        assertEquals(0f, e.value(0f), 1f)
        assertEquals(45f, e.value(45f), 1f)
        assertEquals(90f, e.value(90f), 1f)
    }

    @Test fun percent() {
        val e = PitchUnitEntity.Percent()
        assertEquals(0f, e.value(0f), 1f)
        assertEquals(17.6f, e.value(10f), 1f)
        assertEquals(100f, e.value(45f), 1f)
    }

}