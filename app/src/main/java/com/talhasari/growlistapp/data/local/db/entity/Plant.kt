package com.talhasari.growlistapp.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.concurrent.TimeUnit

@Entity(tableName = "plants")
data class Plant(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,
    val type: String,
    val acquisitionDate: Long,
    val location: String,


    val imageUrl: String? = null,

    val lastWateredDate: Long? = null,

    val wateringIntervalDays: Int = 7
)

fun Plant.needsWatering(): Boolean {

    if (lastWateredDate == null) {
        return false
    }

    val millisSinceLastWatering = System.currentTimeMillis() - lastWateredDate
    val daysSinceLastWatering = TimeUnit.MILLISECONDS.toDays(millisSinceLastWatering)

    return daysSinceLastWatering >= wateringIntervalDays
}