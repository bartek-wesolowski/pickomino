package com.bartoszwesolowski.pickomino.strategy

import com.bartoszwesolowski.pickomino.model.*

data object SimpleStrategy : Strategy {
    override fun chooseSide(
        gameState: GameState,
        turnState: TurnState,
        roll: Roll,
    ): Side? {
        if (Side.WORM in roll && Side.WORM !in turnState.usedSides) return Side.WORM
        val sidesSortedByValue = roll.sides.sortedBy { -it.value }
        for (side in sidesSortedByValue) {
            if (side !in turnState.usedSides) return side
        }
        return null
    }

    override fun shouldContinue(gameState: GameState, turnState: TurnState): Boolean {
        return Side.WORM !in turnState.usedSides ||
                (
                        turnState.pointsSoFar < gameState.availableHelpings.getSmallest().points &&
                                turnState.pointsSoFar !in gameState.opponentTopHelpings
                        )
    }
}