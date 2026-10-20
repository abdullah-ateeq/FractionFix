package com.example.fractionfix.domain

import com.example.fractionfix.data.model.Fraction
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode

class FractionDecimalConverter {

    private val simplifier = FractionSimplifier()

    /**
     * Determines whether the simplified fraction produces a repeating decimal in base 10.
     * In base 10, a fraction has a finite (terminating) decimal representation if and only
     * if the reduced denominator's only prime factors are 2 and 5.
     */
    fun isRepeatingDecimal(fraction: Fraction): Boolean {
        require(fraction.denominator != BigInteger.ZERO) { "Denominator cannot be zero." }
        val simplified = simplifier.simplify(fraction)
        if (simplified.numerator == BigInteger.ZERO) {
            return false
        }

        var d = simplified.denominator
        val two = BigInteger.valueOf(2)
        val five = BigInteger.valueOf(5)

        while (d.mod(two) == BigInteger.ZERO) {
            d = d.divide(two)
        }
        while (d.mod(five) == BigInteger.ZERO) {
            d = d.divide(five)
        }

        return d != BigInteger.ONE
    }

    /**
     * Formats the fraction to a decimal string using the given precision and rounding mode.
     * For terminating decimals whose representation fits within the precision, trailing zeros
     * are stripped (e.g. 1/2 -> "0.5").
     * For repeating decimals or terminating decimals exceeding precision, the value is rounded
     * to [precision] decimal places.
     */
    fun toDecimal(
        fraction: Fraction,
        precision: Int = 10,
        roundingMode: RoundingMode = RoundingMode.HALF_UP
    ): String {
        require(fraction.denominator != BigInteger.ZERO) { "Denominator cannot be zero." }
        val simplified = simplifier.simplify(fraction)

        if (simplified.numerator == BigInteger.ZERO) {
            return "0"
        }

        val num = BigDecimal(simplified.numerator)
        val den = BigDecimal(simplified.denominator)
        val repeating = isRepeatingDecimal(simplified)

        if (!repeating) {
            try {
                val exact = num.divide(den)
                val stripped = exact.stripTrailingZeros()
                val plain = stripped.toPlainString()
                val decimalLength = if (plain.contains('.')) plain.substringAfter('.').length else 0
                if (decimalLength <= precision) {
                    return if (stripped.scale() <= 0) stripped.toBigInteger().toString() else plain
                }
            } catch (_: ArithmeticException) {
                // If exact division exceeds internal limits, fallback to rounded division
            }
        }

        val rounded = num.divide(den, precision, roundingMode)
        return rounded.toPlainString()
    }

    /**
     * Converts a finite decimal string to an exact Fraction using BigInteger/BigDecimal.
     * Floating-point numbers (Double/Float) are never used.
     * Supports formats like: "0.75", ".75", "-0.625", "10.125", "0.7500".
     */
    fun toFraction(decimalString: String): Fraction {
        val trimmed = decimalString.trim()
        require(trimmed.isNotEmpty()) { "Enter a valid number." }

        val normalized = when {
            trimmed.startsWith("+.") -> "0." + trimmed.substring(2)
            trimmed.startsWith("-.") -> "-0." + trimmed.substring(2)
            trimmed.startsWith(".") -> "0." + trimmed.substring(1)
            else -> trimmed
        }

        val bd = try {
            BigDecimal(normalized)
        } catch (_: Exception) {
            throw IllegalArgumentException("Enter a valid number.")
        }

        val scale = bd.scale()
        val unscaled = bd.unscaledValue()

        val rawFraction = if (scale >= 0) {
            val denominator = BigInteger.TEN.pow(scale)
            Fraction(unscaled, denominator)
        } else {
            val numerator = unscaled.multiply(BigInteger.TEN.pow(-scale))
            Fraction(numerator, BigInteger.ONE)
        }

        return simplifier.simplify(rawFraction)
    }
}
