import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.EnumSet
import java.util.stream.Stream

internal class WormsResultDistributionTest {

    private val worms = Worms()
    private val epsilon = 0.000000000000001

    @ParameterizedTest(name = "result distribution for dye count: {0}, value function: {1}, used sides: {2}, points so far {3}")
    @MethodSource("getParameters")
    fun test(
        dyeCount: Int,
        valueFunction: ValueFunction,
        usedSides: EnumSet<Side>,
        pointsSoFar: Int,
        resultDistribution: ResultDistribution
    ) {
        val actual = worms.getResultDistribution(dyeCount, valueFunction, usedSides, pointsSoFar)
        for (value in 0..40) {
            assertEquals(resultDistribution[value], actual[value], epsilon)
        }
    }

    companion object {
        @JvmStatic
        fun getParameters(): Stream<Arguments> {
            return Stream.of(
                argumentsOf(
                    dyeCount = 1,
                    valueFunction = PointsValueFunction,
                    resultDistribution = ResultDistribution().apply {
                        this[0] = 5.0 / 6
                        this[5] = 1.0 / 6
                    }
                ),
                argumentsOf(
                    dyeCount = 1,
                    valueFunction = PointsValueFunction,
                    usedSides = EnumSet.of(Side.WORM),
                    pointsSoFar = 5,
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
                    dyeCount = 2,
                    valueFunction = PointsValueFunction,
                    resultDistribution = ResultDistribution().apply {
                        this[0] = 140.0 / 216
                        this[6] = 10.0 / 216
                        this[7] = 12.0 / 216
                        this[8] = 14.0 / 216
                        this[9] = 16.0 / 216
                        this[10] = 24.0 / 216
                    }
                ),
                argumentsOf(
                    dyeCount = 2,
                    valueFunction = PointsValueFunction,
                    usedSides = EnumSet.of(Side.WORM),
                    pointsSoFar = 5,
                    resultDistribution = ResultDistribution().apply {
                        this[0] = 10.0 / 216
                        this[7] = 30.0 / 216
                        this[8] = 38.0 / 216
                        this[9] = 56.0 / 216
                        this[10] = 62.0 / 216
                        this[11] = 8.0 / 216
                        this[13] = 6.0 / 216
                        this[15] = 6.0 / 216
                    }
                )
            )
        }

        private fun argumentsOf(
            dyeCount: Int,
            valueFunction: ValueFunction,
            usedSides: EnumSet<Side> = EnumSet.noneOf(Side::class.java),
            pointsSoFar: Int = 0,
            resultDistribution: ResultDistribution
        ): Arguments = Arguments.of(dyeCount, valueFunction, usedSides, pointsSoFar, resultDistribution)
    }
}