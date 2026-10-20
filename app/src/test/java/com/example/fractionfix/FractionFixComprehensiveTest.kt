package com.example.fractionfix

import com.example.fractionfix.data.model.Fraction
import com.example.fractionfix.data.model.FractionFixHistory
import com.example.fractionfix.data.model.FractionInputMode
import com.example.fractionfix.data.model.MixedNumber
import com.example.fractionfix.data.model.RoundingModeOption
import com.example.fractionfix.data.model.ThemeMode
import com.example.fractionfix.data.repository.FractionFixHistoryRepository
import com.example.fractionfix.data.repository.SettingsRepository
import com.example.fractionfix.domain.FractionDecimalConverter
import com.example.fractionfix.domain.FractionFixCalculator
import com.example.fractionfix.domain.FractionFixFormatter
import com.example.fractionfix.domain.FractionFixParser
import com.example.fractionfix.domain.FractionPercentageConverter
import com.example.fractionfix.domain.FractionSimplifier
import com.example.fractionfix.domain.MixedNumberConverter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import java.math.BigInteger
import java.math.RoundingMode

class FractionFixComprehensiveTest {

    private lateinit var simplifier: FractionSimplifier
    private lateinit var mixedConverter: MixedNumberConverter
    private lateinit var decimalConverter: FractionDecimalConverter
    private lateinit var percentageConverter: FractionPercentageConverter
    private lateinit var calculator: FractionFixCalculator
    private lateinit var parser: FractionFixParser
    private lateinit var formatter: FractionFixFormatter
    private lateinit var fakePrefs: FakeSharedPreferences
    private lateinit var historyRepo: FractionFixHistoryRepository
    private lateinit var settingsRepo: SettingsRepository

    @Before
    fun setUp() {
        simplifier = FractionSimplifier()
        mixedConverter = MixedNumberConverter()
        decimalConverter = FractionDecimalConverter()
        percentageConverter = FractionPercentageConverter()
        calculator = FractionFixCalculator(simplifier, mixedConverter, decimalConverter, percentageConverter)
        parser = FractionFixParser()
        formatter = FractionFixFormatter()
        fakePrefs = FakeSharedPreferences()
        historyRepo = FractionFixHistoryRepository(fakePrefs)
        settingsRepo = SettingsRepository(fakePrefs)
    }

    // 1. Basic simplification: 18/24 -> 3/4
    @Test
    fun test01_basicSimplification() {
        val result = simplifier.simplify(Fraction(BigInteger.valueOf(18), BigInteger.valueOf(24)))
        assertEquals(BigInteger.valueOf(3), result.numerator)
        assertEquals(BigInteger.valueOf(4), result.denominator)
    }

    // 2. Already simplified fraction: 3/7 -> 3/7
    @Test
    fun test02_alreadySimplifiedFraction() {
        val frac = Fraction(BigInteger.valueOf(3), BigInteger.valueOf(7))
        assertTrue(simplifier.isAlreadySimplified(frac))
        val result = simplifier.simplify(frac)
        assertEquals(BigInteger.valueOf(3), result.numerator)
        assertEquals(BigInteger.valueOf(7), result.denominator)
    }

    // 3. GCD calculation: 18 and 24 -> 6
    @Test
    fun test03_gcdCalculation() {
        val gcd = simplifier.calculateGcd(BigInteger.valueOf(18), BigInteger.valueOf(24))
        assertEquals(BigInteger.valueOf(6), gcd)
    }

    // 4. Large GCD: 84/126 -> GCD 42, simplified 2/3
    @Test
    fun test04_largeGcd() {
        val gcd = simplifier.calculateGcd(BigInteger.valueOf(84), BigInteger.valueOf(126))
        assertEquals(BigInteger.valueOf(42), gcd)
        val result = simplifier.simplify(Fraction(BigInteger.valueOf(84), BigInteger.valueOf(126)))
        assertEquals(BigInteger.valueOf(2), result.numerator)
        assertEquals(BigInteger.valueOf(3), result.denominator)
    }

    // 5. Zero numerator: 0/25 -> 0/1
    @Test
    fun test05_zeroNumerator() {
        val result = simplifier.simplify(Fraction(BigInteger.ZERO, BigInteger.valueOf(25)))
        assertEquals(BigInteger.ZERO, result.numerator)
        assertEquals(BigInteger.ONE, result.denominator)
    }

