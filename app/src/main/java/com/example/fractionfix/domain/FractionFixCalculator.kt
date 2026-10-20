package com.example.fractionfix.domain

import com.example.fractionfix.data.model.Fraction
import com.example.fractionfix.data.model.FractionFixResult
import com.example.fractionfix.data.model.MixedNumber
import java.math.BigInteger
import java.math.RoundingMode

class FractionFixCalculator(
    private val simplifier: FractionSimplifier = FractionSimplifier(),
    private val mixedConverter: MixedNumberConverter = MixedNumberConverter(),
    private val decimalConverter: FractionDecimalConverter = FractionDecimalConverter(),
    private val percentageConverter: FractionPercentageConverter = FractionPercentageConverter()
) {

    /**
     * Authoritative calculation of a FractionFixResult from an input Fraction.
     */
    fun calculate(
        originalFraction: Fraction,
        precision: Int = 10,
        roundingMode: RoundingMode = RoundingMode.HALF_UP
    ): FractionFixResult {
        require(originalFraction.denominator != BigInteger.ZERO) { "Denominator cannot be zero." }

        val simplified = simplifier.simplify(originalFraction)
        val gcd = simplifier.calculateGcd(originalFraction.numerator, originalFraction.denominator)
        val mixed = mixedConverter.toMixed(simplified)
        val decimalVal = decimalConverter.toDecimal(simplified, precision, roundingMode)
        val percentageVal = percentageConverter.toPercentage(simplified, precision, roundingMode)
        val repeating = decimalConverter.isRepeatingDecimal(simplified)

        return FractionFixResult(
            originalFraction = originalFraction,
            simplifiedFraction = simplified,
            mixedNumber = mixed,
            decimalValue = decimalVal,
            percentageValue = percentageVal,
            gcd = gcd,
            isRepeatingDecimal = repeating
        )
    }

    /**
     * Calculates result from a MixedNumber.
     */
    fun calculateFromMixed(
        mixedNumber: MixedNumber,
        precision: Int = 10,
        roundingMode: RoundingMode = RoundingMode.HALF_UP
    ): FractionFixResult {
        val improperFraction = mixedConverter.toImproper(mixedNumber)
        return calculate(improperFraction, precision, roundingMode)
    }

    /**
     * Calculates result from a finite decimal string.
     */
    fun calculateFromDecimal(
        decimalString: String,
        precision: Int = 10,
        roundingMode: RoundingMode = RoundingMode.HALF_UP
    ): FractionFixResult {
        val fraction = decimalConverter.toFraction(decimalString)
        return calculate(fraction, precision, roundingMode)
    }

    /**
     * Direct delegate to simplify fraction.
     */
    fun simplify(fraction: Fraction): Fraction = simplifier.simplify(fraction)

    /**
     * Direct delegate to calculate GCD.
     */
    fun calculateGcd(num: BigInteger, den: BigInteger): BigInteger = simplifier.calculateGcd(num, den)

    /**
     * Direct delegate to convert fraction to mixed.
     */
    fun toMixed(fraction: Fraction): MixedNumber? = mixedConverter.toMixed(fraction)

    /**
     * Direct delegate to convert mixed to improper fraction.
     */
    fun toImproper(mixedNumber: MixedNumber): Fraction = mixedConverter.toImproper(mixedNumber)

    /**
     * Direct delegate to convert fraction to decimal.
     */
    fun toDecimal(
        fraction: Fraction,
        precision: Int = 10,
        roundingMode: RoundingMode = RoundingMode.HALF_UP
    ): String = decimalConverter.toDecimal(fraction, precision, roundingMode)

    /**
     * Direct delegate to convert fraction to percentage.
     */
    fun toPercentage(
        fraction: Fraction,
        precision: Int = 10,
        roundingMode: RoundingMode = RoundingMode.HALF_UP
    ): String = percentageConverter.toPercentage(fraction, precision, roundingMode)

    /**
     * Direct delegate to convert decimal to fraction.
     */
    fun decimalToFraction(decimalString: String): Fraction = decimalConverter.toFraction(decimalString)

    /**
     * Direct delegate to check repeating decimal.
     */
    fun isRepeatingDecimal(fraction: Fraction): Boolean = decimalConverter.isRepeatingDecimal(fraction)
}
