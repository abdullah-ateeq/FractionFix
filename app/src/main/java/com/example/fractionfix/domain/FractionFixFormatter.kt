package com.example.fractionfix.domain

import com.example.fractionfix.data.model.Fraction
import com.example.fractionfix.data.model.FractionFixResult
import com.example.fractionfix.data.model.MixedNumber
import java.math.BigInteger
import java.math.RoundingMode

class FractionFixFormatter {

    private val decimalConverter = FractionDecimalConverter()
    private val percentageConverter = FractionPercentageConverter()

    /**
     * Formats a Fraction as "numerator/denominator".
     */
    fun formatFraction(fraction: Fraction): String {
        return "${fraction.numerator}/${fraction.denominator}"
    }

    /**
     * Formats a MixedNumber as "whole num/den" or "whole" if remainder is zero,
     * or "num/den" if whole is zero.
     */
    fun formatMixed(mixedNumber: MixedNumber): String {
        if (mixedNumber.numerator == BigInteger.ZERO) {
            return mixedNumber.whole.toString()
        }
        if (mixedNumber.whole == BigInteger.ZERO) {
            return "${mixedNumber.numerator}/${mixedNumber.denominator}"
        }
        return "${mixedNumber.whole} ${mixedNumber.numerator}/${mixedNumber.denominator}"
    }

    /**
     * Formats the mixed form display for any result.
     * For proper fractions (mixedNumber is null), returns the simplified fraction "num/den".
     */
    fun formatMixedForm(result: FractionFixResult): String {
        return result.mixedNumber?.let { formatMixed(it) } ?: formatFraction(result.simplifiedFraction)
    }

    /**
     * Formats a fraction to decimal string.
     */
    fun formatDecimal(
        fraction: Fraction,
        precision: Int = 10,
        roundingMode: RoundingMode = RoundingMode.HALF_UP
    ): String {
        return decimalConverter.toDecimal(fraction, precision, roundingMode)
    }

    /**
     * Formats a fraction to percentage string.
     */
    fun formatPercentage(
        fraction: Fraction,
        precision: Int = 10,
        roundingMode: RoundingMode = RoundingMode.HALF_UP
    ): String {
        return percentageConverter.toPercentage(fraction, precision, roundingMode)
    }

    /**
     * Formats copy text exactly as specified:
     * FractionFix
     *
     * Original: 18/24
     * Simplified: 3/4
     * Mixed form: 3/4
     * Decimal: 0.75
     * Percentage: 75%
     */
    fun formatCopyText(result: FractionFixResult): String {
        val mixedForm = formatMixedForm(result)
        return """
            FractionFix

            Original: ${formatFraction(result.originalFraction)}
            Simplified: ${formatFraction(result.simplifiedFraction)}
            Mixed form: $mixedForm
            Decimal: ${result.decimalValue}
            Percentage: ${result.percentageValue}
        """.trimIndent()
    }

    /**
     * Formats share text.
     */
    fun formatShareText(result: FractionFixResult): String {
        return formatCopyText(result)
    }
}
