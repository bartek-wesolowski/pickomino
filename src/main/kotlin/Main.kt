import java.util.EnumSet

fun main() {
    val pickomino = Pickomino()

    val dyeCount = 3
    val roll = rollOf(2, 2, 2, 3, 4, 6)
    val usedSides = EnumSet.of(Side.FIVE, Side.THREE, Side.WORM)
    val pointsSoFar = 21
    val availableHelpings = HelpingCollection.fromPoints(24, 27, 29, 31, 32, 33, 34, 35, 36)
    val topHelping = Helping.fromPoints(30)
    val opponentTopHelpings = HelpingCollection.fromPoints(22)
    val valueFunction = ValueFunction.WormsFromAvailableHelpings

    println("Result distribution")
    println(
        pickomino.getResultDistribution(
            dyeCount = dyeCount,
            usedSides = usedSides,
            pointsSoFar = pointsSoFar,
            availableHelpings = availableHelpings,
            topHelping = topHelping,
            opponentTopHelpings = opponentTopHelpings,
            valueFunction = valueFunction
        )
            .toPrettyString()
    )
    println()

    val advice = pickomino.getAdvice(
        roll = roll,
        usedSides = usedSides,
        pointsSoFar = pointsSoFar,
        availableHelpings = availableHelpings,
        topHelping = topHelping,
        opponentTopHelpings = opponentTopHelpings,
        valueFunction = valueFunction
    )
    println("Advice")
    for ((symbol, resultDistribution) in advice) {
        print("$symbol worms: ")
        print("%.3f".format(resultDistribution.getExpectedValue()))
        print(" ")
        println(resultDistribution.toPrettyString())
    }
    println()

    println(
        "Symbol chosen: " +
        OptimalStrategy.chooseSymbol(
            roll = roll,
            usedSides = usedSides,
            pointsSoFar = pointsSoFar,
            availableHelpings = availableHelpings,
            opponentTopHelpings = opponentTopHelpings,
            topHelping = topHelping
        )
    )
    println(
        "Should continue: " +
        OptimalStrategy.shouldContinue(
            dyeCount = dyeCount,
            usedSides = usedSides,
            pointsSoFar = pointsSoFar,
            availableHelpings = availableHelpings,
            opponentTopHelpings = opponentTopHelpings,
            topHelping = topHelping
        )
    )
}

fun rollOf(vararg values: Int): List<Side> {
    require(values.size <= 8) { "There can't be more then 8 dice" }
    return values.map { Side.entries[it - 1] }
}
