package com.talhasari.growlistapp.utils

import java.util.concurrent.TimeUnit

fun formatTimeRemaining(lastActionMillis: Long?, intervalDays: Int): String {
    if (intervalDays <= 0) return "Belirtilmemiş"
    val currentTime = System.currentTimeMillis()
    val nextActionTime = (lastActionMillis ?: currentTime) + TimeUnit.DAYS.toMillis(intervalDays.toLong())

    val remainingMillis = nextActionTime - currentTime

    if (remainingMillis <= 0) {
        return "Zamanı geldi!"
    }

    val remainingDays = TimeUnit.MILLISECONDS.toDays(remainingMillis)
    val remainingHours = TimeUnit.MILLISECONDS.toHours(remainingMillis) % 24

    return when {
        remainingDays > 0 -> "$remainingDays gün $remainingHours saat sonra"
        else -> "$remainingHours saat sonra"
    }
}


fun frequencyToDays(frequency: String): Int {
    return try {
        val number = frequency.filter { it.isDigit() }.toIntOrNull() ?: 0
        when {
            frequency.contains("hafta", ignoreCase = true) -> number * 7
            frequency.contains("ay", ignoreCase = true) -> number * 30
            frequency.contains("gün", ignoreCase = true) -> number
            else -> 0
        }
    } catch (e: Exception) {
        0
    }
}