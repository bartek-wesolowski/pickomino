package com.bartoszwesolowski.pickomino.valuefunction

import com.bartoszwesolowski.pickomino.model.GameState
import com.bartoszwesolowski.pickomino.model.ValueFunction
import kotlin.math.max

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