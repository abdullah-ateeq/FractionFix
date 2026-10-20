package com.example.fractionfix.data.model

import java.math.BigInteger

data class FractionFixResult(
    val originalFraction: Fraction,
    val simplifiedFraction: Fraction,
    val mixedNumber: MixedNumber?,
    val decimalValue: String,
    val percentageValue: String,
    val gcd: BigInteger,
    val isRepeatingDecimal: Boolean
)
