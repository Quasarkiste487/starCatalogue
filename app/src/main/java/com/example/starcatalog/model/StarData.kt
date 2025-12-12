package com.example.starcatalog.model

data class StarData(
    val id: String,
    val name: String,
    val constellation: String,
    val distanceLightYears: Double,
    val isVisited: Boolean = false
)
