package com.fanxu.pokemon.room

import com.fanxu.pokemon.model.CaughtPokemon
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CaughtPokemonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemon(pokemon: CaughtPokemon)

    @Delete
    suspend fun deletePokemon(pokemon: CaughtPokemon)

    @Query("SELECT * FROM caught_pokemon")
    fun getAllCaughtPokemon(): Flow<List<CaughtPokemon>>

    @Query("SELECT EXISTS(SELECT 1 FROM caught_pokemon WHERE name = :name LIMIT 1)")
    fun isPokemonCaught(name: String): Flow<Boolean>
}