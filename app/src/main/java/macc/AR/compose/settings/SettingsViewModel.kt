package macc.AR.compose.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import macc.AR.compose.settings.SignOutEvent
import macc.AR.compose.settings.UpdateEvent
import macc.AR.data.manager.UpdateListener
import macc.AR.domain.usecase.settings.SettingsUseCases
import javax.inject.Inject

/* Class responsible for handling authentication related events. It relies on the appEntryUseCases dependency
* to perform operations related to saving the app entry. Notice that the latter class is injected using hilt
 */
@HiltViewModel
class SettingsViewModel  @Inject constructor(
 private val settingsUseCases: SettingsUseCases
): ViewModel(),UpdateListener{

    private val _data = MutableLiveData<String>()
    val data: LiveData<String> = _data

    private val _userProfile = MutableStateFlow<UserProfileBundle?>(null)
    val userProfile: StateFlow<UserProfileBundle?> = _userProfile

    init {
        // Set ViewModel as the listener for updates
        settingsUseCases.subscribe.invoke(this)
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

    override fun onUpdate(data:String) {
        // Update UI state with received data
        _data.value= data
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


}
data class UserProfileBundle(
    val displayName: String?,
    val email: String?
)

