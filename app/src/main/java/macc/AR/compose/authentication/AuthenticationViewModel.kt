package macc.AR.compose.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import macc.AR.compose.authentication.events.BioSignInEvent
import macc.AR.compose.authentication.events.SignInEvent
import macc.AR.compose.authentication.events.SignUpEvent
import macc.AR.data.manager.UpdateListener
import macc.AR.domain.usecase.auth.AuthenticationUseCases
import macc.AR.util.Constants.USER_AUTH
import javax.inject.Inject

/* Class responsible for handling authentication related events. It relies on the appEntryUseCases dependency
* to perform operations related to saving the app entry. Notice that the latter class is injected using hilt
 */
@HiltViewModel
class AuthenticationViewModel  @Inject constructor(
    private val authenticationUseCases: AuthenticationUseCases
): ViewModel(),UpdateListener {

    private val _data = MutableLiveData<String>()
    val data: LiveData<String> = _data

    private val _navigateToAnotherScreen = MutableLiveData<Boolean>()
    val navigateToAnotherScreen: LiveData<Boolean> = _navigateToAnotherScreen


    init {
        // Set ViewModel as the listener for updates
        authenticationUseCases.subscribe.invoke(this,USER_AUTH)
    }

    fun onSignInEvent(event: SignInEvent) {
        when (event) {
            is SignInEvent.SignIn -> {
                goSignIn(event.email, event.password)
            }
        }
    }

    fun onSignUpEvent(event: SignUpEvent) {
        when (event) {
            is SignUpEvent.SignUp -> {
                goSignUp(event.name, event.email, event.password, event.confirmPass)
            }
        }
    }


    fun onBioSignInEvent(event: BioSignInEvent) {
        when (event) {
            is BioSignInEvent.BioSignIn -> {
                goBioSignIn(event.context, event.callback)
            }
        }
    }



    private fun goSignIn(email: String, password: String) {
        viewModelScope.launch {
            authenticationUseCases.signIn(email, password)
        }

    }

    private fun goSignUp(name: String, email: String, password: String, confirmPass: String) {
        viewModelScope.launch {
            authenticationUseCases.signUp(name, email, password, confirmPass)
        }
    }


    override fun onUpdate(data: String) {
        // Update UI state with received data
        _data.value = data
        if (_data.value == "SignUp Success" || _data.value == "Login Success" || _data.value=="Confirmation Success") {
            _navigateToAnotherScreen.value = true
        }

    }

    fun onNavigationComplete() {
        _navigateToAnotherScreen.value = false
        _data.value=""
    }


    private fun goBioSignIn(
        context: android.content.Context,
        callback: (String) -> Unit
    ) {
        viewModelScope.launch {
            authenticationUseCases.bioSignIn(context, callback)
        }
    }
}

