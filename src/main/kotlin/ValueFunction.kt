import kotlin.math.max

sealed class ValueFunction(val valueRange: IntRange) {
    abstract fun getValue(
        points: Int,
        availableHelpings: HelpingCollection,
        topHelping: Helping?,
        opponentTopHelpings: HelpingCollection
    ): Int

    data object Worms : ValueFunction(0..4) {
        override fun getValue(
            points: Int,
            availableHelpings: HelpingCollection,
            topHelping: Helping?,
            opponentTopHelpings: HelpingCollection
        ): Int {
            if (points > 36) return 4
            return Helping.fromPoints(points)?.getWorms() ?: 0
        }
    }

    data object WormsFromAvailableHelpings : ValueFunction(-4..4) {
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

    data object Points : ValueFunction(0..40) {
        override fun getValue(
            points: Int,
            availableHelpings: HelpingCollection,
            topHelping: Helping?,
            opponentTopHelpings: HelpingCollection
        ): Int = points
    }
}