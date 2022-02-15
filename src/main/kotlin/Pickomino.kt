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
        memo: MutableMap<Key, ValueWithSuccessProbability> = mutableMapOf()
    ): Map<Side, ValueWithSuccessProbability> {
        val advice = mutableMapOf<Side, ValueWithSuccessProbability>()
        for (side in roll) {
            if (side in advice) continue
            val count = roll.count { it == side }
            val (expectedValue, wormProbability) = getExpectedValue(
                dyeCount = roll.size - count,
                valueFunction = valueFunction,
                usedSides = usedSides.withUsed(side),
                memo = memo
            )
            val value = side.value * count * wormProbability + expectedValue
            advice[side] = ValueWithSuccessProbability(value, wormProbability)
        }
        return advice
    }

    fun getExpectedValue(
        dyeCount: Int,
        valueFunction: ValueFunction,
        usedSides: EnumSet<Side>,
        pointsSoFar: Int = 0,
        memo: MutableMap<Key, ValueWithSuccessProbability> = mutableMapOf()
    ): ValueWithSuccessProbability {
        val key = Key(dyeCount, valueFunction, usedSides, pointsSoFar)
        if (memo.containsKey(key)) {
            return memo.getValue(key)
        }
        val wormSoFar = Side.WORM in usedSides
        if (dyeCount == 0) return ValueWithSuccessProbability(0.0, if (wormSoFar) 1.0 else 0.0)
        val combinationProbability = sixth[dyeCount]
        var expectedValue = 0.0
        var successProbability = 0.0
        for (combination in combinations(dyeCount)) {
            val (combinationBestValue, combinationBestSuccessProbability) =
                getExpectedValueForBestMove(combination, valueFunction, usedSides, dyeCount, pointsSoFar, memo)
            expectedValue += combinationProbability * combinationBestValue
            successProbability += combinationProbability * combinationBestSuccessProbability
        }
        val result = if (wormSoFar) {
            if (pointsSoFar * successProbability + expectedValue > pointsSoFar) {
                ValueWithSuccessProbability(expectedValue, successProbability)
            } else {
                ValueWithSuccessProbability(0.0, 1.0)
            }
        } else {
            ValueWithSuccessProbability(expectedValue, successProbability)
        }
        memo[key] = result
        return result
    }

    private fun getExpectedValueForBestMove(
        combination: List<Side>,
        valueFunction: ValueFunction,
        usedSides: EnumSet<Side>,
        dyeCount: Int,
        pointsSoFar: Int,
        memo: MutableMap<Key, ValueWithSuccessProbability>
    ): Pair<Double, Double> {
        val symbols = combination.toSet()
        var combinationBestValue = 0.0
        var combinationBestSuccessProbability = 0.0
        for (symbol in symbols) {
            if (symbol in usedSides) continue
            val symbolCount = combination.count { it == symbol }
            val (symbolExpectedValue, symbolSuccessProbability) = getExpectedValue(
                dyeCount - symbolCount,
                valueFunction,
                usedSides.withUsed(symbol),
                pointsSoFar + symbolCount * symbol.value,
                memo
            )
            val symbolValue = symbolExpectedValue + symbolSuccessProbability * symbolCount * symbol.value
            val symbolTotalValue = symbolSuccessProbability * pointsSoFar + symbolValue
            val bestTotalValue = combinationBestSuccessProbability * pointsSoFar + combinationBestValue
            if (symbolTotalValue > bestTotalValue) {
                combinationBestValue = symbolValue
                combinationBestSuccessProbability = symbolSuccessProbability
            }
        }
        return combinationBestValue to combinationBestSuccessProbability
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
