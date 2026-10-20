package com.example.fractionfix.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fractionfix.data.model.CalculationState
import com.example.fractionfix.data.model.Fraction
import com.example.fractionfix.data.model.FractionFixHistory
import com.example.fractionfix.data.model.FractionFixResult
import com.example.fractionfix.data.model.FractionFixUiState
import com.example.fractionfix.data.model.FractionInputMode
import com.example.fractionfix.data.model.MixedNumber
import com.example.fractionfix.data.model.RoundingModeOption
import com.example.fractionfix.data.model.ThemeMode
import com.example.fractionfix.data.repository.FractionFixHistoryRepository
import com.example.fractionfix.data.repository.SettingsRepository
import com.example.fractionfix.domain.FractionFixCalculator
import com.example.fractionfix.domain.FractionFixFormatter
import com.example.fractionfix.domain.FractionFixParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class FractionFixViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val settingsRepo = SettingsRepository(application)
    private val historyRepo = FractionFixHistoryRepository(application)

    private val calculator = FractionFixCalculator()
    private val parser = FractionFixParser()
    private val formatter = FractionFixFormatter()

    private val _uiState = MutableStateFlow(FractionFixUiState())
    val uiState: StateFlow<FractionFixUiState> = _uiState.asStateFlow()

    private val _calculationState = MutableStateFlow<CalculationState>(CalculationState.Idle)
    val calculationState: StateFlow<CalculationState> = _calculationState.asStateFlow()

    init {
        loadInitialState()
    }

    private fun loadInitialState() {
        val preserve = settingsRepo.preserveLastInput
        val initialMode = if (preserve) settingsRepo.lastInputMode else FractionInputMode.FRACTION
        val initialNum = if (preserve) settingsRepo.lastNumerator else "18"
        val initialDen = if (preserve) settingsRepo.lastDenominator else "24"
        val initialWhole = if (preserve) settingsRepo.lastWhole else "2"
        val initialDec = if (preserve) settingsRepo.lastDecimal else "0.75"

        val historyList = historyRepo.getHistory()

        _uiState.update {
            it.copy(
                inputMode = initialMode,
                numerator = initialNum,
                denominator = initialDen,
                wholeNumber = initialWhole,
                decimalInput = initialDec,
                decimalPrecision = settingsRepo.decimalPrecision,
                roundingMode = settingsRepo.roundingMode,
                themeMode = settingsRepo.themeMode,
                dynamicColor = settingsRepo.dynamicColor,
                hapticFeedbackEnabled = settingsRepo.hapticFeedbackEnabled,
                animationsEnabled = settingsRepo.animationsEnabled,
                confirmHistoryDelete = settingsRepo.confirmHistoryDelete,
                preserveLastInput = settingsRepo.preserveLastInput,
                history = historyList
            )
        }

        // Perform initial calculation
        calculateInternal()
    }

    fun onInputModeSelected(mode: FractionInputMode) {
        if (_uiState.value.inputMode == mode) return

        _uiState.update {
            it.copy(
                inputMode = mode,
                errorMessage = null,
                isSaved = false
            )
        }
        if (_uiState.value.preserveLastInput) {
            settingsRepo.lastInputMode = mode
        }
        calculateInternal()
    }

    fun onNumeratorChanged(value: String) {
        _uiState.update { it.copy(numerator = value, isSaved = false) }
        if (_uiState.value.preserveLastInput) {
            settingsRepo.lastNumerator = value
        }
        calculateInternal()
    }

    fun onDenominatorChanged(value: String) {
        _uiState.update { it.copy(denominator = value, isSaved = false) }
        if (_uiState.value.preserveLastInput) {
            settingsRepo.lastDenominator = value
        }
        calculateInternal()
    }

    fun onWholeNumberChanged(value: String) {
        _uiState.update { it.copy(wholeNumber = value, isSaved = false) }
        if (_uiState.value.preserveLastInput) {
            settingsRepo.lastWhole = value
        }
        calculateInternal()
    }

    fun onDecimalInputChanged(value: String) {
        _uiState.update { it.copy(decimalInput = value, isSaved = false) }
        if (_uiState.value.preserveLastInput) {
            settingsRepo.lastDecimal = value
        }
        calculateInternal()
    }

    fun calculate() {
        calculateInternal()
    }

    private fun calculateInternal() {
        val currentState = _uiState.value
        val precision = currentState.decimalPrecision
        val rounding = currentState.roundingMode.mode

        try {
            when (currentState.inputMode) {
                FractionInputMode.FRACTION -> {
                    val parseResult = parser.parseFraction(currentState.numerator, currentState.denominator)
                    if (parseResult.isFailure) {
                        val message = parseResult.exceptionOrNull()?.message ?: "Enter a valid number."
                        _uiState.update { it.copy(result = null, errorMessage = message) }
                        _calculationState.value = CalculationState.Error(message)
                        return
                    }
                    val fraction = parseResult.getOrThrow()
                    val result = calculator.calculate(fraction, precision, rounding)
                    _uiState.update { it.copy(result = result, errorMessage = null) }
                    _calculationState.value = CalculationState.Calculated
                }

                FractionInputMode.MIXED -> {
                    val parseResult = parser.parseMixedNumber(
                        currentState.wholeNumber,
                        currentState.numerator,
                        currentState.denominator
                    )
                    if (parseResult.isFailure) {
                        val message = parseResult.exceptionOrNull()?.message ?: "Enter a valid number."
                        _uiState.update { it.copy(result = null, errorMessage = message) }
                        _calculationState.value = CalculationState.Error(message)
                        return
                    }
                    val mixed = parseResult.getOrThrow()
                    val result = calculator.calculateFromMixed(mixed, precision, rounding)
                    _uiState.update { it.copy(result = result, errorMessage = null) }
                    _calculationState.value = CalculationState.Calculated
                }

                FractionInputMode.DECIMAL -> {
                    val parseResult = parser.parseDecimal(currentState.decimalInput)
                    if (parseResult.isFailure) {
                        val message = parseResult.exceptionOrNull()?.message ?: "Enter a valid number."
                        _uiState.update { it.copy(result = null, errorMessage = message) }
                        _calculationState.value = CalculationState.Error(message)
                        return
                    }
                    val result = calculator.calculateFromDecimal(currentState.decimalInput, precision, rounding)
                    _uiState.update { it.copy(result = result, errorMessage = null) }
                    _calculationState.value = CalculationState.Calculated
                }
            }
        } catch (e: Exception) {
            val message = e.message ?: "Calculation error."
            _uiState.update { it.copy(result = null, errorMessage = message) }
            _calculationState.value = CalculationState.Error(message)
        }
    }

    fun loadSample() {
        _uiState.update {
            it.copy(
                inputMode = FractionInputMode.FRACTION,
                numerator = "18",
                denominator = "24",
                errorMessage = null,
                isSaved = false
            )
        }
        if (_uiState.value.preserveLastInput) {
            settingsRepo.lastInputMode = FractionInputMode.FRACTION
            settingsRepo.lastNumerator = "18"
            settingsRepo.lastDenominator = "24"
        }
        calculateInternal()
    }

    fun loadExample(
        mode: FractionInputMode,
        num: String = "",
        den: String = "",
        whole: String = "",
        decimal: String = ""
    ) {
        _uiState.update {
            it.copy(
                inputMode = mode,
                numerator = num,
                denominator = den,
                wholeNumber = whole,
                decimalInput = decimal,
                errorMessage = null,
                isSaved = false
            )
        }
        if (_uiState.value.preserveLastInput) {
            settingsRepo.lastInputMode = mode
            if (num.isNotEmpty()) settingsRepo.lastNumerator = num
            if (den.isNotEmpty()) settingsRepo.lastDenominator = den
            if (whole.isNotEmpty()) settingsRepo.lastWhole = whole
            if (decimal.isNotEmpty()) settingsRepo.lastDecimal = decimal
        }
        calculateInternal()
    }

    fun clearInput() {
        _uiState.update {
            it.copy(
                numerator = "",
                denominator = "",
                wholeNumber = "",
                decimalInput = "",
                result = null,
                errorMessage = "Enter a fraction to begin.",
                isSaved = false
            )
        }
        _calculationState.value = CalculationState.Idle
    }

    fun reset() {
        settingsRepo.resetSettings()
        _uiState.update {
            it.copy(
                inputMode = FractionInputMode.FRACTION,
                numerator = "18",
                denominator = "24",
                wholeNumber = "2",
                decimalInput = "0.75",
                decimalPrecision = 10,
                roundingMode = RoundingModeOption.HALF_UP,
                themeMode = ThemeMode.SYSTEM,
                dynamicColor = true,
                hapticFeedbackEnabled = true,
                animationsEnabled = true,
                confirmHistoryDelete = true,
                preserveLastInput = true,
                errorMessage = null,
                isSaved = false
            )
        }
        calculateInternal()
    }

    fun saveToHistory() {
        val res = _uiState.value.result ?: return
        if (_uiState.value.isSaved) return

        val state = _uiState.value
        val historyItem = FractionFixHistory(
            id = UUID.randomUUID().toString(),
            inputMode = state.inputMode,
            originalNumerator = if (state.inputMode != FractionInputMode.DECIMAL) state.numerator else null,
            originalDenominator = if (state.inputMode != FractionInputMode.DECIMAL) state.denominator else null,
            originalWhole = if (state.inputMode == FractionInputMode.MIXED) state.wholeNumber else null,
            originalDecimal = if (state.inputMode == FractionInputMode.DECIMAL) state.decimalInput else null,
            simplifiedNumerator = res.simplifiedFraction.numerator.toString(),
            simplifiedDenominator = res.simplifiedFraction.denominator.toString(),
            calculatedAt = System.currentTimeMillis()
        )

        val updatedHistory = historyRepo.saveHistoryItem(historyItem)
        _uiState.update { it.copy(history = updatedHistory, isSaved = true) }
    }

    fun restoreHistoryItem(item: FractionFixHistory) {
        _uiState.update {
            it.copy(
                inputMode = item.inputMode,
                numerator = item.originalNumerator ?: "",
                denominator = item.originalDenominator ?: "",
                wholeNumber = item.originalWhole ?: "",
                decimalInput = item.originalDecimal ?: "",
                errorMessage = null,
                isSaved = true
            )
        }
        if (_uiState.value.preserveLastInput) {
            settingsRepo.lastInputMode = item.inputMode
            item.originalNumerator?.let { settingsRepo.lastNumerator = it }
            item.originalDenominator?.let { settingsRepo.lastDenominator = it }
            item.originalWhole?.let { settingsRepo.lastWhole = it }
            item.originalDecimal?.let { settingsRepo.lastDecimal = it }
        }
        calculateInternal()
    }

    fun deleteHistoryItem(id: String) {
        val updated = historyRepo.deleteHistoryItem(id)
        _uiState.update { it.copy(history = updated) }
    }

    fun clearAllHistory() {
        historyRepo.clearHistory()
        _uiState.update { it.copy(history = emptyList(), isSaved = false) }
    }

    fun updateDecimalPrecision(precision: Int) {
        settingsRepo.decimalPrecision = precision
        _uiState.update { it.copy(decimalPrecision = precision) }
        calculateInternal()
    }

    fun updateRoundingMode(mode: RoundingModeOption) {
        settingsRepo.roundingMode = mode
        _uiState.update { it.copy(roundingMode = mode) }
        calculateInternal()
    }

    fun updateThemeMode(theme: ThemeMode) {
        settingsRepo.themeMode = theme
        _uiState.update { it.copy(themeMode = theme) }
    }

    fun updateDynamicColor(enabled: Boolean) {
        settingsRepo.dynamicColor = enabled
        _uiState.update { it.copy(dynamicColor = enabled) }
    }

    fun updateHapticFeedback(enabled: Boolean) {
        settingsRepo.hapticFeedbackEnabled = enabled
        _uiState.update { it.copy(hapticFeedbackEnabled = enabled) }
    }

    fun updateAnimations(enabled: Boolean) {
        settingsRepo.animationsEnabled = enabled
        _uiState.update { it.copy(animationsEnabled = enabled) }
    }

    fun updateConfirmHistoryDelete(enabled: Boolean) {
        settingsRepo.confirmHistoryDelete = enabled
        _uiState.update { it.copy(confirmHistoryDelete = enabled) }
    }

    fun updatePreserveLastInput(enabled: Boolean) {
        settingsRepo.preserveLastInput = enabled
        _uiState.update { it.copy(preserveLastInput = enabled) }
    }

    fun resetSettings() {
        reset()
    }

    fun getCopyText(): String {
        val result = _uiState.value.result ?: return ""
        return formatter.formatCopyText(result)
    }

    fun getShareText(): String {
        val result = _uiState.value.result ?: return ""
        return formatter.formatShareText(result)
    }
}
