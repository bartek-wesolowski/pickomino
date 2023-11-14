class GameSimulator(
    private val players: List<Player>,
    private val verbose: Boolean,
) {

    fun simulate(): Map<Player, Int> {
        val availableHelpings = HelpingCollection.all()
        val playerHelpings = players.map { mutableListOf<Helping>() }
        var playerIndex = 0
        var turn = 1
        while (availableHelpings.isNotEmpty()) {
            if (verbose) {
                if (turn != 1) println()
                println("${players[playerIndex].name}'s turn ($turn)")
            }
            val topHelping = playerHelpings[playerIndex].lastOrNull()
            val lastPlayerHelpings = playerHelpings.map { it.lastOrNull() }
            val opponentTopHelpings = HelpingCollection.fromHelpings(
                lastPlayerHelpings.filterNotNull().filter { it != topHelping }
            )
            val gameState = GameState(availableHelpings, topHelping, opponentTopHelpings)
            val points = simulatePlayerTurn(gameState, players[playerIndex].strategy)
            if (points != 0) {
                if (verbose) {
                    println("points: $points")
                }
            }
            if (points != 0) {
                val helpingToRob = Helping.fromPoints(points)
                val playerToRobIndex = helpingToRob?.let { lastPlayerHelpings.indexOf(helpingToRob) } ?: -1
                if (playerToRobIndex != -1 && playerToRobIndex != playerIndex) {
                    playerHelpings[playerIndex].add(helpingToRob!!)
                    playerHelpings[playerToRobIndex].removeLast()
                    val totalWorms = playerHelpings[playerIndex].sumOf { it.getWorms() }
                    if (verbose) {
                        println("stealing the last helping from ${players[playerToRobIndex].name} for a total of $totalWorms worms")
                    }
                } else {
                    val helping = availableHelpings.getExactOrSmaller(points)
                    if (helping != null) {
                        playerHelpings[playerIndex].add(helping)
                        availableHelpings.remove(helping)
                        val totalWorms = playerHelpings[playerIndex].sumOf { it.getWorms() }
                        if (verbose) {
                            println("taking the available helping $helping worth ${helping.getWorms()} worms for a total of $totalWorms worms")
                            println("available helpings: $availableHelpings")
                        }
                    } else {
                        returnHelping(availableHelpings, playerHelpings, playerIndex)
                        if (verbose) {
                            println("available helpings: $availableHelpings")
                        }
                    }
                }
            } else {
                returnHelping(availableHelpings, playerHelpings, playerIndex)
                if (verbose) {
                    println("available helpings: $availableHelpings")
                }
            }
            playerIndex = (playerIndex + 1) % players.size
            turn += 1
        }
        val result = mutableMapOf<Player, Int>()
        players.forEachIndexed { index, player ->
            result[player] = playerHelpings[index].fold(0) { totalWorms, helping -> totalWorms + helping.getWorms() }
        }
        if (verbose) {
            println()
            println("game finished with result: $result")
            println()
        }
        return result
    }

    private fun returnHelping(
        availableHelpings: HelpingCollection,
        playerHelpings: List<MutableList<Helping>>,
        playerIndex: Int
    ) {
        val helpingToReturn = playerHelpings[playerIndex].lastOrNull()
        if (helpingToReturn != null) {
            if (verbose) {
                println("returning helping $helpingToReturn worth ${helpingToReturn.getWorms()} worms")
            }
            playerHelpings[playerIndex].removeLast()
            availableHelpings.add(helpingToReturn)
        } else {
            if (verbose) {
                println("not helping to return")
            }
        }
        val biggestHelping = availableHelpings.getBiggest()
        if (biggestHelping != helpingToReturn) {
            availableHelpings.remove(biggestHelping)
            if (verbose) {
                println("removing helping $biggestHelping from available helpings")
            }
        }
    }

    private fun simulatePlayerTurn(gameState: GameState, strategy: Strategy): Int {
        var turnState = TurnState.initial()
        var shouldContinue: Boolean
        do {
            val roll = Roll.random(turnState.dyeCount)
            if (verbose) {
                println("roll: $roll")
            }
            val sideChosen = strategy.chooseSide(gameState, turnState, roll)
            if (sideChosen == null) {
                if (verbose) {
                    println("cannot choose any side")
                }
                return 0
            }
            if (verbose) {
                println("side chosen: $sideChosen")
            }
            val sideCount = roll[sideChosen]
            turnState = TurnState(
                dyeCount = turnState.dyeCount - sideCount,
                usedSides = turnState.usedSides.withUsed(sideChosen),
                pointsSoFar = turnState.pointsSoFar + sideChosen.value * sideCount
            )
            if (verbose) {
                println("points so far: ${turnState.pointsSoFar}")
            }
            shouldContinue = strategy.shouldContinue(gameState, turnState)
            if (verbose) {
                println("continue rolling: $shouldContinue")
            }
        } while (turnState.dyeCount > 0 && shouldContinue)
        return if (Side.WORM in turnState.usedSides) turnState.pointsSoFar else 0
    }
}