package com.bartoszwesolowski.pickomino

import kotlin.math.max

class OptimalStrategy(
    valueFunction: ValueFunction = WormsFromAvailableHelpings
) : Strategy {
    private val resultDistributionCalculator = ResultDistributionCalculator(valueFunction)

    override fun chooseSide(
        gameState: GameState,
        turnState: TurnState,
        roll: Roll
    ): Side? {
        require(roll.dyeCount == turnState.dyeCount)
        var bestSide: Side? = null
        var bestValue = Double.NEGATIVE_INFINITY
        for (side in roll.sides) {
            if (side in turnState.usedSides) continue
            val sideCount = roll[side]
            val expectedValue = resultDistributionCalculator.getResultDistribution(
                gameState = gameState,
                turnState = TurnState(
                    dyeCount = roll.dyeCount - sideCount,
                    usedSides = turnState.usedSides.withUsed(side),
                    pointsSoFar = turnState.pointsSoFar + side.value * sideCount,
                )
            ).getExpectedValue()
            if (expectedValue > bestValue) {
                bestSide = side
                bestValue = expectedValue
            }
        }
        return bestSide
    }

    override fun shouldContinue(
        gameState: GameState,
        turnState: TurnState
    ): Boolean {
        val wormsIfContinued = resultDistributionCalculator.getResultDistribution(
            gameState,
            turnState,
        ).getExpectedValue()
        return if (Side.WORM in turnState.usedSides) {
            var wormsIfStopped = max(
                gameState.availableHelpings.getExactOrSmaller(turnState.pointsSoFar)?.getWorms() ?: 0,
                gameState.opponentTopHelpings.getOrNull(turnState.pointsSoFar)?.getWorms() ?: 0
            )
            if (wormsIfStopped == 0 && gameState.topHelping != null) {
                wormsIfStopped = -gameState.topHelping.getWorms()
            }
            wormsIfContinued > wormsIfStopped
        } else {
            true
        }
    }
}