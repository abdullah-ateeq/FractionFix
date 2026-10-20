package com.example.fractionfix.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.fractionfix.data.model.FractionInputMode
import com.example.fractionfix.data.model.RoundingModeOption
import com.example.fractionfix.data.model.ThemeMode

class SettingsRepository(
    private val prefs: SharedPreferences
) {
    constructor(context: Context) : this(
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    )

    var themeMode: ThemeMode
        get() {
            val name = prefs.getString(KEY_THEME_MODE, ThemeMode.SYSTEM.name)
            return try {
                ThemeMode.valueOf(name ?: ThemeMode.SYSTEM.name)
            } catch (_: Exception) {
                ThemeMode.SYSTEM
            }
        }
        set(value) {
            prefs.edit().putString(KEY_THEME_MODE, value.name).apply()
        }

    var dynamicColor: Boolean
        get() = prefs.getBoolean(KEY_DYNAMIC_COLOR, true)
        set(value) = prefs.edit().putBoolean(KEY_DYNAMIC_COLOR, value).apply()

    var hapticFeedbackEnabled: Boolean
        get() = prefs.getBoolean(KEY_HAPTIC_FEEDBACK, true)
        set(value) = prefs.edit().putBoolean(KEY_HAPTIC_FEEDBACK, value).apply()

    var animationsEnabled: Boolean
        get() = prefs.getBoolean(KEY_ANIMATIONS, true)
        set(value) = prefs.edit().putBoolean(KEY_ANIMATIONS, value).apply()

    var decimalPrecision: Int
        get() = prefs.getInt(KEY_DECIMAL_PRECISION, 10)
        set(value) = prefs.edit().putInt(KEY_DECIMAL_PRECISION, value).apply()

    var roundingMode: RoundingModeOption
        get() {
            val name = prefs.getString(KEY_ROUNDING_MODE, RoundingModeOption.HALF_UP.name)
            return try {
                RoundingModeOption.valueOf(name ?: RoundingModeOption.HALF_UP.name)
            } catch (_: Exception) {
                RoundingModeOption.HALF_UP
            }
        }
        set(value) {
            prefs.edit().putString(KEY_ROUNDING_MODE, value.name).apply()
        }

    var confirmHistoryDelete: Boolean
        get() = prefs.getBoolean(KEY_CONFIRM_HISTORY_DELETE, true)
        set(value) = prefs.edit().putBoolean(KEY_CONFIRM_HISTORY_DELETE, value).apply()

    var preserveLastInput: Boolean
        get() = prefs.getBoolean(KEY_PRESERVE_LAST_INPUT, true)
        set(value) = prefs.edit().putBoolean(KEY_PRESERVE_LAST_INPUT, value).apply()

    var lastInputMode: FractionInputMode
        get() {
            val name = prefs.getString(KEY_LAST_INPUT_MODE, FractionInputMode.FRACTION.name)
            return try {
                FractionInputMode.valueOf(name ?: FractionInputMode.FRACTION.name)
            } catch (_: Exception) {
                FractionInputMode.FRACTION
            }
        }
        set(value) = prefs.edit().putString(KEY_LAST_INPUT_MODE, value.name).apply()

    var lastNumerator: String
        get() = prefs.getString(KEY_LAST_NUMERATOR, "18") ?: "18"
        set(value) = prefs.edit().putString(KEY_LAST_NUMERATOR, value).apply()

    var lastDenominator: String
        get() = prefs.getString(KEY_LAST_DENOMINATOR, "24") ?: "24"
        set(value) = prefs.edit().putString(KEY_LAST_DENOMINATOR, value).apply()

    var lastWhole: String
        get() = prefs.getString(KEY_LAST_WHOLE, "2") ?: "2"
        set(value) = prefs.edit().putString(KEY_LAST_WHOLE, value).apply()

    var lastDecimal: String
        get() = prefs.getString(KEY_LAST_DECIMAL, "0.75") ?: "0.75"
        set(value) = prefs.edit().putString(KEY_LAST_DECIMAL, value).apply()

    /**
     * Resets settings to default values.
     * Note: History is NOT cleared on settings reset.
     */
    fun resetSettings() {
        prefs.edit()
            .putString(KEY_THEME_MODE, ThemeMode.SYSTEM.name)
            .putBoolean(KEY_DYNAMIC_COLOR, true)
            .putBoolean(KEY_HAPTIC_FEEDBACK, true)
            .putBoolean(KEY_ANIMATIONS, true)
            .putInt(KEY_DECIMAL_PRECISION, 10)
            .putString(KEY_ROUNDING_MODE, RoundingModeOption.HALF_UP.name)
            .putBoolean(KEY_CONFIRM_HISTORY_DELETE, true)
            .putBoolean(KEY_PRESERVE_LAST_INPUT, true)
            .putString(KEY_LAST_INPUT_MODE, FractionInputMode.FRACTION.name)
            .putString(KEY_LAST_NUMERATOR, "18")
            .putString(KEY_LAST_DENOMINATOR, "24")
            .putString(KEY_LAST_WHOLE, "2")
            .putString(KEY_LAST_DECIMAL, "0.75")
            .apply()
    }

    companion object {
        private const val PREFS_NAME = "fractionfix_settings"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_DYNAMIC_COLOR = "dynamic_color"
        private const val KEY_HAPTIC_FEEDBACK = "haptic_feedback"
        private const val KEY_ANIMATIONS = "animations"
        private const val KEY_DECIMAL_PRECISION = "decimal_precision"
        private const val KEY_ROUNDING_MODE = "rounding_mode"
        private const val KEY_CONFIRM_HISTORY_DELETE = "confirm_history_delete"
        private const val KEY_PRESERVE_LAST_INPUT = "preserve_last_input"
        private const val KEY_LAST_INPUT_MODE = "last_input_mode"
        private const val KEY_LAST_NUMERATOR = "last_numerator"
        private const val KEY_LAST_DENOMINATOR = "last_denominator"
        private const val KEY_LAST_WHOLE = "last_whole"
        private const val KEY_LAST_DECIMAL = "last_decimal"
    }
}