    // 6. Negative numerator: -18/24 -> -3/4
    @Test
    fun test06_negativeNumerator() {
        val result = simplifier.simplify(Fraction(BigInteger.valueOf(-18), BigInteger.valueOf(24)))
        assertEquals(BigInteger.valueOf(-3), result.numerator)
        assertEquals(BigInteger.valueOf(4), result.denominator)
    }

    // 7. Negative denominator: 18/-24 -> -3/4
    @Test
    fun test07_negativeDenominator() {
        val result = simplifier.simplify(Fraction(BigInteger.valueOf(18), BigInteger.valueOf(-24)))
        assertEquals(BigInteger.valueOf(-3), result.numerator)
        assertEquals(BigInteger.valueOf(4), result.denominator)
    }

    // 8. Both negative: -18/-24 -> 3/4
    @Test
    fun test08_bothNegative() {
        val result = simplifier.simplify(Fraction(BigInteger.valueOf(-18), BigInteger.valueOf(-24)))
        assertEquals(BigInteger.valueOf(3), result.numerator)
        assertEquals(BigInteger.valueOf(4), result.denominator)
    }

    // 9. Denominator zero throws IllegalArgumentException
    @Test(expected = IllegalArgumentException::class)
    fun test09_denominatorZero() {
        Fraction(BigInteger.valueOf(5), BigInteger.ZERO)
    }

    // 10. Improper fraction: 11/4
    @Test
    fun test10_improperFraction() {
        val result = calculator.calculate(Fraction(BigInteger.valueOf(11), BigInteger.valueOf(4)))
        assertEquals("11/4", formatter.formatFraction(result.simplifiedFraction))
        assertEquals("2 3/4", formatter.formatMixedForm(result))
        assertEquals("2.75", result.decimalValue)
    }

    // 11. Proper fraction: 3/4 has null mixedNumber and formats as 3/4
    @Test
    fun test11_properFraction() {
        val result = calculator.calculate(Fraction(BigInteger.valueOf(3), BigInteger.valueOf(4)))
        assertNull(result.mixedNumber)
        assertEquals("3/4", formatter.formatMixedForm(result))
    }

    // 12. Mixed conversion: 11/4 -> 2 3/4
    @Test
    fun test12_mixedConversion() {
        val mixed = mixedConverter.toMixed(Fraction(BigInteger.valueOf(11), BigInteger.valueOf(4)))
        assertNotNull(mixed)
        assertEquals(BigInteger.valueOf(2), mixed?.whole)
        assertEquals(BigInteger.valueOf(3), mixed?.numerator)
        assertEquals(BigInteger.valueOf(4), mixed?.denominator)
    }

    // 13. Negative mixed conversion: -11/4 -> -2 3/4
    @Test
    fun test13_negativeMixedConversion() {
        val mixed = mixedConverter.toMixed(Fraction(BigInteger.valueOf(-11), BigInteger.valueOf(4)))
        assertNotNull(mixed)
        assertEquals(BigInteger.valueOf(-2), mixed?.whole)
        assertEquals(BigInteger.valueOf(3), mixed?.numerator)
        assertEquals(BigInteger.valueOf(4), mixed?.denominator)
        assertEquals("-2 3/4", formatter.formatMixed(mixed!!))
    }

    // 14. Mixed to improper: 2 3/4 -> 11/4, -2 3/4 -> -11/4
    @Test
    fun test14_mixedToImproper() {
        val pos = mixedConverter.toImproper(MixedNumber(BigInteger.valueOf(2), BigInteger.valueOf(3), BigInteger.valueOf(4)))
        assertEquals(BigInteger.valueOf(11), pos.numerator)
        assertEquals(BigInteger.valueOf(4), pos.denominator)

        val neg = mixedConverter.toImproper(MixedNumber(BigInteger.valueOf(-2), BigInteger.valueOf(3), BigInteger.valueOf(4)))
        assertEquals(BigInteger.valueOf(-11), neg.numerator)
        assertEquals(BigInteger.valueOf(4), neg.denominator)
    }

    // 15. Decimal to fraction: 0.75 -> 3/4
    @Test
    fun test15_decimalToFraction() {
        val frac = decimalConverter.toFraction("0.75")
        assertEquals(BigInteger.valueOf(3), frac.numerator)
        assertEquals(BigInteger.valueOf(4), frac.denominator)
    }

