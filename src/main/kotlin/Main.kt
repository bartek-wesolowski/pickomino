fun main() {
    val pickomino = Pickomino()
    val memo = mutableMapOf<Pickomino.Key, ResultDistribution>()
    println(
        pickomino.getResultDistribution(
            dyeCount = 8,
            valueFunction = WormsValueFunction,
            memo = memo
        )
            .toPrettyString()
    )
    val advice = pickomino.getAdvice(
        roll = rollOf(1, 2, 2, 2, 2, 2, 4, 5),
        valueFunction = WormsValueFunction,
        memo = memo
    )
    for ((symbol, resultDistribution) in advice) {
        print("$symbol worms: ")
        print("%.3f".format(resultDistribution.getExpectedValue()))
        print(" ")
        println(resultDistribution.toPrettyString())
    }
}

fun rollOf(vararg values: Int): List<Side> {
    require(values.size <= 8) { "There can't be more then 8 dice" }
    return values.map { Side.values()[it - 1] }
}
