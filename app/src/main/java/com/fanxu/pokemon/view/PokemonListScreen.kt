package com.fanxu.pokemon.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.fanxu.pokemon.ui.theme.components.ErrorState
import com.fanxu.pokemon.viewmodel.PokemonListViewModel

@Composable
fun PokemonListScreen(
    navController: NavController,
    viewModel: PokemonListViewModel = viewModel()
) {
    val pokemonList = viewModel.pokemonList.collectAsState()
    val isLoading = viewModel.isLoading.collectAsState()
    val errorMessage = viewModel.errorMessage.collectAsState()

    LaunchedEffect(pokemonList) {
        viewModel.getPokemonList()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Button(
                onClick = { viewModel.getPokemonList() }
            ) {
                Text(text = "Reload")
            }

            if (errorMessage.value != null) {
                ErrorState()
            } else if (isLoading.value) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(200.dp),
                        color = Color.Blue,
                        trackColor = Color.Red
                    )
                }
            } else {
                LazyColumn {
                    pokemonList.value?.let { results ->
                        items(results.results.size) { index ->
                            val name = results.results[index].name
                            val id = index + 1 // La API empieza en 1, no en 0
                            val imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png"

                            PokemonCell(
                                modifier = Modifier
                                    .clickable {
                                        navController.navigate("details/$name")
                                    },
                                index = "$id",
                                name = name,
                                imageUrl = imageUrl
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PokemonCell(
    modifier: Modifier = Modifier,
    index: String,
    name: String,
    imageUrl: String
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier.padding(start = 20.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = index, fontSize = 20.sp)
            Image(
                modifier = Modifier.size(64.dp),
                painter = rememberImagePainter(
                    data = imageUrl,
                    builder = {
                        transformations(CircleCropTransformation())
                    }
                ),
                contentDescription = "Imagen de $name"
            )
            Text(text = name, fontSize = 20.sp, modifier = Modifier.padding(start = 16.dp))
        }
        Divider(color = Color.Gray, modifier = Modifier.padding(top = 20.dp))
    }
}


