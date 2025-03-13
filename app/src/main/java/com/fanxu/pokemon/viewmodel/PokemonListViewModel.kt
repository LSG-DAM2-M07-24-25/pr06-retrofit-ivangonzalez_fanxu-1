package com.fanxu.pokemon.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fanxu.pokemon.model.PokemonListModel
import com.fanxu.pokemon.model.PokemonListItem
import com.fanxu.pokemon.api.PokemonListRepository
import com.fanxu.pokemon.api.PokemonListRepositoryInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PokemonListViewModel(
    private val repository: PokemonListRepositoryInterface = PokemonListRepository()
): ViewModel() {
    private val _pokemonList = MutableStateFlow<PokemonListModel?>(null)
    private val _filteredPokemonList = MutableStateFlow<List<PokemonListItem>>(emptyList())
    private val _errorMessage = MutableStateFlow<String?>(null)
    private val _isLoading = MutableStateFlow<Boolean>(true)
    private val _searchQuery = MutableStateFlow("")

    val pokemonList: StateFlow<PokemonListModel?> get() = _pokemonList.asStateFlow()
    val filteredPokemonList: StateFlow<List<PokemonListItem>> get() = _filteredPokemonList.asStateFlow()
    val errorMessage: StateFlow<String?> get() = _errorMessage.asStateFlow()
    val isLoading: StateFlow<Boolean> get() = _isLoading.asStateFlow()
    val searchQuery: StateFlow<String> get() = _searchQuery.asStateFlow()

    fun getPokemonList() {
        viewModelScope.launch {
            _isLoading.value = true
            val response = repository.getPokemonList(0,1025)
            if(response.isSuccessful) {
                val body = response.body()
                if(body != null) {
                    Log.d("Success", "${body.results.size} Pokémon cargados")
                    _pokemonList.value = body
                    updateFilteredList()
                    _isLoading.value = false
                }
            } else {
                val error = response.errorBody()
                if(error != null) {
                    Log.d("Pokemon List Error", error.string())
                    _errorMessage.value = error.string()
                    _isLoading.value = false
                }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        updateFilteredList()
    }

    private fun updateFilteredList() {
        val currentList = _pokemonList.value?.results ?: emptyList()
        val query = _searchQuery.value.trim().lowercase()

        if (query.isEmpty()) {
            _filteredPokemonList.value = currentList
            return
        }

        _filteredPokemonList.value = currentList.filter { pokemon ->
            pokemon.name.contains(query)
        }
    }

    fun getPokemonId(pokemon: PokemonListItem): String {
        return pokemon.id
    }

    fun getPokemonImageUrl(pokemon: PokemonListItem): String {
        return pokemon.imageUrl
    }
}