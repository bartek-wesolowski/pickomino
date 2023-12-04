package com.bartoszwesolowski.pickomino.model

interface ResultDistribution : Iterable<Map.Entry<Int, Double>> {
    operator fun get(value: Int): Double
    fun getExpectedValue(): Double
}