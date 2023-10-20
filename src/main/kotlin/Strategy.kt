import java.util.EnumSet

interface Strategy {
    fun shouldContinue(
        dyeCount: Int,
        usedSides: EnumSet<Side>,
        pointsSoFar: Int,
        availableHelpings: HelpingSet,
        topHelping: Int?,
        opponentTopHelpings: HelpingSet
    ): Boolean

    fun chooseSymbol(
        roll: List<Side>,
        usedSides: EnumSet<Side>,
        pointsSoFar: Int,
        availableHelpings: HelpingSet,
        topHelping: Int?,
        opponentTopHelpings: HelpingSet
    ): Side?
}