import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class HelpingCollectionTest {

    @Test
    fun testAddingAndRemovingHelpings() {
        val helpings = HelpingCollection.fromPoints(21, 25, 29)
        val helping29 = Helping.fromPoints(29)
        assertEquals(helping29, helpings.getBiggest())
        val helping33 = Helping.fromPoints(33)!!
        helpings.add(helping33)
        assertEquals(helping33, helpings.getBiggest())
        helpings.remove(helping33)
        assertEquals(helping29, helpings.getBiggest())
    }

    @Test
    fun testGetExactOrSmaller() {
        val helpings = HelpingCollection.all()
        assertEquals(Helping.fromPoints(21), helpings.getExactOrSmaller(21))
    }
}