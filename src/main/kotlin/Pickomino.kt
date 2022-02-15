import java.util.EnumSet

class Pickomino {

    fun getResultDistribution(
        dyeCount: Int,
        valueFunction: ValueFunction,
        usedSides: EnumSet<Side> = EnumSet.noneOf(Side::class.java),
        pointsSoFar: Int = 0,
        memo: MutableMap<Key, ResultDistribution> = mutableMapOf()
    ): ResultDistribution {
        require(dyeCount > 0) { "dyeCount has to be greater than 0" }
        val key = Key(dyeCount, valueFunction, usedSides, pointsSoFar)
        if (memo.containsKey(key)) {
            return memo.getValue(key)
        }
        if (dyeCount == 1) {
            return ResultDistribution.createForOneDye(valueFunction, usedSides, pointsSoFar)
        }
        val resultDistribution = ResultDistribution(valueFunction.maxValue)
        val combinationProbability = sixth[dyeCount]
        for (combination in combinations(dyeCount)) {
            val combinationResultDistribution =
                getResultDistributionForCombination(combination, valueFunction, usedSides, pointsSoFar, memo)
            resultDistribution.merge(combinationResultDistribution, combinationProbability)
        }
        resultDistribution.setFailedIfEmpty()
        val result = if (Side.WORM in usedSides) {
            val expectedValue = resultDistribution.getExpectedValue()
            val successProbability = resultDistribution.getSuccessProbability()
            if (pointsSoFar * successProbability + expectedValue > pointsSoFar) {
                resultDistribution
            } else {
                ResultDistribution.successful(valueFunction.maxValue, valueFunction.getValue(pointsSoFar))
            }
        } else {
            resultDistribution
        }
        memo[key] = result
        return result
    }

    internal fun getResultDistributionForCombination(
        combination: List<Side>,
        valueFunction: ValueFunction,
        usedSides: EnumSet<Side>,
        pointsSoFar: Int,
        memo: MutableMap<Key, ResultDistribution>
    ): ResultDistribution {
        val symbols = EnumSet.copyOf(combination)
        var combinationBestValue = 0.0
        var combinationBestResultDistribution = ResultDistribution(valueFunction.maxValue)
        for (symbol in symbols) {
            if (symbol in usedSides) continue
            val symbolCount = combination.count { it == symbol }
            val symbolsValue = symbolCount * symbol.value
            val symbolResultDistribution = if (symbolCount == combination.size) {
                if (Side.WORM in usedSides || symbol == Side.WORM) {
                    ResultDistribution.successful(valueFunction.maxValue, valueFunction.getValue(pointsSoFar + symbolsValue))
                } else {
                    return ResultDistribution.failed(valueFunction.maxValue)
                }
            } else {
                getResultDistribution(
                    dyeCount = combination.size - symbolCount,
                    valueFunction = valueFunction,
                    usedSides = usedSides.withUsed(symbol),
                    pointsSoFar = pointsSoFar + symbolsValue,
                    memo = memo
                )
            }
            val canTakeDecision = Side.WORM in usedSides || symbol == Side.WORM
            val symbolResultDistributionAfterDecision =
                if (!canTakeDecision || symbolResultDistribution.getExpectedValue() > (pointsSoFar + symbolsValue)) {
                    symbolResultDistribution
                } else {
                    ResultDistribution.successful(valueFunction.maxValue, valueFunction.getValue(pointsSoFar + symbolsValue))
                }
            val symbolExpectedValueAfterDecision = symbolResultDistributionAfterDecision.getExpectedValue()
            if (symbolExpectedValueAfterDecision > combinationBestValue) {
                combinationBestValue = symbolExpectedValueAfterDecision
                combinationBestResultDistribution = symbolResultDistributionAfterDecision
            }
        }
        combinationBestResultDistribution.setFailedIfEmpty()
        return combinationBestResultDistribution
    }

    fun getAdvice(
        roll: List<Side>,
        valueFunction: ValueFunction,
        usedSides: EnumSet<Side> = EnumSet.noneOf(Side::class.java),
        memo: MutableMap<Key, ResultDistribution> = mutableMapOf()
    ): Map<Side, ResultDistribution> {
        val advice = mutableMapOf<Side, ResultDistribution>()
        for (side in roll) {
            if (side in advice) continue
            val count = roll.count { it == side }
            val resultDistribution = getResultDistribution(
                dyeCount = roll.size - count,
                valueFunction = valueFunction,
                usedSides = usedSides.withUsed(side),
                pointsSoFar = side.value * count,
                memo = memo
            )
            advice[side] = resultDistribution
        }
        return advice
    }

    private fun EnumSet<Side>.withUsed(side: Side): EnumSet<Side> {
        return EnumSet.copyOf(this).apply {
            add(side)
        }
    }

    data class Key(
        val dyeCount: Int,
        val valueFunction: ValueFunction,
        val usedSides: EnumSet<Side>,
        val pointsSoFar: Int
    )
}
