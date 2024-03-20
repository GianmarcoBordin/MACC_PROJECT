
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import macc.AR.compose.authentication.AuthenticationViewModel
import macc.AR.compose.authentication.components.BackButton
import macc.AR.compose.authentication.events.SignUpEvent
import macc.AR.compose.navgraph.Route
import macc.AR.data.manager.AuthManagerImpl
import macc.AR.domain.usecase.auth.AuthCheck
import macc.AR.domain.usecase.auth.AuthenticationUseCases
import macc.AR.domain.usecase.auth.BioSignIn
import macc.AR.domain.usecase.auth.Confirm
import macc.AR.domain.usecase.auth.SendEmail
import macc.AR.domain.usecase.auth.SignIn
import macc.AR.domain.usecase.auth.SignUp
import macc.AR.domain.usecase.auth.Subscribe

// Presentation Layer
@Composable
fun SignUpScreen(signInHandler: (SignUpEvent.SignUp) -> Unit, viewModel: AuthenticationViewModel,navController: NavController) {

    // fields of interest
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPass by remember { mutableStateOf("") }
    // observed state
    val data by viewModel.data.observeAsState()
    val focusManager = LocalFocusManager.current

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
            text = "SignUp",
            style = androidx.compose.material3.MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = Color.Black,
            fontSize=30.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            shape = RoundedCornerShape(size = 20.dp)
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            shape = RoundedCornerShape(size = 20.dp)
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                }
            ),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            shape = RoundedCornerShape(size = 20.dp)
        )
        OutlinedTextField(
            value = confirmPass,
            onValueChange = { confirmPass = it },
            label = { Text("Confirm Password") },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            shape = RoundedCornerShape(size = 20.dp)
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
            append("You already have an account? Click here to ")
            pushStringAnnotation(
                tag = "LINK",
                annotation = "destination_page"
            )
            withStyle(
                style = SpanStyle(
                    color = Color.Blue,
                    textDecoration = TextDecoration.Underline
                )
            ) {
                append("signIn")
            }
            pop()
        }.toAnnotatedString()

        ClickableText(
            text = text,
            onClick = { offset ->
                text.getStringAnnotations("LINK", offset, offset)
                    .firstOrNull()?.let {
                        navController.navigate(Route.SignInScreen.route)
                    }
            }
        )
        // Observe changes in data
        if (data != null) {
            // Display data
            Text(text = data!!.toString(),color = if (data.equals("SignUp Success")) Color.Green else Color.Red)
            // Change page if all ok
            if(data.equals("SignUp Success")) {
                navController.navigate(Route.EmailScreen.route)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewSignUpScreen() {
    val authManager= AuthManagerImpl()
    val navController = rememberNavController()
    val viewModel = remember { AuthenticationViewModel(AuthenticationUseCases(signIn = SignIn(authManager = authManager), signUp = SignUp(authManager), confirm= Confirm(authManager), sendEmail = SendEmail(authManager), authCheck = AuthCheck(authManager), bioSignIn = BioSignIn(authManager), subscribe = Subscribe(authManager))) }
    SignUpScreen(signInHandler = {}, viewModel = viewModel, navController = navController)
}
