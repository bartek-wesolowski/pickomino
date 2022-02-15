fun main() {
    val pickomino = Pickomino()
    println(
        pickomino.getResultDistribution(
            dyeCount = 8,
            valueFunction = WormsValueFunction
        )
            .toPrettyString()
    )
    pickomino.getAdvice(
        roll = rollOf(1, 2, 2, 2, 2, 2, 4, 5),
        valueFunction = WormsValueFunction
    ).prettyPrint()
}

fun rollOf(vararg values: Int): List<Side> {
    require(values.size <= 8) { "There can't be more then 8 dice" }
    return values.map { Side.values()[it - 1] }
}

fun Map<Side, ValueWithSuccessProbability>.prettyPrint(precision: Int = 4) {
    for (side in keys) {
        val valueWithWormProbability = getValue(side)
        print("${side.toString().padStart(5)} ")
        print("value: " + "%.${precision}f".format(valueWithWormProbability.value) + " ")
        println("worm: " + "%.${precision}f".format(valueWithWormProbability.successProbability))
    }
}