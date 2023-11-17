package com.bartoszwesolowski.pickomino.valuefunction

import com.bartoszwesolowski.pickomino.model.GameState
import com.bartoszwesolowski.pickomino.model.ValueFunction

data object Points : ValueFunction {
    override val valueRange = 0..40

    override fun getValue(gameState: GameState, points: Int): Int = points
}