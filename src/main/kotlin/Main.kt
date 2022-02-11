import java.util.*

fun main() {
    val worms = Worms()
    for (i in 1..8) {
        println(i.toString())
        println(worms.getResultDistribution(i))
        println(worms.getResultDistribution(i, EnumSet.of(Side.WORM)))
    }
//    worms.getAdvice(rollOf(1, 2, 2, 2, 2, 2)).prettyPrint()
//    println()
//    val resultProbabilities = worms.getResultProbabilities(2).toSortedMap()
//    resultProbabilities.forEach { (value, probabilityWithWormProbability) ->
//        println(
//            "$value -> ${probabilityWithWormProbability.probability}, ${probabilityWithWormProbability.wormProbability}"
//        )
//    }
//
//    val totalProbability = resultProbabilities.values.fold(0f) { total, probabilityWithWormProbability ->
//        total + probabilityWithWormProbability.probability
//    }
//    println("totalProbability: $totalProbability")
//
//    val totalWormProbability = resultProbabilities.values.fold(0f) { total, probabilityWithWormProbability ->
//        total + probabilityWithWormProbability.wormProbability
//    }
//    println("totalWormProbability: $totalWormProbability")
}

fun rollOf(vararg values: Int): List<Side> {
    return values.map { Side.values()[it - 1] }
}

fun Map<Side, ValueWithSuccessProbability>.prettyPrint() {
    for (side in keys) {
        val valueWithWormProbability = getValue(side)
        print("${side.toString().padStart(5)} ")
        print("value: ${valueWithWormProbability.value.toFloat()} ${valueWithWormProbability.value}".padEnd(36))
        println("worm: ${valueWithWormProbability.successProbability.toFloat()} ${valueWithWormProbability.successProbability}")
    }
}