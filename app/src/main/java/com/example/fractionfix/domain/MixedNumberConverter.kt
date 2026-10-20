package com.example.fractionfix.domain

import com.example.fractionfix.data.model.Fraction
import com.example.fractionfix.data.model.MixedNumber
import java.math.BigInteger

class MixedNumberConverter {

    /**
     * Converts a Fraction to a MixedNumber if it is an improper fraction
     * (|numerator| >= denominator and denominator > 0 and remainder > 0 or whole != 0).
     * Returns null if the fraction is a proper fraction (|numerator| < denominator).
     */
    fun toMixed(fraction: Fraction): MixedNumber? {
        val simplifier = FractionSimplifier()
        val normalized = simplifier.simplify(fraction)

        val absNum = normalized.numerator.abs()
        val den = normalized.denominator

        if (absNum < den) {
            // Proper fraction has no mixed number form (displays as proper fraction)
            return null
        }

        val wholePart = absNum / den
        val remainder = absNum % den

        val signedWhole = if (normalized.numerator < BigInteger.ZERO) -wholePart else wholePart

        return MixedNumber(
            whole = signedWhole,
            numerator = remainder,
            denominator = den
        )
    }

    /**
     * Converts a MixedNumber back to an improper Fraction.
     * E.g., 2 3/4 -> 11/4
     * E.g., -2 3/4 -> -11/4
     */
    fun toImproper(mixedNumber: MixedNumber): Fraction {
        require(mixedNumber.denominator > BigInteger.ZERO) { "Denominator must be positive." }
        require(mixedNumber.numerator >= BigInteger.ZERO) { "Numerator must be non-negative." }

        val totalNum = mixedNumber.whole.abs()
            .multiply(mixedNumber.denominator)
            .add(mixedNumber.numerator)

        val finalNumerator = if (mixedNumber.whole < BigInteger.ZERO) {
            -totalNum
        } else {
            totalNum
        }

        return Fraction(finalNumerator, mixedNumber.denominator)
    }
}
