import hm.binkley.math.fixed.FixedBigRational

data class ProbabilityWithSuccessProbability(
    val probability: FixedBigRational,
    val successProbability: FixedBigRational
) {
    operator fun plus(other: ProbabilityWithSuccessProbability): ProbabilityWithSuccessProbability = copy(
        probability = probability + other.probability,
        successProbability = successProbability + other.successProbability
    )

    operator fun times(factor: FixedBigRational): ProbabilityWithSuccessProbability = copy(
        probability = factor * probability,
        successProbability = factor * successProbability
    )
}