import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class HelpingCollectionTest {

    @Test
    fun testAddingAndRemovingHelpings() {
        val helpings = HelpingCollection.of(21, 25, 29)
        assertEquals(29, helpings.getBiggest())
        helpings.add(33)
        assertEquals(33, helpings.getBiggest())
        helpings.remove(33)
        assertEquals(29, helpings.getBiggest())
    }
}