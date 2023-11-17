package com.bartoszwesolowski.pickomino.model

interface ValueFunction {
    val valueRange: IntRange
    fun getValue(gameState: GameState, points: Int): Int
}