import java.util.EnumSet

interface Strategy {
    fun shouldContinue(
        gameState: GameState,
        dyeCount: Int,
        usedSides: EnumSet<Side>,
        pointsSoFar: Int,
    ): Boolean

    fun chooseSide(
        gameState: GameState,
        roll: Roll,
        usedSides: EnumSet<Side>,
        pointsSoFar: Int,
    ): Side?
}