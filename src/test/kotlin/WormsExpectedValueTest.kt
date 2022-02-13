import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.EnumSet
import java.util.stream.Stream

internal class WormsExpectedValueTest {

    private val worms = Worms()
    private val epsilon = 0.000000000000001

    @ParameterizedTest(name = "expected value for dye count: {0}, value function: {1}, used sides: {2}, points so far {3}")
    @MethodSource("provideExpectedValueParameters")
    fun test(
        dyeCount: Int,
        valueFunction: ValueFunction,
        usedSides: EnumSet<Side>,
        valueSoFar: Int,
        expected: ValueWithSuccessProbability
    ) {
        val actual = worms.getExpectedValue(dyeCount, valueFunction, usedSides, valueSoFar)
        assertEquals(expected.value, actual.value, epsilon)
        assertEquals(expected.successProbability, actual.successProbability, epsilon)
    }

    companion object {
        @JvmStatic
        fun provideExpectedValueParameters(): Stream<Arguments> {
            return Stream.of(
                argumentsOf(
                    dyeCount = 1,
                    valueFunction = PointsValueFunction,
                    usedSides = EnumSet.noneOf(Side::class.java),
                    valueSoFar = 0,
                    valueWithSuccessProbability = ValueWithSuccessProbability(5.0 / 6, 1.0 / 6)
                ),
                argumentsOf(
                    dyeCount = 1,
                    valueFunction = PointsValueFunction,
                    usedSides = EnumSet.of(Side.WORM),
                    valueSoFar = 5,
                    valueWithSuccessProbability = ValueWithSuccessProbability(
                        15.0 / 6,
                        5.0 / 6
                    )
                ),
                argumentsOf(
                    dyeCount = 2,
                    valueFunction = PointsValueFunction,
                    usedSides = EnumSet.noneOf(Side::class.java),
                    valueSoFar = 0,
                    valueWithSuccessProbability = ValueWithSuccessProbability(640.0 / 216, 76.0 / 216)
                ),
                argumentsOf(
                    dyeCount = 2,
                    valueFunction = PointsValueFunction,
                    usedSides = EnumSet.of(Side.WORM),
                    valueSoFar = 5,
                    valueWithSuccessProbability = ValueWithSuccessProbability(4.0 / 1, 103.0 / 108)
                ),
            )
        }

        private fun argumentsOf(
            dyeCount: Int,
            valueFunction: ValueFunction,
            usedSides: EnumSet<Side>,
            valueSoFar: Int,
            valueWithSuccessProbability: ValueWithSuccessProbability,
        ) = Arguments.of(
            dyeCount,
            valueFunction,
            usedSides,
            valueSoFar,
            valueWithSuccessProbability
        )
    }
}