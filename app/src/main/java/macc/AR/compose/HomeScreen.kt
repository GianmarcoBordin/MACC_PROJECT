package macc.AR.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import macc.AR.compose.authentication.AuthenticationViewModel
import macc.AR.compose.authentication.events.SignOutEvent
import macc.AR.compose.navgraph.Route
import macc.AR.data.manager.AuthManagerImpl
import macc.AR.domain.usecase.auth.AuthenticationUseCases
import macc.AR.domain.usecase.auth.SignIn
import macc.AR.domain.usecase.auth.SignOut
import macc.AR.domain.usecase.auth.SignUp
import macc.AR.domain.usecase.auth.Subscribe

@Composable
fun ArHomeScreen(settingsHandler: ()->Unit, signOutHandler: (SignOutEvent.SignOut) -> Unit, viewModel:AuthenticationViewModel, navController:NavController) {
    var isSettingsVisible by remember { mutableStateOf(false) }
    val signOutResult by viewModel.data.observeAsState()

    Column {
        // Your UI content here

        Button(onClick = { signOutHandler(SignOutEvent.SignOut)
        }) {
            Text(text = "Logout")
        }

        // Observe changes in data
        if (signOutResult != null) {
            // Display data
            Text(text = signOutResult!!.toString(),color = if (signOutResult.equals("Signout Success")) Color.Green else Color.Red)
            // Change page if all ok
            if(signOutResult.equals("SignOut Success")) {
                navController.navigate(Route.HomeScreen.route)
            }

        }
    }
    Column {
        TopAppBar(
            title = { Text(text = "AR Home") },
            actions = {
                IconButton(onClick = { isSettingsVisible = true }) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                }
            }
        )

        Row(modifier = Modifier.fillMaxSize()) {
            ArContent(modifier = Modifier.weight(1f))

            if (isSettingsVisible) {
                SettingsSidebar(onClose = { isSettingsVisible = false })
            }
        }
    }
}



@Composable
fun ArContent(modifier: Modifier) {
    // Content of the AR home screen
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "AR Content",
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun SettingsSidebar(onClose: () -> Unit) {
    // Settings sidebar content
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(200.dp)
            .background(MaterialTheme.colors.surface)
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.h6
            )
            // Add your settings options here
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onClose) {
                Text(text = "Close")
            }
        }
    }
}

@Preview
@Composable
fun PreviewArHomeScreen() {
    val authManager= AuthManagerImpl()
    val navController = rememberNavController()
    val viewModel = remember { AuthenticationViewModel(AuthenticationUseCases(signIn = SignIn(authManager = authManager), signUp = SignUp(authManager), signOut = SignOut(authManager), subscribe = Subscribe(authManager))) }
    ArHomeScreen(settingsHandler = {}, signOutHandler = {}, viewModel =viewModel , navController =navController )
}
