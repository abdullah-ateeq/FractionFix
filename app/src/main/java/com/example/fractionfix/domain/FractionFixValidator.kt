package com.example.fractionfix.domain

class FractionFixValidator {

    fun validateFractionInputs(numerator: String, denominator: String): String? {
        val num = numerator.trim()
        val den = denominator.trim()
        if (num.isEmpty() && den.isEmpty()) return "Enter a value to begin."
        if (num.isEmpty()) return "Enter a numerator."
        if (den.isEmpty()) return "Enter a denominator."
        return null
    }

    fun validateMixedInputs(whole: String, numerator: String, denominator: String): String? {
        val w = whole.trim()
        val num = numerator.trim()
        val den = denominator.trim()
        if (w.isEmpty() && num.isEmpty() && den.isEmpty()) return "Enter a value to begin."
        if (w.isEmpty()) return "Enter a whole number."
        if (num.isEmpty()) return "Enter a numerator."
        if (den.isEmpty()) return "Enter a denominator."
        return null
    }

    fun validateDecimalInput(input: String): String? {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return "Enter a value to begin."
        return null
    }
}
