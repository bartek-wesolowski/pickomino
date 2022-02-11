import hm.binkley.math.fixed.FixedBigRational
import hm.binkley.math.fixed.over
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.*
import java.util.stream.Stream

internal class WormsResultDistributionForCombinationTest {

    private val worms = Worms()

    @ParameterizedTest(name = "result distribution for combinaiton: {0}, dye count: {1}, used sides: {2}, value so far {3}")
    @MethodSource("getParameters")
    fun test(
        combination: List<Side>,
        usedSides: EnumSet<Side>,
        valueSoFar: Int,
        expected: Map<Int, FixedBigRational>
    ) {
        assertEquals(
            expected,
            worms.getResultDistributionForCombination(
                combination,
                usedSides,
                valueSoFar,
                mutableMapOf()
            )
        )
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
                        0 to (5 over 6),
                        7 to (1 over 6)
                    )
                ),
                argumentsOf(
                    combination = listOf(Side.WORM, Side.WORM),
                    expected = successful(10)
                ),
                argumentsOf(
                    combination = listOf(Side.ONE, Side.WORM),
                    expected = mapOf(
                        0 to (1 over 6),
                        6 to (1 over 6),
                        7 to (1 over 6),
                        8 to (1 over 6),
                        9 to (1 over 6),
                        10 to (1 over 6)
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
            expected: Map<Int, FixedBigRational>
        ): Arguments = Arguments.of(
            combination, usedSides, valueSoFar, expected
        )
    }
}