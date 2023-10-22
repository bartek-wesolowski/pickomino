import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CombinationsTest {
    @Test
    fun oneDieCombinations() {
        assertEquals(
            listOf(
                listOf(Side.ONE),
                listOf(Side.TWO),
                listOf(Side.THREE),
                listOf(Side.FOUR),
                listOf(Side.FIVE),
                listOf(Side.WORM),
            ),
            combinations(1).map { it.toList() }.toList(),
        )
    }

    @Test
    fun twoDiceCombinations() {
        assertEquals(
            listOf(
                listOf(Side.ONE, Side.ONE),
                listOf(Side.ONE, Side.TWO),
                listOf(Side.ONE, Side.THREE),
                listOf(Side.ONE, Side.FOUR),
                listOf(Side.ONE, Side.FIVE),
                listOf(Side.ONE, Side.WORM),
                listOf(Side.TWO, Side.ONE),
                listOf(Side.TWO, Side.TWO),
                listOf(Side.TWO, Side.THREE),
                listOf(Side.TWO, Side.FOUR),
                listOf(Side.TWO, Side.FIVE),
                listOf(Side.TWO, Side.WORM),
                listOf(Side.THREE, Side.ONE),
                listOf(Side.THREE, Side.TWO),
                listOf(Side.THREE, Side.THREE),
                listOf(Side.THREE, Side.FOUR),
                listOf(Side.THREE, Side.FIVE),
                listOf(Side.THREE, Side.WORM),
                listOf(Side.FOUR, Side.ONE),
                listOf(Side.FOUR, Side.TWO),
                listOf(Side.FOUR, Side.THREE),
                listOf(Side.FOUR, Side.FOUR),
                listOf(Side.FOUR, Side.FIVE),
                listOf(Side.FOUR, Side.WORM),
                listOf(Side.FIVE, Side.ONE),
                listOf(Side.FIVE, Side.TWO),
                listOf(Side.FIVE, Side.THREE),
                listOf(Side.FIVE, Side.FOUR),
                listOf(Side.FIVE, Side.FIVE),
                listOf(Side.FIVE, Side.WORM),
                listOf(Side.WORM, Side.ONE),
                listOf(Side.WORM, Side.TWO),
                listOf(Side.WORM, Side.THREE),
                listOf(Side.WORM, Side.FOUR),
                listOf(Side.WORM, Side.FIVE),
                listOf(Side.WORM, Side.WORM),
            ),
            combinations(2).map { it.toList() }.toList(),
        )
    }
}