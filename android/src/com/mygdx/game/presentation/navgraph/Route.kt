package com.mygdx.game.presentation.navgraph

sealed class Route(
    val route: String
){
    data object OnBoardingScreen : Route(route = "onBoardingScreen")
    data object HomeScreen : Route(route = "homeScreen")
    data object SignInScreen : Route(route = "signInScreen")
    data object SignUpScreen : Route(route = "signUpScreen")
    data object SettingsScreen : Route(route = "settingsScreen")
    data object MapScreen : Route(route = "mapScreen")
    data object RankScreen : Route(route = "rankScreen")
    data object AppStartNavigation : Route(route = "appStartNavigation")
    data object ARScreen : Route(route = "arScreen")
    data object CaptureScreen : Route(route = "captureScreen")
    data object InventoryScreen : Route(route = "inventoryScreen")

}
