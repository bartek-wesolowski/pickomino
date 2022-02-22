import kotlin.math.max

sealed class ValueFunction(val maxValue: Int) {
    abstract fun getValue(points: Int): Int

    object Worms : ValueFunction(maxValue = 4) {
        override fun getValue(points: Int): Int {
            return Helping.getWorms(points)
        }
    }

    class WormsFromAvailableHelpings(
        private val availableHelpings: Helpings,
        private val topHelping: Int?,
        private val opponentTopHelpings: Helpings
    ) : ValueFunction(maxValue = 4) {
        override fun getValue(points: Int): Int {
            val worms = max(availableHelpings.getWorms(points), opponentTopHelpings.getWormsExact(points))
            return if (worms > 0) {
                worms
            } else {
                if (topHelping != null) {
                    -Helping.getWorms(points)
                } else {
                    0
                }
            }
        }
    }

    object Points : ValueFunction(maxValue = 40) {
        override fun getValue(points: Int): Int = points
    }
}