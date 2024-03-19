package macc.AR

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import macc.AR.compose.MainViewModel
import macc.AR.compose.ar.ARScreen

// the only activity present in the app
@AndroidEntryPoint
class MainActivity: ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ARScreen()
            //Route.ARScreen.route
            /*ArAppTheme(
                dynamicColor = false
            ) {
                Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                    val startDestination = viewModel.startDestination
                    NavGraph(startDestination = startDestination)
                }
            }

             */
        }
    }
}