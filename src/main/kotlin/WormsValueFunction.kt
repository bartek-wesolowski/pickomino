object WormsValueFunction : ValueFunction {
    override val maxValue: Int = 4
    override fun getValue(points: Int): Int {
        return when (points) {
            21, 22, 23, 24 -> 1
            25, 26, 27, 28 -> 2
            29, 30, 31, 32 -> 3
            33, 34, 35, 36 -> 4
            else           -> 0
        }
    }
}