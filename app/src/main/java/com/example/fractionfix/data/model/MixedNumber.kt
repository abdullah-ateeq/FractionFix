package com.example.fractionfix.data.model

import java.math.BigInteger

data class MixedNumber(
    val whole: BigInteger,
    val numerator: BigInteger,
    val denominator: BigInteger
) {
    init {
        require(denominator > BigInteger.ZERO) { "Denominator must be positive." }
        require(numerator >= BigInteger.ZERO) { "Numerator must be non-negative." }
    }
}
