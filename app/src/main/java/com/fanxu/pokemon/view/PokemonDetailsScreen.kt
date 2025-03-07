package com.fanxu.pokemon.view

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.fanxu.pokemon.R
import com.fanxu.pokemon.ui.theme.components.ErrorState
import com.fanxu.pokemon.model.PokemonDetailsModel
import com.fanxu.pokemon.viewmodel.PokemonDetailsViewModel

@Composable
fun PokemonDetailsScreen(
    navController: NavController,
    name: String
) {
    // Get application context for the ViewModel factory
    val context = LocalContext.current
    val viewModel = viewModel<PokemonDetailsViewModel>(
        factory = PokemonDetailsViewModel.Factory(context.applicationContext as android.app.Application)
    )

    val pokemonDetails = viewModel.pokemonDetails.collectAsState()
    val isLoading = viewModel.isLoading.collectAsState()
    val gotError = viewModel.gotError.collectAsState()
    val isCaught = viewModel.isCaught.collectAsState()

    DisposableEffect(key1 = viewModel) {
        viewModel.fetchDetails(name)
        onDispose { }
    }

    Content(
        navController = navController,
        isLoading = isLoading.value,
        gotError = gotError.value,
        pokemonDetails = pokemonDetails.value,
        isCaught = isCaught.value,
        onCatchClicked = { pokemonName, imageUrl ->
            viewModel.toggleCaughtStatus(pokemonName, imageUrl)
        }
    )
}

@Composable
private fun Content(
    navController: NavController,
    isLoading: Boolean,
    gotError: Boolean,
    pokemonDetails: PokemonDetailsModel?,
    isCaught: Boolean,
    onCatchClicked: (String, String) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> CircularProgressIndicator(modifier = Modifier.size(200.dp))
            gotError -> ErrorState()
            pokemonDetails != null -> DetailedContent(
                navController = navController,
                details = pokemonDetails,
                isCaught = isCaught,
                onCatchClicked = onCatchClicked
            )
        }
    }
}

@Composable
private fun DetailedContent(
    navController: NavController,
    details: PokemonDetailsModel,
    isCaught: Boolean,
    onCatchClicked: (String, String) -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLargeScreen = configuration.screenWidthDp > 600

    var isClicked by remember { mutableStateOf(false) }

    // Animación para la rotación (agitar)
    val rotationAngle by animateFloatAsState(
        targetValue = if (isClicked) 30f else 0f,
        animationSpec = keyframes {
            durationMillis = 400
            0f at 0
            15f at 100
            -15f at 300
            0f at 400
        }
    )

    // Resetear el estado después de la animación
    LaunchedEffect(isClicked) {
        if (isClicked) {
            kotlinx.coroutines.delay(200) // Duración de la animación
            isClicked = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.padding(10.dp))
        AsyncImage(url = details.sprite.imageURL)

        Text(
            modifier = Modifier.padding(top = 16.dp, bottom = 24.dp),
            text = details.name.capitalize(),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            fontSize = if (isLargeScreen) 32.sp else 24.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = if (isLargeScreen) 32.dp else 8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Tipos", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Row(modifier = Modifier.padding(vertical = 8.dp)) {
                    details.types.forEach { type ->
                        TypeBadge(type.type.name)
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

        Spacer(modifier = Modifier.height(16.dp))

        // Add Catch/Release Button
        Button(
            onClick = {
                onCatchClicked(details.name, details.sprite.imageURL)
                isClicked = true // Activamos la animación de agitación
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            val painter = if (isCaught) {
                painterResource(id = R.drawable.pokeball)
            } else {
                painterResource(id = R.drawable.pokeball_bw)
            }

            Image(
                painter = painter,
                contentDescription = "Pokéball Image",
                modifier = Modifier
                    .size(100.dp)
                    .graphicsLayer(rotationZ = rotationAngle) // Aplicamos la rotación a la imagen
            )
        }

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Volver")
        }
    }
}

@Composable
private fun TypeBadge(type: String) {
    val typeColor = getTypeColor(type)
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(typeColor)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = type.capitalize(), color = Color.White, fontWeight = FontWeight.Medium, fontSize = 14.sp)
    }
}

@Composable
private fun AsyncImage(url: String) {
    Image(
        modifier = Modifier.size(180.dp),
        painter = rememberImagePainter(data = url, builder = { transformations(CircleCropTransformation()) }),
        contentDescription = "Imagen de Pokémon"
    )
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

fun getTypeColor(type: String): Color {
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

private fun String.capitalize(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}