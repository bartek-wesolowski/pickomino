import java.util.EnumSet

fun main() {
    val resultDistributionCalculator = ResultDistributionCalculator(WormsFromAvailableHelpings)

    val dyeCount = 8
    val roll = Roll.of(Side.TWO to 3, Side.THREE to 1, Side.FOUR to 1, Side.WORM to 1)
    val usedSides = EnumSet.noneOf(Side::class.java)
    val pointsSoFar = 0
    val gameState = GameState(
        availableHelpings = HelpingCollection.all(),
        topHelping = null,
        opponentTopHelpings = HelpingCollection.empty()
    )

    println("Result distribution")
    println(
        resultDistributionCalculator.getResultDistribution(
            gameState = gameState,
            dyeCount = dyeCount,
            usedSides = usedSides,
            pointsSoFar = pointsSoFar,
        )
    )
    println()

    val advice = resultDistributionCalculator.getResultDistributionsForAllChoices(
        gameState = gameState,
        roll = roll,
        usedSides = usedSides,
        pointsSoFar = pointsSoFar,
    )
    println("Advice")
    for ((side, resultDistribution) in advice) {
        print("$side worms: ")
        print("%.3f".format(resultDistribution.getExpectedValue()))
        print(" ")
        println(resultDistribution)
    }
    println()

    println(
        "Side chosen: " +
        OptimalStrategy.chooseSide(
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