package com.example.fractionfix.domain

import com.example.fractionfix.data.model.Fraction
import com.example.fractionfix.data.model.MixedNumber
import java.math.BigDecimal
import java.math.BigInteger

class FractionFixParser {

    /**
     * Parses numerator and denominator strings into a Fraction.
     */
    fun parseFraction(
        numerator: String,
        denominator: String
    ): Result<Fraction> {
        val numTrimmed = numerator.trim()
        val denTrimmed = denominator.trim()

        if (numTrimmed.isEmpty() && denTrimmed.isEmpty()) {
            return Result.failure(IllegalArgumentException("Enter a value to begin."))
        }
        if (numTrimmed.isEmpty()) {
            return Result.failure(IllegalArgumentException("Enter a numerator."))
        }
        if (denTrimmed.isEmpty()) {
            return Result.failure(IllegalArgumentException("Enter a denominator."))
        }

        val numBig = try {
            BigInteger(numTrimmed)
        } catch (_: Exception) {
            return Result.failure(IllegalArgumentException("Enter a valid number."))
        }

        val denBig = try {
            BigInteger(denTrimmed)
        } catch (_: Exception) {
            return Result.failure(IllegalArgumentException("Enter a valid number."))
        }

        if (denBig == BigInteger.ZERO) {
            return Result.failure(IllegalArgumentException("Denominator cannot be zero."))
        }

        return Result.success(Fraction(numBig, denBig))
    }

    /**
     * Parses whole number, numerator, and denominator strings into a MixedNumber.
     * Enforces the rule that the fraction part must be smaller than the denominator.
     */
    fun parseMixedNumber(
        whole: String,
        numerator: String,
        denominator: String
    ): Result<MixedNumber> {
        val wholeTrimmed = whole.trim()
        val numTrimmed = numerator.trim()
        val denTrimmed = denominator.trim()

        if (wholeTrimmed.isEmpty() && numTrimmed.isEmpty() && denTrimmed.isEmpty()) {
            return Result.failure(IllegalArgumentException("Enter a value to begin."))
        }
        if (wholeTrimmed.isEmpty()) {
            return Result.failure(IllegalArgumentException("Enter a valid number."))
        }
        if (numTrimmed.isEmpty()) {
            return Result.failure(IllegalArgumentException("Enter a numerator."))
        }
        if (denTrimmed.isEmpty()) {
            return Result.failure(IllegalArgumentException("Enter a denominator."))
        }

        val wholeBig = try {
            BigInteger(wholeTrimmed)
        } catch (_: Exception) {
            return Result.failure(IllegalArgumentException("Enter a valid number."))
        }

        val numBig = try {
            BigInteger(numTrimmed)
        } catch (_: Exception) {
            return Result.failure(IllegalArgumentException("Enter a valid number."))
        }

        val denBig = try {
            BigInteger(denTrimmed)
        } catch (_: Exception) {
            return Result.failure(IllegalArgumentException("Enter a valid number."))
        }

        if (denBig == BigInteger.ZERO) {
            return Result.failure(IllegalArgumentException("Denominator cannot be zero."))
        }
        if (denBig < BigInteger.ZERO) {
            return Result.failure(IllegalArgumentException("Enter a valid number."))
        }
        if (numBig < BigInteger.ZERO) {
            return Result.failure(IllegalArgumentException("Enter a valid number."))
        }
        if (numBig >= denBig) {
            return Result.failure(IllegalArgumentException("Fraction part must be smaller than the denominator."))
        }

        return Result.success(MixedNumber(wholeBig, numBig, denBig))
    }

    /**
     * Parses a decimal string into a BigDecimal.
     */
    fun parseDecimal(
        input: String
    ): Result<BigDecimal> {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) {
            return Result.failure(IllegalArgumentException("Enter a value to begin."))
        }

        val normalized = when {
            trimmed.startsWith("+.") -> "0." + trimmed.substring(2)
            trimmed.startsWith("-.") -> "-0." + trimmed.substring(2)
            trimmed.startsWith(".") -> "0." + trimmed.substring(1)
            else -> trimmed
        }

        // Basic sanity check to prevent cases like "--2" or multiple dots
        if (normalized.count { it == '.' } > 1 || normalized.count { it == '-' } > 1 || normalized.count { it == '+' } > 1) {
            return Result.failure(IllegalArgumentException("Enter a valid number."))
        }

        return try {
            val bd = BigDecimal(normalized)
            Result.success(bd)
        } catch (_: Exception) {
            Result.failure(IllegalArgumentException("Enter a valid number."))
        }
    }
}
