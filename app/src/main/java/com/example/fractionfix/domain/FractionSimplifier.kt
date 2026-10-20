package com.example.fractionfix.domain

import com.example.fractionfix.data.model.Fraction
import java.math.BigInteger

class FractionSimplifier {

    /**
     * Normalizes the fraction so that the denominator is strictly positive,
     * zero numerator is represented canonically as 0/1, and the fraction
     * is reduced to lowest terms via BigInteger GCD.
     */
    fun simplify(fraction: Fraction): Fraction {
        require(fraction.denominator != BigInteger.ZERO) { "Denominator cannot be zero." }

        // Canonical zero
        if (fraction.numerator == BigInteger.ZERO) {
            return Fraction(BigInteger.ZERO, BigInteger.ONE)
        }

        // Normalize denominator to positive
        val (normNum, normDen) = if (fraction.denominator < BigInteger.ZERO) {
            Pair(-fraction.numerator, -fraction.denominator)
        } else {
            Pair(fraction.numerator, fraction.denominator)
        }

        val gcd = calculateGcd(normNum, normDen)
        val simplifiedNumerator = normNum / gcd
        val simplifiedDenominator = normDen / gcd

        return Fraction(simplifiedNumerator, simplifiedDenominator)
    }

    /**
     * Computes the greatest common divisor of numerator and denominator.
     */
    fun calculateGcd(numerator: BigInteger, denominator: BigInteger): BigInteger {
        require(denominator != BigInteger.ZERO) { "Denominator cannot be zero." }
        val gcd = numerator.abs().gcd(denominator.abs())
        return if (gcd == BigInteger.ZERO) BigInteger.ONE else gcd
    }

    /**
     * Checks if the fraction is already in simplest canonical form.
     */
    fun isAlreadySimplified(fraction: Fraction): Boolean {
        if (fraction.denominator <= BigInteger.ZERO) return false
        if (fraction.numerator == BigInteger.ZERO) {
            return fraction.denominator == BigInteger.ONE
        }
        val gcd = calculateGcd(fraction.numerator, fraction.denominator)
        return gcd == BigInteger.ONE
    }
}
