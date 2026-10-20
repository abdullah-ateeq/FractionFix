package com.example.fractionfix.data.model

data class FractionFixUiState(
    val inputMode: FractionInputMode = FractionInputMode.FRACTION,
    val numerator: String = "18",
    val denominator: String = "24",
    val wholeNumber: String = "",
    val decimalInput: String = "",
    val decimalPrecision: Int = 10,
    val roundingMode: RoundingModeOption = RoundingModeOption.HALF_UP,
    val result: FractionFixResult? = null,
    val history: List<FractionFixHistory> = emptyList(),
    val errorMessage: String? = null,
    val isCalculating: Boolean = false,
    val isSaved: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColor: Boolean = true,
    val hapticFeedbackEnabled: Boolean = true,
    val animationsEnabled: Boolean = true,
    val confirmHistoryDelete: Boolean = true,
    val preserveLastInput: Boolean = true
)
