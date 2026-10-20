package com.example.fractionfix.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fractionfix.data.model.FractionInputMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputModeSelector(
    selectedMode: FractionInputMode,
    onModeSelected: (FractionInputMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val modes = listOf(
        FractionInputMode.FRACTION to "Fraction",
        FractionInputMode.MIXED to "Mixed",
        FractionInputMode.DECIMAL to "Decimal"
    )

    SingleChoiceSegmentedButtonRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
    ) {
        modes.forEachIndexed { index, (mode, label) ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(index = index, count = modes.size),
                onClick = { onModeSelected(mode) },
                selected = selectedMode == mode,
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (selectedMode == mode) FontWeight.Bold else FontWeight.Medium
                        )
                    )
                }
            )
        }
    }
}
