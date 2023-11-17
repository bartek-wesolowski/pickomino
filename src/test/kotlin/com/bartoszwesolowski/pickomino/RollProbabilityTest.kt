package com.bartoszwesolowski.pickomino

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class RollProbabilityTest {

    @ParameterizedTest
    @MethodSource("getOneDieParameters")
    fun oneDie(
        sideCount: Array<Pair<Side, Int>>,
        expectedProbability: Double
    ) {
        assertEquals(expectedProbability, Roll.of(*sideCount).probability())
    }

    @ParameterizedTest
    @MethodSource("getTwoDiceParameters")
    fun twoDice(
        sideCount: Array<Pair<Side, Int>>,
        expectedProbability: Double
    ) {
        assertEquals(expectedProbability, Roll.of(*sideCount).probability())
    }

    companion object {
        @JvmStatic
        fun getOneDieParameters(): Stream<Arguments> {
            return Stream.of(
                argumentsOf(
                    sideCount = arrayOf(Side.ONE to 1),
                    expectedProbability = 1.0 / 6
                ),
                argumentsOf(
                    sideCount = arrayOf(Side.TWO to 1),
                    expectedProbability = 1.0 / 6
                ),
                argumentsOf(
                    sideCount = arrayOf(Side.THREE to 1),
                    expectedProbability = 1.0 / 6
                ),
                argumentsOf(
                    sideCount = arrayOf(Side.FOUR to 1),
                    expectedProbability = 1.0 / 6
                ),
                argumentsOf(
                    sideCount = arrayOf(Side.FIVE to 1),
                    expectedProbability = 1.0 / 6
                ),
                argumentsOf(
                    sideCount = arrayOf(Side.WORM to 1),
                    expectedProbability = 1.0 / 6
                ),
            )
        }

        @JvmStatic
        fun getTwoDiceParameters(): Stream<Arguments> {
            return Stream.of(
                argumentsOf(
                    sideCount = arrayOf(Side.ONE to 2),
                    expectedProbability = 1.0 / 36
                ),
                argumentsOf(
                    sideCount = arrayOf(Side.ONE to 1, Side.TWO to 1),
                    expectedProbability = 2.0 / 36
                ),
                argumentsOf(
                    sideCount = arrayOf(Side.ONE to 1, Side.THREE to 1),
                    expectedProbability = 2.0 / 36
                ),
                argumentsOf(
                    sideCount = arrayOf(Side.ONE to 1, Side.FOUR to 1),
                    expectedProbability = 2.0 / 36
                ),
                argumentsOf(
                    sideCount = arrayOf(Side.ONE to 1, Side.FIVE to 1),
                    expectedProbability = 2.0 / 36
                ),
                argumentsOf(
                    sideCount = arrayOf(Side.ONE to 1, Side.WORM to 1),
                    expectedProbability = 2.0 / 36
                ),
                argumentsOf(
                    sideCount = arrayOf(Side.TWO to 2),
                    expectedProbability = 1.0 / 36
                ),
                argumentsOf(
                    sideCount = arrayOf(Side.TWO to 1, Side.THREE to 1),
                    expectedProbability = 2.0 / 36
                ),
                argumentsOf(
                    sideCount = arrayOf(Side.TWO to 1, Side.FOUR to 1),
                    expectedProbability = 2.0 / 36
                ),
                argumentsOf(
                    sideCount = arrayOf(Side.TWO to 1, Side.FIVE to 1),
                    expectedProbability = 2.0 / 36
                ),
                argumentsOf(
                    sideCount = arrayOf(Side.TWO to 1, Side.WORM to 1),
                    expectedProbability = 2.0 / 36
                ),
                argumentsOf(
                    sideCount = arrayOf(Side.THREE to 2),
                    expectedProbability = 1.0 / 36
                ),
                argumentsOf(
                    sideCount = arrayOf(Side.THREE to 1, Side.FOUR to 1),
                    expectedProbability = 2.0 / 36
                ),
                argumentsOf(
                    sideCount = arrayOf(Side.THREE to 1, Side.FIVE to 1),
                    expectedProbability = 2.0 / 36
                ),
                argumentsOf(
                    sideCount = arrayOf(Side.THREE to 1, Side.WORM to 1),
                    expectedProbability = 2.0 / 36
                ),
                argumentsOf(
                    sideCount = arrayOf(Side.FOUR to 2),
                    expectedProbability = 1.0 / 36
                ),
                argumentsOf(
                    sideCount = arrayOf(Side.FOUR to 1, Side.FIVE to 1),
                    expectedProbability = 2.0 / 36
                ),
                argumentsOf(
                    sideCount = arrayOf(Side.FOUR to 1, Side.WORM to 1),
                    expectedProbability = 2.0 / 36
                ),
                argumentsOf(
                    sideCount = arrayOf(Side.FIVE to 2),
                    expectedProbability = 1.0 / 36
                ),
                argumentsOf(
                    sideCount = arrayOf(Side.FIVE to 1, Side.WORM to 1),
                    expectedProbability = 2.0 / 36
                ),
                argumentsOf(
                    sideCount = arrayOf(Side.WORM to 2),
                    expectedProbability = 1.0 / 36
                ),
            )
        }

        private fun argumentsOf(
            sideCount: Array<Pair<Side, Int>>,
            expectedProbability: Double
        ) = Arguments.of(sideCount, expectedProbability)
    }
}