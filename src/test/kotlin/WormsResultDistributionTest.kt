import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.EnumSet
import java.util.stream.Stream

internal class WormsResultDistributionTest {

    private val worms = Worms()
    private val epsilon = 0.000000000000001

    @ParameterizedTest(name = "result distribution for dye count: {0}, used sides: {1}, value so far {2}")
    @MethodSource("getParameters")
    fun test(
        dyeCount: Int,
        usedSides: EnumSet<Side>,
        valueSoFar: Int,
        resultProbabilities: Map<Int, Double>
    ) {
        val actual = worms.getResultDistribution(dyeCount, usedSides, valueSoFar)
        for (value in resultProbabilities.keys) {
            assertTrue(actual.containsKey(value))
            assertEquals(resultProbabilities.getValue(value), actual.getValue(value), epsilon)
        }
    }

    companion object {
        @JvmStatic
        fun getParameters(): Stream<Arguments> {
            return Stream.of(
                argumentsOf(
                    dyeCount = 1,
                    resultProbabilities = mapOf(
                        0 to (5.0 / 6),
                        5 to (1.0 / 6)
                    )
                ),
                argumentsOf(
                    dyeCount = 1,
                    usedSides = EnumSet.of(Side.WORM),
                    valueSoFar = 5,
                    resultProbabilities = mapOf(
                        0 to (1.0 / 6),
                        6 to (1.0 / 6),
                        7 to (1.0 / 6),
                        8 to (1.0 / 6),
                        9 to (1.0 / 6),
                        10 to (1.0 / 6)
                    )
                ),
                argumentsOf(
                    dyeCount = 2,
                    resultProbabilities = mapOf(
                        0 to (140.0 / 216),
                        6 to (10.0 / 216),
                        7 to (12.0 / 216),
                        8 to (14.0 / 216),
                        9 to (16.0 / 216),
                        10 to (24.0 / 216)
                    )
                ),
                argumentsOf(
                    dyeCount = 2,
                    usedSides = EnumSet.of(Side.WORM),
                    valueSoFar = 5,
                    resultProbabilities = mapOf(
                        0 to (10.0 / 216),
                        7 to (30.0 / 216),
                        8 to (38.0 / 216),
                        9 to (56.0 / 216),
                        10 to (62.0 / 216),
                        11 to (8.0 / 216),
                        13 to (6.0 / 216),
                        15 to (6.0 / 216)
                    ),
                ),
            )
        }

        private fun argumentsOf(
            dyeCount: Int,
            usedSides: EnumSet<Side> = EnumSet.noneOf(Side::class.java),
            valueSoFar: Int = 0,
            resultProbabilities: Map<Int, Double>
        ): Arguments = Arguments.of(dyeCount, usedSides, valueSoFar, resultProbabilities)
    }
}