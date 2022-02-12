import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.EnumSet
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
        resultDistribution: ResultDistribution
    ) {
        val actual = worms.getResultDistributionForCombination(
            combination,
            usedSides,
            valueSoFar,
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
                    resultDistribution = ResultDistribution.failed()
                ),
                argumentsOf(
                    combination = listOf(Side.TWO, Side.TWO),
                    resultDistribution = ResultDistribution.failed()
                ),
                argumentsOf(
                    combination = listOf(Side.ONE, Side.TWO),
                    resultDistribution = ResultDistribution().apply {
                        this[0] = 5.0 / 6
                        this[7] = 1.0 / 6
                    }
                ),
                argumentsOf(
                    combination = listOf(Side.WORM, Side.WORM),
                    resultDistribution = ResultDistribution.successful(10)
                ),
                argumentsOf(
                    combination = listOf(Side.ONE, Side.WORM),
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
                    usedSides = EnumSet.of(Side.WORM),
                    valueSoFar = 5,
                    resultDistribution = ResultDistribution.successful(7)
                ),
                argumentsOf(
                    combination = listOf(Side.ONE, Side.TWO),
                    usedSides = EnumSet.of(Side.WORM),
                    valueSoFar = 5,
                    resultDistribution = ResultDistribution.successful(7)
                ),
                argumentsOf(
                    combination = listOf(Side.ONE, Side.THREE),
                    usedSides = EnumSet.of(Side.WORM),
                    valueSoFar = 5,
                    resultDistribution = ResultDistribution.successful(8)
                ),
            )
        }

        private fun argumentsOf(
            combination: List<Side>,
            usedSides: EnumSet<Side> = EnumSet.noneOf(Side::class.java),
            valueSoFar: Int = 0,
            resultDistribution: ResultDistribution
        ): Arguments = Arguments.of(
            combination, usedSides, valueSoFar, resultDistribution
        )
    }
}