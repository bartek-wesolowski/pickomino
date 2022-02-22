import java.util.EnumSet

class Pickomino(private val memo: MutableMap<Key, ResultDistribution> = mutableMapOf()) {

    fun getResultDistribution(
        dyeCount: Int,
        valueFunction: ValueFunction,
        usedSides: EnumSet<Side> = EnumSet.noneOf(Side::class.java),
        pointsSoFar: Int = 0
    ): ResultDistribution {
        require(dyeCount >= 0) { "dyeCount cannot be negative" }
        val key = Key(dyeCount, valueFunction, usedSides, pointsSoFar)
        if (memo.containsKey(key)) {
            return memo.getValue(key)
        }
        val valueSoFar = valueFunction.getValue(pointsSoFar)
        if (dyeCount == 0) {
            val result = if (Side.WORM in usedSides) {
                ResultDistribution.successful(valueFunction.maxValue, valueSoFar)
            } else {
                ResultDistribution.failed(valueFunction.maxValue)
            }
            memo[key] = result
            return result
        }
        val resultDistribution = ResultDistribution(valueFunction.maxValue)
        val combinationProbability = sixth[dyeCount]
        for (combination in combinations(dyeCount)) {
            val combinationResultDistribution =
                getResultDistributionForCombination(combination, valueFunction, usedSides, pointsSoFar)
            resultDistribution.merge(combinationResultDistribution, combinationProbability)
        }
        resultDistribution.setFailedIfEmpty()
        val result = if (Side.WORM in usedSides) {
            val expectedValue = resultDistribution.getExpectedValue()
            val successProbability = resultDistribution.getSuccessProbability()
            if (valueSoFar * successProbability + expectedValue > valueSoFar) {
                resultDistribution
            } else {
                ResultDistribution.successful(valueFunction.maxValue, valueSoFar)
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
        pointsSoFar: Int
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
                    pointsSoFar = pointsSoFar + symbolsValue
                )
            }
            val canTakeDecision = Side.WORM in usedSides || symbol == Side.WORM
            val symbolResultDistributionAfterDecision =
                if (!canTakeDecision || symbolResultDistribution.getExpectedValue() > valueFunction.getValue(pointsSoFar + symbolsValue)) {
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
        pointsSoFar: Int
    ): Map<Side, ResultDistribution> {
        val advice = mutableMapOf<Side, ResultDistribution>()
        for (side in roll) {
            if (side in usedSides) continue
            if (side in advice) continue
            val count = roll.count { it == side }
            val resultDistribution = getResultDistribution(
                dyeCount = roll.size - count,
                valueFunction = valueFunction,
                usedSides = usedSides.withUsed(side),
                pointsSoFar = pointsSoFar + side.value * count
            )
            advice[side] = resultDistribution
        }
        return advice
    }

    data class Key(
        val dyeCount: Int,
        val valueFunction: ValueFunction,
        val usedSides: EnumSet<Side>,
        val pointsSoFar: Int
    )
}
