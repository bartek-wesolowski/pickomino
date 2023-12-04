package com.bartoszwesolowski.pickomino

import com.bartoszwesolowski.pickomino.model.*
import com.bartoszwesolowski.pickomino.strategy.ValueFunctionMaximizingStrategy
import com.bartoszwesolowski.pickomino.util.ResultDistributionCalculator
import com.bartoszwesolowski.pickomino.valuefunction.WormsFromAvailableHelpings

fun main() {
    val resultDistributionCalculator = ResultDistributionCalculator(WormsFromAvailableHelpings)

    val roll = Roll.of(Side.TWO to 3, Side.THREE to 3, Side.FOUR to 1, Side.WORM to 1)

    val gameState = GameState(
        availableHelpings = HelpingCollection.all(),
        topHelping = null,
        opponentTopHelpings = HelpingCollection.empty()
    )
    val turnState = TurnState.initial()

    // Calculate probabilities of all possible results
    println("Result distribution")
    println(resultDistributionCalculator.getResultDistribution(gameState, turnState))
    println()

    // Print expected values of all possible choices
    val advice = resultDistributionCalculator.getResultDistributionsForAllChoices(gameState, turnState, roll)
    println("Advice")
    for ((side, resultDistribution) in advice) {
        print("$side worms: ")
        print("%.3f".format(resultDistribution.getExpectedValue()))
        print(" ")
        println(resultDistribution)
    }
    println()

    // Print the choice of the specified strategy
    val strategy = ValueFunctionMaximizingStrategy(WormsFromAvailableHelpings)
    println("Side chosen: ${strategy.chooseSide(gameState, turnState, roll)}")
    println("Should continue: ${strategy.shouldContinue(gameState, turnState)}")
}