package com.example.starccatalogue.ui.list

import kotlinx.serialization.Serializable

@Serializable
data class ListR (
    val starName: String,
)
data class ListRoute(val searchQuery: String? = null)