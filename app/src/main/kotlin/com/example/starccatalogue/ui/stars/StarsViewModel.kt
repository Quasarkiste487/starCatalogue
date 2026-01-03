package com.example.starccatalogue.ui.stars

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.starccatalogue.network.ApiStar
import com.example.starccatalogue.network.starsApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StarsViewModel: ViewModel(){
    private val _stars: MutableStateFlow<List<ApiStar>> = MutableStateFlow(emptyList())
    val stars: StateFlow<List<ApiStar>> = _stars.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val starsResponse = starsApi.getStars()
            val responseBody = starsResponse.body()

            _stars.update {
                if(starsResponse.isSuccessful && responseBody != null) {
                    responseBody
                } else {
                    emptyList()
                }
            }
        }
    }
}