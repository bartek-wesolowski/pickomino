package com.bartoszwesolowski.pickomino

private const val count = 10

fun main() {
    val player1 = Player("Simple", SimpleStrategy)
    val player2 = Player("Optimal", OptimalStrategy())
    val players1 = listOf(player1, player2)
    val players2 = listOf(player2, player1)

    val results = mutableListOf<Map<Player, Int>>()
    repeat (count) { gameNumber ->
        val players = if (gameNumber % 2 == 0) players1 else players2
        val gameSimulator = GameSimulator(players, verbose = false)
        println("game ${gameNumber + 1}")
        val result = gameSimulator.simulate()
        results.add(result)
        for (player in players) {
            print("${player.name}: ${result[player]}, ")
        }
        println()
    }
    val (wins, draws, loses) = results.getStats(player2)
    println()
    println("${player2.name} wins: $wins, draws: $draws, loses: $loses")
}

private fun List<Map<Player, Int>>.getStats(player: Player): Triple<Int, Int, Int> {
    var wins = 0
    var draws = 0
    var loses = 0
    for (game in this) {
        val otherPlayers = game.filterKeys { it != player }
        val playerScore = game.getValue(player)
        val maxOpponentScore = otherPlayers.maxOf { (_, points) -> points }
        if (playerScore > maxOpponentScore) {
            wins += 1
        } else if (playerScore == maxOpponentScore) {
            draws += 1
        } else {
            loses += 1
        }
    }
    return Triple(wins, draws, loses)
}