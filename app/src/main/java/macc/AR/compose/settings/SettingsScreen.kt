package macc.AR.compose.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import macc.AR.compose.authentication.SettingsViewModel
import macc.AR.compose.authentication.components.BackButton
import macc.AR.compose.navgraph.Route
import macc.AR.data.manager.SettingsManagerImpl
import macc.AR.domain.usecase.settings.FetchUserProfile
import macc.AR.domain.usecase.settings.SettingsUseCases
import macc.AR.domain.usecase.settings.Subscribe
import macc.AR.domain.usecase.settings.Update

@Composable
fun SettingsScreen(
    settingsHandler:(UpdateEvent.Update)-> Unit,
    viewModel: SettingsViewModel,
    navController:NavController
) {
    // init state
    val userProfile by viewModel.userProfile.collectAsState()

    var newName by remember { mutableStateOf("") }
    var newEmail by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    // observed state
    val data by viewModel.data.observeAsState()

    BackButton(
        navController = navController,
        modifier = Modifier
            .size(50.dp)
            .clip(CircleShape) // Change the shape of the button here
            .background(Color.DarkGray)
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "User Settings",
            style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // Name field
        OutlinedTextField(
            value = newName,
            onValueChange = { newName = it },
            label = { Text(userProfile?.displayName ?: "New Name") },
            shape = RoundedCornerShape(size = 20.dp),
            modifier = Modifier.fillMaxWidth()
        )

        // Email field
        OutlinedTextField(
            value = newEmail,
            onValueChange = { newEmail = it },
            label = { Text(userProfile?.email ?: "New Email") },
            shape = RoundedCornerShape(size = 20.dp),
            modifier = Modifier.fillMaxWidth()
        )

        // Password field
        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            shape = RoundedCornerShape(size = 20.dp),
            label = { Text("New Password") },
            modifier = Modifier.fillMaxWidth()
        )

        // Save button
        Button(
            onClick = {
                settingsHandler(UpdateEvent.Update(newName,newEmail, newPassword))
            },
            shape = RoundedCornerShape(size = 20.dp),
            modifier = Modifier
                .padding(top = 16.dp)
                .align(Alignment.End)
        ) {
            Text(text = "Save")
        }
        // Observe changes in data
        if (data != null) {
            // Display data
            Text(text = data!!.toString(),color = if (data.equals("Update Success")) Color.Green else Color.Red)
            // Change page if all ok
            if(data.equals("Update Success")) {
                navController.navigate(Route.HomeScreen.route)
            }

        }

    }
}


@Preview(showBackground = true)
@Composable
fun PreviewSignInScreen() {
    val settingsManager= SettingsManagerImpl()
    val viewModel = remember { SettingsViewModel(SettingsUseCases(update = Update(settingsManager),fetch= FetchUserProfile(settingsManager),subscribe=Subscribe(settingsManager)))}
    val navController = rememberNavController()
    SettingsScreen(viewModel = viewModel,navController=navController,settingsHandler={})
}
