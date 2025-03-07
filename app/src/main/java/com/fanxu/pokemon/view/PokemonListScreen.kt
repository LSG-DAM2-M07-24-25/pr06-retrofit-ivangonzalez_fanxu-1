package com.fanxu.pokemon.view


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.fanxu.pokemon.R
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
            modifier = Modifier.fillMaxSize().padding(vertical = 5.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.pokedex_logo),
                    contentDescription = "Pokédex Logo",
                    modifier = Modifier.size(150.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Image(
                    painter = painterResource(id = R.drawable.pokeball),
                    contentDescription = "Pokéball Image",
                    modifier = Modifier.size(50.dp)
                )
            }



            if (errorMessage.value != null) {
                ErrorState()
            } else if (isLoading.value) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    pokemonList.value?.let { results ->
                        items(results.results.size) { index ->
                            val name = results.results[index].name
                            val id = index + 1
                            val imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png"

                            PokemonCell(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { navController.navigate("details/$name") },
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