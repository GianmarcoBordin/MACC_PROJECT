package com.mygdx.game.presentation.navgraph

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.mygdx.game.Multiplayer
import com.mygdx.game.presentation.ArHomeScreen
import com.mygdx.game.presentation.MainViewModel
import com.mygdx.game.presentation.authentication.AuthenticationViewModel
import com.mygdx.game.presentation.authentication.screens.SignInScreen
import com.mygdx.game.presentation.authentication.screens.SignUpScreen
import com.mygdx.game.presentation.map.MapScreen
import com.mygdx.game.presentation.map.MapViewModel
import com.mygdx.game.presentation.onboarding.OnBoardingScreen
import com.mygdx.game.presentation.onboarding.OnBoardingViewModel
import com.mygdx.game.presentation.rank.RankScreen
import com.mygdx.game.presentation.rank.RankViewModel
import com.mygdx.game.presentation.scan.ARScreen
import com.mygdx.game.presentation.scan.ARViewModel
import com.mygdx.game.presentation.scan.CaptureScreen
import com.mygdx.game.presentation.settings.SettingsScreen
import com.mygdx.game.presentation.settings.SettingsViewModel

@Composable
fun NavGraph(
    multiplayer: Multiplayer,
    startDestination: String
) {
    val navController = rememberNavController()
    val authenticationViewModel : AuthenticationViewModel = init()
    val arViewModel : ARViewModel = hiltViewModel()
    NavHost(navController = navController, startDestination = startDestination){
        // construct a nested nav graph
        navigation(
            route = Route.AppStartNavigation.route,
            startDestination = startDestination
        ) {
            composable(
                route = Route.SignUpScreen.route
            ) {
                // set screen as the node state
                SignUpScreen(signInHandler = authenticationViewModel::onSignUpEvent, viewModel = authenticationViewModel, navController = navController)

            }
            composable(
                route = Route.SettingsScreen.route
            ) {
                // set screen as the node state
                val viewModel : SettingsViewModel = hiltViewModel()
                SettingsScreen(settingsHandler = viewModel::onUpdateEvent, signOutHandler = viewModel::onSignOutEvent, viewModel = viewModel, navController = navController)
            }
            composable(
                route = Route.ARScreen.route
            ) {
                // set screen as the node state
                ARScreen(focusHandler = arViewModel::onFocusEvent,
                    visibilityHandler = arViewModel::onVisibilityEvent,
                    dimensionsHandler = arViewModel::onDimensionsEvent,
                    bitmapHandler = arViewModel::onBitmapEvent,
                    readDataStoreHandler = arViewModel::onDataStoreEvent,
                    viewModel = arViewModel,
                    navController = navController)
            }
            composable(
                route = Route.RankScreen.route
            ) {
                // set screen as the node state
                val viewModel : RankViewModel = hiltViewModel()
                RankScreen(rankUpdateHandler = viewModel::onRankUpdateEvent, retryHandler = viewModel::onRetryEvent, viewModel = viewModel, navController = navController)
            }
            composable(
                route = Route.MapScreen.route
            ) {
                // set screen as the node state
                val  viewModel : MapViewModel = hiltViewModel()
                MapScreen(
                    mapUpdateHandler = viewModel::onMapUpdateEvent, mapRetryHandler = viewModel::onMapRetryEvent,
                    locationGrantedHandler = viewModel::onLocationGrantedEvent, locationDeniedHandler = viewModel::onLocationDeniedEvent,
                    routeHandler = viewModel::onRouteEvent,
                    navController = navController, viewModel = viewModel
                )
            }
            composable(
                route = Route.CaptureScreen.route
            ) {
                CaptureScreen(viewModel = arViewModel,
                    navController = navController,
                    gameHandler = arViewModel::onGameEvent,
                    lineAddHandler = arViewModel::onLineEvent,
                    lineDeleteHandler = arViewModel::onLineEvent,
                    updateDatabaseHandler = arViewModel::onUpdateDatabaseEvent,
                    addDatabaseHandler = arViewModel::onUpdateDatabaseEvent,
                    addOwnershipHandler = arViewModel::onUpdateDatabaseEvent,
                    getItemDatabaseHandler = arViewModel::onUpdateDatabaseEvent
                )
            }

            // more nodes...
        }
        // a node of the graph
        composable(
            route = Route.OnBoardingScreen.route
        ) {
            // set screen as the node state
            val viewModel : OnBoardingViewModel = hiltViewModel()
            OnBoardingScreen(
                event = viewModel::onBoardingEvent,navController
            )
        }
        composable(
            route = Route.SignInScreen.route
        ) {
            // set screen as the node state
            SignInScreen(signInHandler = authenticationViewModel::onSignInEvent, viewModel = authenticationViewModel, bioSignInHandler = authenticationViewModel::onBioSignInEvent, navController = navController)
        }
        composable(
            route = Route.HomeScreen.route
        ) {
            // set screen as the node state
            val  viewModel : MainViewModel = hiltViewModel()
            ArHomeScreen(
                multiplayer = multiplayer,
                navController = navController,
                viewModel = viewModel
            )
        }
    }

}

@Composable
private fun init(): AuthenticationViewModel {
    return hiltViewModel()
}
