package com.example.starcatalog.data

import com.example.starcatalog.model.StarData
import kotlinx.coroutines.delay

class StarRepository {

    // Placeholder for API call
    suspend fun getStars(query: String): List<StarData> {
        // Simulate network delay
        delay(500)
        
        // Return dummy data for now
        return listOf(
            StarData("1", "Sirius", "Canis Major", 8.6),
            StarData("2", "Betelgeuse", "Orion", 642.5),
            StarData("3", "Rigel", "Orion", 860.0),
            StarData("4", "Vega", "Lyra", 25.0),
            StarData("5", "Alpha Centauri", "Centaurus", 4.37)
        ).filter { it.name.contains(query, ignoreCase = true) }
    }

    suspend fun getVisitedStars(): List<StarData> {
        delay(300)
        return listOf(
            StarData("1", "Sirius", "Canis Major", 8.6, isVisited = true),
            StarData("5", "Alpha Centauri", "Centaurus", 4.37, isVisited = true)
        )
    }
}
