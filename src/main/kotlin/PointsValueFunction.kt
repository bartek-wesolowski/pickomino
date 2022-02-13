object PointsValueFunction : ValueFunction {
    override val maxValue: Int = 40
    override fun getValue(points: Int): Int = points
}