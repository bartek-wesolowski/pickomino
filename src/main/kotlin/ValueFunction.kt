import kotlin.math.max

sealed class ValueFunction(val maxValue: Int) {
    abstract fun getValue(
        points: Int,
        availableHelpings: HelpingCollection,
        topHelping: Helping?,
        opponentTopHelpings: HelpingCollection
    ): Int

    data object Worms : ValueFunction(maxValue = 4) {
        override fun getValue(
            points: Int,
            availableHelpings: HelpingCollection,
            topHelping: Helping?,
            opponentTopHelpings: HelpingCollection
        ): Int = Helping.fromPoints(points)?.getWorms() ?: 0
    }

    data object WormsFromAvailableHelpings : ValueFunction(maxValue = 4) {
        override fun getValue(
            points: Int,
            availableHelpings: HelpingCollection,
            topHelping: Helping?,
            opponentTopHelpings: HelpingCollection
        ): Int {
            val worms = max(
                availableHelpings.getExactOrSmaller(points)?.getWorms() ?: 0,
                opponentTopHelpings.getOrNull(points)?.getWorms() ?: 0
            )
            return if (worms > 0) {
                worms
            } else {
                if (topHelping != null) {
                    -topHelping.getWorms()
                } else {
                    0
                }
            }
        }
    }

    data object Points : ValueFunction(maxValue = 40) {
        override fun getValue(
            points: Int,
            availableHelpings: HelpingCollection,
            topHelping: Helping?,
            opponentTopHelpings: HelpingCollection
        ): Int = points
    }
}