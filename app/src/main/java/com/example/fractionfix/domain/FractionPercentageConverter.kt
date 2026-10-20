package com.example.fractionfix.domain

import com.example.fractionfix.data.model.Fraction
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode

class FractionPercentageConverter {

    private val simplifier = FractionSimplifier()
    private val decimalConverter = FractionDecimalConverter()

    /**
     * Converts a Fraction to a percentage display string.
     * Examples:
     * 3/4 -> "75%"
     * 1/4 -> "25%"
     * 0/25 -> "0%"
     * 1/3 (precision 10) -> "33.3333333333%"
     */
    fun toPercentage(
        fraction: Fraction,
        precision: Int = 10,
        roundingMode: RoundingMode = RoundingMode.HALF_UP
    ): String {
        require(fraction.denominator != BigInteger.ZERO) { "Denominator cannot be zero." }
        val simplified = simplifier.simplify(fraction)

        if (simplified.numerator == BigInteger.ZERO) {
            return "0%"
        }

        val num100 = BigDecimal(simplified.numerator.multiply(BigInteger.valueOf(100)))
        val den = BigDecimal(simplified.denominator)
        val repeating = decimalConverter.isRepeatingDecimal(simplified)

        if (!repeating) {
            try {
                val exact = num100.divide(den)
                val stripped = exact.stripTrailingZeros()
                val plain = stripped.toPlainString()
                val decimalLength = if (plain.contains('.')) plain.substringAfter('.').length else 0
                if (decimalLength <= precision) {
                    val formattedNumber = if (stripped.scale() <= 0) stripped.toBigInteger().toString() else plain
                    return "$formattedNumber%"
                }
            } catch (_: ArithmeticException) {
                // Fallback to rounded division
            }
        }

        val rounded = num100.divide(den, precision, roundingMode)
        return "${rounded.toPlainString()}%"
    }
}
