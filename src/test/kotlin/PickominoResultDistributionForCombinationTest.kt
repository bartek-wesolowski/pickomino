import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.EnumSet
import java.util.stream.Stream

internal class PickominoResultDistributionForCombinationTest {

    private val pickomino = Pickomino(Points)
    private val epsilon = 0.000000000000001

    @ParameterizedTest(name = "result distribution for combination: {0}, value function: {1}, dye count: {2}, used sides: {3}, points so far {4}")
    @MethodSource("getParameters")
    fun test(
        combination: List<Side>,
        usedSides: EnumSet<Side>,
        pointsSoFar: Int,
        resultDistribution: ResultDistribution
    ) {
        val actual = pickomino.getResultDistributionForCombination(
            gameState = GameState(
                availableHelpings = HelpingCollection.all(),
                topHelping = null,
                opponentTopHelpings = HelpingCollection.empty(),
            ),
            combination = combination,
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
                    combination = listOf(Side.ONE, Side.ONE),
                    resultDistribution = SingleResultDistribution(0)
                ),
                argumentsOf(
                    combination = listOf(Side.TWO, Side.TWO),
                    resultDistribution = SingleResultDistribution(0)
                ),
                argumentsOf(
                    combination = listOf(Side.ONE, Side.TWO),
                    resultDistribution = ArrayResultDistribution(
                        Points,
                        0 to 5.0 / 6,
                        7 to 1.0 / 6
                    )
                ),
                argumentsOf(
                    combination = listOf(Side.WORM, Side.WORM),
                    resultDistribution = SingleResultDistribution(10)
                ),
                argumentsOf(
                    combination = listOf(Side.ONE, Side.WORM),
                    resultDistribution = ArrayResultDistribution(
                        Points,
                        0 to 1.0 / 6,
                        6 to 1.0 / 6,
                        7 to 1.0 / 6,
                        8 to 1.0 / 6,
                        9 to 1.0 / 6,
                        10 to 1.0 / 6
                    )
                ),
                argumentsOf(
                    combination = listOf(Side.ONE, Side.ONE),
                    usedSides = EnumSet.of(Side.WORM),
                    pointsSoFar = 5,
                    resultDistribution = SingleResultDistribution(7)
                ),
                argumentsOf(
                    combination = listOf(Side.ONE, Side.TWO),
                    usedSides = EnumSet.of(Side.WORM),
                    pointsSoFar = 5,
                    resultDistribution = SingleResultDistribution(7)
                ),
                argumentsOf(
                    combination = listOf(Side.ONE, Side.THREE),
                    usedSides = EnumSet.of(Side.WORM),
                    pointsSoFar = 5,
                    resultDistribution = SingleResultDistribution(8)
                ),
            )
        }

        private fun argumentsOf(
            combination: List<Side>,
            usedSides: EnumSet<Side> = EnumSet.noneOf(Side::class.java),
            pointsSoFar: Int = 0,
            resultDistribution: ResultDistribution
        ): Arguments = Arguments.of(
            combination,
            usedSides,
            pointsSoFar,
            resultDistribution
        )
    }
}