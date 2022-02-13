import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.EnumSet
import java.util.stream.Stream

internal class WormsResultDistributionForCombinationTest {

    private val worms = Worms()
    private val epsilon = 0.000000000000001

    @ParameterizedTest(name = "result distribution for combinaiton: {0}, value function: {1}, dye count: {2}, used sides: {3}, points so far {4}")
    @MethodSource("getParameters")
    fun test(
        combination: List<Side>,
        valueFunction: ValueFunction,
        usedSides: EnumSet<Side>,
        pointsSoFar: Int,
        resultDistribution: ResultDistribution
    ) {
        val actual = worms.getResultDistributionForCombination(
            combination,
            valueFunction,
            usedSides,
            pointsSoFar,
            mutableMapOf()
        )
        for (value in 0..40) {
            assertEquals(resultDistribution[value], actual[value], epsilon)
        }
    }

    companion object {
        @JvmStatic
        fun getParameters(): Stream<Arguments> {
            return Stream.of(
                argumentsOf(
                    combination = listOf(Side.ONE, Side.ONE),
                    valueFunction = PointsValueFunction,
                    resultDistribution = ResultDistribution.failed()
                ),
                argumentsOf(
                    combination = listOf(Side.TWO, Side.TWO),
                    valueFunction = PointsValueFunction,
                    resultDistribution = ResultDistribution.failed()
                ),
                argumentsOf(
                    combination = listOf(Side.ONE, Side.TWO),
                    valueFunction = PointsValueFunction,
                    resultDistribution = ResultDistribution().apply {
                        this[0] = 5.0 / 6
                        this[7] = 1.0 / 6
                    }
                ),
                argumentsOf(
                    combination = listOf(Side.WORM, Side.WORM),
                    valueFunction = PointsValueFunction,
                    resultDistribution = ResultDistribution.successful(10)
                ),
                argumentsOf(
                    combination = listOf(Side.ONE, Side.WORM),
                    valueFunction = PointsValueFunction,
                    resultDistribution = ResultDistribution().apply {
                        this[0] = 1.0 / 6
                        this[6] = 1.0 / 6
                        this[7] = 1.0 / 6
                        this[8] = 1.0 / 6
                        this[9] = 1.0 / 6
                        this[10] = 1.0 / 6
                    }
                ),
                argumentsOf(
                    combination = listOf(Side.ONE, Side.ONE),
                    valueFunction = PointsValueFunction,
                    usedSides = EnumSet.of(Side.WORM),
                    pointsSoFar = 5,
                    resultDistribution = ResultDistribution.successful(7)
                ),
                argumentsOf(
                    combination = listOf(Side.ONE, Side.TWO),
                    valueFunction = PointsValueFunction,
                    usedSides = EnumSet.of(Side.WORM),
                    pointsSoFar = 5,
                    resultDistribution = ResultDistribution.successful(7)
                ),
                argumentsOf(
                    combination = listOf(Side.ONE, Side.THREE),
                    valueFunction = PointsValueFunction,
                    usedSides = EnumSet.of(Side.WORM),
                    pointsSoFar = 5,
                    resultDistribution = ResultDistribution.successful(8)
                ),
            )
        }

        private fun argumentsOf(
            combination: List<Side>,
            valueFunction: ValueFunction,
            usedSides: EnumSet<Side> = EnumSet.noneOf(Side::class.java),
            pointsSoFar: Int = 0,
            resultDistribution: ResultDistribution
        ): Arguments = Arguments.of(
            combination, valueFunction, usedSides, pointsSoFar, resultDistribution
        )
    }
}