    // 16. Fraction to decimal: 3/4 -> 0.75
    @Test
    fun test16_fractionToDecimal() {
        val dec = decimalConverter.toDecimal(Fraction(BigInteger.valueOf(3), BigInteger.valueOf(4)))
        assertEquals("0.75", dec)
    }

    // 17. Fraction to percentage: 3/4 -> 75%
    @Test
    fun test17_fractionToPercentage() {
        val pct = percentageConverter.toPercentage(Fraction(BigInteger.valueOf(3), BigInteger.valueOf(4)))
        assertEquals("75%", pct)
    }

    // 18. Terminating decimal: 1/2 -> 0.5, 1/4 -> 0.25, 3/8 -> 0.375
    @Test
    fun test18_terminatingDecimal() {
        assertEquals("0.5", decimalConverter.toDecimal(Fraction(BigInteger.ONE, BigInteger.valueOf(2))))
        assertEquals("0.25", decimalConverter.toDecimal(Fraction(BigInteger.ONE, BigInteger.valueOf(4))))
        assertEquals("0.375", decimalConverter.toDecimal(Fraction(BigInteger.valueOf(3), BigInteger.valueOf(8))))
        assertFalse(decimalConverter.isRepeatingDecimal(Fraction(BigInteger.ONE, BigInteger.valueOf(2))))
        assertFalse(decimalConverter.isRepeatingDecimal(Fraction(BigInteger.ONE, BigInteger.valueOf(4))))
        assertFalse(decimalConverter.isRepeatingDecimal(Fraction(BigInteger.valueOf(3), BigInteger.valueOf(8))))
    }

    // 19. Repeating decimal: 1/3 -> 0.3333333333, 1/6 -> 0.1666666667
    @Test
    fun test19_repeatingDecimal() {
        assertTrue(decimalConverter.isRepeatingDecimal(Fraction(BigInteger.ONE, BigInteger.valueOf(3))))
        assertTrue(decimalConverter.isRepeatingDecimal(Fraction(BigInteger.ONE, BigInteger.valueOf(6))))
        val dec13 = decimalConverter.toDecimal(Fraction(BigInteger.ONE, BigInteger.valueOf(3)), 10)
        assertEquals("0.3333333333", dec13)
        val dec16 = decimalConverter.toDecimal(Fraction(BigInteger.ONE, BigInteger.valueOf(6)), 10)
        assertEquals("0.1666666667", dec16)
    }

    // 20. Decimal rounding: 1/6 at 4 places = 0.1667, 8 places = 0.16666667
    @Test
    fun test20_decimalRounding() {
        val f = Fraction(BigInteger.ONE, BigInteger.valueOf(6))
        assertEquals("0.1667", decimalConverter.toDecimal(f, 4, RoundingMode.HALF_UP))
        assertEquals("0.16666667", decimalConverter.toDecimal(f, 8, RoundingMode.HALF_UP))
        // Fraction itself remains unchanged
        assertEquals(BigInteger.ONE, f.numerator)
        assertEquals(BigInteger.valueOf(6), f.denominator)
    }

    // 21. Decimal precision settings (4, 6, 8, 10, 12)
    @Test
    fun test21_decimalPrecisionSettings() {
        val f = Fraction(BigInteger.ONE, BigInteger.valueOf(3))
        assertEquals("0.3333", decimalConverter.toDecimal(f, 4))
        assertEquals("0.333333", decimalConverter.toDecimal(f, 6))
        assertEquals("0.33333333", decimalConverter.toDecimal(f, 8))
        assertEquals("0.3333333333", decimalConverter.toDecimal(f, 10))
        assertEquals("0.333333333333", decimalConverter.toDecimal(f, 12))
    }

    // 22. BigInteger values calculation
    @Test
    fun test22_bigIntegerValues() {
        val num = BigInteger("1000000000000000000")
        val den = BigInteger("2000000000000000000")
        val simplified = simplifier.simplify(Fraction(num, den))
        assertEquals(BigInteger.ONE, simplified.numerator)
        assertEquals(BigInteger.valueOf(2), simplified.denominator)
    }

    // 23. Very large numerator and 24. Very large denominator
    @Test
    fun test23_and_24_veryLargeNumeratorAndDenominator() {
        val num = BigInteger("123456789123456789")
        val den = BigInteger("987654321987654321")
        val gcd = simplifier.calculateGcd(num, den)
        assertEquals(BigInteger("9000000009"), gcd)

        val simplified = simplifier.simplify(Fraction(num, den))
        assertEquals(BigInteger("13717421"), simplified.numerator)
        assertEquals(BigInteger("109739369"), simplified.denominator)

        // Verify multiplication back without overflow
        assertEquals(num, simplified.numerator.multiply(gcd))
        assertEquals(den, simplified.denominator.multiply(gcd))
    }

