package com.fanxu.pokemon.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fanxu.pokemon.model.PokemonDetailsModel
import com.fanxu.pokemon.api.PokemonDetailsRepository
import com.fanxu.pokemon.api.PokemonDetailsRepositoryInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PokemonDetailsViewModel(
    private val repository: PokemonDetailsRepositoryInterface = PokemonDetailsRepository()
): ViewModel() {
    // Mutable States
    private val _pokemonDetails = MutableStateFlow<PokemonDetailsModel?>(null)
    private val _isLoading = MutableStateFlow<Boolean>(true)
    private val _gotError = MutableStateFlow<Boolean>(false)

    // States
    val pokemonDetails: StateFlow<PokemonDetailsModel?> get() = _pokemonDetails.asStateFlow()
    val isLoading: StateFlow<Boolean> get() = _isLoading.asStateFlow()
    val gotError: StateFlow<Boolean> get() = _gotError.asStateFlow()

    fun fetchDetails(name: String) {
// Start in another thread
        viewModelScope.launch {
// Loading state
            _isLoading.value = true
            val result = repository.getPokemonDetails(name)
            val error = result.errorBody()
            val data = result.body()
            if (error != null || !result.isSuccessful) {
// Handle error state
                Log.d("Got an error", "Got an error")
                _isLoading.value = false
                _gotError.value = true
                return@launch
            }
            if (data != null) {
// Handle success case
                Log.d("Got data", "Got data")
                _isLoading.value = false
                _pokemonDetails.value = data
            } else {
// Handle empty data
                Log.d("Got nothing", "Got data")
                _isLoading.value = false
            }
        }
    }
}