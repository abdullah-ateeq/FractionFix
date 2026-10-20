# FractionFix 🧮

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20M3-blue.svg)](https://developer.android.com/jetpack/compose)
[![API](https://img.shields.io/badge/Min%20SDK-26%20(Android%208.0)-brightgreen.svg)](https://developer.android.com)

**FractionFix** is an intuitive, modern Android application designed for performing fraction math, simplification, and conversions effortlessly. Built with **Kotlin** and **Jetpack Compose (Material 3)**, FractionFix helps students, educators, engineers, and everyday users understand and compute fractions with step-by-step breakdowns and visual representations.

---

## ✨ Features

- **🧮 Multiple Input Modes**:
  - **Fraction Mode**: Simplify standard improper or proper fractions ($a/b$).
  - **Mixed Number Mode**: Convert and compute mixed numbers ($w \frac{a}{b}$).
  - **Decimal Mode**: Convert decimal values into reduced fractions (supports repeating decimals).
  - **Percentage Mode**: Convert percentages into exact simplified fractions.

- **🔍 Step-by-Step Mathematical Explanation**:
  - Automatically calculates the **Greatest Common Divisor (GCD)** using Euclidean Algorithm.
  - Displays explicit step-by-step breakdown showing how the fraction is reduced to its lowest terms.

- **📊 Interactive Fraction Visualizer**:
  - Renders a graphical breakdown of the calculated fraction to aid visual learning and conceptual understanding.

- **📜 Calculation History & Bookmarks**:
  - Keeps a history of your previous calculations locally.
  - Search and filter history records.
  - Bookmark/favorite frequently used fraction results.

- **⚙️ Advanced Mathematical Settings**:
  - **Decimal Precision**: Adjust decimal precision up to 10 decimal places.
  - **Rounding Modes**: Select rounding strategies (`HALF_UP`, `HALF_EVEN`, `FLOOR`, `CEIL`, `DOWN`, `UP`).
  - **Preserve State**: Save and restore your last input across app restarts.

- **🎨 Modern Material 3 UI & Customization**:
  - Seamless Light, Dark, and System Theme modes.
  - **Dynamic Color (Material You)** support on Android 12+.
  - Edge-to-edge layout, responsive animations, and subtle haptic feedback.

---

## 🏗️ Architecture & Tech Stack

FractionFix follows modern Android development best practices and **Clean Architecture** principles:

```
com.example.fractionfix/
├── data/           # Models, Local Repositories, Preferences
├── domain/         # Pure Kotlin domain logic (Calculator, Parsers, Converters, Simplifiers)
├── ui/             # Jetpack Compose UI (Screens, ViewModels, Components, Navigation, Theme)
```

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI Toolkit**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material 3 components
- **Architecture**: Unidirectional Data Flow (UDF) with `ViewModel` and `StateFlow`
- **Navigation**: Jetpack Navigation Compose
- **Concurrency**: Kotlin Coroutines & Flow
- **Testing**: JUnit 4 unit tests covering domain calculators, parsers, and repositories

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio**: Ladybug (2024.2.1) or newer recommended
- **JDK**: Java 17 or higher
- **Android Device / Emulator**: Running Android 8.0 (API level 26) or higher

### Building & Running

1. **Clone the repository**:
   ```bash
   git clone https://github.com/abdullah-ateeq/FractionFix.git
   cd FractionFix
   ```

2. **Open in Android Studio**:
   Open Android Studio, choose **Open**, and select the `FractionFix` project folder.

3. **Build the project**:
   Using Gradle wrapper from terminal:
   ```bash
   ./gradlew assembleDebug
   ```

4. **Run Unit Tests**:
   ```bash
   ./gradlew test
   ```

---

## 📱 App Configuration

| Parameter | Value |
| --- | --- |
| **Application ID** | `com.app.fractionfix55` |
| **Min SDK** | 26 (Android 8.0) |
| **Target SDK** | 37 |
| **Version** | 1.0 (versionCode 1) |

---

## 📄 License

This project is open-source and available under the [MIT License](LICENSE).
