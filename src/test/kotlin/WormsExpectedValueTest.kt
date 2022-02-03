import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

internal class WormsExpectedValueTest {

    private val worms = Worms()

    @ParameterizedTest(name = "expected value for dye count: {1}, used sides: {2}, value so far {3}")
    @MethodSource("provideExpectedValueParameters")
    fun test(expected: ValueWithSuccessProbability, dyeCount: Int, usedSides: UsedSides, valueSoFar: Int) {
        assertEquals(expected, worms.getExpectedValue(dyeCount, usedSides, valueSoFar))
    }

    companion object {
        @JvmStatic
        fun provideExpectedValueParameters(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    ValueWithSuccessProbability(5 over 6, 1 over 6),
                    1,
                    UsedSides(),
                    0
                ),
                Arguments.of(
                    ValueWithSuccessProbability(
                        15 over 6,
                        5 over 6
                    ),
                    1,
                    UsedSides().withUsed(Side.WORM),
                    5
                ),
                Arguments.of(
                    ValueWithSuccessProbability(640 over 216, 76 over 216),
                    2,
                    UsedSides(),
                    0
                ),
                Arguments.of(
                    ValueWithSuccessProbability(4 over 1, 103 over 108),
                    2,
                    UsedSides().withUsed(Side.WORM),
                    5
                ),
            )
        }
    }
}