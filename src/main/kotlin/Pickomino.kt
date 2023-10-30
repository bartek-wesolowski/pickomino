import java.util.EnumSet

class Pickomino {

    fun <V: ValueFunction> getResultDistribution(
        dyeCount: Int,
        usedSides: EnumSet<Side> = EnumSet.noneOf(Side::class.java),
        pointsSoFar: Int = 0,
        availableHelpings: HelpingCollection,
        topHelping: Helping?,
        opponentTopHelpings: HelpingCollection,
        valueFunction: V,
        memo: MutableMap<Key, ResultDistribution<V>> = mutableMapOf()
    ): ResultDistribution<V> {
        require(dyeCount >= 0) { "dyeCount cannot be negative" }
        val key = Key(dyeCount, valueFunction, usedSides, pointsSoFar)
        if (memo.containsKey(key)) {
            return memo.getValue(key)
        }
        val valueSoFar = valueFunction.getValue(pointsSoFar, availableHelpings, topHelping, opponentTopHelpings)
        if (dyeCount == 0) {
            val result = if (Side.WORM in usedSides) {
                ResultDistribution.single(valueFunction, valueSoFar)
            } else {
                ResultDistribution.single(
                    valueFunction,
                    valueFunction.getValue(0, availableHelpings, topHelping, opponentTopHelpings)
                )
            }
            memo[key] = result
            return result
        }
        val resultDistribution = ResultDistribution(valueFunction)
        val combinationProbability = sixth[dyeCount]
        for (combination in combinations(dyeCount)) {
            val combinationResultDistribution = getResultDistributionForCombination(
                combination,
                usedSides,
                pointsSoFar,
                availableHelpings,
                topHelping,
                opponentTopHelpings,
                valueFunction,
                memo
            )
            resultDistribution.merge(combinationResultDistribution, combinationProbability)
        }
        resultDistribution.setFailedIfEmpty(valueFunction.getValue(0, availableHelpings, topHelping, opponentTopHelpings))
        val result = if (Side.WORM in usedSides) {
            val expectedValue = resultDistribution.getExpectedValue()
            val successProbability = resultDistribution.getSuccessProbability()
            if (valueSoFar * successProbability + expectedValue > valueSoFar) {
                resultDistribution
            } else {
                ResultDistribution.single(valueFunction, valueSoFar)
            }
        } else {
            resultDistribution
        }
        memo[key] = result
        return result
    }

    internal fun <V: ValueFunction> getResultDistributionForCombination(
        combination: List<Side>,
        usedSides: EnumSet<Side>,
        pointsSoFar: Int,
        availableHelpings: HelpingCollection,
        topHelping: Helping?,
        opponentTopHelpings: HelpingCollection,
        valueFunction: V,
        memo: MutableMap<Key, ResultDistribution<V>>
    ): ResultDistribution<V> {
        val symbols = EnumSet.copyOf(combination)
        var combinationBestValue = Double.NEGATIVE_INFINITY
        var combinationBestResultDistribution = ResultDistribution(valueFunction)
        for (symbol in symbols) {
            if (symbol in usedSides) continue
            val symbolCount = combination.count { it == symbol }
            val symbolsValue = symbolCount * symbol.value
            val symbolResultDistribution = if (symbolCount == combination.size) {
                if (Side.WORM in usedSides || symbol == Side.WORM) {
                    ResultDistribution.single(
                        valueFunction,
                        valueFunction.getValue(pointsSoFar + symbolsValue, availableHelpings, topHelping, opponentTopHelpings)
                    )
                } else {
                    return ResultDistribution.single(
                        valueFunction,
                        valueFunction.getValue(0, availableHelpings, topHelping, opponentTopHelpings)
                    )
                }
            } else {
                getResultDistribution(
                    dyeCount = combination.size - symbolCount,
                    valueFunction = valueFunction,
                    usedSides = usedSides.withUsed(symbol),
                    pointsSoFar = pointsSoFar + symbolsValue,
                    availableHelpings = availableHelpings,
                    topHelping = topHelping,
                    opponentTopHelpings = opponentTopHelpings,
                    memo = memo
                )
            }
            val canTakeDecision = Side.WORM in usedSides || symbol == Side.WORM
            val symbolResultDistributionAfterDecision =
                if (!canTakeDecision || symbolResultDistribution.getExpectedValue() > valueFunction.getValue(
                        pointsSoFar + symbolsValue,
                        availableHelpings,
                        topHelping,
                        opponentTopHelpings
                    )
                ) {
                    symbolResultDistribution
                } else {
                    ResultDistribution.single(
                        valueFunction,
                        valueFunction.getValue(pointsSoFar + symbolsValue, availableHelpings, topHelping, opponentTopHelpings)
                    )
                }
            val symbolExpectedValueAfterDecision = symbolResultDistributionAfterDecision.getExpectedValue()
            if (symbolExpectedValueAfterDecision > combinationBestValue) {
                combinationBestValue = symbolExpectedValueAfterDecision
                combinationBestResultDistribution = symbolResultDistributionAfterDecision
            }
        }
        combinationBestResultDistribution.setFailedIfEmpty(
            valueFunction.getValue(
                0,
                availableHelpings,
                topHelping,
                opponentTopHelpings
            )
        )
        return combinationBestResultDistribution
    }

    fun <V: ValueFunction> getAdvice(
        roll: List<Side>,
        usedSides: EnumSet<Side> = EnumSet.noneOf(Side::class.java),
        pointsSoFar: Int,
        availableHelpings: HelpingCollection,
        topHelping: Helping?,
        opponentTopHelpings: HelpingCollection,
        valueFunction: V
    ): Map<Side, ResultDistribution<V>> {
        val advice = mutableMapOf<Side, ResultDistribution<V>>()
        for (side in roll) {
            if (side in usedSides) continue
            if (side in advice) continue
            val count = roll.count { it == side }
            val resultDistribution = getResultDistribution(
                dyeCount = roll.size - count,
                valueFunction = valueFunction,
                usedSides = usedSides.withUsed(side),
                pointsSoFar = pointsSoFar + side.value * count,
                availableHelpings = availableHelpings,
                topHelping = topHelping,
                opponentTopHelpings = opponentTopHelpings,
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
