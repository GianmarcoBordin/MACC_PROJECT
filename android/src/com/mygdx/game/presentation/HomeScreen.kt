package com.mygdx.game.presentation


import androidx.compose.foundation.Image
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
//noinspection UsingMaterialAndMaterial3Libraries

import androidx.compose.material.Button
//noinspection UsingMaterialAndMaterial3Libraries

import androidx.compose.material.Surface
//noinspection UsingMaterialAndMaterial3Libraries

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.mygdx.game.MainActivity
import com.mygdx.game.Multiplayer
import com.mygdx.game.R
import com.mygdx.game.presentation.components.LogoUserImage
import com.mygdx.game.presentation.components.UserGreeting
import com.mygdx.game.presentation.multiplayer.StartEvent
import com.mygdx.game.presentation.navgraph.Route

import com.mygdx.game.ui.theme.ArAppTheme


@Composable
fun ArHomeScreen(
    multiplayer: Multiplayer,
    navController: NavController,
    viewModel: MainViewModel
) {
    // mutable state
    val userProfile by viewModel.userProfile.observeAsState()
    //lifecycle
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.resume()

            }
            if (event == Lifecycle.Event.ON_PAUSE) {
                viewModel.release()

            }
            if (event == Lifecycle.Event.ON_DESTROY) {
                viewModel.release()

            }
            if (event == Lifecycle.Event.ON_STOP) {
                viewModel.release()

            }
            if (event == Lifecycle.Event.ON_START) {
                viewModel.resume()

            }
            if (event == Lifecycle.Event.ON_CREATE) {
                viewModel.resume()

            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
        // if startProcessIndicatorAnimation() then stop

    }
    ArAppTheme {
        Surface(color = Color.Black) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                // Background image
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Background Image",
                    modifier = Modifier.fillMaxSize()
                )

                // Menu buttons
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        LogoUserImage(name = userProfile?.displayName ?: "")
                        UserGreeting(name = userProfile?.displayName?: "", color = Color.White)
                        Button(
                            onClick = {navController.navigate(Route.SettingsScreen.route)},
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .size(width = 150.dp, height = 50.dp)
                                .clip(RoundedCornerShape(16.dp))
                        ) {
                            Text(text = "Settings")
                        }
                    }

                    // Grid layout for buttons
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.align(Alignment.BottomCenter)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Button(
                                    onClick = {navController.navigate(Route.RankScreen.route)},
                                    modifier = Modifier
                                        .size(width = 150.dp, height = 50.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                ) {
                                    Text(text = "Rank")
                                }
                                Spacer(modifier = Modifier.width(24.dp))
                                Button(
                                    onClick = {navController.navigate(Route.MapScreen.route)},
                                    modifier = Modifier
                                        .size(width = 150.dp, height = 50.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                ) {
                                    Text(text = "Map")
                                }
                            }
                            Spacer(modifier = Modifier.height(30.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Button(
                                    onClick = {navController.navigate(Route.ARScreen.route)},
                                    modifier = Modifier
                                        .size(width = 150.dp, height = 50.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                ) {
                                    Text(text = "Start Game")
                                }

                                Spacer(modifier = Modifier.width(24.dp))

                                Button(
                                    onClick = { multiplayer.onSetMultiplayer() },
                                    modifier = Modifier
                                        .size(width = 150.dp, height = 50.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                ) {
                                    Text(text = "Multiplayer")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
