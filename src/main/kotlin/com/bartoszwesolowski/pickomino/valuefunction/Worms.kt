package com.bartoszwesolowski.pickomino.valuefunction

import com.bartoszwesolowski.pickomino.model.GameState
import com.bartoszwesolowski.pickomino.model.Helping
import com.bartoszwesolowski.pickomino.model.ValueFunction

data object Worms : ValueFunction {
    override val valueRange = 0..4

    override fun getValue(gameState: GameState, points: Int): Int {
        if (points > 36) return 4
        return Helping.fromPoints(points)?.getWorms() ?: 0
    }
}