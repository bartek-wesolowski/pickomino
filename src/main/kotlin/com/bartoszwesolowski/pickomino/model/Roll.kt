package com.bartoszwesolowski.pickomino.model

import kotlin.math.pow
import kotlin.random.Random

/** A roll of dice. */
class Roll(
    val dyeCount: Int,
    private val countArray: IntArray
) {
    val sides: Sequence<Side> = sequence {
        for (side in Side.entries) {
            if (countArray[side.ordinal] > 0) yield(side)
        }
    }

    operator fun get(side: Side): Int = countArray[side.ordinal]

    operator fun contains(side: Side): Boolean = countArray[side.ordinal] > 0

    fun probability(): Double {
        var probability =  factorial[dyeCount] / Side.entries.count().toDouble().pow(dyeCount)
        for (side in Side.entries) {
            val count = countArray[side.ordinal]
            if (count > 0) {
                probability /= factorial[count]
            }
        }
        return probability
    }

    override fun toString(): String {
        return "{" + sides.joinToString(separator = ", ") { side -> "$side: ${this[side]}" } + "}"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Roll

        if (dyeCount != other.dyeCount) return false
        if (!countArray.contentEquals(other.countArray)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = dyeCount
        result = 31 * result + countArray.contentHashCode()
        return result
    }

    companion object {
        fun of(vararg sideCount: Pair<Side, Int>): Roll {
            val roll = IntArray(Side.entries.size)
            for ((side, count) in sideCount) {
                roll[side.ordinal] = count
            }
            return Roll(
                dyeCount = sideCount.sumOf { it.second },
                countArray = roll
            )
        }

        fun random(dyeCount: Int): Roll {
            val roll = IntArray(Side.entries.size)
            repeat(dyeCount) {
                roll[randomSide().ordinal] += 1
            }
            return Roll(dyeCount, roll)
        }

        private fun randomSide() = Side.entries[Random.nextInt(0, Side.entries.size)]

        fun generateAll(dieCount: Int): Sequence<Roll> = sequence {
            yieldAll(generateAll(dieCount, dieCount, 0, IntArray(Side.entries.size)))
        }

        private fun generateAll(
            dieCount: Int,
            remainingDieCount: Int,
            sideIndex: Int,
            roll: IntArray
        ): Sequence<Roll> = sequence {
            if (remainingDieCount == 0 || sideIndex > Side.entries.lastIndex) {
                if (remainingDieCount == 0 && roll.isNotEmpty()) {
                    yield(Roll(dieCount, roll))
                }
            } else {
                val side = Side.entries[sideIndex]
                for (count in remainingDieCount downTo 0) {
                    if (count > 0) {
                        yieldAll(
                            generateAll(
                                dieCount,
                                remainingDieCount - count,
                                sideIndex + 1,
                                roll.clone().apply { this[side.ordinal] = count }
                            )
                        )
                    } else {
                        yieldAll(generateAll(dieCount, remainingDieCount, sideIndex + 1, roll))
                    }
                }
            }
        }

        private val factorial = arrayOf(
            1,
            1,
            2,
            6,
            24,
            120,
            720,
            5040,
            40320
        )
    }
}