import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class ValueFunctionTest {

    @Test
    fun test() {
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
}