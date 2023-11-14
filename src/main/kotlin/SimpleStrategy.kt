import java.util.EnumSet

data object SimpleStrategy : Strategy {
    override fun shouldContinue(
        gameState: GameState,
        dyeCount: Int,
        usedSides: EnumSet<Side>,
        pointsSoFar: Int,
    ): Boolean {
        return Side.WORM !in usedSides || (pointsSoFar < gameState.availableHelpings.getSmallest().points && pointsSoFar !in gameState.opponentTopHelpings)
    }

    override fun chooseSide(
        gameState: GameState,
        roll: Roll,
        usedSides: EnumSet<Side>,
        pointsSoFar: Int,
    ): Side? {
        if (Side.WORM in roll && Side.WORM !in usedSides) return Side.WORM
        val sidesSortedByValue = roll.sides.sortedBy { -it.value }
        for (side in sidesSortedByValue) {
            if (side !in usedSides) return side
        }
        return null
    }
}