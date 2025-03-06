package com.fanxu.pokemon.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fanxu.pokemon.model.CaughtPokemon
import com.fanxu.pokemon.room.CaughtPokemonRepository
import com.fanxu.pokemon.room.PokemonDatabase
import com.fanxu.pokemon.model.PokemonDetailsModel
import com.fanxu.pokemon.api.PokemonDetailsRepository
import com.fanxu.pokemon.api.PokemonDetailsRepositoryInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PokemonDetailsViewModel(
    application: Application,
    private val repository: PokemonDetailsRepositoryInterface = PokemonDetailsRepository()
): AndroidViewModel(application) {
    // Database repository
    private val caughtRepository: CaughtPokemonRepository

    // Mutable States
    private val _pokemonDetails = MutableStateFlow<PokemonDetailsModel?>(null)
    private val _isLoading = MutableStateFlow<Boolean>(true)
    private val _gotError = MutableStateFlow<Boolean>(false)
    private val _isCaught = MutableStateFlow<Boolean>(false)

    // States
    val pokemonDetails: StateFlow<PokemonDetailsModel?> get() = _pokemonDetails.asStateFlow()
    val isLoading: StateFlow<Boolean> get() = _isLoading.asStateFlow()
    val gotError: StateFlow<Boolean> get() = _gotError.asStateFlow()
    val isCaught: StateFlow<Boolean> get() = _isCaught.asStateFlow()

    init {
        val database = PokemonDatabase.getDatabase(application)
        caughtRepository = CaughtPokemonRepository(database.caughtPokemonDao())
    }

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

                // Check if this pokemon is caught
                caughtRepository.isPokemonCaught(name).collect { isCaught ->
                    _isCaught.value = isCaught
                }
            } else {
                // Handle empty data
                Log.d("Got nothing", "Got data")
                _isLoading.value = false
            }
        }
    }

    fun toggleCaughtStatus(name: String, imageUrl: String) {
        viewModelScope.launch {
            val currentStatus = _isCaught.value
            if (currentStatus) {
                // Release pokemon
                caughtRepository.releasePokemon(CaughtPokemon(name, imageUrl))
            } else {
                // Catch pokemon
                caughtRepository.catchPokemon(CaughtPokemon(name, imageUrl))
            }
            _isCaught.value = !currentStatus
        }
    }

    // Factory class needed for passing Application to ViewModel
    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PokemonDetailsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return PokemonDetailsViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}