import java.util.EnumSet
import kotlin.math.max

data object OptimalStrategy : Strategy {
    private val pickomino = Pickomino(WormsFromAvailableHelpings)

    override fun shouldContinue(
        gameState: GameState,
        dyeCount: Int,
        usedSides: EnumSet<Side>,
        pointsSoFar: Int,
    ): Boolean {
        val wormsIfContinued = pickomino.getResultDistribution(
            gameState,
            dyeCount,
            usedSides,
            pointsSoFar,
        ).getExpectedValue()
        return if (Side.WORM in usedSides) {
            var wormsIfStopped = max(
                gameState.availableHelpings.getExactOrSmaller(pointsSoFar)?.getWorms() ?: 0,
                gameState.opponentTopHelpings.getOrNull(pointsSoFar)?.getWorms() ?: 0
            )
            if (wormsIfStopped == 0 && gameState.topHelping != null) {
                wormsIfStopped = -gameState.topHelping.getWorms()
            }
            wormsIfContinued > wormsIfStopped
        } else {
            true
        }
    }

    override fun chooseSide(
        gameState: GameState,
        roll: Roll,
        usedSides: EnumSet<Side>,
        pointsSoFar: Int,
    ): Side? {
        var bestSide: Side? = null
        var bestValue = Double.NEGATIVE_INFINITY
        for (side in roll.sides) {
            if (side in usedSides) continue
            val sideCount = roll[side]
            val expectedValue = pickomino.getResultDistribution(
                gameState = gameState,
                dyeCount = roll.dyeCount - sideCount,
                usedSides = usedSides.withUsed(side),
                pointsSoFar = pointsSoFar + side.value * sideCount,
            ).getExpectedValue()
            if (expectedValue > bestValue) {
                bestSide = side
                bestValue = expectedValue
            }
        }
        return bestSide
    }
}