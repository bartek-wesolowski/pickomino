package com.bartoszwesolowski.pickomino.model

import kotlin.math.max

interface ValueFunction {
    val valueRange: IntRange
    fun getValue(gameState: GameState, points: Int): Int
}

data object Worms : ValueFunction {
    override val valueRange = 0..4

    override fun getValue(gameState: GameState, points: Int): Int {
        if (points > 36) return 4
        return Helping.fromPoints(points)?.getWorms() ?: 0
    }
}

data object WormsFromAvailableHelpings : ValueFunction {
    override val valueRange = -4..4

    override fun getValue(gameState: GameState, points: Int): Int {
        val worms = max(
            gameState.availableHelpings.getExactOrSmaller(points)?.getWorms() ?: 0,
            gameState.opponentTopHelpings.getOrNull(points)?.getWorms() ?: 0
        )
        return if (worms > 0) {
            worms
        } else {
            if (gameState.topHelping != null) {
                -gameState.topHelping.getWorms()
            } else {
                0
            }
        }
    }
}

data object AnyWormFromAvailableHelpings : ValueFunction {
    override val valueRange = -1..1

    override fun getValue(gameState: GameState, points: Int): Int {
        val worms = max(
            gameState.availableHelpings.getExactOrSmaller(points)?.getWorms() ?: 0,
            gameState.opponentTopHelpings.getOrNull(points)?.getWorms() ?: 0
        )
        return if (worms > 0) {
            1
        } else {
            if (gameState.topHelping != null) {
                -1
            } else {
                0
            }
        }
    }
}

data object Points : ValueFunction {
    override val valueRange = 0..40

    override fun getValue(gameState: GameState, points: Int): Int = points
}