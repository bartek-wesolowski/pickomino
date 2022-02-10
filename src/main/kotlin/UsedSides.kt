class UsedSides {
    private val used: BooleanArray = BooleanArray(6)

    fun withUsed(side: Side): UsedSides {
        val newUsedSides = UsedSides()
        for (s in 0 until Side.values().size) {
            newUsedSides.used[s] = used[s] || s == side.ordinal
        }
        return newUsedSides
    }

    fun isUsed(side: Side): Boolean {
        return used[side.ordinal]
    }

    operator fun contains(side: Side): Boolean {
        return used[side.ordinal]
    }

    fun size(): Int {
        var s = 0
        for (b in used) {
            if (b) s++
        }
        return s
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UsedSides

        if (!used.contentEquals(other.used)) return false

        return true
    }

    override fun hashCode(): Int {
        return used.contentHashCode()
    }

    override fun toString(): String {
        val sb = StringBuilder("{")
        var first = true
        used.forEachIndexed { index, used ->
            if (used) {
                if (first) {
                    sb.append(Side.values()[index])
                    first = false
                } else {
                    sb.append(", ${Side.values()[index]}")
                }
            }
        }
        sb.append("}")
        return sb.toString()
    }
}