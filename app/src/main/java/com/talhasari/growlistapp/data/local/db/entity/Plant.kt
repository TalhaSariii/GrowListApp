package com.talhasari.growlistapp.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.concurrent.TimeUnit

@Entity(tableName = "plants")
data class Plant(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,
    val imageUrl: String? = null,
    val acquisitionDate: Long,
    val location: String,
    val lastWateredDate: Long? = null,
    val lastFertilizedDate: Long? = null,
    val userId: String,


    val type: String,
    val scientificName: String = "",
    val generalInfo: String = "",
    val wateringIntervalDays: Int = 7,
    val lightRequirement: String = "",
    val humidityRequirement: String = "",
    val temperatureRange: String = "",
    val difficultyLevel: String = "",
    val fertilizationFrequency: String = "",
    val pruningFrequency: String = "",
    val repottingFrequency: String = ""
)

fun Plant.needsWatering(): Boolean {
    if (lastWateredDate == null) {
        return false
    }
    val millisSinceLastWatering = System.currentTimeMillis() - lastWateredDate
    val daysSinceLastWatering = TimeUnit.MILLISECONDS.toDays(millisSinceLastWatering)
    return daysSinceLastWatering >= wateringIntervalDays
}