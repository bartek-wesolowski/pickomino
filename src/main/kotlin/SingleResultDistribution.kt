import java.util.AbstractMap.SimpleEntry

@JvmInline
value class SingleResultDistribution(
    private val value: Int
): ResultDistribution {
    override fun get(value: Int) = if (value == this.value) 1.0 else 0.0
    override fun getExpectedValue() = value.toDouble()
    override fun iterator(): Iterator<Map.Entry<Int, Double>> = object: Iterator<Map.Entry<Int, Double>> {
        private var isValueReturned = false
        override fun hasNext() = !isValueReturned
        override fun next(): Map.Entry<Int, Double> {
            if (!isValueReturned) {
                isValueReturned = true
                return SimpleEntry(value, 1.0)
            }
            error("No more elements")
        }
    }

    override fun toString() = "{ $value: 1.0 }"
}