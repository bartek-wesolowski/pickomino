import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.AbstractMap.SimpleEntry

class ArrayResultDistributionTest {

    @Test
    fun `contains correct values`() {
        val resultDistribution = ArrayResultDistribution(
            WormsFromAvailableHelpings,
            -1 to 0.2,
            1 to 0.45,
            2 to 0.3,
            3 to 0.05,
        )
        assertEquals(0.0, resultDistribution[-2])
        assertEquals(0.2, resultDistribution[-1])
        assertEquals(0.45, resultDistribution[1])
        assertEquals(0.3, resultDistribution[2])
        assertEquals(0.05, resultDistribution[3])
        assertEquals(0.0, resultDistribution[4])
    }

    @Test
    fun `expected value is correct`() {
        val resultDistribution = ArrayResultDistribution(
            WormsFromAvailableHelpings,
            -1 to 0.2,
            1 to 0.45,
            2 to 0.3,
            3 to 0.05,
        )
        assertEquals(1.0, resultDistribution.getExpectedValue())
    }

    @Test
    fun `iterator works`() {
        val resultDistribution = ArrayResultDistribution(
            WormsFromAvailableHelpings,
            -1 to 0.2,
            1 to 0.45,
            2 to 0.3,
            3 to 0.05
        )
        assertEquals(
            listOf(
                SimpleEntry(-1, 0.2),
                SimpleEntry(1, 0.45),
                SimpleEntry(2, 0.3),
                SimpleEntry(3, 0.05),
            ),
            resultDistribution.toList()
        )
    }
}