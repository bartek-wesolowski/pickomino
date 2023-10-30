import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.EnumSet
import java.util.stream.Stream

internal class PickominoResultDistributionForCombinationTest {

    private val pickomino = Pickomino(ValueFunction.Points)
    private val epsilon = 0.000000000000001

    @ParameterizedTest(name = "result distribution for combination: {0}, value function: {1}, dye count: {2}, used sides: {3}, points so far {4}")
    @MethodSource("getParameters")
    fun <V: ValueFunction> test(
        combination: List<Side>,
        usedSides: EnumSet<Side>,
        pointsSoFar: Int,
        resultDistribution: ResultDistribution<V>
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
        for (value in ValueFunction.Points.valueRange) {
            assertEquals(resultDistribution[value], actual[value], epsilon)
        }
    }

    companion object {
        @JvmStatic
        fun getParameters(): Stream<Arguments> {
            return Stream.of(
                argumentsOf(
                    combination = listOf(Side.ONE, Side.ONE),
                    resultDistribution = ResultDistribution.single(ValueFunction.Points, 0)
                ),
                argumentsOf(
                    combination = listOf(Side.TWO, Side.TWO),
                    resultDistribution = ResultDistribution.single(ValueFunction.Points, 0)
                ),
                argumentsOf(
                    combination = listOf(Side.ONE, Side.TWO),
                    resultDistribution = ResultDistribution(ValueFunction.Points).apply {
                        this[0] = 5.0 / 6
                        this[7] = 1.0 / 6
                    }
                ),
                argumentsOf(
                    combination = listOf(Side.WORM, Side.WORM),
                    resultDistribution = ResultDistribution.single(ValueFunction.Points, 10)
                ),
                argumentsOf(
                    combination = listOf(Side.ONE, Side.WORM),
                    resultDistribution = ResultDistribution(ValueFunction.Points).apply {
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
                    pointsSoFar = 5,
                    resultDistribution = ResultDistribution.single(ValueFunction.Points, 7)
                ),
                argumentsOf(
                    combination = listOf(Side.ONE, Side.TWO),
                    usedSides = EnumSet.of(Side.WORM),
                    pointsSoFar = 5,
                    resultDistribution = ResultDistribution.single(ValueFunction.Points, 7)
                ),
                argumentsOf(
                    combination = listOf(Side.ONE, Side.THREE),
                    usedSides = EnumSet.of(Side.WORM),
                    pointsSoFar = 5,
                    resultDistribution = ResultDistribution.single(ValueFunction.Points, 8)
                ),
            )
        }

        private fun argumentsOf(
            combination: List<Side>,
            usedSides: EnumSet<Side> = EnumSet.noneOf(Side::class.java),
            pointsSoFar: Int = 0,
            resultDistribution: ResultDistribution<ValueFunction.Points>
        ): Arguments = Arguments.of(
            combination,
            usedSides,
            pointsSoFar,
            resultDistribution
        )
    }
}