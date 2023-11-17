package com.bartoszwesolowski.pickomino

import com.bartoszwesolowski.pickomino.model.*
import com.bartoszwesolowski.pickomino.strategy.OptimalStrategy
import com.bartoszwesolowski.pickomino.util.ResultDistributionCalculator

fun main() {
    val resultDistributionCalculator = ResultDistributionCalculator(WormsFromAvailableHelpings)

    val roll = Roll.of(Side.TWO to 3, Side.THREE to 3, Side.FOUR to 1, Side.WORM to 1)

    val gameState = GameState(
        availableHelpings = HelpingCollection.all(),
        topHelping = null,
        opponentTopHelpings = HelpingCollection.empty()
    )
    val turnState = TurnState.initial()

    println("Result distribution")
    println(resultDistributionCalculator.getResultDistribution(gameState, turnState))
    println()

    val advice = resultDistributionCalculator.getResultDistributionsForAllChoices(gameState, turnState, roll)
    println("Advice")
    for ((side, resultDistribution) in advice) {
        print("$side worms: ")
        print("%.3f".format(resultDistribution.getExpectedValue()))
        print(" ")
        println(resultDistribution)
    }
    println()

    val strategy = OptimalStrategy()
    println("Side chosen: ${strategy.chooseSide(gameState, turnState, roll)}")
    println("Should continue: ${strategy.shouldContinue(gameState, turnState)}")
}