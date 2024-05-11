package com.mygdx.game.presentation

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.mygdx.game.data.dao.UserProfileBundle
import com.mygdx.game.domain.usecase.appEntry.AppEntryUseCases
import com.mygdx.game.domain.usecase.auth.AuthenticationUseCases
import com.mygdx.game.presentation.multiplayer.MultiplayerState
import com.mygdx.game.presentation.multiplayer.StartEvent
import com.mygdx.game.presentation.navgraph.Route
import com.mygdx.game.presentation.scan.GameState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val appEntryUseCases: AppEntryUseCases,
    private val authenticationUseCases: AuthenticationUseCases,
): ViewModel(){


    private var _userProfile = MutableLiveData(appEntryUseCases.readUser())
    val userProfile: LiveData<UserProfileBundle?> = _userProfile

    private var entered :Boolean= false

    var startDestination by mutableStateOf(Route.AppStartNavigation.route)
    init {
        appEntryUseCases.readAppEntry().onEach { shouldStartFromHomeScreen ->
            startDestination = if (shouldStartFromHomeScreen) {
                if (authenticationUseCases.authCheck()) {
                    Route.HomeScreen.route
                } else {
                    Route.SignInScreen.route
                }
            }else {
                Route.OnBoardingScreen.route
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        release()
    }


    fun release(){
        Log.d("MAIN VIEW","releasing resources")
        _userProfile.value=null
    }

    fun resume(){
        _userProfile.value=appEntryUseCases.readUser()
        Log.d("MAIN VIEW","resuming resources ${_userProfile.value}")
    }





}



