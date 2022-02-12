import java.util.EnumSet

class Worms {
    private val expectedValueMemo: MutableMap<Key, ValueWithSuccessProbability> = mutableMapOf()
    private val resultDistributionMemo: MutableMap<Key, ResultDistribution> = mutableMapOf()

    fun getResultDistribution(
        dyeCount: Int,
        usedSides: EnumSet<Side> = EnumSet.noneOf(Side::class.java),
        valueSoFar: Int = 0
    ): ResultDistribution {
        return getResultDistribution(dyeCount, usedSides, valueSoFar, resultDistributionMemo)
    }

    private fun getResultDistribution(
        dyeCount: Int,
        usedSides: EnumSet<Side>,
        valueSoFar: Int,
        memo: MutableMap<Key, ResultDistribution>
    ): ResultDistribution {
        require(dyeCount > 0) { "dyeCount has to be greater than 0.0" }
        val key = Key(dyeCount, usedSides, valueSoFar)
        if (memo.containsKey(key)) {
            return memo.getValue(key)
        }
        if (dyeCount == 1) {
            return ResultDistribution.createForOneDye(usedSides, valueSoFar)
        }
        val resultDistribution = ResultDistribution()
        val combinationProbability = sixth[dyeCount]
        for (combination in combinations(dyeCount)) {
            val combinationResultDistribution =
                getResultDistributionForCombination(
                    combination,
                    usedSides,
                    valueSoFar,
                    memo
                )
            resultDistribution.merge(combinationResultDistribution, combinationProbability)
        }
        resultDistribution.setFailedIfEmpty()
        val result = if (Side.WORM in usedSides) {
            val expectedValue = resultDistribution.getExpectedValue()
            val successProbability = resultDistribution.getSuccessProbability()
            if (valueSoFar * successProbability + expectedValue > valueSoFar) {
                resultDistribution
            } else {
                ResultDistribution.successful(valueSoFar)
            }
        } else {
            resultDistribution
        }
        memo[key] = result
        return result
    }

    internal fun getResultDistributionForCombination(
        combination: List<Side>,
        usedSides: EnumSet<Side>,
        valueSoFar: Int,
        memo: MutableMap<Key, ResultDistribution>
    ): ResultDistribution {
        val symbols = EnumSet.copyOf(combination)
        var combinationBestValue = 0.0
        var combinationBestResultDistribution = ResultDistribution()
        for (symbol in symbols) {
            if (symbol in usedSides) continue
            val symbolCount = combination.count { it == symbol }
            val symbolsValue = symbolCount * symbol.value
            val symbolResultDistribution = if (symbolCount == combination.size) {
                if (Side.WORM in usedSides || symbol == Side.WORM) {
                    ResultDistribution.successful(valueSoFar + symbolsValue)
                } else {
                    return ResultDistribution.failed()
                }
            } else {
                getResultDistribution(
                    combination.size - symbolCount,
                    usedSides.withUsed(symbol),
                    valueSoFar + symbolsValue,
                    memo
                )
            }
            val canTakeDecision = Side.WORM in usedSides || symbol == Side.WORM
            val symbolResultDistributionAfterDecision =
                if (!canTakeDecision || symbolResultDistribution.getExpectedValue() > (valueSoFar + symbolsValue)) {
                    symbolResultDistribution
                } else {
                    ResultDistribution.successful(valueSoFar + symbolsValue)
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
        usedSides: EnumSet<Side> = EnumSet.noneOf(Side::class.java)
    ): Map<Side, ValueWithSuccessProbability> {
        val advice = mutableMapOf<Side, ValueWithSuccessProbability>()
        for (side in roll) {
            if (side in advice) continue
            val count = roll.count { it == side }
            val (expectedValue, wormProbability) = getExpectedValue(roll.size - count, usedSides.withUsed(side))
            val value = side.value * count * wormProbability + expectedValue
            advice[side] = ValueWithSuccessProbability(value, wormProbability)
        }
        return advice
    }

    fun getExpectedValue(
        dyeCount: Int,
        usedSides: EnumSet<Side> = EnumSet.noneOf(Side::class.java),
        valueSoFar: Int = 0
    ): ValueWithSuccessProbability {
        return getExpectedValue(dyeCount, usedSides, valueSoFar, expectedValueMemo)
    }

    private fun getExpectedValue(
        dyeCount: Int,
        usedSides: EnumSet<Side>,
        valueSoFar: Int,
        memo: MutableMap<Key, ValueWithSuccessProbability>
    ): ValueWithSuccessProbability {
        val key = Key(dyeCount, usedSides, valueSoFar)
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
                getExpectedValueForBestMove(combination, usedSides, dyeCount, valueSoFar, memo)
            expectedValue += combinationProbability * combinationBestValue
            successProbability += combinationProbability * combinationBestSuccessProbability
        }
        val result = if (wormSoFar) {
            if (valueSoFar * successProbability + expectedValue > valueSoFar) {
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
        usedSides: EnumSet<Side>,
        dyeCount: Int,
        valueSoFar: Int,
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
                usedSides.withUsed(symbol),
                valueSoFar + symbolCount * symbol.value,
                memo
            )
            val symbolValue = symbolExpectedValue + symbolSuccessProbability * symbolCount * symbol.value
            val symbolTotalValue = symbolSuccessProbability * valueSoFar + symbolValue
            val bestTotalValue = combinationBestSuccessProbability * valueSoFar + combinationBestValue
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

    internal data class Key(
        val dyeCount: Int,
        val usedSides: EnumSet<Side>,
        val valueSoFar: Int
    )
}
