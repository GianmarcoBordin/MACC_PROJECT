package macc.AR.compose.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import macc.AR.compose.authentication.events.EmailEvent
import macc.AR.compose.authentication.events.SignInEvent
import macc.AR.compose.authentication.events.SignOutEvent
import macc.AR.compose.authentication.events.SignUpEvent
import macc.AR.data.manager.UpdateListener
import macc.AR.domain.usecase.auth.AuthenticationUseCases
import javax.inject.Inject

/* Class responsible for handling authentication related events. It relies on the appEntryUseCases dependency
* to perform operations related to saving the app entry. Notice that the latter class is injected using hilt
 */
@HiltViewModel
class AuthenticationViewModel  @Inject constructor(
    private val authenticationUseCases: AuthenticationUseCases
): ViewModel(),UpdateListener{

    private val _data = MutableLiveData<String>()
    val data: LiveData<String> = _data

    init {
        // Set ViewModel as the listener for updates
        authenticationUseCases.subscribe.invoke(this)
    }

    fun onSignInEvent(event: SignInEvent){
            when (event) {
                is SignInEvent.SignIn-> {
                    goSignIn(event.email, event.password)
                }
            }
    }

    fun onSignUpEvent(event: SignUpEvent){
        when (event) {
            is SignUpEvent.SignUp-> {
                goSignUp(event.name,event.email, event.password,event.confirmPass)
            }
        }
    }

    fun onSignOutEvent(event: SignOutEvent){
        when(event){
            is SignOutEvent.SignOut -> {
            goSignOut()
            }
        }
    }

    fun onEmailEvent(event: EmailEvent){
        when(event){
            is EmailEvent.Email -> {
                goConfirmAccount()
            }
        }
    }

    private fun goConfirmAccount() {
        viewModelScope.launch {
            authenticationUseCases.signOut()
        }
    }

    private fun goSignOut() {
        viewModelScope.launch {
            authenticationUseCases.signOut()
        }
    }


    private fun goSignIn(email:String,password:String) {
        viewModelScope.launch {
            authenticationUseCases.signIn(email,password)
        }

    }

    private fun goSignUp(name:String,email:String,password:String,confirmPass:String) {
        viewModelScope.launch {
            authenticationUseCases.signUp(name,email,password,confirmPass)
        }
    }

    // Example email validation function
     fun isValidOTP(otp: String): Boolean {
        return authenticationUseCases.confirm(otp)
    }

    // TODO move to a more generale model view
    override fun onUpdate(data:String) {
        // Update UI state with received data
        _data.value= data
    }


}

