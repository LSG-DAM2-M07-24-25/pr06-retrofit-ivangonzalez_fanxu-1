package com.fanxu.pokemon.api

import android.util.Log
import com.fanxu.pokemon.model.PokemonListModel
import retrofit2.Response

interface PokemonListRepositoryInterface {
    suspend fun getPokemonList(offset: Int, limit: Int): Response<PokemonListModel>
}

class PokemonListRepository(
    private val apiService: APIService = getRetrofitClient()
): PokemonListRepositoryInterface {
    override suspend fun getPokemonList(offset: Int, limit: Int): Response<PokemonListModel> {
        Log.d("Repository getPokemonList", "$offset, $limit")
        return apiService.getPokemonList(offset = offset, limit = limit)
    }
}