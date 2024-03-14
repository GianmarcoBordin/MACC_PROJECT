package macc.AR

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectIndexed

import kotlinx.coroutines.launch
import macc.AR.compose.MainViewModel
import macc.AR.compose.navgraph.NavGraph
import macc.AR.compose.onboarding.OnBoardingScreen
import macc.AR.compose.onboarding.OnBoardingViewModel
import macc.AR.domain.usecase.AppEntryUseCases
import macc.ui.theme.ArAppTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity: ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ArAppTheme(
                dynamicColor = false
            ) {
                Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                    val startDestination = viewModel.startDestination
                    NavGraph(startDestination = startDestination)
                }
            }
        }
    }
}