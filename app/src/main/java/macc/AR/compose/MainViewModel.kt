package macc.AR.compose

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.onEach
import macc.AR.compose.navgraph.Route
import macc.AR.domain.usecase.appEntry.AppEntryUseCases
import macc.AR.domain.usecase.auth.AuthenticationUseCases
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    appEntryUseCases: AppEntryUseCases,
    private val authenticationUseCases: AuthenticationUseCases
): ViewModel(){

    var startDestination by mutableStateOf(Route.AppStartNavigation.route)
    init {
        // check if user has skipped the onBoardingPage and if it is logged in
        // the view model is in charge to recover the user preferences
        // moreover a view model in general is in charge of managing the composable functions
        appEntryUseCases.readAppEntry().onEach { shouldStartFromHomeScreen ->
            startDestination = if (shouldStartFromHomeScreen) {
                if(authenticationUseCases.authCheck()){
                    Route.HomeScreen.route
                }else{
                    Route.SignInScreen.route
                }
            } else {
                //Route.OnBoardingScreen.route
                Route.HomeScreen.route
            }
            delay(300)
        }
    }


}