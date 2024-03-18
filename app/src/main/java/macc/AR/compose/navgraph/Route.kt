package macc.AR.compose.navgraph

sealed class Route(
    val route: String
){
    data object OnBoardingScreen : Route(route = "onBoardingScreen")
    data object HomeScreen : Route(route = "homeScreen")
    data object SignInScreen : Route(route = "signInScreen")
    data object SignUpScreen : Route(route = "signUpScreen")
    data object EmailScreen : Route(route = "emailScreen")
    data object SettingsScreen : Route(route = "settingsScreen")
    data object SearchScreen : Route(route = "searchScreen")
    data object MapScreen : Route(route = "mapScreen")
    data object RankScreen : Route(route = "rankScreen")
    data object AppStartNavigation : Route(route = "appStartNavigation")

}
