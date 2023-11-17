package com.bartoszwesolowski.pickomino

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RollGeneratingTest {
    @Test
    fun oneDieRolls() {
        assertEquals(
            listOf(
                Roll.of(Side.ONE to 1),
                Roll.of(Side.TWO to 1),
                Roll.of(Side.THREE to 1),
                Roll.of(Side.FOUR to 1),
                Roll.of(Side.FIVE to 1),
                Roll.of(Side.WORM to 1),
            ),
            Roll.generateAll(1).toList(),
        )
    }

    @Test
    fun twoDiceRolls() {
        assertEquals(
            listOf(
                Roll.of(Side.ONE to 2),
                Roll.of(Side.ONE to 1, Side.TWO to 1),
                Roll.of(Side.ONE to 1, Side.THREE to 1),
                Roll.of(Side.ONE to 1, Side.FOUR to 1),
                Roll.of(Side.ONE to 1, Side.FIVE to 1),
                Roll.of(Side.ONE to 1, Side.WORM to 1),
                Roll.of(Side.TWO to 2),
                Roll.of(Side.TWO to 1, Side.THREE to 1),
                Roll.of(Side.TWO to 1, Side.FOUR to 1),
                Roll.of(Side.TWO to 1, Side.FIVE to 1),
                Roll.of(Side.TWO to 1, Side.WORM to 1),
                Roll.of(Side.THREE to 2),
                Roll.of(Side.THREE to 1, Side.FOUR to 1),
                Roll.of(Side.THREE to 1, Side.FIVE to 1),
                Roll.of(Side.THREE to 1, Side.WORM to 1),
                Roll.of(Side.FOUR to 2),
                Roll.of(Side.FOUR to 1, Side.FIVE to 1),
                Roll.of(Side.FOUR to 1, Side.WORM to 1),
                Roll.of(Side.FIVE to 2),
                Roll.of(Side.FIVE to 1, Side.WORM to 1),
                Roll.of(Side.WORM to 2),
            ),
            Roll.generateAll(2).toList(),
        )
    }
}