package com.example.fractionfix.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fractionfix.data.model.Fraction
import java.math.BigInteger

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FractionVisualizer(
    fraction: Fraction,
    modifier: Modifier = Modifier
) {
    val den = fraction.denominator
    // Only visualize for denominators 1..20
    if (den <= BigInteger.ZERO || den > BigInteger.valueOf(20)) {
        return
    }

    val denInt = den.toInt()
    val numInt = fraction.numerator.abs().toInt()
    // For proper fractions or simple representation
    val filledCount = numInt.coerceAtMost(denInt)
    val labelText = if (fraction.isNegative) {
        "$filledCount of $denInt parts (negative)"
    } else {
        "$filledCount of $denInt parts"
    }

    Column(
        modifier = modifier
            .semantics(mergeDescendants = true) {
                contentDescription = labelText
            }
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.Center,
            maxItemsInEachRow = 10
        ) {
            for (i in 0 until denInt) {
                val isFilled = i < filledCount
                Box(
                    modifier = Modifier
                        .padding(3.dp)
                        .size(24.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (isFilled) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .border(
                            width = 1.dp,
                            color = if (isFilled) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(4.dp)
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = labelText,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
