import java.util.EnumSet
import kotlin.random.Random

class GameSimulator(private val players: List<Player>) {

    fun simulate(): Map<Player, Int> {
        val availableHelpings = HelpingCollection.all()
        val playerHelpings = players.map { mutableListOf<Helping>() }
        var playerIndex = 0
        var turn = 1
        while (availableHelpings.isNotEmpty()) {
            if (turn != 1) println()
            println("${players[playerIndex].name}'s turn ($turn)")
            val topHelping = playerHelpings[playerIndex].lastOrNull()
            val lastPlayerHelpings = playerHelpings.map { it.lastOrNull() }
            val opponentTopHelpings = HelpingCollection.fromHelpings(
                lastPlayerHelpings.filterNotNull().filter { it != topHelping }
            )
            val gameState = GameState(availableHelpings, topHelping, opponentTopHelpings)
            val points = simulatePlayerTurn(gameState, players[playerIndex].strategy)
            if (points != 0) {
                println("points: $points")
            }
            if (points != 0) {
                val helpingToRob = Helping.fromPoints(points)
                val playerToRobIndex = helpingToRob?.let { lastPlayerHelpings.indexOf(helpingToRob) } ?: -1
                if (playerToRobIndex != -1 && playerToRobIndex != playerIndex) {
                    playerHelpings[playerIndex].add(helpingToRob!!)
                    playerHelpings[playerToRobIndex].removeLast()
                    val totalWorms = playerHelpings[playerIndex].sumOf { it.getWorms() }
                    println("stealing the last helping from ${players[playerToRobIndex].name} for a total of $totalWorms worms")
                } else {
                    val helping = availableHelpings.getExactOrSmaller(points)
                    if (helping != null) {
                        playerHelpings[playerIndex].add(helping)
                        availableHelpings.remove(helping)
                        val totalWorms = playerHelpings[playerIndex].sumOf { it.getWorms() }
                        println("taking the available helping $helping worth ${helping.getWorms()} worms for a total of $totalWorms worms")
                        println("available helpings: $availableHelpings")
                    } else {
                        returnHelping(availableHelpings, playerHelpings, playerIndex)
                        println("available helpings: $availableHelpings")
                    }
                }
            } else {
                returnHelping(availableHelpings, playerHelpings, playerIndex)
                println("available helpings: $availableHelpings")
            }
            playerIndex = (playerIndex + 1) % players.size
            turn += 1
        }
        val result = mutableMapOf<Player, Int>()
        players.forEachIndexed { index, player ->
            result[player] = playerHelpings[index].fold(0) { totalWorms, helping -> totalWorms + helping.getWorms() }
        }
        println()
        println("game finished with result: $result")
        println()
        return result
    }

    private fun returnHelping(
        availableHelpings: HelpingCollection,
        playerHelpings: List<MutableList<Helping>>,
        playerIndex: Int
    ) {
        val helpingToReturn = playerHelpings[playerIndex].lastOrNull()
        if (helpingToReturn != null) {
            println("returning helping $helpingToReturn worth ${helpingToReturn.getWorms()} worms")
            playerHelpings[playerIndex].removeLast()
            availableHelpings.add(helpingToReturn)
        } else {
            println("not helping to return")
        }
        val biggestHelping = availableHelpings.getBiggest()
        if (biggestHelping != helpingToReturn) {
            availableHelpings.remove(biggestHelping)
            println("removing helping $biggestHelping from available helpings")
        }
    }

    private fun simulatePlayerTurn(gameState: GameState, strategy: Strategy): Int {
        var dyeCount = 8
        var pointsSoFar = 0
        val usedSides = EnumSet.noneOf(Side::class.java)
        var shouldContinue: Boolean
        do {
            val roll = randomRoll(dyeCount)
            println("roll: ${roll.sorted()}")
            val symbolChosen = strategy.chooseSymbol(gameState, roll, usedSides, pointsSoFar)
            if (symbolChosen == null) {
                println("cannot choose any symbol")
                return 0
            }
            println("symbol chosen: $symbolChosen")
            val symbolCount = roll.count { it == symbolChosen }
            pointsSoFar += symbolChosen.value * symbolCount
            println("points so far: $pointsSoFar")
            dyeCount -= symbolCount
            usedSides.add(symbolChosen)
            shouldContinue = strategy.shouldContinue(gameState, dyeCount, usedSides, pointsSoFar)
            println("continue rolling: $shouldContinue")
        } while (dyeCount > 0 && shouldContinue)
        return if (Side.WORM in usedSides) pointsSoFar else 0
    }

    private fun randomRoll(dyeCount: Int): List<Side> {
        return List(dyeCount) { randomSide() }
    }

    private fun randomSide() = Side.entries[Random.nextInt(0, Side.entries.size)]
}