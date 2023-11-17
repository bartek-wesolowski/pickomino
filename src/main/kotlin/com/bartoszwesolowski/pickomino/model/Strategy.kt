package com.bartoszwesolowski.pickomino.model

interface Strategy {
    fun chooseSide(
        gameState: GameState,
        turnState: TurnState,
        roll: Roll
    ): Side?

    fun shouldContinue(
        gameState: GameState,
        turnState: TurnState
    ): Boolean
}