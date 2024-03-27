package macc.AR.compose.rank

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import macc.AR.compose.BottomNavigationContent
import macc.AR.compose.navgraph.Route
import macc.AR.data.BiometricState
import macc.AR.data.api.DataRepositoryImpl
import macc.AR.data.manager.AuthManagerImpl
import macc.AR.data.manager.RankManagerImpl
import macc.AR.data.manager.SettingsManagerImpl
import macc.AR.domain.usecase.Subscribe
import macc.AR.domain.usecase.rank.Fetch
import macc.AR.domain.usecase.rank.RankUseCases

// TODO strings to constants
@Composable
fun RankScreen(
    viewModel: RankViewModel,
    navController: NavController
) {
    Surface() {
        Column {
            TopAppBar(
                title = { Text(text = "Rankings") },
                actions = {
                    IconButton(onClick = {
                        navController.navigate(Route.SettingsScreen.route)
                    }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )

            Row(modifier = Modifier.fillMaxSize()
            ) {
                DefaultContent(viewModel = viewModel)
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
    }


@Composable
fun DefaultContent( viewModel: RankViewModel) {
    // mutable state
    val userRankingsState: List<String> by viewModel.rankData.observeAsState(listOf())

    val userRankings: List<String> = userRankingsState
    val isLoading: Boolean by viewModel.isLoading.observeAsState(false)
    val errorMessage: String? by viewModel.data.observeAsState(null)

    Box(

        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else if (errorMessage != null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { /*viewModel.retry()*/ }) {
                    Text(text = "Retry")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(userRankings) { userRanking ->
                    UserRankingItem(userRanking)
                    Divider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = Color.Gray,
                        thickness = 1.dp
                    )
                }
            }
        }
    }
}




@Composable
fun UserRankingItem(userRanking: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = userRanking)
    }
}

// Preview

@Preview(showBackground = true)
@Composable
fun PreviewRankScreen() {
    val dataRepository= DataRepositoryImpl(rankApi = null)
    val rankManager= RankManagerImpl(dataRepository = dataRepository)
    val settingsManager= SettingsManagerImpl(biometricState = BiometricState("",""), firebaseAuth = null)
    val authManager=
        AuthManagerImpl(biometricState = BiometricState("",""), firebaseAuth = null)
    val viewModel=RankViewModel(RankUseCases(fetch = Fetch(rankManager), subscribe = Subscribe(settingsManager,authManager, rankManager)))
    val navController = rememberNavController()
    RankScreen(viewModel =viewModel , navController = navController)
}



