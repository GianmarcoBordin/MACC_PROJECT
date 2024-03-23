package macc.AR

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import macc.AR.compose.MainViewModel
import macc.AR.compose.ar.ARScreen
import macc.ui.theme.ArAppTheme


// the only activity present in the app
@AndroidEntryPoint
class MainActivity: ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ArAppTheme(
                dynamicColor = false
            ) {
                ARScreen()
                /*Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                    val startDestination = viewModel.startDestination
                    NavGraph(startDestination = startDestination)
                }*/
            }
        }
    }
    }
