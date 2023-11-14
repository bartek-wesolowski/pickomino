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

    override fun chooseSide(
        gameState: GameState,
        roll: Roll,
        usedSides: EnumSet<Side>,
        pointsSoFar: Int
    ): Side? {
        if (Side.WORM in roll && Side.WORM !in usedSides) return Side.WORM
        val notUsedSides = roll.sides.filter { it !in usedSides }.toList()
        return if (notUsedSides.isNotEmpty()) {
            notUsedSides[Random.nextInt() % notUsedSides.size]
        } else {
            null
        }
    }
}