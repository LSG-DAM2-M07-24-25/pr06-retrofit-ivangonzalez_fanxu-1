package com.fanxu.pokemon.model

import retrofit2.Response

interface PokemonDetailsRepositoryInterface {
    suspend fun getPokemonDetails(name: String): Response<PokemonDetailsModel>
}

class PokemonDetailsRepository(
    private val apiService: APIService = getRetrofitClient()
): PokemonDetailsRepositoryInterface {
    override suspend fun getPokemonDetails(name: String): Response<PokemonDetailsModel> {
        return apiService.getPokemonDetails(name)
    }
}