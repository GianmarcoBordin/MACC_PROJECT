package com.mygdx.game.presentation


import android.content.Context
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
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
//noinspection UsingMaterialAndMaterial3Libraries

import androidx.compose.material.Surface
//noinspection UsingMaterialAndMaterial3Libraries

import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Gamepad
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.StarBorder
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.mygdx.game.Multiplayer
import com.mygdx.game.R
import com.mygdx.game.data.dao.UserProfileBundle
import com.mygdx.game.presentation.Dimension.ButtonCornerShape
import com.mygdx.game.presentation.components.LogoUserImage
import com.mygdx.game.presentation.components.UserGreeting
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
    ManageLifecycle(lifecycleOwner, viewModel)

    ArAppTheme {
        Surface(color = Color.Black) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                // Background image
                BackgroundImage()

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
                        SettingsButton(navController = navController)
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
                                RankButton(navController = navController)
                                Spacer(modifier = Modifier.width(24.dp))
                                MapButton(navController = navController)
                            }
                            Spacer(modifier = Modifier.height(30.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                StartGameButton(navController = navController)
                                Spacer(modifier = Modifier.width(24.dp))
                                MultiplayerButton(multiplayer = multiplayer)
                            }
                        }
                    }
                }
            }
        }
    }
}



@Composable
private fun BackgroundImage() {
    Image(
        painter = painterResource(id = R.drawable.logo),
        contentDescription = "Background Image",
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
private fun SettingsButton(navController: NavController) {
    ExtendedFloatingActionButton(
        text = { Text("Settings") },
        icon = {Icon(Icons.Outlined.Settings, "settings")},
        onClick = { navController.navigate(Route.SettingsScreen.route) },
        contentColor = Color.White,
        backgroundColor = Color.Gray
    )

}

@Composable
private fun StartGameButton(navController: NavController){
    /*
    Button(
        onClick = {navController.navigate(Route.ARScreen.route)},
        modifier = Modifier
            .size(width = 150.dp, height = 50.dp)
            .clip(RoundedCornerShape(ButtonCornerShape))
    ) {
        Text(text = "Start Game")
    }*/

    ExtendedFloatingActionButton(
        text = { Text("Scan scene") },
        icon = {Icon(Icons.Outlined.CameraAlt, "scan scene")},
        onClick = {navController.navigate(Route.ARScreen.route)},
        contentColor = Color.White,
        backgroundColor = Color.Gray
    )

}

@Composable
private fun MapButton(navController: NavController){
    ExtendedFloatingActionButton(
        text = { Text("Map") },
        icon = {Icon(Icons.Outlined.Place, "Map")},
        onClick = { navController.navigate(Route.MapScreen.route) },
        contentColor = Color.White,
        backgroundColor = Color.Gray
    )
}

@Composable
private fun RankButton(navController: NavController){

    ExtendedFloatingActionButton(
        text = { Text("Rank") },
        icon = {Icon(Icons.Outlined.StarBorder, "Rank")},
        onClick = {navController.navigate(Route.RankScreen.route)},
        contentColor = Color.White,
        backgroundColor = Color.Gray
    )
}

@Composable
private fun MultiplayerButton(multiplayer: Multiplayer){

    ExtendedFloatingActionButton(
        text = { Text("Multiplayer") },
        icon = {Icon(Icons.Outlined.Gamepad, "multiplayer battle")},
        onClick = { multiplayer.onSetMultiplayer() },
        contentColor = Color.White,
        backgroundColor = Color.Gray
    )
}



@Composable
private fun ManageLifecycle(lifecycleOwner: LifecycleOwner, viewModel: MainViewModel) {
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME, Lifecycle.Event.ON_START, Lifecycle.Event.ON_CREATE -> viewModel.resume()
                Lifecycle.Event.ON_PAUSE, Lifecycle.Event.ON_STOP, Lifecycle.Event.ON_DESTROY -> viewModel.release()
                else -> { }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

