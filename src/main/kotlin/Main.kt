import java.util.EnumSet

fun main() {
    val pickomino = Pickomino()
    println(
        pickomino.getResultDistribution(
            dyeCount = 8,
            usedSides = EnumSet.noneOf(Side::class.java),
            pointsSoFar = 0,
            availableHelpings = Helpings(),
            topHelping = null,
            opponentTopHelpings = Helpings(listOf()),
            valueFunction = ValueFunction.Worms
        )
            .toPrettyString()
    )

    val advice = pickomino.getAdvice(
        roll = rollOf(2, 2, 2, 3, 4, 6),
        usedSides = EnumSet.of(Side.FOUR),
        pointsSoFar = 8,
        availableHelpings = Helpings(listOf(27, 29, 31, 32, 33)),
        topHelping = 23,
        opponentTopHelpings = Helpings(listOf(26)),
        valueFunction = ValueFunction.WormsFromAvailableHelpings
    )
    for ((symbol, resultDistribution) in advice) {
        print("$symbol worms: ")
        print("%.3f".format(resultDistribution.getExpectedValue()))
        print(" ")
        println(resultDistribution.toPrettyString())
    }

    val resultDistribution = pickomino.getResultDistribution(
        1,
        usedSides = EnumSet.of(Side.FOUR, Side.FIVE, Side.THREE, Side.ONE, Side.TWO),
        pointsSoFar = 19,
        availableHelpings = Helpings(),
        topHelping = null,
        opponentTopHelpings = Helpings(listOf()),
        valueFunction = ValueFunction.WormsFromAvailableHelpings
    )
    println(resultDistribution.toPrettyString())

    println(
        OptimalStrategy.chooseSymbol(
            roll = rollOf(2, 2, 2, 3, 4, 6),
            usedSides = EnumSet.of(Side.FOUR),
            pointsSoFar = 8,
            availableHelpings = Helpings(listOf(27, 29, 31, 32, 33)),
            opponentTopHelpings = Helpings(listOf(26)),
            topHelping = 23
        )
    )
    println(
        OptimalStrategy.shouldContinue(
            dyeCount = 2,
            usedSides = EnumSet.of(Side.TWO, Side.FOUR, Side.FIVE),
            pointsSoFar = 23,
            availableHelpings = Helpings(listOf(21, 22, 23, 24, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36)),
            opponentTopHelpings = Helpings(listOf(25)),
            topHelping = null
        )
    )

    println(Game(listOf(SimpleStrategy, OptimalStrategy)).simulate())

//    val simpleStrategy = SimpleStrategy()
//    val optimalStrategy = OptimalStrategy()
//    val count = 100
//    val game = Game(listOf(simpleStrategy, optimalStrategy))
//    val results = mutableListOf<Map<Strategy, Int>>()
//    for (i in 0 until count) {
//        results.add(game.simulate())
//    }
//    for (i in 0 until count) {
//        println("${results[i][simpleStrategy]}, ${results[i][optimalStrategy]}")
//    }
}

fun rollOf(vararg values: Int): List<Side> {
    require(values.size <= 8) { "There can't be more then 8 dice" }
    return values.map { Side.entries[it - 1] }
}
