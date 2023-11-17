package com.bartoszwesolowski.pickomino.model

import java.util.*

data class TurnState(
    val dyeCount: Int,
    val usedSides: EnumSet<Side>,
    val pointsSoFar: Int
) {
    companion object {
        private val INITIAL = TurnState(
            dyeCount = 8,
            usedSides = EnumSet.noneOf(Side::class.java),
            pointsSoFar = 0
        )

        fun initial(): TurnState = INITIAL
    }
}