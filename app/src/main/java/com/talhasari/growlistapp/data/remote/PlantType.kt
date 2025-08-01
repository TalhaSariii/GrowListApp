package com.talhasari.growlistapp.data.remote

data class PlantType(
    val id: String = "",
    val name: String = "",
    val scientificName: String = "",
    val imageUrl: String = "",
    val wateringIntervalDays: Int = 7,
    val lightRequirement: String = "",
    val humidityRequirement: String = ""
)