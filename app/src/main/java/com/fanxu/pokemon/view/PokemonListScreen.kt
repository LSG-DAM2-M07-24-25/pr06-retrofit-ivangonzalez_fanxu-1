package com.fanxu.pokemon.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.fanxu.pokemon.R
import com.fanxu.pokemon.model.PokemonListItem
import com.fanxu.pokemon.ui.theme.components.ErrorState
import com.fanxu.pokemon.viewmodel.PokemonListViewModel

@Composable
fun PokemonListScreen(
    navController: NavController,
    viewModel: PokemonListViewModel = viewModel()
) {
    val filteredPokemonList = viewModel.filteredPokemonList.collectAsState()
    val isLoading = viewModel.isLoading.collectAsState()
    val errorMessage = viewModel.errorMessage.collectAsState()
    val searchQuery = viewModel.searchQuery.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getPokemonList()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 5.dp, horizontal = 16.dp),
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

            Spacer(modifier = Modifier.height(16.dp))

            SearchBar(
                searchQuery = searchQuery.value,
                onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (errorMessage.value != null) {
                ErrorState()
            } else if (isLoading.value && filteredPokemonList.value.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                if (filteredPokemonList.value.isEmpty() && searchQuery.value.isNotEmpty()) {
                    EmptySearchResults()
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredPokemonList.value.size) { index ->
                            val pokemon = filteredPokemonList.value[index]
                            PokemonCellItem(
                                pokemon = pokemon,
                                viewModel = viewModel,
                                onPokemonClicked = { navController.navigate("details/${pokemon.name}") }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PokemonCellItem(
    pokemon: PokemonListItem,
    viewModel: PokemonListViewModel,
    onPokemonClicked: () -> Unit
) {
    val pokemonId = viewModel.getPokemonId(pokemon)
    val imageUrl = viewModel.getPokemonImageUrl(pokemon)

    PokemonCell(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPokemonClicked() },
        index = pokemonId,
        name = pokemon.name,
        imageUrl = imageUrl
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        modifier = modifier
            .height(60.dp),
        placeholder = { Text("Buscar Pokémon") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Buscar"
            )
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { onSearchQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Limpiar búsqueda"
                    )
                }
            }
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = Color.Gray
        ),
        shape = RoundedCornerShape(6.dp),
        singleLine = true
    )
}

@Composable
fun EmptySearchResults() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.pokeball),
                contentDescription = "No hay resultados",
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No se encontraron Pokémon",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Intenta con otro nombre",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}