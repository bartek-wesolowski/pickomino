import java.util.EnumSet

data object SimpleStrategy : Strategy {
    override fun shouldContinue(
        dyeCount: Int,
        usedSides: EnumSet<Side>,
        pointsSoFar: Int,
        availableHelpings: HelpingCollection,
        topHelping: Int?,
        opponentTopHelpings: HelpingCollection
    ): Boolean {
        return Side.WORM !in usedSides || (pointsSoFar < availableHelpings.getSmallest() && pointsSoFar !in opponentTopHelpings)
    }

    override fun chooseSymbol(
        roll: List<Side>,
        usedSides: EnumSet<Side>,
        pointsSoFar: Int,
        availableHelpings: HelpingCollection,
        topHelping: Int?,
        opponentTopHelpings: HelpingCollection
    ): Side? {
        if (Side.WORM in roll && Side.WORM !in usedSides) return Side.WORM
        val sidesSortedByValue = roll.distinct().sortedBy { -it.value }
        for (side in sidesSortedByValue) {
            if (side !in usedSides) return side
        }
        return null
    }
}