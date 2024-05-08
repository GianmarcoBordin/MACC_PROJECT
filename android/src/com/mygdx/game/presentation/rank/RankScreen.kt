package com.mygdx.game.presentation.rank

//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Button
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.CircularProgressIndicator
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Divider
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Surface
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.mygdx.game.presentation.Dimension.ButtonCornerShape
import com.mygdx.game.presentation.components.BackButton
import com.mygdx.game.presentation.rank.events.RankUpdateEvent
import com.mygdx.game.presentation.rank.events.RetryEvent
import com.mygdx.game.ui.theme.ArAppTheme


@Composable
fun RankScreen(
    rankUpdateHandler: (RankUpdateEvent.RankUpdate) -> Unit,
    retryHandler:(RetryEvent.Retry) -> Unit,
    viewModel: RankViewModel,
    navController: NavController
) {
    //lifecycle
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.resume()

            }
            if (event == Lifecycle.Event.ON_PAUSE) {
                viewModel.release()

            }
            if (event == Lifecycle.Event.ON_DESTROY) {
                viewModel.release()

            }
            if (event == Lifecycle.Event.ON_STOP) {
                viewModel.release()

            }
            if (event == Lifecycle.Event.ON_START) {
                viewModel.resume()


            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    ArAppTheme {
        Surface(color = MaterialTheme.colorScheme.surface) {
            Column(modifier = Modifier.fillMaxSize()) {
                BackButton(onClick = {  navController.popBackStack()})
                DefaultContent(
                    updateHandler = rankUpdateHandler,
                    retryHandler=retryHandler,
                    viewModel = viewModel
                )
            }
        }
    }
}


@Composable
fun DefaultContent(
    updateHandler: (RankUpdateEvent.RankUpdate) -> Unit,
    retryHandler: (RetryEvent.Retry) -> Unit,
    viewModel: RankViewModel
) {
    // mutable state
    val userRankingsState by viewModel.rankData.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState()
    val isError by viewModel.isError.observeAsState()


    Box(
        contentAlignment = Alignment.Center
    ) {
        if (isLoading == true) {
            val progress = remember { Animatable(0f) }
            LaunchedEffect(Unit) {

                    progress.animateTo(
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(durationMillis = 1000),
                            repeatMode = RepeatMode.Reverse
                        )
                    )

            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(300.dp))
                CircularProgressIndicator(progress = progress.value, color = Color.Blue)
                Button(
                    shape = RoundedCornerShape(size = ButtonCornerShape),
                    onClick = { retryHandler(RetryEvent.Retry())
                    }) {
                    Text(text = "Retry")
                }

                if (isError==true) {
                    Text(
                        text = "Check your internet connection and retry later",
                        color = MaterialTheme.colorScheme.onError,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else if (isLoading==false){
            Spacer(modifier = Modifier.height(500.dp))
            Column( modifier = Modifier
                .padding(16.dp)
                .background(
                    color = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(16.dp)
                ),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
                ) {
                // Header Row
                Row(
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "User",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .weight(1f) // Added weight to occupy available space
                            .padding(vertical = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Score",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .weight(1f) // Added weight to occupy available space
                            .padding(vertical = 8.dp)
                    )
                }
                LazyColumn {
                    userRankingsState?.let {
                        items(it.toList()) { userRanking ->
                            UserRankingItem(userRanking)
                            Divider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = Color.Gray,
                                thickness = 5.dp
                            )
                        }
                    }
                }
                Button(
                    shape = RoundedCornerShape(size = ButtonCornerShape),
                    onClick = { updateHandler(RankUpdateEvent.RankUpdate())
                    }) {
                    Text(text = "Refresh")
                }
            }


        }
    }
}



@Composable
fun UserRankingItem(userRanking: String) {
    val str= userRanking.split(" ")
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = str[0],style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,modifier = Modifier
                .weight(1f) // Added weight to occupy available space
                .padding(vertical = 8.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = str[1],style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,modifier = Modifier
                .weight(1f) // Added weight to occupy available space
                .padding(vertical = 8.dp))
    }
}



