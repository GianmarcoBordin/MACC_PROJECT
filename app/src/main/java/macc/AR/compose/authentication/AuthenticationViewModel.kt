package macc.AR.compose.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import macc.AR.compose.authentication.events.SignInEvent
import macc.AR.compose.onboarding.OnBoardingEvent
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
        sequenceOf(
            when (event) {
                is SignInEvent.SignIn-> {
                    val email= event.email
                    val password=event.password
                    goSignIn(email, password)
                }
            }
        )
    }

    fun onSignUpEvent(event: OnBoardingEvent){
        when(event){
            is OnBoardingEvent.SaveAppEntry -> {
                TODO("Not yet implemented")
            }
        }
    }

    fun onSignOutEvent(event: OnBoardingEvent){
        when(event){
            is OnBoardingEvent.SaveAppEntry -> {
                TODO("Not yet implemented")
            }
        }
    }

    private fun goSignIn(email:String,password:String) {
        viewModelScope.launch {
            authenticationUseCases.signIn(email,password)
        }

    }

    override fun onUpdate(data:String) {
        // Update UI state with received data
        _data.value= data
    }


}

