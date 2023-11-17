package com.bartoszwesolowski.pickomino

import com.bartoszwesolowski.pickomino.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.EnumSet
import java.util.stream.Stream

internal class ResultDistributionCalculatorResultDistributionTest {

    private val epsilon = 0.000000000000001

    @ParameterizedTest
    @MethodSource("getParameters")
    fun <V : ValueFunction> test(
        dyeCount: Int,
        valueFunction: V,
        usedSides: EnumSet<Side>,
        pointsSoFar: Int,
        availableHelpings: HelpingCollection,
        topHelping: Helping?,
        resultDistribution: ResultDistribution
    ) {
        val actual = ResultDistributionCalculator(valueFunction).getResultDistribution(
            gameState = GameState(
                availableHelpings = availableHelpings,
                topHelping = topHelping,
                opponentTopHelpings = HelpingCollection.empty()
            ),
            turnState = TurnState(
                dyeCount = dyeCount,
                usedSides = usedSides,
                pointsSoFar = pointsSoFar
            )
        )
        assertAll(
            (valueFunction.valueRange).map { value ->
                {
                    assertEquals(
                        resultDistribution[value],
                        actual[value],
                        epsilon,
                        "Expected P[$value]"
                    )
                }
            }
        )
    }

    companion object {
        @JvmStatic
        fun getParameters(): Stream<Arguments> {
            return Stream.of(
                argumentsOf(
                    dyeCount = 1,
                    valueFunction = Points,
                    resultDistribution = ArrayResultDistribution(
                        Points,
                        listOf(
                            0 to 5.0 / 6,
                            5 to 1.0 / 6
                        )
                    )
                ),
                argumentsOf(
                    dyeCount = 1,
                    valueFunction = Points,
                    usedSides = EnumSet.of(Side.WORM),
                    pointsSoFar = 5,
                    resultDistribution = ArrayResultDistribution(
                        Points,
                        listOf(
                            0 to 1.0 / 6,
                            6 to 1.0 / 6,
                            7 to 1.0 / 6,
                            8 to 1.0 / 6,
                            9 to 1.0 / 6,
                            10 to 1.0 / 6
                        )
                    )
                ),
                argumentsOf(
                    dyeCount = 2,
                    valueFunction = Points,
                    resultDistribution = ArrayResultDistribution(
                        Points,
                        listOf(
                            0 to 140.0 / 216,
                            6 to 10.0 / 216,
                            7 to 12.0 / 216,
                            8 to 14.0 / 216,
                            9 to 16.0 / 216,
                            10 to 24.0 / 216
                        )
                    )
                ),
                argumentsOf(
                    dyeCount = 2,
                    valueFunction = Points,
                    usedSides = EnumSet.of(Side.WORM),
                    pointsSoFar = 5,
                    resultDistribution = ArrayResultDistribution(
                        Points,
                        listOf(
                            0 to 10.0 / 216,
                            7 to 30.0 / 216,
                            8 to 38.0 / 216,
                            9 to 56.0 / 216,
                            10 to 62.0 / 216,
                            11 to 8.0 / 216,
                            13 to 6.0 / 216,
                            15 to 6.0 / 216
                        )
                    )
                ),
                // snapshot tests
                argumentsOf(
                    dyeCount = 8,
                    valueFunction = Worms,
                    resultDistribution = ArrayResultDistribution(
                        Worms,
                        listOf(
                            0 to 0.23223185528387308,
                            1 to 0.16763805751119396,
                            2 to 0.3631946029658429,
                            3 to 0.1970395263591341,
                            4 to 0.03989595787995435
                        )
                    )
                ),
                argumentsOf(
                    dyeCount = 8,
                    valueFunction = WormsFromAvailableHelpings,
                    resultDistribution = ArrayResultDistribution(
                        WormsFromAvailableHelpings,
                        listOf(
                            0 to 0.23223185528387308,
                            1 to 0.16763805751119396,
                            2 to 0.3631946029658429,
                            3 to 0.1970395263591341,
                            4 to 0.03989595787995435
                        )
                    )
                ),
                argumentsOf(
                    dyeCount = 8,
                    valueFunction = WormsFromAvailableHelpings,
                    availableHelpings = HelpingCollection.fromPoints(31, 32, 33, 34),
                    resultDistribution = ArrayResultDistribution(
                        WormsFromAvailableHelpings,
                        listOf(
                            0 to 0.806094687995294,
                            3 to 0.13398018470072579,
                            4 to 0.05992512730398163
                        )
                    )
                ),
                argumentsOf(
                    dyeCount = 8,
                    valueFunction = WormsFromAvailableHelpings,
                    availableHelpings = HelpingCollection.fromPoints(31, 33),
                    resultDistribution = ArrayResultDistribution(
                        WormsFromAvailableHelpings,
                        listOf(
                            0 to 0.806094687995294,
                            3 to 0.13398018470072579,
                            4 to 0.05992512730398163
                        )
                    )
                ),
                argumentsOf(
                    dyeCount = 8,
                    valueFunction = WormsFromAvailableHelpings,
                    availableHelpings = HelpingCollection.fromPoints(31, 32, 33, 34),
                    topHelping = Helping.fromPoints(21),
                    resultDistribution = ArrayResultDistribution(
                        WormsFromAvailableHelpings,
                        listOf(
                            -1 to 0.8060193325223208,
                            3 to 0.1342935351960034,
                            4 to 0.0596871322816763
                        )
                    )
                ),
                argumentsOf(
                    dyeCount = 8,
                    valueFunction = WormsFromAvailableHelpings,
                    availableHelpings = HelpingCollection.fromPoints(31, 33),
                    topHelping = Helping.fromPoints(21),
                    resultDistribution = ArrayResultDistribution(
                        WormsFromAvailableHelpings,
                        listOf(
                            -1 to 0.8060193325223208,
                            3 to 0.1342935351960034,
                            4 to 0.0596871322816763
                        )
                    )
                ),
                argumentsOf(
                    dyeCount = 8,
                    valueFunction = WormsFromAvailableHelpings,
                    availableHelpings = HelpingCollection.fromPoints(31, 32, 33, 34),
                    topHelping = Helping.fromPoints(25),
                    resultDistribution = ArrayResultDistribution(
                        WormsFromAvailableHelpings,
                        listOf(
                            -2 to 0.8060186854640207,
                            3 to 0.13429697071299848,
                            4 to 0.059684343822981434
                        )
                    )
                ),
                argumentsOf(
                    dyeCount = 8,
                    valueFunction = WormsFromAvailableHelpings,
                    availableHelpings = HelpingCollection.fromPoints(31, 33),
                    topHelping = Helping.fromPoints(25),
                    resultDistribution = ArrayResultDistribution(
                        WormsFromAvailableHelpings,
                        listOf(
                            -2 to 0.8060186854640207,
                            3 to 0.13429697071299848,
                            4 to 0.059684343822981434
                        )
                    )
                ),
                argumentsOf(
                    dyeCount = 8,
                    valueFunction = WormsFromAvailableHelpings,
                    availableHelpings = HelpingCollection.fromPoints(31, 32, 33, 34),
                    topHelping = Helping.fromPoints(29),
                    resultDistribution = ArrayResultDistribution(
                        WormsFromAvailableHelpings,
                        listOf(
                            -3 to 0.806002903827932,
                            3 to 0.13439899280184206,
                            4 to 0.05959810337022896
                        )
                    )
                ),
                argumentsOf(
                    dyeCount = 8,
                    valueFunction = WormsFromAvailableHelpings,
                    availableHelpings = HelpingCollection.fromPoints(31, 33),
                    topHelping = Helping.fromPoints(29),
                    resultDistribution = ArrayResultDistribution(
                        WormsFromAvailableHelpings,
                        listOf(
                            -3 to 0.806002903827932,
                            3 to 0.13439899280184206,
                            4 to 0.05959810337022896
                        )
                    )
                ),
                argumentsOf(
                    dyeCount = 8,
                    valueFunction = Points,
                    availableHelpings = HelpingCollection.fromPoints(31, 32, 33, 34),
                    topHelping = Helping.fromPoints(25),
                    resultDistribution = ArrayResultDistribution(
                        Points,
                        listOf(
                            0 to 0.08619829849860244,
                            11 to 4.621874520193569E-6,
                            12 to 2.5628979776412135E-6,
                            13 to 1.1034460918999309E-4,
                            14 to 8.98564018564324E-4,
                            15 to 0.0020144692373807232,
                            16 to 0.00608030204312011,
                            17 to 0.016628105191700163,
                            18 to 0.01473499752937039,
                            19 to 0.01670114056451229,
                            20 to 0.0462188153757707,
                            21 to 0.061389925921729746,
                            22 to 0.102919592998544,
                            22 to 0.102919592998544,
                            23 to 0.10919134010569487,
                            24 to 0.14252561994336826,
                            25 to 0.07575118422480492,
                            26 to 0.06677756439626718,
                            27 to 0.06118585420158376,
                            28 to 0.06738801423601287,
                            29 to 0.054719149214249564,
                            30 to 0.025809572710271206,
                            31 to 0.015876710792231193,
                            32 to 0.009708923020987281,
                            33 to 0.009714171147375467,
                            34 to 0.002772309492087072,
                            35 to 0.0027755557389654824,
                            36 to 6.494962873670299E-4,
                            37 to 8.200807145623424E-4,
                            38 to 2.2947582456963009E-4,
                            39 to 9.366077573727057E-5,
                            40 to 1.0957641288026388E-4
                        )
                    )
                ),
            )
        }

        private fun <V : ValueFunction> argumentsOf(
            dyeCount: Int,
            valueFunction: V,
            usedSides: EnumSet<Side> = EnumSet.noneOf(Side::class.java),
            pointsSoFar: Int = 0,
            availableHelpings: HelpingCollection = HelpingCollection.all(),
            topHelping: Helping? = null,
            resultDistribution: ResultDistribution
        ) = Arguments.of(
            dyeCount,
            valueFunction,
            usedSides,
            pointsSoFar,
            availableHelpings,
            topHelping,
            resultDistribution
        )
    }
}