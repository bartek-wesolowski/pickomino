import java.util.EnumSet
import kotlin.math.max

data object OptimalStrategy : Strategy {
    private val pickomino = Pickomino()

    override fun shouldContinue(
        dyeCount: Int,
        usedSides: EnumSet<Side>,
        pointsSoFar: Int,
        availableHelpings: HelpingCollection,
        topHelping: Helping?,
        opponentTopHelpings: HelpingCollection
    ): Boolean {
        val wormsIfContinued = pickomino.getResultDistribution(
            dyeCount,
            usedSides,
            pointsSoFar,
            availableHelpings,
            topHelping,
            opponentTopHelpings,
            ValueFunction.WormsFromAvailableHelpings
        ).getExpectedValue()
        return if (Side.WORM in usedSides) {
            var wormsIfStopped = max(
                availableHelpings.getExactOrSmaller(pointsSoFar)?.getWorms() ?: 0,
                opponentTopHelpings.getOrNull(pointsSoFar)?.getWorms() ?: 0
            )
            if (wormsIfStopped == 0 && topHelping != null) {
                wormsIfStopped = -topHelping.getWorms()
            }
            wormsIfContinued > wormsIfStopped
        } else {
            true
        }
    }

    override fun chooseSymbol(
        roll: List<Side>,
        usedSides: EnumSet<Side>,
        pointsSoFar: Int,
        availableHelpings: HelpingCollection,
        topHelping: Helping?,
        opponentTopHelpings: HelpingCollection
    ): Side? {
        val symbols = EnumSet.copyOf(roll)
        var bestSymbol: Side? = null
        var bestValue = Double.NEGATIVE_INFINITY
        for (symbol in symbols) {
            if (symbol in usedSides) continue
            val symbolCount = roll.count { it == symbol }
            val expectedValue = pickomino.getResultDistribution(
                dyeCount = roll.size - symbolCount,
                valueFunction = ValueFunction.WormsFromAvailableHelpings,
                usedSides = usedSides.withUsed(symbol),
                pointsSoFar = pointsSoFar + symbol.value * symbolCount,
                availableHelpings = availableHelpings,
                topHelping = topHelping,
                opponentTopHelpings = opponentTopHelpings
            ).getExpectedValue()
            if (expectedValue > bestValue) {
                bestSymbol = symbol
                bestValue = expectedValue
            }
        }
        return bestSymbol
    }
}