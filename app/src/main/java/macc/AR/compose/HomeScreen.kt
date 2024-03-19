package macc.AR.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.BottomAppBar
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import macc.AR.compose.authentication.AuthenticationViewModel
import macc.AR.data.manager.AuthManagerImpl
import macc.AR.domain.usecase.auth.AuthCheck
import macc.AR.domain.usecase.auth.AuthenticationUseCases
import macc.AR.domain.usecase.auth.BioSignIn
import macc.AR.domain.usecase.auth.Confirm
import macc.AR.domain.usecase.auth.SendEmail
import macc.AR.domain.usecase.auth.SignIn
import macc.AR.domain.usecase.auth.SignUp
import macc.AR.domain.usecase.auth.Subscribe

@Composable
fun ArHomeScreen(settingsHandler: (/*TODO*/)->Unit, viewModel: AuthenticationViewModel, navController: NavController) {
    // mutable state
    var isSettingsVisible by remember { mutableStateOf(false) }

    Surface {
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
               // TODO ArContent goes here
                if (isSettingsVisible) {
                    SettingsSidebar(onClose = { isSettingsVisible = false })
                }
            }
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
        }
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BottomNavigationContent()
    }
}

@Composable
fun BottomNavigationContent() {
    val items = listOf("Scan", "Rank", "Map")
    var selectedIndex by remember { mutableStateOf(0) }

    BottomAppBar(
        modifier=Modifier.fillMaxWidth(),
        content = {
            BottomNavigation {
                items.forEachIndexed { index, item ->
                    BottomNavigationItem(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        icon = {
                            // You can set icons for each destination if you have
                            // specific icons for "Scan", "Rank", and "Map"
                        },
                        label = {
                            Text(text = item)
                        }
                    )
                }
            }
        }
    )
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
            Button(onClick = onClose) {
                Text(text = "Close")
            }
        }
    }
}


@Preview
@Composable
fun HomeScreen() {
    val authManager= AuthManagerImpl()
    val navController = rememberNavController()
    val viewModel = remember { AuthenticationViewModel(AuthenticationUseCases(signIn = SignIn(authManager = authManager), signUp = SignUp(authManager),  confirm = Confirm(authManager),sendEmail= SendEmail(authManager), authCheck = AuthCheck(authManager), bioSignIn = BioSignIn(authManager), subscribe = Subscribe(authManager))) }
    ArHomeScreen(settingsHandler = {}, viewModel =viewModel , navController =navController )
}
