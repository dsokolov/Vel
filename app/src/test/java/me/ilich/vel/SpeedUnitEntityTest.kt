package me.ilich.vel

import junit.framework.Assert.assertEquals
import me.ilich.vel.computer.SpeedUnitEntity
import org.junit.Test

class SpeedUnitEntityTest {

    @Test fun mps() {
        val e = SpeedUnitEntity.MetersPerSecond()
        assertEquals(0f, e.convert(0f))
        assertEquals(1f, e.convert(1f))
        assertEquals(15f, e.convert(15f))
    }

    @Test fun kmph() {
        val e = SpeedUnitEntity.KilometersPerHour()
        assertEquals(0f, e.convert(0f))
        assertEquals(3.6f, e.convert(1f), 1f)
        assertEquals(54f, e.convert(15f), 1f)
    }

    @Test fun milesph() {
        val e = SpeedUnitEntity.MilesPerHour()
        assertEquals(0f, e.convert(0f))
        assertEquals(2.24f, e.convert(1f), 1f)
        assertEquals(33.56f, e.convert(15f), 1f)
    }

}