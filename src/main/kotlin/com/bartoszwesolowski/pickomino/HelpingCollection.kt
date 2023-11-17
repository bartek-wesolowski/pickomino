package com.bartoszwesolowski.pickomino

import java.util.*

class HelpingCollection private constructor() {
    private var helpings = BitSet(16)

    fun isNotEmpty(): Boolean = !helpings.isEmpty

    fun add(helping: Helping) {
        addByPoints(helping.points)
    }

    private fun addByPoints(points: Int) {
        require(!helpings[points - 21]) { "helping is already added" }
        helpings[points - 21] = true
    }

    fun remove(helping: Helping) {
        require(helpings[helping.points - 21]) { "helping is already removed" }
        helpings[helping.points - 21] = false
    }

    fun getSmallest(): Helping {
        val helpingIndex = helpings.nextSetBit(0)
        require(helpingIndex != -1) { "no helpings available" }
        return Helping.fromPoints(helpingIndex + 21)!!
    }

    fun getBiggest(): Helping {
        val helpingIndex = helpings.previousSetBit(16)
        require(helpingIndex != -1) { "no helpings available" }
        return Helping.fromPoints(helpingIndex + 21)!!
    }

    fun getOrNull(points: Int): Helping? {
        if (points < 21) return null
        if (helpings[points - 21]) return Helping.fromPoints(points)
        return null
    }

    fun getExactOrSmaller(points: Int): Helping? {
        if (points < 21) return null
        val helpingIndex = helpings.previousSetBit(points - 21)
        return if (helpingIndex >= 0) {
            Helping.fromPoints(helpingIndex + 21)
        } else {
            null
        }
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
        fun empty() = HelpingCollection()
        fun all() = HelpingCollection().apply {
            for (points in 21..36) {
                addByPoints(points)
            }
        }
        fun fromPoints(vararg pointValues: Int) = HelpingCollection().apply {
            for (points in pointValues) {
                addByPoints(points)
            }
        }
        fun fromHelpings(helpings: Iterable<Helping>) = HelpingCollection().apply {
            for (helping in helpings) {
                add(helping)
            }
        }
    }
}