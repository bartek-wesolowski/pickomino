import hm.binkley.math.fixed.FixedBigRational
import hm.binkley.math.fixed.over
import hm.binkley.math.fixed.FixedBigRational.Companion.ZERO
import hm.binkley.math.fixed.FixedBigRational.Companion.ONE
import hm.binkley.math.times

class Worms {
    private val expectedValueMemo: MutableMap<Key, ValueWithSuccessProbability> = mutableMapOf()
    private val resultProbabilityMemo: MutableMap<Key, Map<Int, FixedBigRational>> = mutableMapOf()

    fun getResultDistribution(
        dyeCount: Int,
        usedSides: UsedSides = UsedSides(),
        valueSoFar: Int = 0
    ): Map<Int, FixedBigRational> {
        return getResultDistribution(dyeCount, usedSides, valueSoFar, resultProbabilityMemo)
    }

    private fun getResultDistribution(
        dyeCount: Int,
        usedSides: UsedSides,
        valueSoFar: Int,
        memo: MutableMap<Key, Map<Int, FixedBigRational>>
    ): Map<Int, FixedBigRational> {
        require(dyeCount > 0) { "dyeCount has to be greater than zero" }
        val key = Key(dyeCount, usedSides, valueSoFar)
        if (memo.containsKey(key)) {
            return memo.getValue(key)
        }
        if (dyeCount == 1) {
            return getResultDistributionForOneDye(usedSides, valueSoFar)
        }
        var expectedValue = ZERO
        var successProbability = ZERO
        val resultDistribution = mutableMapOf<Int, FixedBigRational>()
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
            val valueSoFarRational = valueSoFar over 1
            if (valueSoFarRational * successProbability + expectedValue > valueSoFarRational) {
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
        usedSides: UsedSides,
        valueSoFar: Int
    ): Map<Int, FixedBigRational> {
        return if (usedSides.isUsed(Side.WORM)) {
            buildMap {
                put(0, oneSixth * usedSides.size())
                if (Side.ONE !in usedSides) put(valueSoFar + 1, oneSixth)
                if (Side.TWO !in usedSides) put(valueSoFar + 2, oneSixth)
                if (Side.THREE !in usedSides) put(valueSoFar + 3, oneSixth)
                if (Side.FOUR !in usedSides) put(valueSoFar + 4, oneSixth)
                if (Side.FIVE !in usedSides) put(valueSoFar + 5, oneSixth)
            }
        } else {
            mapOf(
                0 to (5 over 6),
                valueSoFar + Side.WORM.value to oneSixth
            )
        }
    }

    internal fun getResultDistributionForCombination(
        combination: List<Side>,
        usedSides: UsedSides,
        valueSoFar: Int,
        memo: MutableMap<Key, Map<Int, FixedBigRational>>
    ): Map<Int, FixedBigRational> {
        val symbols = combination.toSet()
        var combinationBestValue = ZERO
        var combinationBestResultDistribution = emptyMap<Int, FixedBigRational>()
        for (symbol in symbols) {
            if (usedSides.isUsed(symbol)) continue
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
                if (!canTakeDecision || symbolExpectedValue > (valueSoFar + symbolsValue over 1)) {
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

    private fun Map<Int, FixedBigRational>.expectedValue(): FixedBigRational {
        return entries.fold(ZERO) { acc, entry ->
            val (value, probability) = entry
            acc + value * probability
        }
    }

    private fun Map<Int, FixedBigRational>.successProbability(): FixedBigRational {
        return entries.fold(ZERO) { acc, entry ->
            val (value, probability) = entry
            if (value != 0) {
                acc + probability
            } else {
                acc
            }
        }
    }

    private fun MutableMap<Int, FixedBigRational>.combine(
        other: Map<Int, FixedBigRational>,
        combinationProbability: FixedBigRational
    ) {
        for ((value, probability) in other) {
            if (value in this) {
                put(value, getValue(value) + probability * combinationProbability)
            } else {
                put(value, probability * combinationProbability)
            }
        }
    }

    private fun Map<Int, FixedBigRational>.emptyToFailed(): Map<Int, FixedBigRational> {
        return if (isEmpty()) {
            failed
        } else {
            return this
        }
    }

    fun getAdvice(roll: List<Side>, usedSides: UsedSides = UsedSides()): Map<Side, ValueWithSuccessProbability> {
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
        usedSides: UsedSides = UsedSides(),
        valueSoFar: Int = 0
    ): ValueWithSuccessProbability {
        return getExpectedValue(dyeCount, usedSides, valueSoFar, expectedValueMemo)
    }

    private fun getExpectedValue(
        dyeCount: Int,
        usedSides: UsedSides,
        valueSoFar: Int,
        memo: MutableMap<Key, ValueWithSuccessProbability>
    ): ValueWithSuccessProbability {
        val key = Key(dyeCount, usedSides, valueSoFar)
        if (memo.containsKey(key)) {
            return memo.getValue(key)
        }
        val wormSoFar = usedSides.isUsed(Side.WORM)
        if (dyeCount == 0) return ValueWithSuccessProbability(ZERO, if (wormSoFar) ONE else ZERO)
        val combinationProbability = sixth[dyeCount]
        var expectedValue = ZERO
        var successProbability = ZERO
        for (combination in combinations(dyeCount)) {
            val (combinationBestValue, combinationBestSuccessProbability) =
                getExpectedValueForBestMove(combination, usedSides, dyeCount, valueSoFar, memo)
            expectedValue += combinationProbability * combinationBestValue
            successProbability += combinationProbability * combinationBestSuccessProbability
        }
        val result = if (wormSoFar) {
            val valueSoFarRational = valueSoFar over 1
            if (valueSoFarRational * successProbability + expectedValue > valueSoFarRational) {
                ValueWithSuccessProbability(expectedValue, successProbability)
            } else {
                ValueWithSuccessProbability(ZERO, ONE)
            }
        } else {
            ValueWithSuccessProbability(expectedValue, successProbability)
        }
        memo[key] = result
        return result
    }

    private fun getExpectedValueForBestMove(
        combination: List<Side>,
        usedSides: UsedSides,
        dyeCount: Int,
        valueSoFar: Int,
        memo: MutableMap<Key, ValueWithSuccessProbability>
    ): Pair<FixedBigRational, FixedBigRational> {
        val symbols = combination.toSet()
        var combinationBestValue = ZERO
        var combinationBestSuccessProbability = ZERO
        for (symbol in symbols) {
            if (usedSides.isUsed(symbol)) continue
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

    internal data class Key(
        val dyeCount: Int,
        val usedSides: UsedSides,
        val valueSoFar: Int
    )
}

internal val failed = mapOf(0 to ONE)

internal fun successful(value: Int) = mapOf(
    0 to ZERO,
    value to ONE
)