    // 25. Decimal 0.1: must be exact 1/10 without floating-point artifacts
    @Test
    fun test25_decimalPointOne() {
        val frac = decimalConverter.toFraction("0.1")
        assertEquals(BigInteger.ONE, frac.numerator)
        assertEquals(BigInteger.TEN, frac.denominator)
    }

    // 26. Decimal 0.75 -> 3/4
    @Test
    fun test26_decimalPointSevenFive() {
        val frac = decimalConverter.toFraction("0.75")
        assertEquals(BigInteger.valueOf(3), frac.numerator)
        assertEquals(BigInteger.valueOf(4), frac.denominator)
    }

    // 27. Decimal 0.125 -> 1/8
    @Test
    fun test27_decimalPointOneTwoFive() {
        val frac = decimalConverter.toFraction("0.125")
        assertEquals(BigInteger.ONE, frac.numerator)
        assertEquals(BigInteger.valueOf(8), frac.denominator)
    }

    // 28. Decimal 1.5 -> 3/2, Mixed 1 1/2
    @Test
    fun test28_decimalOnePointFive() {
        val frac = decimalConverter.toFraction("1.5")
        assertEquals(BigInteger.valueOf(3), frac.numerator)
        assertEquals(BigInteger.valueOf(2), frac.denominator)
        val mixed = mixedConverter.toMixed(frac)
        assertNotNull(mixed)
        assertEquals("1 1/2", formatter.formatMixed(mixed!!))
    }

    // 29. Negative decimal: -0.625 -> -5/8
    @Test
    fun test29_negativeDecimal() {
        val frac = decimalConverter.toFraction("-0.625")
        assertEquals(BigInteger.valueOf(-5), frac.numerator)
        assertEquals(BigInteger.valueOf(8), frac.denominator)
    }

    // 30. Leading decimal: .75 -> 3/4
    @Test
    fun test30_leadingDecimal() {
        val frac = decimalConverter.toFraction(".75")
        assertEquals(BigInteger.valueOf(3), frac.numerator)
        assertEquals(BigInteger.valueOf(4), frac.denominator)
    }

    // 31. Trailing decimal & 32. Trailing zeros: 0.7500 -> 3/4
    @Test
    fun test31_and_32_trailingZeros() {
        val frac = decimalConverter.toFraction("0.7500")
        assertEquals(BigInteger.valueOf(3), frac.numerator)
        assertEquals(BigInteger.valueOf(4), frac.denominator)
    }

    // 33. Invalid fraction: 12/abc -> Result.failure
    @Test
    fun test33_invalidFraction() {
        val result = parser.parseFraction("12", "abc")
        assertTrue(result.isFailure)
        assertEquals("Enter a valid number.", result.exceptionOrNull()?.message)
    }

    // 34. Invalid decimal: 1.2.3 and --2
    @Test
    fun test34_invalidDecimal() {
        val res1 = parser.parseDecimal("1.2.3")
        assertTrue(res1.isFailure)
        assertEquals("Enter a valid number.", res1.exceptionOrNull()?.message)

        val res2 = parser.parseDecimal("--2")
        assertTrue(res2.isFailure)
        assertEquals("Enter a valid number.", res2.exceptionOrNull()?.message)
    }

    // 35. Empty input
    @Test
    fun test35_emptyInput() {
        val res = parser.parseFraction("", "")
        assertTrue(res.isFailure)
        assertEquals("Enter a value to begin.", res.exceptionOrNull()?.message)
    }

    // 36. Zero denominator: 5/0 -> "Denominator cannot be zero."
    @Test
    fun test36_zeroDenominator() {
        val res = parser.parseFraction("5", "0")
        assertTrue(res.isFailure)
        assertEquals("Denominator cannot be zero.", res.exceptionOrNull()?.message)
    }

    // 37. Invalid mixed number: 2 5/4 -> "Fraction part must be smaller than the denominator."
    @Test
    fun test37_invalidMixedNumber() {
        val res = parser.parseMixedNumber("2", "5", "4")
        assertTrue(res.isFailure)
        assertEquals("Fraction part must be smaller than the denominator.", res.exceptionOrNull()?.message)
    }

