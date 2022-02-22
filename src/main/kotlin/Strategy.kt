import java.util.EnumSet

interface Strategy {
    fun shouldContinue(
        dyeCount: Int,
        usedSides: EnumSet<Side>,
        pointsSoFar: Int,
        availableHelpings: Helpings,
        topHelping: Int?,
        opponentTopHelpings: Helpings
    ): Boolean

    fun chooseSymbol(
        roll: List<Side>,
        usedSides: EnumSet<Side>,
        pointsSoFar: Int,
        availableHelpings: Helpings,
        topHelping: Int?,
        opponentTopHelpings: Helpings
    ): Side?
}