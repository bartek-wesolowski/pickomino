class Helpings(helpings: Collection<Int>? = null) {

    private val available = if (helpings != null) {
        Array(41) { index -> index in helpings }
    } else {
        Array(41) { index -> index in 21..36 }
    }
    private var size = helpings?.size ?: 16

    fun isNotEmpty(): Boolean = size > 0

    fun add(points: Int) {
        require(!available[points]) { "helping is already added" }
        available[points] = true
        size += 1
    }

    fun remove(points: Int) {
        require(available[points]) { "helping is already removed" }
        available[points] = false
        size -= 1
    }

    fun getSmallest(): Int {
        val smallest = available.indexOf(true)
        require(smallest != -1) { "no helpings available" }
        return smallest
    }

    fun getBiggest(): Int {
        val biggest = available.lastIndexOf(true)
        require(biggest != -1) { "no helpings available" }
        return biggest
    }

    fun getWorms(points: Int): Int {
        val smallest = getSmallest()
        if (points < smallest) return 0
        for (p in points downTo smallest) {
            if (available[p]) return Helping.getWorms(p)
        }
        throw IllegalStateException("no helpings available")
    }

    fun getWormsExact(points: Int): Int {
        return if (available[points]) Helping.getWorms(points) else 0
    }

    fun getHelpingPoints(points: Int): Int {
        val smallest = getSmallest()
        if (points < smallest) return 0
        for (p in points downTo smallest) {
            if (available[p]) return p
        }
        throw IllegalStateException("no helpings available")
    }

    operator fun contains(points: Int): Boolean = available[points]

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("{")
        var first = true
        for (index in 21..36) {
            if (available[index]) {
                if (!first) {
                    sb.append(", ")
                } else {
                    first = false
                }
                sb.append(index)
            }
        }
        sb.append("}")
        return sb.toString()
    }

    companion object {
        fun none() = Helpings(emptyList())
        fun all() = Helpings(null)
        fun of(vararg helping: Int) = Helpings(helping.toList())
    }
}