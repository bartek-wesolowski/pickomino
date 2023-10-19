private const val count = 5

fun main() {
    val player1Strategy = Player("Simple", SimpleStrategy)
    val player2Strategy = Player("Optimal", OptimalStrategy)
    val players = listOf(player1Strategy, player2Strategy)
    val game = Game(players)
    val results = mutableListOf<Map<Player, Int>>()
    for (i in 0 until count) {
        results.add(game.simulate())
    }
    println()
    for (i in 0 until count) {
        for (player in players) {
            print("${player.name}: ${results[i][player]}, ")
        }
        println()
    }
}