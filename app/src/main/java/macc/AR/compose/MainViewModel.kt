package macc.AR.compose

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.onEach
import macc.AR.compose.navgraph.Route
import macc.AR.domain.usecase.AppEntryUseCases
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val appEntryUseCases: AppEntryUseCases
): ViewModel(){

    var startDestination by mutableStateOf(Route.AppStartNavigation.route)


    // TODO here insert code to check if user is already authenticated
    init {
        // check if user has skipped the onBoardingPage
        appEntryUseCases.readAppEntry().onEach { shouldStartFromHomeScreen ->
            startDestination = if (shouldStartFromHomeScreen) {
                Route.HomeScreen.route
            } else {
                Route.AppStartNavigation.route
            }
            delay(300)
        }
    }

}