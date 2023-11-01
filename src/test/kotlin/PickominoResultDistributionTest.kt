import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.EnumSet
import java.util.stream.Stream

internal class PickominoResultDistributionTest {

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
        val actual = Pickomino(valueFunction).getResultDistribution(
            gameState = GameState(
                availableHelpings = availableHelpings,
                topHelping = topHelping,
                opponentTopHelpings = HelpingCollection.empty()
            ),
            dyeCount = dyeCount,
            usedSides = usedSides,
            pointsSoFar = pointsSoFar,
        )
        assertAll(
            (valueFunction.valueRange).map { value ->
                {
                    assertEquals(
                        resultDistribution[value],
                        actual[value],
                        epsilon,
                        "Expected P[$value]: ${resultDistribution[value]} but was: ${actual[value]}"
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
                            0 to 0.2322383576478062,
                            1 to 0.16763793705696214,
                            2 to 0.3631946313179833,
                            3 to 0.19701382156756783,
                            4 to 0.0399152524137451
                        )
                    )
                ),
                argumentsOf(
                    dyeCount = 8,
                    valueFunction = WormsFromAvailableHelpings,
                    resultDistribution = ArrayResultDistribution(
                        WormsFromAvailableHelpings,
                        listOf(
                            0 to 0.2322383576478062,
                            1 to 0.16763793705696214,
                            2 to 0.3631946313179833,
                            3 to 0.19701382156756783,
                            4 to 0.0399152524137451
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
                            0 to 0.8061207349999395,
                            3 to 0.13387599671521075,
                            4 to 0.06000326829412743
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
                            0 to 0.8061207349999395,
                            3 to 0.13387599671521075,
                            4 to 0.06000326829412743
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
                            -1 to 0.8060193325207861,
                            3 to 0.1342935351951597,
                            4 to 0.059687132281632624
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
                            -1 to 0.8060193325207861,
                            3 to 0.1342935351951597,
                            4 to 0.059687132281632624
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
                            -2 to 0.8060186854631007,
                            3 to 0.1342969707120195,
                            4 to 0.059684343822960916
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
                            -2 to 0.8060186854631007,
                            3 to 0.1342969707120195,
                            4 to 0.059684343822960916
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
                            -3 to 0.8060029048102788,
                            3 to 0.13439898586518664,
                            4 to 0.059598109315630435
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
                            -3 to 0.8060029048102788,
                            3 to 0.13439898586518664,
                            4 to 0.059598109315630435
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
                            0 to 0.08655269719808172,
                            11 to 4.621874520193569E-6,
                            12 to 2.5628979776412135E-6,
                            13 to 1.103253548170806E-4,
                            14 to 8.987180535584511E-4,
                            15 to 0.0020144285892626664,
                            16 to 0.006080250698130404,
                            17 to 0.016628104478529682,
                            18 to 0.014734997529290455,
                            19 to 0.016701129867793662,
                            20 to 0.04621881537641437,
                            21 to 0.061389915225378254,
                            22 to 0.102919592999264,
                            22 to 0.102919592999264,
                            23 to 0.10919133939221902,
                            24 to 0.14252561994341648,
                            25 to 0.07575118422430933,
                            26 to 0.06677756368356642,
                            27 to 0.061185854201904345,
                            28 to 0.06419825265404427,
                            29 to 0.05507356716816991,
                            30 to 0.026429804128920546,
                            31 to 0.016585546699087222,
                            32 to 0.009797527509283149,
                            33 to 0.010600216031228146,
                            34 to 0.002860913980455483,
                            35 to 0.0027755557389688925,
                            36 to 6.49496287373086E-4,
                            37 to 8.200807145687093E-4,
                            38 to 3.1808031294733454E-4,
                            39 to 9.366077573623695E-5,
                            40 to 1.0957641287864079E-4
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