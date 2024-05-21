package com.mygdx.game.presentation.inventory


import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource

import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mygdx.game.data.dao.GameItem
import com.mygdx.game.presentation.Dimension
import com.mygdx.game.presentation.components.BackButton
import com.mygdx.game.presentation.inventory.components.MergeButton
import com.mygdx.game.presentation.inventory.components.SelectDialog
import com.mygdx.game.presentation.inventory.events.GameItemEvent
import com.mygdx.game.presentation.inventory.events.ItemEvent
import com.mygdx.game.presentation.inventory.events.UpdateItemsEvent
import com.mygdx.game.ui.theme.ArAppTheme
import com.mygdx.game.util.getItemDrawable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(retrieveItemsHandler: (ItemEvent.RetrieveItems) -> Unit,
                    updateBitmapHandler: (GameItemEvent.UpdateBitmap) -> Unit,
                    updateItemsHandler: (UpdateItemsEvent.UpdateMergedItems) -> Unit,
                    navController: NavController,
                    viewModel: InventoryViewModel) {

    val isLoading by viewModel.isLoading.observeAsState()
    val isError by viewModel.isError.observeAsState()

    val openDialog = remember { mutableStateOf(false) }
    ArAppTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        BackButton(onClick = { navController.popBackStack() })
                    },
                    title = {
                        Text(
                            text = "Inventory"
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            },
            floatingActionButton = {
                MergeButton(onClick = {openDialog.value = true})
            },
            floatingActionButtonPosition = FabPosition.End,
            content = { innerPadding ->
                if (isLoading == true) {
                    val progress = remember { Animatable(0f) }
                    LaunchedEffect(Unit) {

                        progress.animateTo(
                            targetValue = 1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(durationMillis = 1000),
                                repeatMode = RepeatMode.Reverse
                            )
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(300.dp))
                        CircularProgressIndicator(
                            progress = { progress.value },
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Button(
                            shape = RoundedCornerShape(size = Dimension.ButtonCornerShape),
                            onClick = {
                                retrieveItemsHandler(ItemEvent.RetrieveItems)
                            }
                        ) {
                            Text(text = "Retry")
                        }

                        if (isError == true) {
                            Text(
                                text = "Check your internet connection and retry later",
                                color = MaterialTheme.colorScheme.onError,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                } else if (isLoading == false) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.End
                        ) {

                            InventoryPage(viewModel.items, innerPadding, updateBitmapHandler)

                            if (openDialog.value){
                                SelectDialog(
                                    onDismissRequest = {openDialog.value = false},
                                    onMergeClick = {
                                        updateItemsHandler(UpdateItemsEvent.UpdateMergedItems)
                                        openDialog.value = false
                                    }
                                )
                            }


                        }
                    }

                }
            }
        )
    }

}


@Composable
fun InventoryPage(items: List<GameItem>, innerPadding: PaddingValues, updateBitmapHandler: (GameItemEvent.UpdateBitmap) -> Unit) {
    LazyColumn (
        modifier = Modifier
            .padding(innerPadding)
    ) {
        items(items.size) { index ->
            if (items[index].bitmap.height == 1 || items[index].bitmap.width == 1) {
                val bitmap = ImageBitmap.imageResource(id = getItemDrawable(items[index].rarity))
                // update the items using the view model
                updateBitmapHandler(GameItemEvent.UpdateBitmap(index, bitmap))
            }
            InventoryItem(items[index])
        }

    }
}

@Composable
fun InventoryItem(item: GameItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(start = 20.dp)
        ) {
            Image(
                modifier = Modifier
                    .scale(2.8f)
                    .align(Alignment.CenterVertically)
                    .padding(start = 10.dp),
                contentScale = ContentScale.Fit,
                painter = painterResource(id = getItemDrawable(item.rarity)),
                contentDescription = "Image of the item"
            )

            Column(
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 8.dp, start = 50.dp)
            ) {
                Text(text = "HP: ${item.hp}")
                Text(text = "Damage: ${item.damage}")
            }
        }
    }

}





