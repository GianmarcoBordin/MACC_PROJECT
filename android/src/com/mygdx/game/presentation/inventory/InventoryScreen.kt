package com.mygdx.game.presentation.inventory

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.mygdx.game.R
import com.mygdx.game.data.dao.GameItem
import com.mygdx.game.presentation.Dimension
import com.mygdx.game.presentation.components.BackButton
import com.mygdx.game.presentation.inventory.events.GameItemEvent
import com.mygdx.game.presentation.inventory.events.ItemEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(retrieveItemsHandler: (ItemEvent.RetrieveItems) -> Unit,
                    updateBitmapHandler: (GameItemEvent.UpdateBitmap) -> Unit,
                    navController: NavController,
                    viewModel: InventoryViewModel) {

    val isLoading by viewModel.isLoading.observeAsState()
    val isError by viewModel.isError.observeAsState()

    val openDialog = remember { mutableStateOf(false) }

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
                InventoryPage(openDialog, viewModel.items, innerPadding, updateBitmapHandler)
                SelectDialog(openDialog, viewModel.items)
            }
        }
    )
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
        ) {
            Image(
                modifier = Modifier
                    .scale(5f)
                    .align(Alignment.CenterVertically)
                    .padding(start = 10.dp),
                contentScale = ContentScale.Fit,
                bitmap = item.bitmap.asImageBitmap(),
                contentDescription = "Image of the item"
            )
            // TODO distances are hardcoded
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

@Composable
fun InventoryPage(openDialog: MutableState<Boolean>, items: List<GameItem>, innerPadding: PaddingValues, updateBitmapHandler: (GameItemEvent.UpdateBitmap) -> Unit) {
    LazyColumn (
        modifier = Modifier
            .padding(innerPadding)
    ) {
        items(items.size) { index ->
            if (items[index].bitmap.height == 1 || items[index].bitmap.width == 1) {
                val bitmap = when (items[index].rarity) {
                    1 -> ImageBitmap.imageResource(id = R.drawable.gunner_green)
                    2 -> ImageBitmap.imageResource(id = R.drawable.gunner_red)
                    3 -> ImageBitmap.imageResource(id = R.drawable.gunner_yellow)
                    4 -> ImageBitmap.imageResource(id = R.drawable.gunner_blue)
                    5 -> ImageBitmap.imageResource(id = R.drawable.gunner_black)
                    else -> ImageBitmap.imageResource(id = R.drawable.gunner_green)
                }
                updateBitmapHandler(GameItemEvent.UpdateBitmap(index, bitmap))
            }
            InventoryItem(items[index])
        }
        item {
            Button(onClick = {
                openDialog.value = true
            }) {
                Text(text = "Merge")
            }
        }
    }
}

@Composable
fun SelectDialog(openDialog: MutableState<Boolean>, items: List<GameItem>) {
    if (openDialog.value) {
        val options: MutableList<Int> = mutableListOf()
        items.forEach { item ->
            options.add(item.itemId)
        }

        val (selectedOption, onOptionSelected) = remember { mutableIntStateOf(options[0]) }

        Dialog(
            onDismissRequest = {
            },
            properties = DialogProperties(
                dismissOnClickOutside = false,
                dismissOnBackPress = false
            )
        ) {
            Column(

            ) {
                options.forEach { option ->
                    Row(
                        Modifier
                            // using modifier to add max
                            // width to our radio button.
                            .fillMaxWidth()
                            // below method is use to add
                            // selectable to our radio button.
                            .selectable(
                                // this method is called when
                                // radio button is selected.
                                selected = (option == selectedOption),
                                // below method is called on
                                // clicking of radio button.
                                onClick = { onOptionSelected(option) }
                            )
                            // below line is use to add
                            // padding to radio button.
                            .padding(horizontal = 16.dp)
                    ) {
                        // below line is use to
                        // generate radio button
                        RadioButton(
                            // inside this method we are
                            // adding selected with a option.
                            selected = (option == selectedOption),
                            modifier = Modifier.padding(all = Dp(value = 8F)),
                            onClick = {
                                // inside on click method we are setting a
                                // selected option of our radio buttons.
                                onOptionSelected(option)
                            }
                        )
                        // below line is use to add
                        // text to our radio buttons.
                        Text(
                            text = option.toString(),
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
        }
    }
}