    // 38. Copy formatting matches required format exactly
    @Test
    fun test38_copyFormatting() {
        val res = calculator.calculate(Fraction(BigInteger.valueOf(18), BigInteger.valueOf(24)))
        val copyText = formatter.formatCopyText(res)
        assertTrue(copyText.contains("FractionFix"))
        assertTrue(copyText.contains("Original: 18/24"))
        assertTrue(copyText.contains("Simplified: 3/4"))
        assertTrue(copyText.contains("Mixed form: 3/4"))
        assertTrue(copyText.contains("Decimal: 0.75"))
        assertTrue(copyText.contains("Percentage: 75%"))
    }

    // 39. Share formatting
    @Test
    fun test39_shareFormatting() {
        val res = calculator.calculate(Fraction(BigInteger.valueOf(11), BigInteger.valueOf(4)))
        val shareText = formatter.formatShareText(res)
        assertTrue(shareText.contains("Simplified: 11/4"))
        assertTrue(shareText.contains("Mixed form: 2 3/4"))
    }

    // 40. History save & 41. History load
    @Test
    fun test40_and_41_historySaveAndLoad() {
        val item1 = FractionFixHistory("1", FractionInputMode.FRACTION, "18", "24", null, null, "3", "4", 1000L)
        historyRepo.saveHistoryItem(item1)
        val loaded = historyRepo.getHistory()
        assertEquals(1, loaded.size)
        assertEquals("3", loaded[0].simplifiedNumerator)
        assertEquals("4", loaded[0].simplifiedDenominator)
    }

    // 42. History limit: max 10 entries, 11th removes oldest
    @Test
    fun test42_historyLimit() {
        for (i in 1..11) {
            val item = FractionFixHistory("$i", FractionInputMode.FRACTION, "$i", "24", null, null, "$i", "24", 1000L + i)
            historyRepo.saveHistoryItem(item)
        }
        val loaded = historyRepo.getHistory()
        assertEquals(10, loaded.size)
        // Item 11 is newest (front), Item 1 was oldest and dropped
        assertEquals("11", loaded[0].simplifiedNumerator)
        assertFalse(loaded.any { it.id == "1" })
    }

    // 43. History deletion: delete individual item by id
    @Test
    fun test43_historyDeletion() {
        val item1 = FractionFixHistory("id1", FractionInputMode.FRACTION, "1", "2", null, null, "1", "2", 100L)
        val item2 = FractionFixHistory("id2", FractionInputMode.FRACTION, "3", "4", null, null, "3", "4", 200L)
        historyRepo.saveHistoryItem(item1)
        historyRepo.saveHistoryItem(item2)
        assertEquals(2, historyRepo.getHistory().size)

        historyRepo.deleteHistoryItem("id1")
        val remaining = historyRepo.getHistory()
        assertEquals(1, remaining.size)
        assertEquals("id2", remaining[0].id)
    }

    // 44. History clear
    @Test
    fun test44_historyClear() {
        val item = FractionFixHistory("1", FractionInputMode.FRACTION, "1", "2", null, null, "1", "2", 100L)
        historyRepo.saveHistoryItem(item)
        historyRepo.clearHistory()
        assertTrue(historyRepo.getHistory().isEmpty())
    }

    // 45. History restore
    @Test
    fun test45_historyRestore() {
        val item = FractionFixHistory("1", FractionInputMode.FRACTION, "18", "24", null, null, "3", "4", 100L)
        val parsed = parser.parseFraction(item.originalNumerator!!, item.originalDenominator!!)
        assertTrue(parsed.isSuccess)
        val result = calculator.calculate(parsed.getOrThrow())
        assertEquals("3/4", formatter.formatFraction(result.simplifiedFraction))
    }

    // 46. Settings persistence
    @Test
    fun test46_settingsPersistence() {
        settingsRepo.themeMode = ThemeMode.DARK
        assertEquals(ThemeMode.DARK, settingsRepo.themeMode)
    }

    // 47. Last input persistence
    @Test
    fun test47_lastInputPersistence() {
        settingsRepo.lastNumerator = "50"
        settingsRepo.lastDenominator = "100"
        assertEquals("50", settingsRepo.lastNumerator)
        assertEquals("100", settingsRepo.lastDenominator)
    }

