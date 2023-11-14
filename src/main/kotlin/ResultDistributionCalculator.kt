import java.util.*

class ResultDistributionCalculator<V : ValueFunction>(private val valueFunction: V) {

    fun getResultDistribution(
        gameState: GameState,
        turnState: TurnState,
        memo: MutableMap<TurnState, ResultDistribution> = mutableMapOf()
    ): ResultDistribution {
        require(turnState.dyeCount >= 0) { "dyeCount cannot be negative" }
        val cachedResultDistribution = memo[turnState]
        if (cachedResultDistribution != null) {
            return cachedResultDistribution
        }
        val valueSoFar = valueFunction.getValue(gameState, turnState.pointsSoFar)
        if (turnState.dyeCount == 0) {
            val result = if (Side.WORM in turnState.usedSides) {
                SingleResultDistribution(valueSoFar)
            } else {
                SingleResultDistribution(valueFunction.getValue(gameState, 0))
            }
            memo[turnState] = result
            return result
        }
        val resultDistribution = ArrayResultDistribution(valueFunction)
        for (roll in Roll.generateAll(turnState.dyeCount)) {
            val combinationProbability = roll.probability()
            val combinationResultDistribution = getResultDistributionForRoll(
                gameState,
                roll,
                turnState.usedSides,
                turnState.pointsSoFar,
                memo
            )
            resultDistribution.merge(combinationResultDistribution, combinationProbability)
        }
        val result = if (Side.WORM in turnState.usedSides) {
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
        memo[turnState] = result
        return result
    }

    fun getResultDistributionForRoll(
        gameState: GameState,
        roll: Roll,
        usedSides: EnumSet<Side>,
        pointsSoFar: Int,
        memo: MutableMap<TurnState, ResultDistribution>
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
                    turnState = TurnState(
                        dyeCount = roll.dyeCount - sideCount,
                        usedSides = usedSides.withUsed(side),
                        pointsSoFar = pointsSoFar + sideValue,
                    ),
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

    fun getResultDistributionsForAllChoices(
        gameState: GameState,
        turnState: TurnState,
        roll: Roll
    ): Map<Side, ResultDistribution> {
        val resultDistributions = mutableMapOf<Side, ResultDistribution>()
        for (side in roll.sides) {
            if (side in turnState.usedSides) continue
            val count = roll[side]
            val memo = mutableMapOf<TurnState, ResultDistribution>()
            val resultDistribution = getResultDistribution(
                gameState = gameState,
                turnState = TurnState(
                    dyeCount = roll.dyeCount - count,
                    usedSides = turnState.usedSides.withUsed(side),
                    pointsSoFar = turnState.pointsSoFar + side.value * count,
                ),
                memo
            )
            resultDistributions[side] = resultDistribution
        }
        return resultDistributions
    }
}
