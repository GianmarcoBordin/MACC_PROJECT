package macc.AR.compose.navgraph

import SignInScreen
import SignUpScreen
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import macc.AR.compose.authentication.AuthenticationViewModel
import macc.AR.compose.onboarding.OnBoardingScreen
import macc.AR.compose.onboarding.OnBoardingViewModel

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
                SignInScreen(signInHandler = viewModel::onSignInEvent, viewModel = viewModel, navController = navController)
            }
            composable(
                route = Route.SignUpScreen.route
            ) {
                // set screen as the node state
                val viewModel : AuthenticationViewModel = hiltViewModel()
                SignUpScreen(signInHandler = viewModel::onSignUpEvent, viewModel = viewModel, navController = navController)

            }
            composable(
                route = Route.HomeScreen.route
            ) {
                // set screen as the node state
                val viewModel : AuthenticationViewModel = hiltViewModel()
                /*
                SignInScreen(signInHandler = , viewModel = , navController = )
                )

                 */
            }
            // more nodes...


        }
    }

}