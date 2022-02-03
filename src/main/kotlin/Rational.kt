import java.io.Serializable
import kotlin.math.sign

fun Long.toRational() = Rational(this, 1L)
fun Int.toRational() = Rational(this, 1)

private const val BUT_SIGN_BIT = (-1L).ushr(1)
private const val FRACTION_MASK = (-1L).ushr(12)
private const val FRACTION_1 = 1L.shl(52)
fun Double.toRational(): Rational {
    require(this.isFinite())
    require(!this.isNaN())
    if (this == 0.0) return Rational.ZERO
    val bits = this.toBits()
    var exponent = (bits.and(BUT_SIGN_BIT).ushr(52) - 1023 - 52).toInt()
    var fraction = bits.and(FRACTION_MASK).or(FRACTION_1)
    require(exponent < 11) { "Double number is too big" }
    if (exponent < -(62 + 52)) return Rational.ZERO
    if (exponent < -62) {
        fraction = fraction.shr(-(exponent + 62))
        exponent = -62
    }
    val abs = if (exponent >= 0) Rational(fraction * 1L.shl(exponent), 1L)
    else Rational(fraction, 1L.shl(-exponent))
    return if (bits < 0) -abs else abs
}

fun Float.toRational() = this.toDouble().toRational()

fun Number.toRational(): Rational =
    when (this) {
        is Int -> this.toRational()
        is Long -> this.toRational()
        is Double -> this.toRational()
        is Float -> this.toRational()
        is Rational -> this
        else -> this.toDouble().toRational()
    }

val Int.r get() = this.toRational()
val Long.r get() = this.toRational()

val Number.r get() = this.toRational()

infix fun Int.over(d: Int) = Rational(this, d)
infix fun Long.over(d: Long) = Rational(this, d)

infix fun Number.over(d: Number) = (this.r / d.r)

operator fun Number.times(r: Rational) = this.r * r
operator fun Number.div(r: Rational) = this.r / r
operator fun Number.plus(r: Rational) = this.r + r
operator fun Number.minus(r: Rational) = this.r - r

fun gcd(a: Long, b: Long): Long = if (b == 0L) a else gcd(b, a % b)

class Rational private constructor(val numerator: Long, val denominator: Long) : Number(),
    Comparable<Rational>, Serializable {
    companion object {
        val ZERO = Rational(0L, 1L)
        val ONE = Rational(1L, 1L)
        operator fun invoke(numer: Long, denom: Long): Rational {
            require(denom != 0L) { "Denominator cannot be 0" }
            val gcd = gcd(numer, denom).let { if (denom.sign * it.sign < 0) -it else it }
            return Rational(numer / gcd, denom / gcd)
        }

        operator fun invoke(n: Number, d: Number) =
            invoke(n.toLong(), d.toLong())
    }

    override fun toString(): String =
        if (denominator == 1L) "$numerator" else "($numerator/$denominator)"


    override fun compareTo(other: Rational): Int {
        return (numerator * other.denominator).compareTo(other.numerator * denominator)
    }

    fun inv() = Companion(denominator, numerator)

    operator fun unaryMinus() = Rational(-numerator, denominator)
    operator fun unaryPlus() = this

    operator fun times(other: Rational) =
        Companion(
            numerator * other.numerator,
            denominator * other.denominator
        )

    operator fun plus(other: Rational) =
        Companion(
            numerator * other.denominator + other.numerator * denominator,
            denominator * other.denominator
        )

    operator fun minus(other: Rational) = this + (-other)

    operator fun div(other: Rational) = (this * other.inv())

    operator fun times(other: Number) = this * other.r
    operator fun plus(other: Number) = this + other.r
    operator fun minus(other: Number) = this - other.r
    operator fun div(other: Number) = this / other.r

    fun toRational() = this
    val r get() = this

    infix fun over(other: Number) = this / other.r

    // Number functions

    override fun toByte() = toLong().toByte()

    override fun toChar() = toLong().toInt().toChar()

    override fun toInt() = toLong().toInt()

    override fun toLong() = numerator / denominator

    override fun toShort() = toLong().toShort()

    override fun toDouble() = numerator.toDouble() / denominator

    override fun toFloat() = toDouble().toFloat()

    ////////

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Rational

        if (numerator != other.numerator) return false
        if (denominator != other.denominator) return false

        return true
    }

    override fun hashCode(): Int {
        var result = numerator.hashCode()
        result = 31 * result + denominator.hashCode()
        return result
    }
}