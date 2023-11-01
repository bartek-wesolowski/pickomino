import java.util.*
import kotlin.random.Random

data object RandomStrategy : Strategy {
    override fun shouldContinue(
        gameState: GameState,
        dyeCount: Int,
        usedSides: EnumSet<Side>,
        pointsSoFar: Int
    ): Boolean {
        return Side.WORM !in usedSides || (pointsSoFar < gameState.availableHelpings.getSmallest().points && pointsSoFar !in gameState.opponentTopHelpings)
    }

    override fun chooseSymbol(
        gameState: GameState,
        roll: List<Side>,
        usedSides: EnumSet<Side>,
        pointsSoFar: Int
    ): Side? {
        if (Side.WORM in roll && Side.WORM !in usedSides) return Side.WORM
        val distinctNotUsedSides = roll.filter { it !in usedSides }.distinct()
        return if (distinctNotUsedSides.isNotEmpty()) {
            distinctNotUsedSides[Random.nextInt() % distinctNotUsedSides.size]
        } else {
            null
        }
    }
}