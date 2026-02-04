package com.example.spaceinvaders

import android.content.Context

data class HighScore(
    val score: Int,
    val wave: Int
)

object HighScoreManager {

    private const val PREFS_NAME = "high_scores"
    private const val KEY_SCORES = "scores"
    private const val MAX_SCORES = 5

    fun saveScore(context: Context, score: Int, wave: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val scores = getScores(context).toMutableList()

        scores.add(HighScore(score, wave))

        val sorted = scores
            .sortedWith(compareByDescending<HighScore> { it.score }.thenByDescending { it.wave })
            .take(MAX_SCORES)

        val encoded = sorted.joinToString(",") { "${it.score}:${it.wave}" }

        prefs.edit()
            .putString(KEY_SCORES, encoded)
            .apply()
    }

    fun getScores(context: Context): List<HighScore> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val raw = prefs.getString(KEY_SCORES, "") ?: ""
        if (raw.isEmpty()) return emptyList()

        return raw.split(",").mapNotNull { entry ->
            val parts = entry.split(":")
            if (parts.size == 2) {
                val score = parts[0].toIntOrNull()
                val wave = parts[1].toIntOrNull()
                if (score != null && wave != null) {
                    HighScore(score, wave)
                } else null
            } else null
        }
    }
}
