import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable

internal class ValueFunctionTest {

    @Test
    fun `when not enough points to get a helping and top helping exists then return negative helping value`() {
        assertEquals(
            -1,
            ValueFunction.WormsFromAvailableHelpings.getValue(
                points = 19,
                availableHelpings = HelpingCollection.fromPoints(24),
                topHelping = Helping.fromPoints(23),
                opponentTopHelpings = HelpingCollection.empty()
            )
        )
    }

    @Test
    fun `when all helpings are available then WormsFromAvailableHelpings returns correct values`() {
        assertAll(
            (0..40).map { points ->
                Executable {
                    assertEquals(
                        if (points <= 36) Helping.fromPoints(points)?.getWorms() ?: 0 else 4,
                        ValueFunction.WormsFromAvailableHelpings.getValue(
                            points = points,
                            availableHelpings = HelpingCollection.all(),
                            topHelping = null,
                            opponentTopHelpings = HelpingCollection.empty()
                        ),
                        "Value for $points points"
                    )
                }
            }
        )
    }
}