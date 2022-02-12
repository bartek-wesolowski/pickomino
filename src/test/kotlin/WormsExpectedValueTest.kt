import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.EnumSet
import java.util.stream.Stream

internal class WormsExpectedValueTest {

    private val worms = Worms()
    private val epsilon = 0.000000000000001

    @ParameterizedTest(name = "expected value for dye count: {0}, used sides: {1}, value so far {2}")
    @MethodSource("provideExpectedValueParameters")
    fun test(dyeCount: Int, usedSides: EnumSet<Side>, valueSoFar: Int, expected: ValueWithSuccessProbability) {
        val actual = worms.getExpectedValue(dyeCount, usedSides, valueSoFar)
        assertEquals(expected.value, actual.value, epsilon)
        assertEquals(expected.successProbability, actual.successProbability, epsilon)
    }

    companion object {
        @JvmStatic
        fun provideExpectedValueParameters(): Stream<Arguments> {
            return Stream.of(
                argumentsOf(
                    valueSoFar = 0,
                    dyeCount = 1,
                    usedSides = EnumSet.noneOf(Side::class.java),
                    valueWithSuccessProbability = ValueWithSuccessProbability(5.0 / 6, 1.0 / 6)
                ),
                argumentsOf(
                    valueSoFar = 5,
                    dyeCount = 1,
                    usedSides = EnumSet.of(Side.WORM),
                    valueWithSuccessProbability = ValueWithSuccessProbability(
                        15.0 / 6,
                        5.0 / 6
                    )
                ),
                argumentsOf(
                    valueSoFar = 0,
                    dyeCount = 2,
                    usedSides = EnumSet.noneOf(Side::class.java),
                    valueWithSuccessProbability = ValueWithSuccessProbability(640.0 / 216, 76.0 / 216)
                ),
                argumentsOf(
                    valueSoFar = 5,
                    dyeCount = 2,
                    usedSides = EnumSet.of(Side.WORM),
                    valueWithSuccessProbability = ValueWithSuccessProbability(4.0 / 1, 103.0 / 108)
                ),
            )
        }

        private fun argumentsOf(
            valueSoFar: Int,
            dyeCount: Int,
            usedSides: EnumSet<Side>,
            valueWithSuccessProbability: ValueWithSuccessProbability,
        ) = Arguments.of(
            dyeCount,
            usedSides,
            valueSoFar,
            valueWithSuccessProbability
        )
    }
}