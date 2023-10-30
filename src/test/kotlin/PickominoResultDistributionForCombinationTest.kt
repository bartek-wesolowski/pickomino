import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.EnumSet
import java.util.stream.Stream

internal class PickominoResultDistributionForCombinationTest {

    private val pickomino = Pickomino()
    private val epsilon = 0.000000000000001

    @ParameterizedTest(name = "result distribution for combination: {0}, value function: {1}, dye count: {2}, used sides: {3}, points so far {4}")
    @MethodSource("getParameters")
    fun <V: ValueFunction> test(
        combination: List<Side>,
        valueFunction: V,
        usedSides: EnumSet<Side>,
        pointsSoFar: Int,
        resultDistribution: ResultDistribution<V>
    ) {
        val actual = pickomino.getResultDistributionForCombination(
            combination = combination,
            usedSides = usedSides,
            pointsSoFar = pointsSoFar,
            availableHelpings = HelpingCollection.all(),
            topHelping = null,
            opponentTopHelpings = HelpingCollection.empty(),
            valueFunction = valueFunction,
            memo = mutableMapOf()
        )
        for (value in valueFunction.valueRange) {
            assertEquals(resultDistribution[value], actual[value], epsilon)
        }
    }

    companion object {
        @JvmStatic
        fun getParameters(): Stream<Arguments> {
            return Stream.of(
                argumentsOf(
                    combination = listOf(Side.ONE, Side.ONE),
                    valueFunction = ValueFunction.Points,
                    resultDistribution = ResultDistribution.single(ValueFunction.Points, 0)
                ),
                argumentsOf(
                    combination = listOf(Side.TWO, Side.TWO),
                    valueFunction = ValueFunction.Points,
                    resultDistribution = ResultDistribution.single(ValueFunction.Points, 0)
                ),
                argumentsOf(
                    combination = listOf(Side.ONE, Side.TWO),
                    valueFunction = ValueFunction.Points,
                    resultDistribution = ResultDistribution(ValueFunction.Points).apply {
                        this[0] = 5.0 / 6
                        this[7] = 1.0 / 6
                    }
                ),
                argumentsOf(
                    combination = listOf(Side.WORM, Side.WORM),
                    valueFunction = ValueFunction.Points,
                    resultDistribution = ResultDistribution.single(ValueFunction.Points, 10)
                ),
                argumentsOf(
                    combination = listOf(Side.ONE, Side.WORM),
                    valueFunction = ValueFunction.Points,
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
                    valueFunction = ValueFunction.Points,
                    usedSides = EnumSet.of(Side.WORM),
                    pointsSoFar = 5,
                    resultDistribution = ResultDistribution.single(ValueFunction.Points, 7)
                ),
                argumentsOf(
                    combination = listOf(Side.ONE, Side.TWO),
                    valueFunction = ValueFunction.Points,
                    usedSides = EnumSet.of(Side.WORM),
                    pointsSoFar = 5,
                    resultDistribution = ResultDistribution.single(ValueFunction.Points, 7)
                ),
                argumentsOf(
                    combination = listOf(Side.ONE, Side.THREE),
                    valueFunction = ValueFunction.Points,
                    usedSides = EnumSet.of(Side.WORM),
                    pointsSoFar = 5,
                    resultDistribution = ResultDistribution.single(ValueFunction.Points, 8)
                ),
            )
        }

        private fun <V: ValueFunction> argumentsOf(
            combination: List<Side>,
            valueFunction: V,
            usedSides: EnumSet<Side> = EnumSet.noneOf(Side::class.java),
            pointsSoFar: Int = 0,
            resultDistribution: ResultDistribution<V>
        ): Arguments = Arguments.of(
            combination,
            valueFunction,
            usedSides,
            pointsSoFar,
            resultDistribution
        )
    }
}