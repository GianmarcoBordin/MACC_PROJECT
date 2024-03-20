package macc.AR.compose.navgraph

import ARScreen
import SignInScreen
import SignUpScreen
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import macc.AR.compose.ArHomeScreen
import macc.AR.compose.ar.ARViewModel
import macc.AR.compose.authentication.AuthenticationViewModel
import macc.AR.compose.authentication.SettingsViewModel
import macc.AR.compose.authentication.screens.EmailScreen
import macc.AR.compose.onboarding.OnBoardingScreen
import macc.AR.compose.onboarding.OnBoardingViewModel
import macc.AR.compose.settings.SettingsScreen

@Composable
fun NavGraph(
    startDestination: String
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = startDestination){
        // construct a nested nav graph
        navigation(
            route = Route.AppStartNavigation.route,
            startDestination = Route.OnBoardingScreen.route
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
                val viewModel : AuthenticationViewModel = hiltViewModel()
                SignInScreen(signInHandler = viewModel::onSignInEvent, viewModel = viewModel, bioSignInHandler = viewModel::onBioSignInEvent, navController = navController)
            }
            composable(
                route = Route.SignUpScreen.route
            ) {
                // set screen as the node state
                val viewModel : AuthenticationViewModel = hiltViewModel()
                SignUpScreen(signInHandler = viewModel::onSignUpEvent, viewModel = viewModel, navController = navController)

            }
            composable(
                route = Route.EmailScreen.route
            ) {
                // set screen as the node state
                val viewModel : AuthenticationViewModel = hiltViewModel()
                EmailScreen(otpHandler = viewModel::onEmailEvent, viewModel = viewModel, navController = navController)
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
                // set screen as the node state
                val viewModel : AuthenticationViewModel = hiltViewModel()
                ArHomeScreen(
                    navController = navController
                )
            }
            composable(
                route = Route.ARScreen.route
            ) {
                // set screen as the node state
                val viewModel : ARViewModel = hiltViewModel()
                ARScreen()
            }
            // more nodes...


        }
    }

}