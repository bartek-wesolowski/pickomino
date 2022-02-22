import java.util.EnumSet
import kotlin.math.max

class OptimalStrategy(memo: MutableMap<Pickomino.Key, ResultDistribution> = mutableMapOf()) : Strategy {

    private val pickomino = Pickomino(memo)

    override fun shouldContinue(
        dyeCount: Int,
        usedSides: EnumSet<Side>,
        pointsSoFar: Int,
        availableHelpings: Helpings,
        opponentTopHelpings: Helpings
    ): Boolean {
        val wormsIfContinued = pickomino.getResultDistribution(
            dyeCount,
            ValueFunction.WormsFromAvailableHelpings(availableHelpings, opponentTopHelpings),
            usedSides,
            pointsSoFar
        ).getExpectedValue()
        val wormsIfStopped = max(availableHelpings.getWorms(pointsSoFar), opponentTopHelpings.getWormsExact(pointsSoFar))
        return wormsIfContinued > wormsIfStopped
    }

    override fun chooseSymbol(
        roll: List<Side>,
        usedSides: EnumSet<Side>,
        pointsSoFar: Int,
        availableHelpings: Helpings,
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
                valueFunction = ValueFunction.WormsFromAvailableHelpings(availableHelpings, opponentTopHelpings),
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