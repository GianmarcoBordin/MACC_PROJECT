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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import macc.AR.compose.navgraph.Route
import macc.signinup.R

@Composable
fun ArHomeScreen( navController: NavController) {
    // mutable state

    Surface (color = Color.Black){
        Column {
            TopAppBar(
                title = { Text(text = "AR Home") },
                actions = {
                    IconButton(onClick = {
                        navController.navigate(Route.SettingsScreen.route)
                    }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
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
        BottomNavigationContent()
    }
}

@Composable
fun BottomNavigationContent() {
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
                            onClick = { selectedIndex = index },
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
    // Replace R.drawable.your_logo with the resource ID of your PNG file
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
    val navController = rememberNavController()
    ArHomeScreen(  navController =navController )
}
