data class ProbabilityWithWormProbability(
    val probability: Float,
    val wormProbability: Float
) {
    operator fun plus(other: ProbabilityWithWormProbability): ProbabilityWithWormProbability = copy(
        probability = probability + other.probability,
        wormProbability = wormProbability + other.wormProbability
    )

    operator fun times(factor: Float): ProbabilityWithWormProbability = copy(
        probability = factor * probability,
        wormProbability = factor * wormProbability
    )
}