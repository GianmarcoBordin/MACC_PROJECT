package com.mygdx.game.presentation.authentication.screens

import androidx.biometric.BiometricManager
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image

import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions

//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Button
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.CircularProgressIndicator

//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.OutlinedTextField
//noinspection UsingMaterialAndMaterial3Libraries

import androidx.compose.material.Text
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
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
import com.mygdx.game.R
import com.mygdx.game.presentation.Dimension.ButtonCornerShape
import com.mygdx.game.presentation.authentication.AuthenticationViewModel
import com.mygdx.game.presentation.authentication.events.BioSignInEvent
import com.mygdx.game.presentation.authentication.events.SignInEvent
import com.mygdx.game.presentation.navgraph.Route
import com.mygdx.game.ui.theme.ArAppTheme
import com.mygdx.game.util.Constants.BIO_AUTH_FAILED
import com.mygdx.game.util.Constants.BIO_AUTH_SUCCESS
import com.mygdx.game.util.Constants.LOGIN_SUCCESS


@Composable
fun SignInScreen(
    signInHandler: (SignInEvent.SignIn) -> Unit,
    bioSignInHandler:(BioSignInEvent.BioSignIn) -> Unit,
    viewModel: AuthenticationViewModel,
    navController: NavController) {
    ArAppTheme {
        DefaultSignInContent(signInHandler,
            bioSignInHandler,
            viewModel,
            navController)
    }
}


@Composable
fun DefaultSignInContent(
    signInHandler: (SignInEvent.SignIn) -> Unit,
    bioSignInHandler:(BioSignInEvent.BioSignIn) -> Unit,
    viewModel: AuthenticationViewModel,
    navController: NavController
) {
    // mutable state
    val isLoading by viewModel.isLoading.observeAsState()
    val isError by viewModel.isError.observeAsState()

    // fields of interest
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    // observed state
    val data by viewModel.data.observeAsState()
    var authenticationResult by remember { mutableStateOf("") }
    // focus
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Login",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            fontSize = 30.sp,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = {
                Text(
                    text = "Email",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                )
            },
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
            shape = RoundedCornerShape(size = ButtonCornerShape),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.onSurface, // Set the contour color when focused
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface // Set the contour color when not focused
            )

        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = {
                Text(
                    text = "Password",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                )
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                }
            ),
            textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface), // Set the text color to white

            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            shape = RoundedCornerShape(size = ButtonCornerShape),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.onSurface, // Set the contour color when focused
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface // Set the contour color when not focused
            )
        )
        Button(
            onClick = {
                focusManager.clearFocus()
                signInHandler(SignInEvent.SignIn(email, password))
            },
            shape = RoundedCornerShape(size = ButtonCornerShape),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "Sign In",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            )

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
                append("You don't have an account? Click here to signUp ")
            }
            pop()
        }.toAnnotatedString()

        ClickableText(
            text = text,
            onClick = { offset ->
                text.getStringAnnotations("LINK", offset, offset)
                    .firstOrNull()?.let {
                        navController.navigate(Route.SignUpScreen.route)
                        viewModel.onNavigationComplete()
                    }
            }
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Button(
                shape = RoundedCornerShape(size = ButtonCornerShape),
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = {
                    val biometricManager = BiometricManager.from(context)
                    if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK) == BiometricManager.BIOMETRIC_SUCCESS) {
                        bioSignInHandler(BioSignInEvent.BioSignIn(context) { success ->
                            authenticationResult = when (success) {
                                BIO_AUTH_SUCCESS -> {
                                    "Biometric authentication successful"
                                }

                                BIO_AUTH_FAILED -> {
                                    "Biometric authentication failed"
                                }

                                else -> {
                                    "Biometric authentication failed, unknown error"
                                }
                            }
                        })
                    } else {
                        authenticationResult = "Biometric authentication not available"
                    }
                }) {
                Row(verticalAlignment = Alignment.CenterVertically) {

                    Text(
                        text = "Authenticate with Biometric",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.width(8.dp)) // Add some space between the image and text

                    Image(
                        painter = painterResource(id = R.drawable.fingerprint), // Assuming fingerprint_icon is the image resource for the fingerprint
                        contentDescription = "Fingerprint Icon",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            // Observe changes in data
            if (data?.isNotEmpty() == true || authenticationResult == BIO_AUTH_SUCCESS) {
                // Display data
                    Text(
                        text = data!!.toString(),
                        color = if (data.equals(LOGIN_SUCCESS) ||data.equals(BIO_AUTH_SUCCESS) || authenticationResult == BIO_AUTH_SUCCESS) Color.Green else MaterialTheme.colorScheme.onError,
                    )

                // Change page if all ok
                if (viewModel.navigateToAnotherScreen.value == true) {
                    navController.navigate(Route.HomeScreen.route)
                    viewModel.onNavigationComplete()
                }

            }

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
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    CircularProgressIndicator(progress = progress.value, color = Color.Blue)
                    if (isError == true) {
                        Text(
                            text = "Check your internet connection and retry later",
                            color = MaterialTheme.colorScheme.onError,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

        }
    }
}
