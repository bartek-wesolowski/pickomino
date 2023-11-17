package com.bartoszwesolowski.pickomino

data class GameState(
    val availableHelpings: HelpingCollection,
    val topHelping: Helping?,
    val opponentTopHelpings: HelpingCollection,
)
