package com.mygdx.game.presentation.scan

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.mygdx.game.R
import com.mygdx.game.data.dao.Line
import com.mygdx.game.presentation.navgraph.Route
import com.mygdx.game.presentation.scan.events.GameEvent
import com.mygdx.game.presentation.scan.events.LineEvent
import com.mygdx.game.presentation.scan.events.UpdateDatabaseEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import com.mygdx.game.presentation.components.CustomBackHandler
import com.mygdx.game.presentation.components.ExitPopup
import com.mygdx.game.presentation.scan.events.UpdateMappingEvent
import com.mygdx.game.ui.theme.ArAppTheme


@Composable
fun CaptureScreen(viewModel: ARViewModel, navController: NavController, gameHandler: (GameEvent.StartGame) -> Unit,
                  lineAddHandler: (LineEvent.AddNewLine) -> Unit, lineDeleteHandler: (LineEvent.DeleteAllLines) -> Unit,
                  addDatabaseHandler: (UpdateDatabaseEvent.AddItem) -> Unit,
                  resetGameHandler: (GameEvent.ResetGame) -> Unit, updateMappingHandler: (UpdateMappingEvent.UpdateMapping) -> Unit) {

    val openPopup = remember { mutableStateOf(false) }
    val gameState by viewModel.state.collectAsState()

    val item = when(gameState.gameItem.rarity) {
        1 -> ImageBitmap.imageResource(id = R.drawable.gunner_green)
        2 -> ImageBitmap.imageResource(id = R.drawable.gunner_red)
        3 -> ImageBitmap.imageResource(id = R.drawable.gunner_yellow)
        4 -> ImageBitmap.imageResource(id = R.drawable.gunner_blue)
        5 -> ImageBitmap.imageResource(id = R.drawable.gunner_black)
        else -> ImageBitmap.imageResource(id = R.drawable.gunner_green)
    }
    val bullets = ImageBitmap.imageResource(id = R.drawable.bullet_stream)

    if (!gameState.isStarted) {
        // start the game
        gameHandler(GameEvent.StartGame(bullets))
    }
    CustomBackHandler(
        onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher ?: return,
        enabled = true // Set to false to disable back press handling
    ) {
        openPopup.value = true
    }


    ArAppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            viewModel.bitmap?.let {
                Image(
                    modifier = Modifier
                        .rotate(90f)
                        .scale(viewModel.imageRatio)
                        .blur(5.dp)
                        .fillMaxSize(),
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Item",
                )
            }
            if (!gameState.isGameOver) {
                Column(
                    modifier = Modifier
                        .padding(top = 32.dp, start = 32.dp, end = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    HealthBar(health = gameState.hp, maxHealth = gameState.gameItem.hp, barWidth = (viewModel.screenWidth - 64).dp, barHeight = 24.dp)
                    Text(modifier = Modifier
                        .padding(top = 6.dp),
                        color = Color.Red,
                        text = "HP: ${gameState.hp}/${gameState.gameItem.hp}"
                    )
                }
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(true) {
                            detectDragGestures(onDragEnd = {
                                lineDeleteHandler(LineEvent.DeleteAllLines)
                            }) { change, dragAmount ->
                                change.consume()

                                val line = Line(
                                    start = change.position - dragAmount,
                                    end = change.position
                                )

                                // update the game state with the lines drawn by the user
                                lineAddHandler(LineEvent.AddNewLine(line))
                            }
                        }
                ) {
                    drawItem(
                        coordinates = gameState.position,
                        itemImage = item,
                        width = gameState.gameItem.bitmap.width,
                        height = gameState.gameItem.bitmap.height
                    )
                    drawLines(
                        lines = gameState.lines
                    )

                    if (gameState.shoot) {
                        gameState.shootBitmap?.let {
                            drawItem(
                                coordinates = gameState.shootPosition,
                                itemImage = bullets,
                                width = it.width,
                                height = it.height
                            )
                        }
                    }
                }
            } else {
                Dialog(
                    onDismissRequest = {
                    },
                    properties = DialogProperties(
                        dismissOnClickOutside = false,
                        dismissOnBackPress = false
                    )
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 10.dp
                        ),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Column(
                            modifier = Modifier
                                .width(width = (viewModel.screenWidth / 2).dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                modifier = Modifier
                                    .width(width = (viewModel.screenWidth / 2).dp)
                                    .padding(16.dp),
                                textAlign = TextAlign.Center,
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                text = "Item captured!"
                            )

                            addDatabaseHandler(UpdateDatabaseEvent.AddItem)
                            updateMappingHandler(UpdateMappingEvent.UpdateMapping(gameState.itemId))

                            Text(
                                modifier = Modifier
                                    .padding(16.dp),
                                textAlign = TextAlign.Center,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                text = "Stats: \n" +
                                        "- HP: ${gameState.gameItem.hp}\n" +
                                        "- Damage: ${gameState.gameItem.damage}"
                            )
                            ElevatedButton(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(bottom = 16.dp),
                                onClick = {
                                    // whenever the session is started, reset the game state
                                    // so that the game can be played again
                                    resetGameHandler(GameEvent.ResetGame)
                                    // go back to map screen
                                    navController.navigate(Route.MapScreen.route)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
                            ) {
                                Text(
                                    color = Color.White,
                                    text = "OK"
                                )
                            }
                        }
                    }
                }
            }
        }
        ExitPopup(openPopup)
    }


}

private fun DrawScope.drawLines(lines: List<Line>) {
    lines.forEach { line ->
        drawLine(
            color = line.color,
            start = line.start,
            end = line.end,
            strokeWidth = line.strokeWidth.toPx(),
            cap = StrokeCap.Round
        )
    }
}

private fun DrawScope.drawItem(coordinates: Vector2, itemImage: ImageBitmap, width: Int, height: Int) {
    drawImage(
        image = itemImage,
        dstOffset = IntOffset(
            x = coordinates.x.toInt(),
            y = coordinates.y.toInt()
        ),
        dstSize = IntSize(width, height)
    )
}

@Composable
fun HealthBar(health: Int, maxHealth: Int, barWidth: Dp, barHeight: Dp) {
    val progress = (health.toFloat() / maxHealth.toFloat()) * 100
    Box(
        modifier = Modifier
            .size(barWidth, barHeight)
            .background(Color.Black, RoundedCornerShape(4.dp))
    ) {
        Box(
            modifier = Modifier
                .size((barWidth.value - 2).dp, (barHeight.value - 2).dp)
                .background(Color.Gray, RoundedCornerShape(4.dp))
                .align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress / 100f)
                    .background(Color.Red, RoundedCornerShape(4.dp))
            )
        }
    }
}
