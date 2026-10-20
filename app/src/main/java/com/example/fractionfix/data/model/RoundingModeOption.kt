package com.example.fractionfix.data.model

import java.math.RoundingMode

enum class RoundingModeOption(val displayName: String, val mode: RoundingMode) {
    HALF_UP("Half Up", RoundingMode.HALF_UP),
    HALF_DOWN("Half Down", RoundingMode.HALF_DOWN),
    HALF_EVEN("Half Even", RoundingMode.HALF_EVEN),
    UP("Up", RoundingMode.UP),
    DOWN("Down", RoundingMode.DOWN),
    CEILING("Ceiling", RoundingMode.CEILING),
    FLOOR("Floor", RoundingMode.FLOOR)
}
