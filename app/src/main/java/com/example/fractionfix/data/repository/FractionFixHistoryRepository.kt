package com.example.fractionfix.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.fractionfix.data.model.FractionFixHistory
import com.example.fractionfix.data.model.FractionInputMode
import org.json.JSONArray
import org.json.JSONObject

class FractionFixHistoryRepository(
    private val prefs: SharedPreferences
) {
    constructor(context: Context) : this(
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    )

    /**
     * Loads the stored history items, ordered from newest to oldest.
     */
    fun getHistory(): List<FractionFixHistory> {
        val jsonString = prefs.getString(KEY_HISTORY, null) ?: return emptyList()
        val list = mutableListOf<FractionFixHistory>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val item = FractionFixHistory(
                    id = obj.getString("id"),
                    inputMode = FractionInputMode.valueOf(obj.getString("inputMode")),
                    originalNumerator = if (obj.has("originalNumerator") && !obj.isNull("originalNumerator")) obj.getString("originalNumerator") else null,
                    originalDenominator = if (obj.has("originalDenominator") && !obj.isNull("originalDenominator")) obj.getString("originalDenominator") else null,
                    originalWhole = if (obj.has("originalWhole") && !obj.isNull("originalWhole")) obj.getString("originalWhole") else null,
                    originalDecimal = if (obj.has("originalDecimal") && !obj.isNull("originalDecimal")) obj.getString("originalDecimal") else null,
                    simplifiedNumerator = obj.getString("simplifiedNumerator"),
                    simplifiedDenominator = obj.getString("simplifiedDenominator"),
                    calculatedAt = obj.getLong("calculatedAt")
                )
                list.add(item)
            }
        } catch (_: Exception) {
            return emptyList()
        }
        return list
    }

    /**
     * Saves a new calculation to history.
     * Enforces the maximum limit of 10 items (removes oldest when 11th added).
     * Prevents exact duplicate consecutive entries.
     */
    @Synchronized
    fun saveHistoryItem(item: FractionFixHistory): List<FractionFixHistory> {
        val current = getHistory().toMutableList()

        // Rapid input & duplicate protection:
        // Do not add if the newest item already has the identical input mode and values
        val newest = current.firstOrNull()
        if (newest != null &&
            newest.inputMode == item.inputMode &&
            newest.originalNumerator == item.originalNumerator &&
            newest.originalDenominator == item.originalDenominator &&
            newest.originalWhole == item.originalWhole &&
            newest.originalDecimal == item.originalDecimal &&
            newest.simplifiedNumerator == item.simplifiedNumerator &&
            newest.simplifiedDenominator == item.simplifiedDenominator
        ) {
            return current
        }

        // Add to front (newest first)
        current.add(0, item)

        // Enforce maximum 10 items
        while (current.size > MAX_HISTORY_ITEMS) {
            current.removeAt(current.size - 1)
        }

        persistList(current)
        return current
    }

    /**
     * Deletes a single history item by ID.
     */
    @Synchronized
    fun deleteHistoryItem(id: String): List<FractionFixHistory> {
        val current = getHistory().toMutableList()
        val changed = current.removeAll { it.id == id }
        if (changed) {
            persistList(current)
        }
        return current
    }

    /**
     * Clears all history.
     */
    @Synchronized
    fun clearHistory() {
        prefs.edit().remove(KEY_HISTORY).apply()
    }

    private fun persistList(list: List<FractionFixHistory>) {
        val jsonArray = JSONArray()
        for (item in list) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("inputMode", item.inputMode.name)
                put("originalNumerator", item.originalNumerator)
                put("originalDenominator", item.originalDenominator)
                put("originalWhole", item.originalWhole)
                put("originalDecimal", item.originalDecimal)
                put("simplifiedNumerator", item.simplifiedNumerator)
                put("simplifiedDenominator", item.simplifiedDenominator)
                put("calculatedAt", item.calculatedAt)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_HISTORY, jsonArray.toString()).apply()
    }

    companion object {
        private const val PREFS_NAME = "fractionfix_history"
        private const val KEY_HISTORY = "history_entries"
        const val MAX_HISTORY_ITEMS = 10
    }
}
