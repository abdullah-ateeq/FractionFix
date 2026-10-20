package com.example.fractionfix.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.math.BigInteger

@Composable
fun FractionView(
    numerator: BigInteger,
    denominator: BigInteger,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.headlineMedium.copy(
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp
    ),
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    dividerColor: Color = MaterialTheme.colorScheme.onSurface,
    dividerHeight: Dp = 2.dp,
    minDividerWidth: Dp = 32.dp,
    contentDescriptionText: String? = null
) {
    val numStr = numerator.toString()
    val denStr = denominator.toString()
    val spokenDescription = contentDescriptionText ?: "Fraction, $numStr over $denStr"

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .semantics(mergeDescendants = true) {
                contentDescription = spokenDescription
            }
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        // Centered Numerator
        Text(
            text = numStr,
            style = textStyle,
            color = textColor,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(2.dp))

        // Horizontal fraction bar
        Box(
            modifier = Modifier
                .widthIn(min = minDividerWidth)
                .height(dividerHeight)
                .background(dividerColor)
        )

        Spacer(modifier = Modifier.height(2.dp))

        // Centered Denominator
        Text(
            text = denStr,
            style = textStyle,
            color = textColor,
            textAlign = TextAlign.Center
        )
    }
}
