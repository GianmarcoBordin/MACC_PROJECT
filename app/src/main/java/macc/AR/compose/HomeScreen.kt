package macc.AR.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomAppBar
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import macc.AR.compose.navgraph.Route
import macc.AR.data.BiometricState
import macc.AR.data.manager.AuthManagerImpl
import macc.AR.data.manager.LocalUserManagerImpl
import macc.AR.domain.usecase.appEntry.AppEntryUseCases
import macc.AR.domain.usecase.appEntry.ReadAppEntry
import macc.AR.domain.usecase.appEntry.SaveAppEntry
import macc.AR.domain.usecase.auth.AuthCheck
import macc.AR.domain.usecase.auth.AuthenticationUseCases
import macc.AR.domain.usecase.auth.BioSignIn
import macc.AR.domain.usecase.auth.SignIn
import macc.AR.domain.usecase.auth.SignUp
import macc.AR.domain.usecase.auth.Subscribe
import macc.signinup.R

@Composable
fun ArHomeScreen( navController: NavController, viewModel: MainViewModel) {
    // mutable state
    val userProfile by viewModel.userProfile.collectAsState()
    var name by remember { mutableStateOf(userProfile?.displayName ?: "New Name") }

    Surface (color = Color.Black){
        Column {
        TopAppBar(
                title = { LogoUserImage(name = name) },
                actions = {
                    IconButton(onClick = {
                        navController.navigate(Route.SettingsScreen.route)
                    }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
            Row (modifier = Modifier
                .padding(16.dp)
            ){
                UserGreeting(name = name, color = Color.White)
                IconButton(onClick = {
                    navController.navigate(Route.SettingsScreen.route)
                }) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                }
            }
            Row(modifier = Modifier.fillMaxSize()) {
                LogoImage()
            }
        }
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
      BottomNavigationContent(navController)
    }
}

@Composable
fun BottomNavigationContent(navController: NavController) {
    val items = listOf(
        BottomNavigationItemInfo("Rank", R.drawable.rank),
        BottomNavigationItemInfo("Scan", R.drawable.search),
        BottomNavigationItemInfo("Map", R.drawable.map))
    var selectedIndex by remember { mutableStateOf(0) }

    BottomAppBar(
        modifier= Modifier
            .fillMaxWidth()
            .height(105.dp),
        contentColor = Color.White,
        content = {
                BottomNavigation {
                    items.forEachIndexed { index, item ->
                        BottomNavigationItem(
                            selected = selectedIndex == index,
                            onClick = {
                                selectedIndex = index
                                // Navigate to the corresponding screen when a bottom navigation item is clicked
                                when (index) {
                                    0 -> navController.navigate(Route.RankScreen.route)
                                    1 -> navController.navigate(Route.ARScreen.route)
                                    2 -> navController.navigate(Route.MapScreen.route)
                                }
                            },
                            modifier = Modifier.fillMaxHeight(),
                            icon = {
                                Column(
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier.padding(end = 4.dp) // Adjust padding between icon and text
                                ) {
                                    Icon(
                                        painter = painterResource(id = item.iconResId),
                                        contentDescription = item.title,
                                        modifier = Modifier.size(50.dp) // Adjust icon size

                                    )
                                    Text(
                                        text = item.title,
                                        style = TextStyle(
                                            color = Color.White,
                                            fontSize = 24.sp, // Adjust font size
                                            fontWeight = FontWeight.Bold, // Adjust font weight
                                            letterSpacing = 0.1.sp, // Adjust letter spacing
                                            lineHeight = 24.sp // Adjust line height
                                        ),
                                    )
                                }
                            }

                        )
                    }
                }

        }
    )
}



@Composable
fun LogoImage(modifier: Modifier = Modifier) {

    Image(
        painter = painterResource(id = R.drawable.logo),
        contentDescription = "Logo",
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
    )
}

data class BottomNavigationItemInfo(val title: String, val iconResId: Int)

@Preview
@Composable
fun HomeScreen() {

    val localUserManagerImpl=LocalUserManagerImpl(LocalContext.current)
    val authManager=
        AuthManagerImpl(biometricState = BiometricState("",""), firebaseAuth = null)
    val navController = rememberNavController()
    val authUseCases = remember { AuthenticationUseCases(signIn = SignIn(authManager = authManager), signUp = SignUp(authManager), authCheck=AuthCheck(authManager), bioSignIn = BioSignIn(authManager), subscribe = Subscribe(authManager)) }
    val appEntryUseCases=AppEntryUseCases(ReadAppEntry(localUserManagerImpl), SaveAppEntry(localUserManagerImpl))
    val viewmodel = remember{MainViewModel(authenticationUseCases =authUseCases , appEntryUseCases = appEntryUseCases )}
    ArHomeScreen(  navController =navController , viewModel = viewmodel )
}
