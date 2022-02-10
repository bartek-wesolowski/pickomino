import hm.binkley.math.fixed.FixedBigRational
import hm.binkley.math.fixed.over
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

internal class WormsResultDistributionTest {

    private val worms = Worms()

    @ParameterizedTest(name = "result distribution for dye count: {0}, used sides: {1}, value so far {2}")
    @MethodSource("getParameters")
    fun test(
        dyeCount: Int,
        usedSides: UsedSides,
        valueSoFar: Int,
        resultProbabilities: Map<Int, FixedBigRational>
    ) {
        assertEquals(resultProbabilities, worms.getResultDistribution(dyeCount, usedSides, valueSoFar))
    }

    companion object {
        @JvmStatic
        fun getParameters(): Stream<Arguments> {
            return Stream.of(
                argumentsOf(
                    dyeCount = 1,
                    resultProbabilities = mapOf(
                        0 to (5 over 6),
                        5 to (1 over 6)
                    )
                ),
                argumentsOf(
                    dyeCount = 1,
                    usedSides = UsedSides().withUsed(Side.WORM),
                    valueSoFar = 5,
                    resultProbabilities = mapOf(
                        0 to (1 over 6),
                        6 to (1 over 6),
                        7 to (1 over 6),
                        8 to (1 over 6),
                        9 to (1 over 6),
                        10 to (1 over 6)
                    )
                ),
                argumentsOf(
                    dyeCount = 2,
                    resultProbabilities = mapOf(
                        0 to (140 over 216),
                        6 to (10 over 216),
                        7 to (12 over 216),
                        8 to (14 over 216),
                        9 to (16 over 216),
                        10 to (24 over 216)
                    )
                ),
                argumentsOf(
                    dyeCount = 2,
                    usedSides = UsedSides().withUsed(Side.WORM),
                    valueSoFar = 5,
                    resultProbabilities = mapOf(
                        0 to (10 over 216),
                        7 to (30 over 216),
                        8 to (38 over 216),
                        9 to (56 over 216),
                        10 to (62 over 216),
                        11 to (8 over 216),
                        13 to (6 over 216),
                        15 to (6 over 216)
                    ),
                ),
            )
        }

        private fun argumentsOf(
            dyeCount: Int,
            usedSides: UsedSides = UsedSides(),
            valueSoFar: Int = 0,
            resultProbabilities: Map<Int, FixedBigRational>
        ): Arguments = Arguments.of(dyeCount, usedSides, valueSoFar, resultProbabilities)
    }
}