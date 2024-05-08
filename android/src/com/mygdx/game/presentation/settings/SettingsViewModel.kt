package com.mygdx.game.presentation.settings

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.mygdx.game.data.dao.Message
import com.mygdx.game.data.dao.UserProfileBundle
import com.mygdx.game.data.manager.UpdateListener
import com.mygdx.game.domain.usecase.settings.SettingsUseCases
import com.mygdx.game.presentation.settings.events.SignOutEvent
import com.mygdx.game.presentation.settings.events.UpdateEvent
import com.mygdx.game.util.Constants.SIGN_OUT_SUCCESS
import com.mygdx.game.util.Constants.UPDATE_SUCCESS
import com.mygdx.game.util.Constants.USER_SETTINGS
import javax.inject.Inject

/* Class responsible for handling authentication related events. It relies on the appEntryUseCases dependency
* to perform operations related to saving the app entry. Notice that the latter class is injected using hilt
 */
@HiltViewModel
class SettingsViewModel  @Inject constructor(
 private val settingsUseCases: SettingsUseCases
): ViewModel(), UpdateListener {


    private val _data = MutableLiveData<String>()
    val data: LiveData<String> = _data

    private val _userProfile = MutableStateFlow<UserProfileBundle?>(null)
    val userProfile: StateFlow<UserProfileBundle?> = _userProfile

    private val _navigateToAnotherScreen = MutableLiveData<Boolean>()
    val navigateToAnotherScreen: LiveData<Boolean> = _navigateToAnotherScreen

    init {
        // Set ViewModel as the listener for updates
        settingsUseCases.subscribe.invoke(this,USER_SETTINGS)
        fetch()
    }

    override fun onCleared() {
        super.onCleared()
        release()
    }
    private fun fetch(){
        viewModelScope.launch {
            _userProfile.value = settingsUseCases.fetch.invoke()
        }
    }
    fun onUpdateEvent(event: UpdateEvent){
        when (event) {
            is UpdateEvent.Update-> {
                goUpdate(event.name,event.email, event.password)
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

    override fun onUpdate(data: Location) {
    }

    override fun onUpdate(data: String) {
        // Update UI state with received data
        _data.value= data
        if (_data.value == UPDATE_SUCCESS || _data.value == SIGN_OUT_SUCCESS ) {
            _navigateToAnotherScreen.value = true
        }
    }

    override fun onUpdate(data: Message) {

    }

    fun onNavigationComplete() {
        _navigateToAnotherScreen.value = false
        _data.value=""
    }

    private fun goSignOut() {
        viewModelScope.launch {
            settingsUseCases.signOut()
        }
    }

    private fun goUpdate(name:String,email:String,password:String) {
        viewModelScope.launch {
            settingsUseCases.update(name,email,password)
        }
    }
    fun release() {
        _userProfile.value=null
    }

    fun resume() {
        fetch()
    }


}

