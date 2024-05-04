package com.mygdx.game.presentation.authentication.screens


//noinspection UsingMaterialAndMaterial3Libraries

//noinspection UsingMaterialAndMaterial3Libraries

//noinspection UsingMaterialAndMaterial3Libraries

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mygdx.game.presentation.authentication.AuthenticationViewModel
import com.mygdx.game.presentation.authentication.events.SignUpEvent
import com.mygdx.game.presentation.navgraph.Route
import com.mygdx.game.ui.theme.ArAppTheme

// Presentation Layer
@Composable
fun SignUpScreen(signInHandler: (SignUpEvent.SignUp) -> Unit, viewModel: AuthenticationViewModel, navController: NavController) {

    // fields of interest
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPass by remember { mutableStateOf("") }
    // observed state
    val data by viewModel.data.observeAsState()
    val focusManager = LocalFocusManager.current


    ArAppTheme {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween    ){
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "SignUp",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize=30.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name",style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,) },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface), // Set the text color to white

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                shape = RoundedCornerShape(size = 20.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.onSurface, // Set the contour color when focused
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface // Set the contour color when not focused
                )
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email",style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,) },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface), // Set the text color to white

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                shape = RoundedCornerShape(size = 20.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.onSurface, // Set the contour color when focused
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface // Set the contour color when not focused
                )
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password",style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,) },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface), // Set the text color to white

                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                    }
                ),
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                shape = RoundedCornerShape(size = 20.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.onSurface, // Set the contour color when focused
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface // Set the contour color when not focused
                )
            )
            OutlinedTextField(
                value = confirmPass,
                onValueChange = { confirmPass = it },
                label = { Text("Confirm Password",style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,) },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface), // Set the text color to white

                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                shape = RoundedCornerShape(size = 20.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.onSurface, // Set the contour color when focused
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface // Set the contour color when not focused
                )
            )
            Button(
                onClick = {
                    focusManager.clearFocus()
                    signInHandler(SignUpEvent.SignUp(name,email,password,confirmPass))
                },
                shape = RoundedCornerShape(size = 20.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text("Sign Up")
            }
            val text = AnnotatedString.Builder().apply {
                pushStringAnnotation(
                    tag = "LINK",
                    annotation = "destination_page"
                )
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline
                    )
                ) {
                    append("You already have an account? Click here to signIn")
                }
                pop()
            }.toAnnotatedString()

            ClickableText(
                text = text,
                onClick = { offset ->
                    text.getStringAnnotations("LINK", offset, offset)
                        .firstOrNull()?.let {
                            navController.navigate(Route.SignInScreen.route)
                            viewModel.onNavigationComplete()
                        }
                }
            )
            // Observe changes in data
            if (data?.isNotEmpty() == true) {
                // Display data
                Text(
                    text = data!!.toString(),
                    color = if (data.equals("SignUp Success")) Color.Green else MaterialTheme.colorScheme.onError
                )
                // Change page if all ok
                if (viewModel.navigateToAnotherScreen.value==true) {
                    navController.navigate(Route.HomeScreen.route)
                    viewModel.onNavigationComplete()
                }

            }
        }

    }


}


