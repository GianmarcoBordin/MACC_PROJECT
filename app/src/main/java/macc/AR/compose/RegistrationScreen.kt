package macc.AR.compose

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.core.content.ContextCompat.startActivity
import macc.AR.EmailVerificationActivity
import macc.AR.SignUpActivity
import macc.AR.data.registerFirebase

@Composable
fun RegistrationScreen(activity: SignUpActivity) {
    Column(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        var username by rememberSaveable { mutableStateOf("") }
        var email by rememberSaveable { mutableStateOf("") }
        var pass by rememberSaveable { mutableStateOf("") }
        var confirmPass by rememberSaveable { mutableStateOf("") }

        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier
                .fillMaxWidth()
        )

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
        )

        TextField(
            value = pass,
            onValueChange = { pass = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier
                .fillMaxWidth()
        )

        TextField(
            value = confirmPass,
            onValueChange = { confirmPass = it },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier
                .fillMaxWidth()
        )

        FloatingActionButton(
            onClick = { goToLogin() }
        ) {
            Text("Go To Login")
        }

        Button(onClick = { registerUser(username, email, pass, confirmPass, activity) }) {
            Text("Register")
        }
    }
}

private fun goToLogin() {

}

private fun registerUser(username : String, email : String, pass : String, confirmPass : String, activity: SignUpActivity) {
    registerFirebase(username, email, pass, confirmPass, activity)
}