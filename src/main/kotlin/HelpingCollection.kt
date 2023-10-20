import java.util.*

class HelpingCollection(helpingsToAdd: Collection<Int>? = null) {

    private var helpings = BitSet(16)

    init {
        if (helpingsToAdd != null) {
            helpingsToAdd.forEach { helpings.set(it - 21) }
        } else {
            repeat(16) {helpings.set(it) }
        }
    }

    fun isNotEmpty(): Boolean = !helpings.isEmpty

    fun add(helping: Int) {
        require(helping >= 21) { "points < 21" }
        require(helping <= 36) { "points > 36" }
        require(!helpings[helping - 21]) { "helping is already added" }
        helpings[helping - 21] = true
    }

    fun remove(helping: Int) {
        require(helpings[helping - 21]) { "helping is already removed" }
        helpings[helping - 21] = false
    }

    fun getSmallest(): Int {
        val smallest = helpings.nextSetBit(0)
        require(smallest != -1) { "no helpings available" }
        return smallest + 21
    }

    fun getBiggest(): Int {
        val biggest = helpings.previousSetBit(16)
        require(biggest != -1) { "no helpings available" }
        return biggest + 21
    }

    fun getWorms(points: Int): Int {
        val helping = helpings.previousSetBit(points)
        require(helping != -1)
        return Helping.getWorms(helping + 21)
    }

    fun getWormsExact(points: Int): Int {
        if (points < 21) return 0
        return if (helpings[points - 21]) Helping.getWorms(points) else 0
    }

    fun getHelpingPoints(points: Int): Int {
        val helping = helpings.previousSetBit(points)
        require(helping != -1)
        return helping + 21
    }

    operator fun contains(points: Int): Boolean {
        return if (points >= 21) {
            helpings[points - 21]
        } else {
            false
        }
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("[")
        var helping = helpings.nextSetBit(0)
        var first = true
        while (helping != -1) {
            if (!first) {
                sb.append(", ")
            } else {
                first = false
            }
            sb.append(helping + 21)
            helping = helpings.nextSetBit(helping + 1)
        }
        sb.append("]")
        return sb.toString()
    }

    companion object {
        fun none() = HelpingCollection(emptyList())
        fun all() = HelpingCollection(null)
        fun of(vararg helping: Int) = HelpingCollection(helping.toList())
    }
}