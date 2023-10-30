import java.util.EnumSet

interface Strategy {
    fun shouldContinue(
        gameState: GameState,
        dyeCount: Int,
        usedSides: EnumSet<Side>,
        pointsSoFar: Int,
    ): Boolean

    fun chooseSymbol(
        gameState: GameState,
        roll: List<Side>,
        usedSides: EnumSet<Side>,
        pointsSoFar: Int,
    ): Side?
}