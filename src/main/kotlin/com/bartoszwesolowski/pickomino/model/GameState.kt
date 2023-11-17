package com.bartoszwesolowski.pickomino.model

data class GameState(
    val availableHelpings: HelpingCollection,
    val topHelping: Helping?,
    val opponentTopHelpings: HelpingCollection,
)
