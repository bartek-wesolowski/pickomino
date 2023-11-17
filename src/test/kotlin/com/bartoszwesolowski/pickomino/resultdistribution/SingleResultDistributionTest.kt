package com.bartoszwesolowski.pickomino.resultdistribution

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.AbstractMap.SimpleEntry

class SingleResultDistributionTest {

    @Test
    fun `contains the value`() {
        assertEquals(1.0, SingleResultDistribution(3)[3])
    }

    @Test
    fun `doesn't contain other values`() {
        assertEquals(0.0, SingleResultDistribution(3)[4])
    }

    @Test
    fun `expected value is correct`() {
        assertEquals(3.0, SingleResultDistribution(3).getExpectedValue())
    }

    @Test
    fun `iterator works`() {
        assertEquals(listOf(SimpleEntry(3, 1.0)), SingleResultDistribution(3).toList())
    }
}