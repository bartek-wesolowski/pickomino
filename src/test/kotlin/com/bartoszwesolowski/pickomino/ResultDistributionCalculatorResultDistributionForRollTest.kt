package com.bartoszwesolowski.pickomino

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.*
import java.util.stream.Stream

internal class ResultDistributionCalculatorResultDistributionForRollTest {

    private val resultDistributionCalculator = ResultDistributionCalculator(Points)
    private val epsilon = 0.000000000000001

    @ParameterizedTest(name = "result distribution for combination: {0}, used sides: {1}, dye count: {2}, points so far {3}")
    @MethodSource("getParameters")
    fun test(
        roll: Roll,
        usedSides: EnumSet<Side>,
        pointsSoFar: Int,
        resultDistribution: ResultDistribution
    ) {
        val actual = resultDistributionCalculator.getResultDistributionForRoll(
            gameState = GameState(
                availableHelpings = HelpingCollection.all(),
                topHelping = null,
                opponentTopHelpings = HelpingCollection.empty(),
            ),
            roll = roll,
            usedSides = usedSides,
            pointsSoFar = pointsSoFar,

            memo = mutableMapOf()
        )
        for (value in Points.valueRange) {
            assertEquals(resultDistribution[value], actual[value], epsilon)
        }
    }

    companion object {
        @JvmStatic
        fun getParameters(): Stream<Arguments> {
            return Stream.of(
                argumentsOf(
                    roll = Roll.of(Side.ONE to 2),
                    resultDistribution = SingleResultDistribution(0)
                ),
                argumentsOf(
                    roll = Roll.of(Side.TWO to 2),
                    resultDistribution = SingleResultDistribution(0)
                ),
                argumentsOf(
                    roll = Roll.of(Side.ONE to 1, Side.TWO to 1),
                    resultDistribution = ArrayResultDistribution(
                        Points,
                        listOf(
                            0 to 5.0 / 6,
                            7 to 1.0 / 6
                        )
                    )
                ),
                argumentsOf(
                    roll = Roll.of(Side.WORM to 2),
                    resultDistribution = SingleResultDistribution(10)
                ),
                argumentsOf(
                    roll = Roll.of(Side.ONE to 1, Side.WORM to 1),
                    resultDistribution = ArrayResultDistribution(
                        Points,
                        listOf(
                            0 to 1.0 / 6,
                            6 to 1.0 / 6,
                            7 to 1.0 / 6,
                            8 to 1.0 / 6,
                            9 to 1.0 / 6,
                            10 to 1.0 / 6
                        )
                    )
                ),
                argumentsOf(
                    roll = Roll.of(Side.ONE to 2),
                    usedSides = EnumSet.of(Side.WORM),
                    pointsSoFar = 5,
                    resultDistribution = SingleResultDistribution(7)
                ),
                argumentsOf(
                    roll = Roll.of(Side.ONE to 1, Side.TWO to 1),
                    usedSides = EnumSet.of(Side.WORM),
                    pointsSoFar = 5,
                    resultDistribution = SingleResultDistribution(7)
                ),
                argumentsOf(
                    roll = Roll.of(Side.ONE to 1, Side.THREE to 1),
                    usedSides = EnumSet.of(Side.WORM),
                    pointsSoFar = 5,
                    resultDistribution = SingleResultDistribution(8)
                ),
            )
        }

        private fun argumentsOf(
            roll: Roll,
            usedSides: EnumSet<Side> = EnumSet.noneOf(Side::class.java),
            pointsSoFar: Int = 0,
            resultDistribution: ResultDistribution
        ): Arguments = Arguments.of(
            roll,
            usedSides,
            pointsSoFar,
            resultDistribution
        )
    }
}