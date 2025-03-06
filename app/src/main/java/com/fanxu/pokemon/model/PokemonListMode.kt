package com.fanxu.pokemon.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PokemonListModel(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<PokemonListItem>
) : Parcelable

@Parcelize
data class PokemonListItem(
    @SerializedName("name") val name: String,
    @SerializedName("url") val url: String
): Parcelable
