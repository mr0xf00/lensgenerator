package com.mr0xf00.lensgenerator

import com.mr0xf00.lensgenerator.functional.setValue
import org.junit.Assert
import org.junit.Test

class GenericTest {
    @Test
    fun valuesAreChanged() {
        var result = RangedValue(
            0, 100, "", 0
        )
        result = result
            .setValue(RangedValue.from(), 10)
            .setValue(RangedValue.to(), 90)
            .setValue(RangedValue.value0(), "value0")
            .setValue(RangedValue.value1(), 10)
        Assert.assertEquals(10, result.from)
        Assert.assertEquals(90, result.to)
        Assert.assertEquals("value0", result.value0)
        Assert.assertEquals(10, result.value1)
    }
}