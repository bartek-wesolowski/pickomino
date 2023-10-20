import java.util.EnumSet

interface Strategy {
    fun shouldContinue(
        dyeCount: Int,
        usedSides: EnumSet<Side>,
        pointsSoFar: Int,
        availableHelpings: HelpingCollection,
        topHelping: Int?,
        opponentTopHelpings: HelpingCollection
    ): Boolean

    fun chooseSymbol(
        roll: List<Side>,
        usedSides: EnumSet<Side>,
        pointsSoFar: Int,
        availableHelpings: HelpingCollection,
        topHelping: Int?,
        opponentTopHelpings: HelpingCollection
    ): Side?
}