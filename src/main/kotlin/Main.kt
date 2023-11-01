import java.util.EnumSet

fun main() {
    val pickomino = Pickomino(WormsFromAvailableHelpings)

    val dyeCount = 3
    val roll = rollOf(2, 2, 2, 3, 4, 6)
    val usedSides = EnumSet.of(Side.FIVE, Side.THREE, Side.WORM)
    val pointsSoFar = 21
    val gameState = GameState(
        availableHelpings = HelpingCollection.fromPoints(24, 27, 29, 31, 32, 33, 34, 35, 36),
        topHelping = Helping.fromPoints(30),
        opponentTopHelpings = HelpingCollection.fromPoints(22)
    )

    println("Result distribution")
    println(
        pickomino.getResultDistribution(
            gameState = gameState,
            dyeCount = dyeCount,
            usedSides = usedSides,
            pointsSoFar = pointsSoFar,
        )
    )
    println()

    val advice = pickomino.getAdvice(
        gameState = gameState,
        roll = roll,
        usedSides = usedSides,
        pointsSoFar = pointsSoFar,
    )
    println("Advice")
    for ((symbol, resultDistribution) in advice) {
        print("$symbol worms: ")
        print("%.3f".format(resultDistribution.getExpectedValue()))
        print(" ")
        println(resultDistribution)
    }
    println()

    println(
        "Symbol chosen: " +
        OptimalStrategy.chooseSymbol(
            gameState = gameState,
            roll = roll,
            usedSides = usedSides,
            pointsSoFar = pointsSoFar,
        )
    )
    println(
        "Should continue: " +
        OptimalStrategy.shouldContinue(
            gameState = gameState,
            dyeCount = dyeCount,
            usedSides = usedSides,
            pointsSoFar = pointsSoFar,
        )
    )
}

fun rollOf(vararg values: Int): List<Side> {
    require(values.size <= 8) { "There can't be more then 8 dice" }
    return values.map { Side.entries[it - 1] }
}
