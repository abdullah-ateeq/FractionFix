package com.example.fractionfix.data.model

import java.math.BigInteger

data class Fraction(
    val numerator: BigInteger,
    val denominator: BigInteger
) {
    init {
        require(denominator != BigInteger.ZERO) { "Denominator cannot be zero." }
    }

    /**
     * Returns true if the fraction is mathematically negative.
     */
    val isNegative: Boolean
        get() = (numerator < BigInteger.ZERO) xor (denominator < BigInteger.ZERO)

    /**
     * Absolute fraction.
     */
    val abs: Fraction
        get() = Fraction(numerator.abs(), denominator.abs())
}
