fun combinations(length: Int): Sequence<List<Side>> = sequence {
    val combination = MutableList(length) { Side.ONE }
    yieldAll(combinations(length, combination))
}

private fun combinations(length: Int, combination: MutableList<Side>): Sequence<List<Side>> = sequence {
    if (length == 0) {
        yield(combination.toList())
    } else {
        for (side in Side.values()) {
            combination[combination.size - length] = side
            yieldAll(combinations(length - 1, combination))
        }
    }
}