import java.util.EnumSet
import kotlin.random.Random

class Game(private val players: List<Strategy>) {

    fun simulate(): Map<Strategy, Int> {
        val availableHelpings = Helpings()
        val playerHelpings = mutableListOf<MutableList<Int>>().apply {
            for (i in players.indices) {
                add(mutableListOf())
            }
        }
        var playerIndex = 0
        var turn = 1
        while (availableHelpings.isNotEmpty()) {
            println("Player $playerIndex's turn ($turn)")
            val lastPlayerHelpings = playerHelpings.map { it.lastOrNull() }
            val opponentTopHelpings =
                Helpings(lastPlayerHelpings.filterNotNull().filter { it != lastPlayerHelpings[playerIndex] })
            val points = simulatePlayerTurn(players[playerIndex], availableHelpings, opponentTopHelpings)
            println("points: $points")
            if (points != 0) {
                val playerToRobIndex = lastPlayerHelpings.indexOf(points)
                if (playerToRobIndex != -1 && playerToRobIndex != playerIndex) {
                    playerHelpings[playerIndex].add(points)
                    playerHelpings[playerToRobIndex].removeLast()
                    val totalWorms = playerHelpings[playerIndex].sumOf { Helping.getWorms(it) }
                    println("stealing the last helping from player $playerToRobIndex for a total of $totalWorms worms")
                } else {
                    val helpingPoints = availableHelpings.getHelpingPoints(points)
                    if (helpingPoints != 0) {
                        playerHelpings[playerIndex].add(helpingPoints)
                        availableHelpings.remove(helpingPoints)
                        val totalWorms = playerHelpings[playerIndex].sumOf { Helping.getWorms(it) }
                        println("taking the available helping $helpingPoints worth ${Helping.getWorms(helpingPoints)} worms for a total of $totalWorms worms")
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
        val result = mutableMapOf<Strategy, Int>()
        players.forEachIndexed { index, player ->
            result[player] = playerHelpings[index].fold(0) { totalWorms, points -> totalWorms + Helping.getWorms(points) }
        }
        return result
    }

    private fun returnHelping(
        availableHelpings: Helpings,
        playerHelpings: MutableList<MutableList<Int>>,
        playerIndex: Int
    ) {
        val helpingToReturn = playerHelpings[playerIndex].lastOrNull()
        if (helpingToReturn != null) {
            println("returning a helping (${playerHelpings[playerIndex].last()})")
            playerHelpings[playerIndex].removeLast()
            availableHelpings.add(helpingToReturn)
        } else {
            println("not helping to return")
        }
        val biggestHelping = availableHelpings.getBiggest()
        if (biggestHelping != helpingToReturn) {
            availableHelpings.remove(biggestHelping)
        }
    }

    private fun simulatePlayerTurn(strategy: Strategy, availableHelpings: Helpings, opponentTopHelpings: Helpings): Int {
        var dyeCount = 8
        var pointsSoFar = 0
        val usedSides = EnumSet.noneOf(Side::class.java)
        var shouldContinue = false
        do {
            val roll = randomRoll(dyeCount)
            println("roll: ${roll.sorted()}")
            val symbolChosen = strategy.chooseSymbol(roll, usedSides, pointsSoFar, availableHelpings, opponentTopHelpings)
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
            shouldContinue = strategy.shouldContinue(
                dyeCount,
                usedSides,
                pointsSoFar,
                availableHelpings,
                opponentTopHelpings
            )
            println("continue rolling: $shouldContinue")
        } while (dyeCount > 0 && shouldContinue)
        return pointsSoFar
    }

    private fun randomRoll(dyeCount: Int): List<Side> {
        return List(dyeCount) { randomSide() }
    }

    private fun randomSide() = Side.values()[Random.nextInt(0, Side.values().size)]
}