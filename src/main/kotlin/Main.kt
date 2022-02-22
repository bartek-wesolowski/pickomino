import java.util.EnumSet

fun main() {
    val memo = mutableMapOf<Pickomino.Key, ResultDistribution>()
    val pickomino = Pickomino(memo)
//    println(
//        pickomino.getResultDistribution(
//            dyeCount = 8,
//            valueFunction = ValueFunction.Worms
//        )
//            .toPrettyString()
//    )

//    val advice = pickomino.getAdvice(
//        roll = rollOf(2, 6),
//        valueFunction = ValueFunction.WormsFromAvailableHelpings(
//            Helpings(),
//            Helpings(listOf())
//        ),
//        usedSides = EnumSet.of(Side.FOUR, Side.FIVE, Side.THREE, Side.ONE),
//        pointsSoFar = 17
//    )
//    for ((symbol, resultDistribution) in advice) {
//        print("$symbol worms: ")
//        print("%.3f".format(resultDistribution.getExpectedValue()))
//        print(" ")
//        println(resultDistribution.toPrettyString())
//    }
//
//    val resultDistribution = pickomino.getResultDistribution(
//        1,
//        valueFunction = ValueFunction.WormsFromAvailableHelpings(Helpings(), Helpings(listOf())),
//        usedSides = EnumSet.of(Side.FOUR, Side.FIVE, Side.THREE, Side.ONE, Side.TWO),
//        pointsSoFar = 19
//    )
//    println(resultDistribution.toPrettyString())
//
//    val strategy = OptimalStrategy()
//    println(
//        strategy.chooseSymbol(
//            rollOf(2, 6),
//            usedSides = EnumSet.of(Side.THREE, Side.FIVE, Side.FOUR, Side.ONE),
//            pointsSoFar = 17,
//            availableHelpings = Helpings(),
//            opponentTopHelpings = Helpings(listOf())
//        )
//    )

    println(Game(listOf(SimpleStrategy(), OptimalStrategy(memo))).simulate())
}

fun rollOf(vararg values: Int): List<Side> {
    require(values.size <= 8) { "There can't be more then 8 dice" }
    return values.map { Side.values()[it - 1] }
}
