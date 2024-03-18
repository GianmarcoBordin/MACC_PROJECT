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
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val appEntryUseCases: AppEntryUseCases,
): ViewModel(){

    var startDestination by mutableStateOf(Route.AppStartNavigation.route)
    // TODO if(firebaseAuth.currentUser != null){
    //          val intent = Intent(this, HomeActivity::class.java)
    //            startActivity(intent)
    //        }
    // TODO here insert code to check if user is already authenticated
    init {
        // check if user has skipped the onBoardingPage
        // the view model is in charge to recover the user preferences
        // moreover a view model in general is in charge of managing the composable functions
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