import java.util.EnumSet
import kotlin.math.max

class OptimalStrategy : Strategy {
    private val pickomino = Pickomino()

    override fun shouldContinue(
        dyeCount: Int,
        usedSides: EnumSet<Side>,
        pointsSoFar: Int,
        availableHelpings: Helpings,
        topHelping: Int?,
        opponentTopHelpings: Helpings
    ): Boolean {
        val wormsIfContinued = pickomino.getResultDistribution(
            dyeCount,
            ValueFunction.WormsFromAvailableHelpings(availableHelpings, topHelping, opponentTopHelpings),
            usedSides,
            pointsSoFar
        ).getExpectedValue()
        return if (Side.WORM in usedSides) {
            val wormsIfStopped = max(availableHelpings.getWorms(pointsSoFar), opponentTopHelpings.getWormsExact(pointsSoFar))
            wormsIfContinued > wormsIfStopped
        } else {
            true
        }
    }

    override fun chooseSymbol(
        roll: List<Side>,
        usedSides: EnumSet<Side>,
        pointsSoFar: Int,
        availableHelpings: Helpings,
        topHelping: Int?,
        opponentTopHelpings: Helpings
    ): Side? {
        val symbols = EnumSet.copyOf(roll)
        var bestSymbol: Side? = null
        var bestValue = 0.0
        for (symbol in symbols) {
            if (symbol in usedSides) continue
            val symbolCount = roll.count { it == symbol }
            val expectedValue = pickomino.getResultDistribution(
                dyeCount = roll.size - symbolCount,
                valueFunction = ValueFunction.WormsFromAvailableHelpings(availableHelpings, topHelping, opponentTopHelpings),
                usedSides = usedSides.withUsed(symbol),
                pointsSoFar = pointsSoFar + symbol.value * symbolCount
            ).getExpectedValue()
            if (expectedValue > bestValue) {
                bestSymbol = symbol
                bestValue = expectedValue
            }
        }
        return bestSymbol
    }
}