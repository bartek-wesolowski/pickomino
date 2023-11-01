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

    override fun chooseSymbol(
        gameState: GameState,
        roll: List<Side>,
        usedSides: EnumSet<Side>,
        pointsSoFar: Int,
    ): Side? {
        val symbols = EnumSet.copyOf(roll)
        var bestSymbol: Side? = null
        var bestValue = Double.NEGATIVE_INFINITY
        for (symbol in symbols) {
            if (symbol in usedSides) continue
            val symbolCount = roll.count { it == symbol }
            val expectedValue = pickomino.getResultDistribution(
                gameState = gameState,
                dyeCount = roll.size - symbolCount,
                usedSides = usedSides.withUsed(symbol),
                pointsSoFar = pointsSoFar + symbol.value * symbolCount,
            ).getExpectedValue()
            if (expectedValue > bestValue) {
                bestSymbol = symbol
                bestValue = expectedValue
            }
        }
        return bestSymbol
    }
}