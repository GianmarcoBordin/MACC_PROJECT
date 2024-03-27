package macc.AR.compose.navgraph

import SignInScreen
import SignUpScreen
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import macc.AR.compose.ArHomeScreen
import macc.AR.compose.MainViewModel
import macc.AR.compose.ar.ARScreen
import macc.AR.compose.ar.ARViewModel
import macc.AR.compose.authentication.AuthenticationViewModel
import macc.AR.compose.authentication.SettingsViewModel
import macc.AR.compose.onboarding.OnBoardingViewModel
import macc.AR.compose.onboarding.screens.OnBoardingScreen
import macc.AR.compose.rank.RankScreen
import macc.AR.compose.rank.RankViewModel
import macc.AR.compose.settings.SettingsScreen

@Composable
fun NavGraph(
    startDestination: String
) {
    val navController = rememberNavController()
    val authenticationViewModel: AuthenticationViewModel= init()
    NavHost(navController = navController, startDestination = startDestination){
        // construct a nested nav graph
        navigation(
            route = Route.AppStartNavigation.route,
            startDestination = Route.SignInScreen.route
        ) {
            // a node of the graph
            composable(
                route = Route.OnBoardingScreen.route
            ) {
                // set screen as the node state
                val viewModel : OnBoardingViewModel = hiltViewModel()
                OnBoardingScreen(
                    event = viewModel::onEvent,navController
                )
            }
            composable(
                route = Route.SignInScreen.route
            ) {
                // set screen as the node state
                SignInScreen(signInHandler = authenticationViewModel::onSignInEvent, viewModel = authenticationViewModel, bioSignInHandler = authenticationViewModel::onBioSignInEvent, navController = navController)
            }
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
                route = Route.HomeScreen.route
            ) {
                // set screen as the node stateù
                val  viewModel : MainViewModel= hiltViewModel()
                ArHomeScreen(
                    navController = navController, viewModel = viewModel
                )
            }
            composable(
                route = Route.ARScreen.route
            ) {
                // set screen as the node state
                val viewModel : ARViewModel = hiltViewModel()
                ARScreen()
            }
            composable(
                route = Route.RankScreen.route
            ) {
                // set screen as the node state
                val viewModel : RankViewModel = hiltViewModel()
                RankScreen( viewModel = viewModel, navController = navController)
            }
            // more nodes...


        }
    }

}

@Composable
private fun init(): AuthenticationViewModel {
    return hiltViewModel()
}