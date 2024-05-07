package com.mygdx.game.presentation.authentication

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import com.mygdx.game.data.dao.Message
import com.mygdx.game.data.manager.UpdateListener
import com.mygdx.game.domain.usecase.auth.AuthenticationUseCases
import com.mygdx.game.presentation.authentication.events.BioSignInEvent
import com.mygdx.game.presentation.authentication.events.SignInEvent
import com.mygdx.game.presentation.authentication.events.SignUpEvent
import com.mygdx.game.util.Constants.USER_AUTH
import kotlinx.coroutines.delay
import javax.inject.Inject

/* Class responsible for handling authentication related events. It relies on the appEntryUseCases dependency
* to perform operations related to saving the app entry. Notice that the latter class is injected using hilt
 */
@HiltViewModel
class AuthenticationViewModel  @Inject constructor(
    private val authenticationUseCases: AuthenticationUseCases
): ViewModel(), UpdateListener {

    private val _data = MutableLiveData<String?>()
    val data: MutableLiveData<String?> = _data

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    private val _navigateToAnotherScreen = MutableLiveData<Boolean>()
    val navigateToAnotherScreen: LiveData<Boolean> = _navigateToAnotherScreen


    init {
        // Set ViewModel as the listener for updates
        authenticationUseCases.subscribe.invoke(this,USER_AUTH)
    }
    fun onSignInEvent(event: SignInEvent) {
        when (event) {
            is SignInEvent.SignIn -> {
                _isError.value = false
                _isLoading.value = true
                goSignIn(event.email, event.password)
            }

            else -> {}
        }
    }

    fun onSignUpEvent(event: SignUpEvent) {
        when (event) {
            is SignUpEvent.SignUp -> {
                _isError.value = false
                _isLoading.value = true
                goSignUp(event.name, event.email, event.password, event.confirmPass)
            }

            else -> {}
        }
    }


    fun onBioSignInEvent(event: BioSignInEvent) {
        when (event) {
            is BioSignInEvent.BioSignIn -> {
                _isError.value = false
                _isLoading.value = true
                goBioSignIn(event.context, event.callback)
            }

            else -> {}
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

    override fun onUpdate(data: Location) {
    }


    override fun onUpdate(data: String) {
        // Update UI state with received data
        _data.value = data
        if (_data.value == "SignUp Success" || _data.value == "Login Success" || _data.value == "Bio Auth Success") {
            _isLoading.value=false
            _isError.value = false
            _navigateToAnotherScreen.value = true
        }
        else{
            _isLoading.value=false
            _isError.value = true
        }

    }

    override fun onUpdate(data: Message) {

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

