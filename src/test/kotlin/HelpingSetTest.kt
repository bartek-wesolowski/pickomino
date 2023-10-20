import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class HelpingSetTest {

    @Test
    fun testAddingAndRemovingHelpings() {
        val helpings = HelpingSet.of(21, 25, 29, 33)
        assertEquals(33, helpings.getBiggest())
        helpings.add(37)
        assertEquals(37, helpings.getBiggest())
        helpings.remove(37)
        assertEquals(33, helpings.getBiggest())
    }
}