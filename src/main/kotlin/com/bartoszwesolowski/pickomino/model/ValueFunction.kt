package com.bartoszwesolowski.pickomino.model

/** A function that assigns a value to a certain amount of points in a game state. */
interface ValueFunction {
    /** The range of possible values. */
    val valueRange: IntRange
    /** Returns the value of a certain amount of points in a game state. */
    fun getValue(gameState: GameState, points: Int): Int
}