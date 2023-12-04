package com.bartoszwesolowski.pickomino.model

/** The current game state. */
data class GameState(
    val availableHelpings: HelpingCollection,
    val topHelping: Helping?,
    val opponentTopHelpings: HelpingCollection,
)
