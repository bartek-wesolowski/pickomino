import java.util.EnumSet

class ResultDistribution(private val maxValue: Int) {
    private val probability: DoubleArray = DoubleArray(maxValue + 1)

    fun getExpectedValue(): Double {
        var expectedValue = 0.0
        for (value in 0..maxValue) {
            expectedValue += value * probability[value]
        }
        return expectedValue
    }

    fun getSuccessProbability(): Double {
        var successProbability = 0.0
        for (value in 0..maxValue) {
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

    fun toPrettyString(precision: Int = 4): String {
        val items = probability
            .mapIndexed { index, value -> index to value }
            .filter { it.second != 0.0 }
            .joinToString { it.first.toString() + ": " + "%.${precision}f".format(it.second) }
        return "{ $items }"
    }

    companion object {
        fun successful(maxValue: Int, value: Int) = ResultDistribution(maxValue).apply {
            probability[value] = 1.0
        }

        fun failed(maxValue: Int) = ResultDistribution(maxValue).apply {
            probability[0] = 1.0
        }

        fun createForOneDye(
            valueFunction: ValueFunction,
            usedSides: EnumSet<Side>,
            pointsSoFar: Int
        ): ResultDistribution {
            return ResultDistribution(valueFunction.maxValue).apply {
                if (Side.WORM in usedSides) {
                    probability[0] = oneSixth * usedSides.size
                    if (Side.ONE !in usedSides) probability[valueFunction.getValue(pointsSoFar + 1)] += oneSixth
                    if (Side.TWO !in usedSides) probability[valueFunction.getValue(pointsSoFar + 2)] += oneSixth
                    if (Side.THREE !in usedSides) probability[valueFunction.getValue(pointsSoFar + 3)] += oneSixth
                    if (Side.FOUR !in usedSides) probability[valueFunction.getValue(pointsSoFar + 4)] += oneSixth
                    if (Side.FIVE !in usedSides) probability[valueFunction.getValue(pointsSoFar + 5)] += oneSixth
                } else {
                    probability[0] = 5.0 / 6
                    probability[valueFunction.getValue(pointsSoFar + Side.WORM.value)] += oneSixth
                }
            }
        }
    }
}