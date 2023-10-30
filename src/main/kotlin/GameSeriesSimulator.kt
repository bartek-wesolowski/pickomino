private const val count = 10

fun main() {
    val player1 = Player("Simple", SimpleStrategy)
    val player2 = Player("Optimal", OptimalStrategy)
    val players = listOf(player1, player2)
    val gameSimulator = GameSimulator(players)
    val results = mutableListOf<Map<Player, Int>>()
    for (i in 0 until count) {
        results.add(gameSimulator.simulate())
    }
    println()
    for (i in 0 until count) {
        for (player in players) {
            print("${player.name}: ${results[i][player]}, ")
        }
        println()
    }
    val (wins, draws, loses) = results.getStats(player2)
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