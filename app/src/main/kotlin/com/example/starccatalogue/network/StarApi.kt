package com.example.starccatalogue.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiStar(
    @param:Json(name = "name") val name: String,
    @param:Json(name = "id") val id: Long,
)
