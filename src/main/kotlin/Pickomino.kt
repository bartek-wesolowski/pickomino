import java.util.*

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
        for (roll in Roll.generateAll(dyeCount)) {
            val combinationProbability = roll.probability()
            val combinationResultDistribution = getResultDistributionForRoll(
                gameState,
                roll,
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

    internal fun getResultDistributionForRoll(
        gameState: GameState,
        roll: Roll,
        usedSides: EnumSet<Side>,
        pointsSoFar: Int,
        memo: MutableMap<Key, ResultDistribution>
    ): ResultDistribution {
        var combinationBestValue = Double.NEGATIVE_INFINITY
        var combinationBestResultDistribution: ResultDistribution? = null
        for (side in roll.sides) {
            if (side in usedSides) continue
            val sideCount = roll[side]
            val sideValue = sideCount * side.value
            val sideResultDistribution = if (sideCount == roll.dyeCount) {
                if (Side.WORM in usedSides || side == Side.WORM) {
                    SingleResultDistribution(valueFunction.getValue(gameState, pointsSoFar + sideValue))
                } else {
                    SingleResultDistribution(valueFunction.getValue(gameState, 0))
                }
            } else {
                getResultDistribution(
                    gameState = gameState,
                    dyeCount = roll.dyeCount - sideCount,
                    usedSides = usedSides.withUsed(side),
                    pointsSoFar = pointsSoFar + sideValue,
                    memo = memo
                )
            }
            val canTakeDecision = Side.WORM in usedSides || side == Side.WORM
            val sideResultDistributionAfterDecision =
                if (!canTakeDecision || sideResultDistribution.getExpectedValue() > valueFunction.getValue(
                        gameState, pointsSoFar + sideValue
                    )
                ) {
                    sideResultDistribution
                } else {
                    SingleResultDistribution(valueFunction.getValue(gameState, pointsSoFar + sideValue))
                }
            val sideExpectedValueAfterDecision = sideResultDistributionAfterDecision.getExpectedValue()
            if (sideExpectedValueAfterDecision > combinationBestValue) {
                combinationBestValue = sideExpectedValueAfterDecision
                combinationBestResultDistribution = sideResultDistributionAfterDecision
            }
        }
        return combinationBestResultDistribution ?: SingleResultDistribution(valueFunction.getValue(gameState, 0))
    }

    fun getAdvice(
        gameState: GameState,
        roll: Roll,
        usedSides: EnumSet<Side> = EnumSet.noneOf(Side::class.java),
        pointsSoFar: Int,
    ): Map<Side, ResultDistribution> {
        val advice = mutableMapOf<Side, ResultDistribution>()
        for (side in roll.sides) {
            if (side in usedSides) continue
            if (side in advice) continue
            val count = roll[side]
            val resultDistribution = getResultDistribution(
                gameState = gameState,
                dyeCount = roll.dyeCount - count,
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
