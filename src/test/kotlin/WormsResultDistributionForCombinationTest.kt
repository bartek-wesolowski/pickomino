import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.*
import java.util.stream.Stream

internal class WormsResultDistributionForCombinationTest {

    private val worms = Worms()
    private val epsilon = 0.000000000000001

    @ParameterizedTest(name = "result distribution for combinaiton: {0}, dye count: {1}, used sides: {2}, value so far {3}")
    @MethodSource("getParameters")
    fun test(
        combination: List<Side>,
        usedSides: EnumSet<Side>,
        valueSoFar: Int,
        resultProbabilities: Map<Int, Double>
    ) {
        val actual = worms.getResultDistributionForCombination(
            combination,
            usedSides,
            valueSoFar,
            mutableMapOf()
        )
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
                    combination = listOf(Side.ONE, Side.ONE),
                    expected = failed
                ),
                argumentsOf(
                    combination = listOf(Side.TWO, Side.TWO),
                    expected = failed
                ),
                argumentsOf(
                    combination = listOf(Side.ONE, Side.TWO),
                    expected = mapOf(
                        0 to (5.0 / 6),
                        7 to (1.0 / 6)
                    )
                ),
                argumentsOf(
                    combination = listOf(Side.WORM, Side.WORM),
                    expected = successful(10)
                ),
                argumentsOf(
                    combination = listOf(Side.ONE, Side.WORM),
                    expected = mapOf(
                        0 to (1.0 / 6),
                        6 to (1.0 / 6),
                        7 to (1.0 / 6),
                        8 to (1.0 / 6),
                        9 to (1.0 / 6),
                        10 to (1.0 / 6)
                    )
                ),
                argumentsOf(
                    combination = listOf(Side.ONE, Side.ONE),
                    usedSides = EnumSet.of(Side.WORM),
                    valueSoFar = 5,
                    expected = successful(7)
                ),
                argumentsOf(
                    combination = listOf(Side.ONE, Side.TWO),
                    usedSides = EnumSet.of(Side.WORM),
                    valueSoFar = 5,
                    expected = successful(7)
                ),
                argumentsOf(
                    combination = listOf(Side.ONE, Side.THREE),
                    usedSides = EnumSet.of(Side.WORM),
                    valueSoFar = 5,
                    expected = successful(8)
                ),
            )
        }

        private fun argumentsOf(
            combination: List<Side>,
            usedSides: EnumSet<Side> = EnumSet.noneOf(Side::class.java),
            valueSoFar: Int = 0,
            expected: Map<Int, Double>
        ): Arguments = Arguments.of(
            combination, usedSides, valueSoFar, expected
        )
    }
}