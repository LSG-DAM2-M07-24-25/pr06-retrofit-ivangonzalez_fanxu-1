package com.fanxu.pokemon.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import androidx.compose.ui.graphics.painter.Painter
import com.fanxu.pokemon.ui.theme.components.ErrorState
import com.fanxu.pokemon.model.PokemonDetailsModel
import com.fanxu.pokemon.model.PokemonDetailsTypeItemModel
import com.fanxu.pokemon.viewmodel.PokemonDetailsViewModel

@Composable
fun PokemonDetailsScreen(
    navController: NavController,
    name: String
) {
    val viewModel = remember { PokemonDetailsViewModel() }

    // MARK: - State
    val pokemonDetails = viewModel.pokemonDetails.collectAsState()
    val isLoading = viewModel.isLoading.collectAsState()
    val gotError = viewModel.gotError.collectAsState()

    // Usar DisposableEffect para asegurar que fetchDetails se llama solo una vez
    DisposableEffect(key1 = viewModel) {
        viewModel.fetchDetails(name)
        onDispose { /* Limpieza si es necesaria */ }
    }

    Content(
        isLoading = isLoading.value,
        gotError = gotError.value,
        pokemonDetails = pokemonDetails.value
    )
}

@Composable
private fun Content(
    isLoading: Boolean,
    gotError: Boolean,
    pokemonDetails: PokemonDetailsModel?
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if(isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(200.dp),
                color = Color.Blue,
                trackColor = Color.Red
            )
        } else if(gotError) {
            ErrorState()
        } else {
            pokemonDetails?.let { details ->
                DetailedContent(details = details)
            }
        }
    }
}

@Composable
private fun DetailedContent(details: PokemonDetailsModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Imagen del Pokémon
        AsyncImage(url = details.sprite.imageURL)

        // Nombre del Pokémon
        Text(
            modifier = Modifier
                .padding(top = 16.dp, bottom = 24.dp)
                .fillMaxWidth(),
            text = details.name.capitalize(),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            fontSize = 28.sp,
            color = MaterialTheme.colorScheme.primary
        )

        // Tarjeta de información
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Tipos de Pokémon
                Text(
                    text = "Tipos",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    details.types.forEach { typeItem ->
                        TypeBadge(type = typeItem)
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 12.dp))

                // Características físicas
                Text(
                    text = "Características",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Altura
                InfoRow(label = "Altura", value = "${details.height / 10.0} m")

                // Peso
                InfoRow(label = "Peso", value = "${details.weight / 10.0} kg")
            }
        }
    }
}

@Composable
private fun TypeBadge(type: PokemonDetailsTypeItemModel) {
    val typeColor = getTypeColor(type.type.name)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(typeColor)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = type.type.name.capitalize(),
            color = Color.White,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            fontWeight = FontWeight.Medium
        )

        Text(
            text = value,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
private fun AsyncImage(url: String) {
    val painter: Painter = rememberImagePainter(data = url, builder = {
        transformations(CircleCropTransformation())
    })

    Image(
        modifier = Modifier.size(180.dp),
        painter = painter,
        contentDescription = "Imagen de Pokémon"
    )
}

// Función para obtener un color basado en el tipo de Pokémon
private fun getTypeColor(type: String): Color {
    return when (type.lowercase()) {
        "normal" -> Color(0xFFA8A77A)
        "fire" -> Color(0xFFEE8130)
        "water" -> Color(0xFF6390F0)
        "electric" -> Color(0xFFF7D02C)
        "grass" -> Color(0xFF7AC74C)
        "ice" -> Color(0xFF96D9D6)
        "fighting" -> Color(0xFFC22E28)
        "poison" -> Color(0xFFA33EA1)
        "ground" -> Color(0xFFE2BF65)
        "flying" -> Color(0xFFA98FF3)
        "psychic" -> Color(0xFFF95587)
        "bug" -> Color(0xFFA6B91A)
        "rock" -> Color(0xFFB6A136)
        "ghost" -> Color(0xFF735797)
        "dragon" -> Color(0xFF6F35FC)
        "dark" -> Color(0xFF705746)
        "steel" -> Color(0xFFB7B7CE)
        "fairy" -> Color(0xFFD685AD)
        else -> Color.Gray
    }
}

// Extensión para capitalizar la primera letra de un string
private fun String.capitalize(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}