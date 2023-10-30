import kotlin.math.max

sealed class ValueFunction(val valueRange: IntRange) {
    abstract fun getValue(gameState: GameState, points: Int): Int

    data object Worms : ValueFunction(0..4) {
        override fun getValue(gameState: GameState, points: Int): Int {
            if (points > 36) return 4
            return Helping.fromPoints(points)?.getWorms() ?: 0
        }
    }

    data object WormsFromAvailableHelpings : ValueFunction(-4..4) {
        override fun getValue(gameState: GameState, points: Int): Int {
            val worms = max(
                gameState.availableHelpings.getExactOrSmaller(points)?.getWorms() ?: 0,
                gameState.opponentTopHelpings.getOrNull(points)?.getWorms() ?: 0
            )
            return if (worms > 0) {
                worms
            } else {
                if (gameState.topHelping != null) {
                    -gameState.topHelping.getWorms()
                } else {
                    0
                }
            }
        }
    }

    data object Points : ValueFunction(0..40) {
        override fun getValue(gameState: GameState, points: Int): Int = points
    }
}