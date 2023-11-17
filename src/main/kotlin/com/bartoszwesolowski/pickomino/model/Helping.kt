package com.bartoszwesolowski.pickomino.model

@JvmInline
value class Helping private constructor(val points: Int) {
    init {
        require(points >= 21) { "points" }
        require(points <= 36)
    }

    fun getWorms(): Int = when (points) {
        21, 22, 23, 24 -> 1
        25, 26, 27, 28 -> 2
        29, 30, 31, 32 -> 3
        33, 34, 35, 36 -> 4
        else -> error("Unexpected points: $points")
    }

    override fun toString(): String = points.toString()

    companion object {
        fun fromPoints(points: Int): Helping? {
            return if (points in 21..36) {
                Helping(points)
            } else {
                null
            }
        }
    }
}