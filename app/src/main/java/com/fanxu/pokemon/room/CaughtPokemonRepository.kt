package com.fanxu.pokemon.room

import com.fanxu.pokemon.model.CaughtPokemon
import kotlinx.coroutines.flow.Flow

class CaughtPokemonRepository(private val caughtPokemonDao: CaughtPokemonDao) {
    val allCaughtPokemon: Flow<List<CaughtPokemon>> = caughtPokemonDao.getAllCaughtPokemon()

    fun isPokemonCaught(name: String): Flow<Boolean> {
        return caughtPokemonDao.isPokemonCaught(name)
    }

    suspend fun catchPokemon(pokemon: CaughtPokemon) {
        caughtPokemonDao.insertPokemon(pokemon)
    }

    suspend fun releasePokemon(pokemon: CaughtPokemon) {
        caughtPokemonDao.deletePokemon(pokemon)
    }
}