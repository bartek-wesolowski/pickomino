import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.EnumSet
import java.util.stream.Stream

internal class WormsExpectedValueTest {

    private val worms = Worms()
    private val epsilon = 0.000000000000001

    @ParameterizedTest(name = "expected value for dye count: {1}, used sides: {2}, value so far {3}")
    @MethodSource("provideExpectedValueParameters")
    fun test(expected: ValueWithSuccessProbability, dyeCount: Int, usedSides: EnumSet<Side>, valueSoFar: Int) {
        val actual = worms.getExpectedValue(dyeCount, usedSides, valueSoFar)
        assertEquals(expected.value, actual.value, epsilon)
        assertEquals(expected.successProbability, actual.successProbability, epsilon)
    }

    companion object {
        @JvmStatic
        fun provideExpectedValueParameters(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    ValueWithSuccessProbability(5.0 / 6, 1.0 / 6),
                    1,
                    EnumSet.noneOf(Side::class.java),
                    0
                ),
                Arguments.of(
                    ValueWithSuccessProbability(
                        15.0 / 6,
                        5.0 / 6
                    ),
                    1,
                    EnumSet.of(Side.WORM),
                    5
                ),
                Arguments.of(
                    ValueWithSuccessProbability(640.0 / 216, 76.0 / 216),
                    2,
                    EnumSet.noneOf(Side::class.java),
                    0
                ),
                Arguments.of(
                    ValueWithSuccessProbability(4.0 / 1, 103.0 / 108),
                    2,
                    EnumSet.of(Side.WORM),
                    5
                ),
            )
        }
    }
}