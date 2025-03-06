package com.fanxu.pokemon.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "caught_pokemon")
data class CaughtPokemon(
    @PrimaryKey val name: String,
    val imageUrl: String,
    val dateTimeCaught: Long = System.currentTimeMillis()
)