package com.bartoszwesolowski.pickomino.model

/** A strategy of playing the game. */
interface Strategy {
    /** Returns a side to choose based on the current game state or null if no side can be chosen. */
    fun chooseSide(
        gameState: GameState,
        turnState: TurnState,
        roll: Roll
    ): Side?

    /** Returns whether you should continue rolling the dice or not. */
    fun shouldContinue(
        gameState: GameState,
        turnState: TurnState
    ): Boolean
}