package com.bartoszwesolowski.pickomino.model

/**
 * A distribution of results of a game.
 * It's essentially a map, but there are different implementations for performance reasons.
 */
interface ResultDistribution : Iterable<Map.Entry<Int, Double>> {
    operator fun get(value: Int): Double
    fun getExpectedValue(): Double
}