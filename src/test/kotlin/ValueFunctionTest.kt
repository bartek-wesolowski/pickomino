import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class ValueFunctionTest {

    @Test
    fun test() {
        assertEquals(
            -1,
            ValueFunction.WormsFromAvailableHelpings.getValue(
                points = 19,
                availableHelpings = HelpingCollection(listOf(24)),
                topHelping = 23,
                opponentTopHelpings = HelpingCollection(listOf())
            )
        )
    }
}