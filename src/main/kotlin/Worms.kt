import java.util.*

class Worms {
    private val expectedValueMemo: MutableMap<Key, ValueWithSuccessProbability> = mutableMapOf()
    private val resultProbabilityMemo: MutableMap<Key, Map<Int, Double>> = mutableMapOf()

    fun getResultDistribution(
        dyeCount: Int,
        usedSides: EnumSet<Side> = EnumSet.noneOf(Side::class.java),
        valueSoFar: Int = 0
    ): Map<Int, Double> {
        return getResultDistribution(dyeCount, usedSides, valueSoFar, resultProbabilityMemo)
    }

    private fun getResultDistribution(
        dyeCount: Int,
        usedSides: EnumSet<Side>,
        valueSoFar: Int,
        memo: MutableMap<Key, Map<Int, Double>>
    ): Map<Int, Double> {
        require(dyeCount > 0) { "dyeCount has to be greater than 0.0" }
        val key = Key(dyeCount, usedSides, valueSoFar)
        if (memo.containsKey(key)) {
            return memo.getValue(key)
        }
        if (dyeCount == 1) {
            return getResultDistributionForOneDye(usedSides, valueSoFar)
        }
        var expectedValue = 0.0
        var successProbability = 0.0
        val resultDistribution = mutableMapOf<Int, Double>()
        val combinationProbability = sixth[dyeCount]
        for (combination in combinations(dyeCount)) {
            val combinationResultDistribution =
                getResultDistributionForCombination(
                    combination,
                    usedSides,
                    valueSoFar,
                    memo
                )
            expectedValue += combinationResultDistribution.expectedValue() * combinationProbability
            successProbability += combinationResultDistribution.successProbability() * combinationProbability
            resultDistribution.combine(combinationResultDistribution, combinationProbability)
        }
        val result = if (Side.WORM in usedSides) {
            if (valueSoFar * successProbability + expectedValue > valueSoFar) {
                resultDistribution.emptyToFailed()
            } else {
                successful(valueSoFar)
            }
        } else {
            resultDistribution.emptyToFailed()
        }
        memo[key] = result
        return result
    }

    private fun getResultDistributionForOneDye(
        usedSides: EnumSet<Side>,
        valueSoFar: Int
    ): Map<Int, Double> {
        return if (Side.WORM in usedSides) {
            buildMap {
                put(0, oneSixth * usedSides.size)
                if (Side.ONE !in usedSides) put(valueSoFar + 1, oneSixth)
                if (Side.TWO !in usedSides) put(valueSoFar + 2, oneSixth)
                if (Side.THREE !in usedSides) put(valueSoFar + 3, oneSixth)
                if (Side.FOUR !in usedSides) put(valueSoFar + 4, oneSixth)
                if (Side.FIVE !in usedSides) put(valueSoFar + 5, oneSixth)
            }
        } else {
            mapOf(
                0 to 5.0 / 6,
                valueSoFar + Side.WORM.value to oneSixth
            )
        }
    }

    internal fun getResultDistributionForCombination(
        combination: List<Side>,
        usedSides: EnumSet<Side>,
        valueSoFar: Int,
        memo: MutableMap<Key, Map<Int, Double>>
    ): Map<Int, Double> {
        val symbols = combination.toSet()
        var combinationBestValue = 0.0
        var combinationBestResultDistribution = emptyMap<Int, Double>()
        for (symbol in symbols) {
            if (symbol in usedSides) continue
            val symbolCount = combination.count { it == symbol }
            val symbolsValue = symbolCount * symbol.value
            val symbolResultDistribution = if (symbolCount == combination.size) {
                if (Side.WORM in usedSides || symbol == Side.WORM) {
                    successful(valueSoFar + symbolsValue)
                } else {
                    return failed
                }
            } else {
                getResultDistribution(
                    combination.size - symbolCount,
                    usedSides.withUsed(symbol),
                    valueSoFar + symbolsValue,
                    memo
                )
            }
            val symbolExpectedValue = symbolResultDistribution.expectedValue()
            val canTakeDecision = Side.WORM in usedSides || symbol == Side.WORM
            val symbolResultDistributionAfterDecision =
                if (!canTakeDecision || symbolExpectedValue > (valueSoFar + symbolsValue)) {
                    symbolResultDistribution
                } else {
                    successful(valueSoFar + symbolsValue)
                }
            val symbolExpectedValueAfterDecision = symbolResultDistributionAfterDecision.expectedValue()
            if (symbolExpectedValueAfterDecision > combinationBestValue) {
                combinationBestValue = symbolExpectedValueAfterDecision
                combinationBestResultDistribution = symbolResultDistributionAfterDecision
            }
        }
        return combinationBestResultDistribution.emptyToFailed()
    }

    private fun Map<Int, Double>.expectedValue(): Double {
        return entries.fold(0.0) { acc, entry ->
            val (value, probability) = entry
            acc + value * probability
        }
    }

    private fun Map<Int, Double>.successProbability(): Double {
        return entries.fold(0.0) { acc, entry ->
            val (value, probability) = entry
            if (value != 0) {
                acc + probability
            } else {
                acc
            }
        }
    }

    private fun MutableMap<Int, Double>.combine(
        other: Map<Int, Double>,
        combinationProbability: Double
    ) {
        for ((value, probability) in other) {
            if (value in this) {
                put(value, getValue(value) + probability * combinationProbability)
            } else {
                put(value, probability * combinationProbability)
            }
        }
    }

    private fun Map<Int, Double>.emptyToFailed(): Map<Int, Double> {
        return if (isEmpty()) {
            failed
        } else {
            return this
        }
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

internal val failed = mapOf(0 to 1.0)

internal fun successful(value: Int) = mapOf(
    0 to 0.0,
    value to 1.0
)
