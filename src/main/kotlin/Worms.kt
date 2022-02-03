class Worms {
    private val expectedValueMemo: MutableMap<Key, ValueWithSuccessProbability> = mutableMapOf()
    private val resultProbabilityMemo: MutableMap<Key, Map<Int, ProbabilityWithWormProbability>> = mutableMapOf()

    fun getResultProbabilities(
        dyeCount: Int,
        usedSides: UsedSides = UsedSides(),
        valueSoFar: Int = 0
    ): Map<Int, ProbabilityWithWormProbability> {
        return getResultProbabilities(dyeCount, usedSides, valueSoFar, resultProbabilityMemo)
    }

    private fun getResultProbabilities(
        dyeCount: Int,
        usedSides: UsedSides,
        valueSoFar: Int,
        memo: MutableMap<Key, Map<Int, ProbabilityWithWormProbability>>
    ): Map<Int, ProbabilityWithWormProbability> {
        val key = Key(dyeCount, usedSides, valueSoFar)
        if (memo.containsKey(key)) {
            return memo.getValue(key)
        }
        val wormSoFar = usedSides.isUsed(Side.WORM)
        if (dyeCount == 0) return mapOf(0 to ProbabilityWithWormProbability(1f, if (wormSoFar) 1f else 0f))
//        if (dyeCount == 0) {
//            return if (wormSoFar) {
//                mapOf(0 to ProbabilityWithWormProbability(1f, 1f))
//            } else {
//                emptyMap()
//            }
//        }
        val combinationProbability = 1 / sixPow[dyeCount].toFloat()
        val resultProbabilities = mutableMapOf<Int, ProbabilityWithWormProbability>()
        for (combination in combinations(dyeCount)) {
            val symbols = combination.toSet()
            var combinationBestSymbol = symbols.first()
            var combinationBestValue = 0f
            var combinationBestResultProbabilities = mapOf<Int, ProbabilityWithWormProbability>()
            for (side in symbols) {
                val symbolCount = combination.count { it == side }
                val symbolResultProbabilities = getResultProbabilities(
                    dyeCount - symbolCount,
                    usedSides.withUsed(side),
                    valueSoFar + symbolCount * side.value,
                    memo
                )
                val symbolExpectedValue = symbolResultProbabilities.entries.fold(0f) { symbolExpectedValueAcc, entry ->
                    val (value, probabilityWithWormProbability) = entry
                    symbolExpectedValueAcc + symbolCount * side.value + value * probabilityWithWormProbability.probability * probabilityWithWormProbability.wormProbability
                }
                val symbolWormProbability =
                    symbolResultProbabilities.entries.fold(0f) { symbolWormProbabilityAcc, entry ->
                        val (_, probabilityWithWormProbability) = entry
                        symbolWormProbabilityAcc + probabilityWithWormProbability.wormProbability
                    }
                val value = symbolExpectedValue + symbolWormProbability * symbolCount * side.value
                if (value > combinationBestValue || combinationBestResultProbabilities.isEmpty()) {
                    combinationBestValue = value
                    combinationBestSymbol = side
                    combinationBestResultProbabilities = symbolResultProbabilities
                }
            }
            combinationBestResultProbabilities.forEach { (value, probabilityWithWormProbability) ->
                if (resultProbabilities.containsKey(value + combinationBestSymbol.value)) {
                    resultProbabilities[value + combinationBestSymbol.value] =
                        resultProbabilities.getValue(value + combinationBestSymbol.value) + probabilityWithWormProbability * combinationProbability
                } else {
                    resultProbabilities[value + combinationBestSymbol.value] =
                        probabilityWithWormProbability * combinationProbability
                }
            }
        }
        memo[key] = resultProbabilities
        return resultProbabilities
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
        if (dyeCount == 0) return ValueWithSuccessProbability(
            Rational.ZERO,
            if (wormSoFar) Rational.ONE else Rational.ZERO
        )
        val combinationProbability = 1 over sixPow[dyeCount]
        var expectedValue = Rational.ZERO
        var successProbability = Rational.ZERO
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
                ValueWithSuccessProbability(Rational.ZERO, Rational.ONE)
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
    ): Pair<Rational, Rational> {
        val symbols = combination.toSet()
        var combinationBestValue = Rational.ZERO
        var combinationBestSuccessProbability = Rational.ZERO
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

    private data class Key(
        val dyeCount: Int,
        val usedSides: UsedSides,
        val valueSoFar: Int
    )
}

private val sixPow = IntArray(9).apply {
    this[0] = 1
    this[1] = 6
    this[2] = 36
    this[3] = 216
    this[4] = 1296
    this[5] = 7776
    this[6] = 46656
    this[7] = 279936
    this[8] = 1679616
}