    // 48. Decimal precision persistence
    @Test
    fun test48_decimalPrecisionPersistence() {
        settingsRepo.decimalPrecision = 6
        assertEquals(6, settingsRepo.decimalPrecision)
    }

    // 49. Rounding mode persistence
    @Test
    fun test49_roundingModePersistence() {
        settingsRepo.roundingMode = RoundingModeOption.HALF_EVEN
        assertEquals(RoundingModeOption.HALF_EVEN, settingsRepo.roundingMode)
    }

    // 50. Reset restores default settings without affecting history
    @Test
    fun test50_resetSettingsKeepsHistory() {
        val item = FractionFixHistory("1", FractionInputMode.FRACTION, "1", "2", null, null, "1", "2", 100L)
        historyRepo.saveHistoryItem(item)
        settingsRepo.decimalPrecision = 4
        settingsRepo.resetSettings()

        assertEquals(10, settingsRepo.decimalPrecision)
        assertEquals(RoundingModeOption.HALF_UP, settingsRepo.roundingMode)
        assertEquals(1, historyRepo.getHistory().size)
    }

    // 51. Fraction normalization: -6/8 -> -3/4, 6/-8 -> -3/4, -6/-8 -> 3/4
    @Test
    fun test51_fractionNormalization() {
        val f1 = simplifier.simplify(Fraction(BigInteger.valueOf(-6), BigInteger.valueOf(8)))
        val f2 = simplifier.simplify(Fraction(BigInteger.valueOf(6), BigInteger.valueOf(-8)))
        val f3 = simplifier.simplify(Fraction(BigInteger.valueOf(-6), BigInteger.valueOf(-8)))
        assertEquals("-3/4", formatter.formatFraction(f1))
        assertEquals("-3/4", formatter.formatFraction(f2))
        assertEquals("3/4", formatter.formatFraction(f3))
    }

    // 52. Canonical zero: 0/5, 0/-5, 0/999999 -> 0/1
    @Test
    fun test52_canonicalZero() {
        val z1 = simplifier.simplify(Fraction(BigInteger.ZERO, BigInteger.valueOf(5)))
        val z2 = simplifier.simplify(Fraction(BigInteger.ZERO, BigInteger.valueOf(-5)))
        val z3 = simplifier.simplify(Fraction(BigInteger.ZERO, BigInteger.valueOf(999999)))
        assertEquals("0/1", formatter.formatFraction(z1))
        assertEquals("0/1", formatter.formatFraction(z2))
        assertEquals("0/1", formatter.formatFraction(z3))
    }

    // 53. Decimal exactness: 0.000125 -> 1/8000
    @Test
    fun test53_decimalExactness() {
        val frac = decimalConverter.toFraction("0.000125")
        assertEquals(BigInteger.ONE, frac.numerator)
        assertEquals(BigInteger.valueOf(8000), frac.denominator)
    }

    // 54. Repeating decimal detection
    @Test
    fun test54_repeatingDecimalDetection() {
        assertTrue(decimalConverter.isRepeatingDecimal(Fraction(BigInteger.ONE, BigInteger.valueOf(3))))
        assertTrue(decimalConverter.isRepeatingDecimal(Fraction(BigInteger.ONE, BigInteger.valueOf(7))))
        assertFalse(decimalConverter.isRepeatingDecimal(Fraction(BigInteger.ONE, BigInteger.valueOf(8))))
        assertFalse(decimalConverter.isRepeatingDecimal(Fraction(BigInteger.ONE, BigInteger.valueOf(10))))
    }

    // 55. Rapid input protection & 56. Duplicate history protection
    @Test
    fun test55_and_56_duplicateHistoryProtection() {
        val item1 = FractionFixHistory("1", FractionInputMode.FRACTION, "18", "24", null, null, "3", "4", 100L)
        val item2 = FractionFixHistory("2", FractionInputMode.FRACTION, "18", "24", null, null, "3", "4", 200L)
        historyRepo.saveHistoryItem(item1)
        historyRepo.saveHistoryItem(item2) // identical calculation inputs
        val history = historyRepo.getHistory()
        assertEquals(1, history.size)
    }

    // Critical Test: 123456789123456789.125 large decimal
    @Test
    fun testCritical_largeDecimalToFraction() {
        val frac = decimalConverter.toFraction("123456789123456789.125")
        assertEquals(BigInteger("987654312987654313"), frac.numerator)
        assertEquals(BigInteger.valueOf(8), frac.denominator)
    }
}
