import java.text.DecimalFormat

private val percentageFormat by lazy { DecimalFormat("#,##0.00'%'") }

class ResultDistribution(val maxValue: Int) {
    private val probability: DoubleArray = DoubleArray(2 * maxValue + 1)

    fun getExpectedValue(): Double {
        var expectedValue = 0.0
        for (value in -maxValue..maxValue) {
            expectedValue += value * probability[value + maxValue]
        }
        return expectedValue
    }

    fun getSuccessProbability(): Double {
        var successProbability = 0.0
        for (value in 1..maxValue) {
            successProbability += probability[value + maxValue]
        }
        return successProbability
    }

    fun setFailedIfEmpty(value: Int) {
        if (probability.all { it == 0.0 }) {
            probability[value + maxValue] = 1.0
        }
    }

    fun merge(other: ResultDistribution, scale: Double) {
        for (i in probability.indices) {
            probability[i] += other.probability[i] * scale
        }
    }

    operator fun get(value: Int): Double = probability[value + maxValue]

    operator fun set(value: Int, p: Double) {
        probability[value + maxValue] = p
    }

    fun toPrettyString(): String {
        val items = probability
            .mapIndexed { index, value -> index - maxValue to value }
            .filter { it.second != 0.0 }
            .joinToString { it.first.toString() + ": " + percentageFormat.format(it.second * 100) }
        return "{ $items }"
    }

    companion object {
        fun single(maxValue: Int, value: Int) = ResultDistribution(maxValue).apply {
            probability[value + maxValue] = 1.0
        }
    }
}