package com.bartoszwesolowski.pickomino.strategy

import com.bartoszwesolowski.pickomino.model.*
import kotlin.math.absoluteValue
import kotlin.random.Random

data object RandomStrategy : Strategy {
    override fun chooseSide(
        gameState: GameState,
        turnState: TurnState,
        roll: Roll,
    ): Side? {
        if (Side.WORM in roll && Side.WORM !in turnState.usedSides) return Side.WORM
        val notUsedSides = roll.sides.filter { it !in turnState.usedSides }.toList()
        return if (notUsedSides.isNotEmpty()) {
            notUsedSides[Random.nextInt().absoluteValue % notUsedSides.size]
        } else {
            null
        }
    }

    override fun shouldContinue(
        gameState: GameState,
        turnState: TurnState
    ): Boolean {
        return Side.WORM !in turnState.usedSides ||
                (
                        turnState.pointsSoFar < gameState.availableHelpings.getSmallest().points &&
                                turnState.pointsSoFar !in gameState.opponentTopHelpings
                        )
    }
}