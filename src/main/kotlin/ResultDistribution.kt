import java.text.DecimalFormat

class ResultDistribution(private val maxValue: Int) {
    private val probability: DoubleArray = DoubleArray(maxValue + 1)
    private val percentageFormat by lazy { DecimalFormat("#,##0.00'%'") }

    fun getExpectedValue(): Double {
        var expectedValue = 0.0
        for (value in 0..maxValue) {
            expectedValue += value * probability[value]
        }
        return expectedValue
    }

    fun getSuccessProbability(): Double {
        var successProbability = 0.0
        for (value in 1..maxValue) {
            successProbability += probability[value]
        }
        return successProbability
    }

    fun setFailedIfEmpty() {
        if (probability.all { it == 0.0 }) {
            probability[0] = 1.0
        }
    }

    fun merge(other: ResultDistribution, scale: Double) {
        for (i in probability.indices) {
            probability[i] += other.probability[i] * scale
        }
    }

    operator fun get(value: Int): Double = probability[value]

    operator fun set(value: Int, p: Double) {
        probability[value] = p
    }

    fun toPrettyString(precision: Int = 3): String {
        val items = probability
            .mapIndexed { index, value -> index to value }
            .filter { it.second != 0.0 }
            .joinToString { it.first.toString() + ": " + percentageFormat.format(it.second * 100) }
        return "{ $items }"
    }

    companion object {
        fun successful(maxValue: Int, value: Int) = ResultDistribution(maxValue).apply {
            probability[value] = 1.0
        }

        fun failed(maxValue: Int) = ResultDistribution(maxValue).apply {
            probability[0] = 1.0
        }
    }
}