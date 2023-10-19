import kotlin.math.max

sealed class ValueFunction(val maxValue: Int) {
    abstract fun getValue(points: Int, availableHelpings: Helpings, topHelping: Int?, opponentTopHelpings: Helpings): Int

    data object Worms : ValueFunction(maxValue = 4) {
        override fun getValue(points: Int, availableHelpings: Helpings, topHelping: Int?, opponentTopHelpings: Helpings): Int {
            return Helping.getWorms(points)
        }
    }

    data object WormsFromAvailableHelpings : ValueFunction(maxValue = 4) {
        override fun getValue(points: Int, availableHelpings: Helpings, topHelping: Int?, opponentTopHelpings: Helpings): Int {
            val worms = max(availableHelpings.getWorms(points), opponentTopHelpings.getWormsExact(points))
            return if (worms > 0) {
                worms
            } else {
                if (topHelping != null) {
                    -Helping.getWorms(topHelping)
                } else {
                    0
                }
            }
        }
    }

    data object Points : ValueFunction(maxValue = 40) {
        override fun getValue(points: Int, availableHelpings: Helpings, topHelping: Int?, opponentTopHelpings: Helpings): Int {
            return points
        }
    }
}