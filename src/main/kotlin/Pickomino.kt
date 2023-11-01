import java.util.EnumSet

class Pickomino<V : ValueFunction>(private val valueFunction: V) {

    fun getResultDistribution(
        gameState: GameState,
        dyeCount: Int,
        usedSides: EnumSet<Side> = EnumSet.noneOf(Side::class.java),
        pointsSoFar: Int = 0,
        memo: MutableMap<Key, ResultDistribution> = mutableMapOf()
    ): ResultDistribution {
        require(dyeCount >= 0) { "dyeCount cannot be negative" }
        val key = Key(dyeCount, usedSides, pointsSoFar)
        val cachedResultDistribution = memo[key]
        if (cachedResultDistribution != null) {
            return cachedResultDistribution
        }
        val valueSoFar = valueFunction.getValue(gameState, pointsSoFar)
        if (dyeCount == 0) {
            val result = if (Side.WORM in usedSides) {
                SingleResultDistribution(valueSoFar)
            } else {
                SingleResultDistribution(valueFunction.getValue(gameState, 0))
            }
            memo[key] = result
            return result
        }
        val resultDistribution = ArrayResultDistribution(valueFunction)
        val combinationProbability = sixth[dyeCount]
        for (combination in combinations(dyeCount)) {
            val combinationResultDistribution = getResultDistributionForCombination(
                gameState,
                combination,
                usedSides,
                pointsSoFar,
                memo
            )
            resultDistribution.merge(combinationResultDistribution, combinationProbability)
        }
        val result = if (Side.WORM in usedSides) {
            val expectedValue = resultDistribution.getExpectedValue()
            val successProbability = resultDistribution.getSuccessProbability()
            if (valueSoFar * successProbability + expectedValue > valueSoFar) {
                resultDistribution
            } else {
                SingleResultDistribution(valueSoFar)
            }
        } else {
            resultDistribution
        }
        memo[key] = result
        return result
    }

    internal fun getResultDistributionForCombination(
        gameState: GameState,
        combination: List<Side>,
        usedSides: EnumSet<Side>,
        pointsSoFar: Int,
        memo: MutableMap<Key, ResultDistribution>
    ): ResultDistribution {
        val symbols = EnumSet.copyOf(combination)
        var combinationBestValue = Double.NEGATIVE_INFINITY
        var combinationBestResultDistribution: ResultDistribution? = null
        for (symbol in symbols) {
            if (symbol in usedSides) continue
            val symbolCount = combination.count { it == symbol }
            val symbolsValue = symbolCount * symbol.value
            val symbolResultDistribution = if (symbolCount == combination.size) {
                if (Side.WORM in usedSides || symbol == Side.WORM) {
                    SingleResultDistribution(valueFunction.getValue(gameState, pointsSoFar + symbolsValue))
                } else {
                    SingleResultDistribution(valueFunction.getValue(gameState, 0))
                }
            } else {
                getResultDistribution(
                    gameState = gameState,
                    dyeCount = combination.size - symbolCount,
                    usedSides = usedSides.withUsed(symbol),
                    pointsSoFar = pointsSoFar + symbolsValue,
                    memo = memo
                )
            }
            val canTakeDecision = Side.WORM in usedSides || symbol == Side.WORM
            val symbolResultDistributionAfterDecision =
                if (!canTakeDecision || symbolResultDistribution.getExpectedValue() > valueFunction.getValue(
                        gameState, pointsSoFar + symbolsValue
                    )
                ) {
                    symbolResultDistribution
                } else {
                    SingleResultDistribution(valueFunction.getValue(gameState, pointsSoFar + symbolsValue))
                }
            val symbolExpectedValueAfterDecision = symbolResultDistributionAfterDecision.getExpectedValue()
            if (symbolExpectedValueAfterDecision > combinationBestValue) {
                combinationBestValue = symbolExpectedValueAfterDecision
                combinationBestResultDistribution = symbolResultDistributionAfterDecision
            }
        }
        return combinationBestResultDistribution ?: SingleResultDistribution(valueFunction.getValue(gameState, 0))
    }

    fun getAdvice(
        gameState: GameState,
        roll: List<Side>,
        usedSides: EnumSet<Side> = EnumSet.noneOf(Side::class.java),
        pointsSoFar: Int,
    ): Map<Side, ResultDistribution> {
        val advice = mutableMapOf<Side, ResultDistribution>()
        for (side in roll) {
            if (side in usedSides) continue
            if (side in advice) continue
            val count = roll.count { it == side }
            val resultDistribution = getResultDistribution(
                gameState = gameState,
                dyeCount = roll.size - count,
                usedSides = usedSides.withUsed(side),
                pointsSoFar = pointsSoFar + side.value * count,
            )
            advice[side] = resultDistribution
        }
        return advice
    }

    data class Key(
        val dyeCount: Int,
        val usedSides: EnumSet<Side>,
        val pointsSoFar: Int
    )
}
