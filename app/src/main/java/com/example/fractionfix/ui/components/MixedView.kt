package com.example.fractionfix.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fractionfix.data.model.MixedNumber
import java.math.BigInteger

@Composable
fun MixedView(
    mixedNumber: MixedNumber,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.headlineMedium.copy(
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    ),
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    val spokenText = if (mixedNumber.numerator == BigInteger.ZERO) {
        "Mixed number: ${mixedNumber.whole}"
    } else if (mixedNumber.whole == BigInteger.ZERO) {
        "Fraction: ${mixedNumber.numerator} over ${mixedNumber.denominator}"
    } else {
        "Mixed number: ${mixedNumber.whole} and ${mixedNumber.numerator} over ${mixedNumber.denominator}"
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.semantics(mergeDescendants = true) {
            contentDescription = spokenText
        }
    ) {
        if (mixedNumber.whole != BigInteger.ZERO || mixedNumber.numerator == BigInteger.ZERO) {
            Text(
                text = mixedNumber.whole.toString(),
                style = textStyle,
                color = textColor
            )
            if (mixedNumber.numerator != BigInteger.ZERO) {
                Spacer(modifier = Modifier.width(8.dp))
            }
        }

        if (mixedNumber.numerator != BigInteger.ZERO) {
            FractionView(
                numerator = mixedNumber.numerator,
                denominator = mixedNumber.denominator,
                textStyle = textStyle.copy(fontSize = (textStyle.fontSize.value * 0.85f).sp),
                textColor = textColor,
                dividerColor = textColor,
                contentDescriptionText = "${mixedNumber.numerator} over ${mixedNumber.denominator}"
            )
        }
    }
}
