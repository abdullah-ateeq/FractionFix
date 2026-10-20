package com.example.fractionfix.ui.home

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fractionfix.data.model.FractionFixResult
import com.example.fractionfix.data.model.FractionFixUiState
import com.example.fractionfix.data.model.FractionInputMode
import com.example.fractionfix.ui.FractionFixViewModel
import com.example.fractionfix.ui.components.FractionView
import com.example.fractionfix.ui.components.FractionVisualizer
import com.example.fractionfix.ui.components.InputModeSelector
import com.example.fractionfix.ui.components.MixedView
import com.example.fractionfix.ui.components.SimplificationExplanationCard
import java.math.BigInteger

@Composable
fun HomeScreen(
    uiState: FractionFixUiState,
    viewModel: FractionFixViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val scrollState = rememberScrollState()

    fun triggerHaptic(type: HapticFeedbackType = HapticFeedbackType.TextHandleMove) {
        if (uiState.hapticFeedbackEnabled) {
            haptic.performHapticFeedback(type)
        }
    }

    fun copyToClipboard(label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        triggerHaptic()
        Toast.makeText(context, "Copied: $text", Toast.LENGTH_SHORT).show()
    }

    fun shareResult(text: String) {
        triggerHaptic()
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(intent, "Share FractionFix Result"))
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
            .animateContentSize()
    ) {
        // App Header
        Column(modifier = Modifier.padding(bottom = 12.dp)) {
            Text(
                text = "FractionFix",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Simplify and convert fractions.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Input Mode Selector
        InputModeSelector(
            selectedMode = uiState.inputMode,
            onModeSelected = {
                triggerHaptic()
                viewModel.onInputModeSelected(it)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Input Fields based on Mode
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (uiState.inputMode) {
                    FractionInputMode.FRACTION -> {
                        FractionInputFields(
                            numerator = uiState.numerator,
                            denominator = uiState.denominator,
                            onNumeratorChange = viewModel::onNumeratorChanged,
                            onDenominatorChange = viewModel::onDenominatorChanged
                        )
                    }

                    FractionInputMode.MIXED -> {
                        MixedInputFields(
                            whole = uiState.wholeNumber,
                            numerator = uiState.numerator,
                            denominator = uiState.denominator,
                            onWholeChange = viewModel::onWholeNumberChanged,
                            onNumeratorChange = viewModel::onNumeratorChanged,
                            onDenominatorChange = viewModel::onDenominatorChanged
                        )
                    }

                    FractionInputMode.DECIMAL -> {
                        DecimalInputField(
                            decimal = uiState.decimalInput,
                            onDecimalChange = viewModel::onDecimalInputChanged
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Primary Action Button (CALCULATE / SIMPLIFY / CONVERT)
                val actionButtonLabel = when (uiState.inputMode) {
                    FractionInputMode.FRACTION -> "SIMPLIFY"
                    FractionInputMode.MIXED -> "CONVERT"
                    FractionInputMode.DECIMAL -> "CONVERT"
                }

                Button(
                    onClick = {
                        triggerHaptic()
                        viewModel.calculate()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .semantics { contentDescription = "Calculate fraction." },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Calculate, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = actionButtonLabel,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Auxiliary Actions: Load Sample, Clear, Reset
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = {
                            triggerHaptic()
                            viewModel.loadSample()
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Sample", fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    OutlinedButton(
                        onClick = {
                            triggerHaptic()
                            viewModel.clearInput()
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Clear", fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    OutlinedButton(
                        onClick = {
                            triggerHaptic()
                            viewModel.reset()
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Reset", fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Quick Examples Row
        QuickExamplesRow(
            onSelectExample = { mode, num, den, whole, dec ->
                triggerHaptic()
                viewModel.loadExample(mode, num, den, whole, dec)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Error message card
        if (uiState.errorMessage != null && uiState.result == null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline,
                        contentDescription = "Error",
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = uiState.errorMessage,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }

        // Result Dashboard
        uiState.result?.let { result ->
            ResultDashboard(
                result = result,
                isSaved = uiState.isSaved,
                onCopy = { label, text -> copyToClipboard(label, text) },
                onCopyAll = {
                    val fullText = viewModel.getCopyText()
                    copyToClipboard("FractionFix Result", fullText)
                },
                onShare = {
                    val fullText = viewModel.getShareText()
                    shareResult(fullText)
                },
                onSave = {
                    triggerHaptic()
                    viewModel.saveToHistory()
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun FractionInputFields(
    numerator: String,
    denominator: String,
    onNumeratorChange: (String) -> Unit,
    onDenominatorChange: (String) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // NUMERATOR FIELD
        OutlinedTextField(
            value = numerator,
            onValueChange = onNumeratorChange,
            label = { Text("Numerator") },
            placeholder = { Text("e.g. 18") },
            textStyle = MaterialTheme.typography.titleLarge.copy(
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier
                .width(220.dp)
                .semantics { contentDescription = "Numerator." },
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Fraction Bar
        Box(
            modifier = Modifier
                .width(220.dp)
                .height(3.dp)
                .background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(2.dp))
        )

        Spacer(modifier = Modifier.height(6.dp))

        // DENOMINATOR FIELD
        OutlinedTextField(
            value = denominator,
            onValueChange = onDenominatorChange,
            label = { Text("Denominator") },
            placeholder = { Text("e.g. 24") },
            textStyle = MaterialTheme.typography.titleLarge.copy(
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier
                .width(220.dp)
                .semantics { contentDescription = "Denominator." },
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
private fun MixedInputFields(
    whole: String,
    numerator: String,
    denominator: String,
    onWholeChange: (String) -> Unit,
    onNumeratorChange: (String) -> Unit,
    onDenominatorChange: (String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Whole Number Field
        OutlinedTextField(
            value = whole,
            onValueChange = onWholeChange,
            label = { Text("Whole") },
            placeholder = { Text("2") },
            textStyle = MaterialTheme.typography.titleLarge.copy(
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier
                .width(90.dp)
                .semantics { contentDescription = "Whole number." },
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.width(10.dp))

        // Fraction part
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            OutlinedTextField(
                value = numerator,
                onValueChange = onNumeratorChange,
                label = { Text("Numerator") },
                placeholder = { Text("3") },
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier
                    .width(130.dp)
                    .semantics { contentDescription = "Numerator." },
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Box(
                modifier = Modifier
                    .width(130.dp)
                    .height(2.dp)
                    .background(MaterialTheme.colorScheme.primary)
            )

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = denominator,
                onValueChange = onDenominatorChange,
                label = { Text("Denominator") },
                placeholder = { Text("4") },
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier
                    .width(130.dp)
                    .semantics { contentDescription = "Denominator." },
                shape = RoundedCornerShape(10.dp)
            )
        }
    }
}

@Composable
private fun DecimalInputField(
    decimal: String,
    onDecimalChange: (String) -> Unit
) {
    OutlinedTextField(
        value = decimal,
        onValueChange = onDecimalChange,
        label = { Text("Decimal number") },
        placeholder = { Text("e.g. 0.75 or 1.5") },
        textStyle = MaterialTheme.typography.titleLarge.copy(
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Decimal number input." },
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
private fun QuickExamplesRow(
    onSelectExample: (FractionInputMode, String, String, String, String) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "QUICK EXAMPLES",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SuggestionChip(
                onClick = { onSelectExample(FractionInputMode.FRACTION, "18", "24", "", "") },
                label = { Text("Simple (18/24)") }
            )
            SuggestionChip(
                onClick = { onSelectExample(FractionInputMode.FRACTION, "11", "4", "", "") },
                label = { Text("Improper (11/4)") }
            )
            SuggestionChip(
                onClick = { onSelectExample(FractionInputMode.FRACTION, "1", "3", "", "") },
                label = { Text("Repeating (1/3)") }
            )
            SuggestionChip(
                onClick = { onSelectExample(FractionInputMode.FRACTION, "-12", "18", "", "") },
                label = { Text("Negative (-12/18)") }
            )
            SuggestionChip(
                onClick = { onSelectExample(FractionInputMode.DECIMAL, "", "", "", "0.625") },
                label = { Text("Decimal (0.625)") }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ResultDashboard(
    result: FractionFixResult,
    isSaved: Boolean,
    onCopy: (String, String) -> Unit,
    onCopyAll: () -> Unit,
    onShare: () -> Unit,
    onSave: () -> Unit
) {
    val isAlreadySimple = result.gcd == BigInteger.ONE &&
            result.originalFraction.denominator > BigInteger.ZERO &&
            result.originalFraction.numerator == result.simplifiedFraction.numerator &&
            result.originalFraction.denominator == result.simplifiedFraction.denominator

    val subtitle = if (isAlreadySimple) {
        "Already simplified"
    } else {
        "Simplified from ${result.originalFraction.numerator}/${result.originalFraction.denominator}"
    }

    val mixedFormText = result.mixedNumber?.let {
        if (it.numerator == BigInteger.ZERO) it.whole.toString()
        else if (it.whole == BigInteger.ZERO) "${it.numerator}/${it.denominator}"
        else "${it.whole} ${it.numerator}/${it.denominator}"
    } ?: "${result.simplifiedFraction.numerator}/${result.simplifiedFraction.denominator}"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Hero Card: Simplified Fraction
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "SIMPLIFIED FRACTION",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(12.dp))

                FractionView(
                    numerator = result.simplifiedFraction.numerator,
                    denominator = result.simplifiedFraction.denominator,
                    textStyle = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 38.sp
                    ),
                    textColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    dividerColor = MaterialTheme.colorScheme.primary,
                    dividerHeight = 3.dp,
                    contentDescriptionText = "Simplified fraction, ${result.simplifiedFraction.numerator} over ${result.simplifiedFraction.denominator}"
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )

                // Visualizer for denominator <= 20
                FractionVisualizer(fraction = result.simplifiedFraction)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Supporting Conversions Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Mixed Form Card
            ConversionCard(
                title = "MIXED FORM",
                value = mixedFormText,
                modifier = Modifier.weight(1f),
                onCopy = { onCopy("Mixed form", mixedFormText) }
            )

            // Decimal Card
            ConversionCard(
                title = "DECIMAL",
                value = result.decimalValue,
                badge = if (result.isRepeatingDecimal) "Repeating decimal" else null,
                modifier = Modifier.weight(1f),
                accessibilityDescription = "Decimal value, ${result.decimalValue}",
                onCopy = { onCopy("Decimal", result.decimalValue) }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Percentage Card
            ConversionCard(
                title = "PERCENTAGE",
                value = result.percentageValue,
                modifier = Modifier.weight(1f),
                accessibilityDescription = "Percentage, ${result.percentageValue}",
                onCopy = { onCopy("Percentage", result.percentageValue) }
            )

            // Original & GCD Card
            ConversionCard(
                title = "ORIGINAL / GCD",
                value = "${result.originalFraction.numerator}/${result.originalFraction.denominator}",
                badge = "GCD: ${result.gcd}",
                modifier = Modifier.weight(1f),
                onCopy = { onCopy("Original fraction", "${result.originalFraction.numerator}/${result.originalFraction.denominator}") }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Collapsible Simplification Explanation
        SimplificationExplanationCard(result = result)

        Spacer(modifier = Modifier.height(16.dp))

        // Action Buttons: Copy All, Share, Save
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onCopyAll,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("COPY", fontSize = 13.sp)
            }

            OutlinedButton(
                onClick = onShare,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("SHARE", fontSize = 13.sp)
            }

            OutlinedButton(
                onClick = onSave,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = if (isSaved) {
                    ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                    )
                } else ButtonDefaults.outlinedButtonColors()
            ) {
                Icon(
                    imageVector = if (isSaved) Icons.Default.Check else Icons.Default.BookmarkBorder,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = if (isSaved) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isSaved) "SAVED" else "SAVE",
                    fontSize = 13.sp,
                    color = if (isSaved) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun ConversionCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    badge: String? = null,
    accessibilityDescription: String? = null,
    onCopy: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .semantics {
                    if (accessibilityDescription != null) {
                        contentDescription = accessibilityDescription
                    }
                }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                IconButton(
                    onClick = onCopy,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy $title",
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            if (badge != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}
