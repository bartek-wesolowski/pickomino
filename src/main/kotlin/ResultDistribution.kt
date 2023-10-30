import java.text.DecimalFormat

private val percentageFormat by lazy { DecimalFormat("#,##0.00'%'") }

class ResultDistribution<V: ValueFunction>(private val valueFunction: V) {
    private val probability: DoubleArray = DoubleArray(valueFunction.valueRange.count())

    fun getExpectedValue(): Double {
        var expectedValue = 0.0
        for (value in valueFunction.valueRange) {
            expectedValue += value * probability[value - valueFunction.valueRange.first]
        }
        return expectedValue
    }

    fun getSuccessProbability(): Double {
        var successProbability = 0.0
        for (value in 1..valueFunction.valueRange.last) {
            successProbability += probability[value - valueFunction.valueRange.first]
        }
        return successProbability
    }

    fun setFailedIfEmpty(value: Int) {
        if (probability.all { it == 0.0 }) {
            probability[value - valueFunction.valueRange.first] = 1.0
        }
    }

    fun merge(other: ResultDistribution<V>, scale: Double) {
        for (i in probability.indices) {
            probability[i] += other.probability[i] * scale
        }
    }

    operator fun get(value: Int): Double = probability[value - valueFunction.valueRange.first]

    operator fun set(value: Int, p: Double) {
        probability[value - valueFunction.valueRange.first] = p
    }

    fun toPrettyString(): String {
        val items = probability
            .mapIndexed { index, value -> index + valueFunction.valueRange.first to value }
            .filter { it.second != 0.0 }
            .joinToString { it.first.toString() + ": " + percentageFormat.format(it.second * 100) }
        return "{ $items }"
    }

    companion object {
        fun <V: ValueFunction> single(valueFunction: V, value: Int) = ResultDistribution(valueFunction).apply {
            probability[value - valueFunction.valueRange.first] = 1.0
        }
    }
}