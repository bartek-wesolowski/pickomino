import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.EnumSet
import java.util.stream.Stream

internal class PickominoResultDistributionTest {

    private val pickomino = Pickomino()
    private val epsilon = 0.000000000000001

    @ParameterizedTest()
    @MethodSource("getParameters")
    fun test(
        dyeCount: Int,
        valueFunction: ValueFunction,
        usedSides: EnumSet<Side>,
        pointsSoFar: Int,
        availableHelpings: HelpingCollection,
        topHelping: Helping?,
        resultDistribution: ResultDistribution
    ) {
        val actual = pickomino.getResultDistribution(
            dyeCount = dyeCount,
            usedSides = usedSides,
            pointsSoFar = pointsSoFar,
            availableHelpings = availableHelpings,
            topHelping = topHelping,
            opponentTopHelpings = HelpingCollection.empty(),
            valueFunction = valueFunction
        )
        assertEquals(resultDistribution.maxValue, actual.maxValue)
        assertAll(
            (-resultDistribution.maxValue..resultDistribution.maxValue).map { value ->
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
                    valueFunction = ValueFunction.Points,
                    resultDistribution = ResultDistribution(ValueFunction.Points.maxValue).apply {
                        this[0] = 5.0 / 6
                        this[5] = 1.0 / 6
                    }
                ),
                argumentsOf(
                    dyeCount = 1,
                    valueFunction = ValueFunction.Points,
                    usedSides = EnumSet.of(Side.WORM),
                    pointsSoFar = 5,
                    resultDistribution = ResultDistribution(ValueFunction.Points.maxValue).apply {
                        this[0] = 1.0 / 6
                        this[6] = 1.0 / 6
                        this[7] = 1.0 / 6
                        this[8] = 1.0 / 6
                        this[9] = 1.0 / 6
                        this[10] = 1.0 / 6
                    }
                ),
                argumentsOf(
                    dyeCount = 2,
                    valueFunction = ValueFunction.Points,
                    resultDistribution = ResultDistribution(ValueFunction.Points.maxValue).apply {
                        this[0] = 140.0 / 216
                        this[6] = 10.0 / 216
                        this[7] = 12.0 / 216
                        this[8] = 14.0 / 216
                        this[9] = 16.0 / 216
                        this[10] = 24.0 / 216
                    }
                ),
                argumentsOf(
                    dyeCount = 2,
                    valueFunction = ValueFunction.Points,
                    usedSides = EnumSet.of(Side.WORM),
                    pointsSoFar = 5,
                    resultDistribution = ResultDistribution(ValueFunction.Points.maxValue).apply {
                        this[0] = 10.0 / 216
                        this[7] = 30.0 / 216
                        this[8] = 38.0 / 216
                        this[9] = 56.0 / 216
                        this[10] = 62.0 / 216
                        this[11] = 8.0 / 216
                        this[13] = 6.0 / 216
                        this[15] = 6.0 / 216
                    }
                ),
                // snapshot tests
                argumentsOf(
                    dyeCount = 8,
                    valueFunction = ValueFunction.Worms,
                    resultDistribution = ResultDistribution(ValueFunction.Worms.maxValue).apply {
                        this[0] = 0.2322383576478062
                        this[1] = 0.16763793705696214
                        this[2] = 0.3631946313179833
                        this[3] = 0.19701382156756783
                        this[4] = 0.0399152524137451
                    }
                ),
                argumentsOf(
                    dyeCount = 8,
                    valueFunction = ValueFunction.WormsFromAvailableHelpings,
                    resultDistribution = ResultDistribution(ValueFunction.WormsFromAvailableHelpings.maxValue).apply {
                        this[0] = 0.2322383576478062
                        this[1] = 0.16763793705696214
                        this[2] = 0.3631946313179833
                        this[3] = 0.19701382156756783
                        this[4] = 0.0399152524137451
                    }
                ),
                argumentsOf(
                    dyeCount = 8,
                    valueFunction = ValueFunction.WormsFromAvailableHelpings,
                    availableHelpings = HelpingCollection.fromPoints(31, 32, 33, 34),
                    resultDistribution = ResultDistribution(ValueFunction.WormsFromAvailableHelpings.maxValue).apply {
                        this[0] = 0.8061207349999395
                        this[3] = 0.13387599671521075
                        this[4] = 0.06000326829412743
                    }
                ),
                argumentsOf(
                    dyeCount = 8,
                    valueFunction = ValueFunction.WormsFromAvailableHelpings,
                    availableHelpings = HelpingCollection.fromPoints(31, 33),
                    resultDistribution = ResultDistribution(ValueFunction.WormsFromAvailableHelpings.maxValue).apply {
                        this[0] = 0.8061207349999395
                        this[3] = 0.13387599671521075
                        this[4] = 0.06000326829412743
                    }
                ),
                argumentsOf(
                    dyeCount = 8,
                    valueFunction = ValueFunction.WormsFromAvailableHelpings,
                    availableHelpings = HelpingCollection.fromPoints(31, 32, 33, 34),
                    topHelping = Helping.fromPoints(21),
                    resultDistribution = ResultDistribution(ValueFunction.WormsFromAvailableHelpings.maxValue).apply {
                        this[-1] = 0.9661285085070774
                        this[3] = 0.01846505395940725
                        this[4] = 0.015406437518721379
                    }
                ),
                argumentsOf(
                    dyeCount = 8,
                    valueFunction = ValueFunction.WormsFromAvailableHelpings,
                    availableHelpings = HelpingCollection.fromPoints(31, 33),
                    topHelping = Helping.fromPoints(21),
                    resultDistribution = ResultDistribution(ValueFunction.WormsFromAvailableHelpings.maxValue).apply {
                        this[-1] = 0.9661285085070774
                        this[3] = 0.01846505395940725
                        this[4] = 0.015406437518721379
                    }
                ),
                argumentsOf(
                    dyeCount = 8,
                    valueFunction = ValueFunction.WormsFromAvailableHelpings,
                    availableHelpings = HelpingCollection.fromPoints(31, 32, 33, 34),
                    topHelping = Helping.fromPoints(25),
                    resultDistribution = ResultDistribution(ValueFunction.WormsFromAvailableHelpings.maxValue).apply {
                        this[-2] = 0.9944862948223797
                        this[3] = 0.0019183054108233067
                        this[4] = 0.003595399750003831
                    }
                ),
                argumentsOf(
                    dyeCount = 8,
                    valueFunction = ValueFunction.WormsFromAvailableHelpings,
                    availableHelpings = HelpingCollection.fromPoints(31, 33),
                    topHelping = Helping.fromPoints(25),
                    resultDistribution = ResultDistribution(ValueFunction.WormsFromAvailableHelpings.maxValue).apply {
                        this[-2] = 0.9944862948223797
                        this[3] = 0.0019183054108233067
                        this[4] = 0.003595399750003831
                    }
                ),
                argumentsOf(
                    dyeCount = 8,
                    valueFunction = ValueFunction.WormsFromAvailableHelpings,
                    availableHelpings = HelpingCollection.fromPoints(31, 32, 33, 34),
                    topHelping = Helping.fromPoints(29),
                    resultDistribution = ResultDistribution(ValueFunction.WormsFromAvailableHelpings.maxValue).apply {
                        this[-3] = 0.9960748780919186
                        this[3] = 0.001516335960075063
                        this[4] = 0.0024087859313955662
                    }
                ),
                argumentsOf(
                    dyeCount = 8,
                    valueFunction = ValueFunction.WormsFromAvailableHelpings,
                    availableHelpings = HelpingCollection.fromPoints(31, 33),
                    topHelping = Helping.fromPoints(29),
                    resultDistribution = ResultDistribution(ValueFunction.WormsFromAvailableHelpings.maxValue).apply {
                        this[-3] = 0.9960748780919186
                        this[3] = 0.001516335960075063
                        this[4] = 0.0024087859313955662
                    }
                ),
                argumentsOf(
                    dyeCount = 8,
                    valueFunction = ValueFunction.Points,
                    availableHelpings = HelpingCollection.fromPoints(31, 32, 33, 34),
                    topHelping = Helping.fromPoints(25),
                    resultDistribution = ResultDistribution(ValueFunction.Points.maxValue).apply {
                        this[0] = 0.08655269719808172
                        this[11] = 4.621874520193569E-6
                        this[12] = 2.5628979776412135E-6
                        this[13] = 1.103253548170806E-4
                        this[14] = 8.987180535584511E-4
                        this[15] = 0.0020144285892626664
                        this[16] = 0.006080250698130404
                        this[17] = 0.016628104478529682
                        this[18] = 0.014734997529290455
                        this[19] = 0.016701129867793662
                        this[20] = 0.04621881537641437
                        this[21] = 0.061389915225378254
                        this[22] = 0.102919592999264
                        this[22] = 0.102919592999264
                        this[23] = 0.10919133939221902
                        this[24] = 0.14252561994341648
                        this[25] = 0.07575118422430933
                        this[26] = 0.06677756368356642
                        this[27] = 0.061185854201904345
                        this[28] = 0.06419825265404427
                        this[29] = 0.05507356716816991
                        this[30] = 0.026429804128920546
                        this[31] = 0.016585546699087222
                        this[32] = 0.009797527509283149
                        this[33] = 0.010600216031228146
                        this[34] = 0.002860913980455483
                        this[35] = 0.0027755557389688925
                        this[36] = 6.49496287373086E-4
                        this[37] = 8.200807145687093E-4
                        this[38] = 3.1808031294733454E-4
                        this[39] = 9.366077573623695E-5
                        this[40] = 1.0957641287864079E-4
                    }
                ),
            )
        }

        private fun argumentsOf(
            dyeCount: Int,
            valueFunction: ValueFunction,
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