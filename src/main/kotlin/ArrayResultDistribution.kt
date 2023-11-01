import java.text.DecimalFormat
import java.util.AbstractMap.SimpleEntry

private val percentageFormat by lazy { DecimalFormat("#,##0.00'%'") }

class ArrayResultDistribution<V: ValueFunction>(private val valueFunction: V): ResultDistribution {
    // count() is much slower then doing the manual calculation
    private val size = valueFunction.valueRange.last - valueFunction.valueRange.first + 1
    private val probability: DoubleArray = DoubleArray(size)

    override operator fun get(value: Int): Double = probability[value - valueFunction.valueRange.first]

    override fun getExpectedValue(): Double {
        var expectedValue = 0.0
        for (value in valueFunction.valueRange) {
            expectedValue += value * probability[value - valueFunction.valueRange.first]
        }
        return expectedValue
    }

    override fun iterator(): Iterator<Map.Entry<Int, Double>> {
        return object: Iterator<Map.Entry<Int, Double>> {
            private var index = -1
            init {
                findNextIndex()
            }
            override fun hasNext() = index <= probability.lastIndex
            override fun next(): Map.Entry<Int, Double> {
                val element = SimpleEntry(index + valueFunction.valueRange.first, probability[index])
                findNextIndex()
                return element
            }
            private fun findNextIndex() {
                do {
                    index++
                } while (index < size && probability[index] == 0.0)
            }
        }
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

    fun merge(other: ResultDistribution, scale: Double) {
        for ((value, p) in other) {
            probability[value - valueFunction.valueRange.first] += p * scale
        }
    }

    operator fun set(value: Int, p: Double) {
        probability[value - valueFunction.valueRange.first] = p
    }

    override fun toString(): String {
        val items = probability
            .mapIndexed { index, value -> index + valueFunction.valueRange.first to value }
            .filter { it.second != 0.0 }
            .joinToString { it.first.toString() + ": " + percentageFormat.format(it.second * 100) }
        return "{ $items }"
    }
}