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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import macc.AR.compose.authentication.SettingsViewModel
import macc.AR.compose.authentication.components.BackButton
import macc.AR.compose.navgraph.Route
import macc.AR.compose.settings.events.SignOutEvent
import macc.AR.compose.settings.events.UpdateEvent
import macc.AR.data.BiometricState
import macc.AR.data.manager.SettingsManagerImpl
import macc.AR.domain.usecase.settings.FetchUserProfile
import macc.AR.domain.usecase.settings.SettingsUseCases
import macc.AR.domain.usecase.settings.SignOut
import macc.AR.domain.usecase.settings.Subscribe
import macc.AR.domain.usecase.settings.Update

@Composable
fun SettingsScreen(
    settingsHandler:(UpdateEvent.Update)-> Unit,
    signOutHandler: (SignOutEvent.SignOut) -> Unit,
    viewModel: SettingsViewModel,
    navController:NavController
) {
    // observable state
    val userProfile by viewModel.userProfile.collectAsState()
    val data by viewModel.data.observeAsState()
    // fields of interest
    var newName by remember { mutableStateOf(userProfile?.displayName ?: "New Name") }
    var newEmail by remember { mutableStateOf(userProfile?.email ?: "New Name") }
    var newPassword by remember { mutableStateOf("New Password") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.End
    ){
    // logout button
    Button(
        onClick = {
            signOutHandler(SignOutEvent.SignOut) }
    ) {
        Text(text = "Logout")
    }
    }
    BackButton(
        navController = navController,
        modifier = Modifier
            .size(50.dp)
            .clip(CircleShape) // Change the shape of the button here
            .background(Color.Black)
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
            value = newName ,
            onValueChange = { newValue ->
                newName = newValue },
            label = { Text( "New Name") },
            shape = RoundedCornerShape(size = 20.dp),
            modifier = Modifier.fillMaxWidth()
        )

        // Email field
        OutlinedTextField(
            value = newEmail,
            onValueChange = { newValue ->
                newEmail = newValue },
            label = { Text( "New Email") },
            shape = RoundedCornerShape(size = 20.dp),
            modifier = Modifier.fillMaxWidth()
        )

        // Password field
        OutlinedTextField(
            value = newPassword,
            onValueChange = { newValue ->
                newPassword = newValue },
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(size = 20.dp),
            label = { Text("New Password") },
            modifier = Modifier.fillMaxWidth()
        )
        // Save button
        Button(
            onClick = {
                settingsHandler(UpdateEvent.Update(newName ,newEmail, newPassword))
            },
            shape = RoundedCornerShape(size = 20.dp),
            modifier = Modifier
                .padding(top = 16.dp)
                .align(Alignment.End)
        ) {
            Text(text = "Save")
        }
        // Observe changes in data
        if (data?.isNotEmpty() == true) {
            // Display data
            Text(
                text = data!!.toString(),
                color = if (data.equals("Update Success") || data.equals("SignOut Success")) Color.Green else Color.Red
            )
            // Change page if all ok
            if (viewModel.navigateToAnotherScreen.value == true) {
                val route:String
                if (data.equals("SignOut Success")){
                    route=Route.SignInScreen.route
                }else{
                    route=Route.HomeScreen.route
                }
                // navigate
                navController.navigate(route)
                viewModel.onNavigationComplete()
            }

        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewSignInScreen() {
    val settingsManager= SettingsManagerImpl(biometricState = BiometricState("",""), firebaseAuth = FirebaseAuth.getInstance())
    val viewModel = remember { SettingsViewModel(SettingsUseCases(update = Update(settingsManager),fetch= FetchUserProfile(settingsManager), signOut = SignOut(settingsManager), subscribe=Subscribe(settingsManager)))}
    val navController = rememberNavController()
    SettingsScreen(viewModel = viewModel,navController=navController,settingsHandler={}, signOutHandler = {})
}
