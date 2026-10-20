package com.example.fractionfix.data.model

data class FractionFixHistory(
    val id: String,
    val inputMode: FractionInputMode,
    val originalNumerator: String?,
    val originalDenominator: String?,
    val originalWhole: String?,
    val originalDecimal: String?,
    val simplifiedNumerator: String,
    val simplifiedDenominator: String,
    val calculatedAt: Long
)
