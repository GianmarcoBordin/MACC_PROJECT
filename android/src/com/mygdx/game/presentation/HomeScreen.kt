package com.mygdx.game.presentation


import android.content.Context
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
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

//noinspection UsingMaterialAndMaterial3Libraries

import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
//noinspection UsingMaterialAndMaterial3Libraries

import androidx.compose.material.Surface
//noinspection UsingMaterialAndMaterial3Libraries

import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Gamepad
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip

import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.mygdx.game.Multiplayer
import com.mygdx.game.presentation.Dimension.ButtonCornerShape
import com.mygdx.game.presentation.components.CustomBackHandler
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
    CustomBackHandler(
        onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher ?: return,
        enabled = true // Set to false to disable back press handling
    ) {
        // Define your custom back press behavior here
        // For example, navigate to another destination
    }

    ArAppTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                // Background
                Background()

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
                        UserGreeting(name = userProfile?.displayName?: "")
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
private fun Background() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "AR",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            fontSize = 90.sp,
            modifier = Modifier.padding(bottom = 35.dp)
        )
    }

}


@Composable
private fun SettingsButton(navController: NavController) {
    ExtendedFloatingActionButton(
        text = { Text("Settings") },
        icon = {Icon(
            imageVector = Icons.Outlined.Settings,
            contentDescription = "settings",
            tint = MaterialTheme.colorScheme.onPrimaryContainer)},
        onClick = { navController.navigate(Route.SettingsScreen.route) },

    )

}

@Composable
private fun StartGameButton(navController: NavController){
    ExtendedFloatingActionButton(
        text = { Text("Scan scene") },
        icon = {Icon(
            imageVector =  Icons.Outlined.CameraAlt,
            contentDescription = "scan scene",
            tint = MaterialTheme.colorScheme.onPrimaryContainer
            )},
        onClick = {navController.navigate(Route.ARScreen.route)},

    )

}

@Composable
private fun MapButton(navController: NavController){
    ExtendedFloatingActionButton(
        text = { Text("Map") },
        icon = {Icon(Icons.Outlined.Place, "Map")},
        onClick = { navController.navigate(Route.MapScreen.route) },

    )
}

@Composable
private fun RankButton(navController: NavController){

    ExtendedFloatingActionButton(
        text = { Text("Rank") },
        icon = {Icon(Icons.Outlined.StarBorder, "Rank")},
        onClick = {navController.navigate(Route.RankScreen.route)},

    )
}

@Composable
private fun MultiplayerButton(multiplayer: Multiplayer){

    ExtendedFloatingActionButton(
        text = { Text("Multiplayer") },
        icon = {Icon(Icons.Outlined.Gamepad, "multiplayer battle")},
        onClick = { multiplayer.onSetMultiplayer() },

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

