package com.talhasari.growlistapp.ui.theme.screens.encyclopedia

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.talhasari.growlistapp.navigation.Screen
import com.talhasari.growlistapp.ui.theme.screens.dashboard.PlantTypeCard

@Composable
fun EncyclopediaScreen(
    navController: NavController,
    encyclopediaViewModel: EncyclopediaViewModel = viewModel()
) {
    val uiState by encyclopediaViewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    Column(modifier = Modifier.fillMaxSize()) {

        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = { encyclopediaViewModel.onSearchQueryChanged(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            label = { Text("Bitki Ara (İsim, Bilimsel Ad)") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Ara") },
            singleLine = true
        )

        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.error != null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = uiState.error!!, color = Color.Red)
            }
        } else {

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.displayedPlants, key = { it.id }) { plantType ->
                    PlantTypeCard(plantType = plantType) {
                        navController.navigate(Screen.EncyclopediaDetail.createRoute(plantType.id))
                    }
                }


                item {
                    if (uiState.isLoadingMore) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }


            LaunchedEffect(listState) {
                val layoutInfo = listState.layoutInfo
                if (!uiState.isLoadingMore && !uiState.endReached && layoutInfo.visibleItemsInfo.isNotEmpty()) {
                    val lastVisibleItem = layoutInfo.visibleItemsInfo.last()
                    if (lastVisibleItem.index == uiState.displayedPlants.size - 1) {
                        encyclopediaViewModel.loadPlants(isInitialLoad = false)
                    }
                }
            }
        }
    }
}