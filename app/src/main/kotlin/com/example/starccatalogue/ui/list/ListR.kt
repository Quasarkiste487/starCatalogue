package com.example.starccatalogue.ui.list

import kotlinx.serialization.Serializable

@Serializable
data class ListR (
    val starName: String,
)
@Serializable
data class ListRoute(val searchQuery: String? = null)