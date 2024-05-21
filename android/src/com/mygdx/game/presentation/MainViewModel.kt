package com.mygdx.game.presentation

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mygdx.game.data.dao.GameItem
import com.mygdx.game.data.dao.Item
import dagger.hilt.android.lifecycle.HiltViewModel
import com.mygdx.game.data.dao.UserProfileBundle
import com.mygdx.game.domain.usecase.appEntry.AppEntryUseCases
import com.mygdx.game.domain.usecase.auth.AuthenticationUseCases
import com.mygdx.game.domain.usecase.home.HomeUseCases
import com.mygdx.game.presentation.event.MultiplayerEvent

import com.mygdx.game.presentation.navgraph.Route
import kotlinx.coroutines.launch

import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val appEntryUseCases: AppEntryUseCases,
    private val authenticationUseCases: AuthenticationUseCases,
    private val homeUseCases: HomeUseCases
): ViewModel(){


    private var _userProfile = MutableLiveData(appEntryUseCases.readUser())
    val userProfile: LiveData<UserProfileBundle?> = _userProfile


    private val _skinItems = MutableLiveData<List<GameItem>?>()
    val skinItems: LiveData<List<GameItem>?> = _skinItems



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
        viewModelScope.launch {
            val username = userProfile.value?.displayName
            val result = homeUseCases.getGameItemsUser(username!!)
            val items = mutableListOf<GameItem>()
            result.observeForever { gameItemList ->
                if (gameItemList != null) {
                    for (i in gameItemList.indices) {
                        val properties = gameItemList[i].split(" ")
                        val owner = properties[0]
                        val itemId = properties[1]
                        val rarity = properties[2]
                        val hp = properties[3]
                        val damage = properties[4]
                        items.add(GameItem(owner, itemId.toInt(), rarity.toInt(), hp.toInt(), damage.toInt()))
                    }

                    _skinItems.postValue(items)

                }
            }
        }
        Log.d("MAIN VIEW","resuming resources ${_userProfile.value}")
    }

    fun onMultiplayerEvent(Event:MultiplayerEvent){

        when (Event) {
            MultiplayerEvent.GetSkinEvent -> {

                viewModelScope.launch {
                    val username = userProfile.value?.displayName
                    val result = homeUseCases.getGameItemsUser(username!!)
                    val items = mutableListOf<GameItem>()
                    result.observeForever { gameItemList ->
                        if (gameItemList != null) {
                            for (i in gameItemList.indices) {
                                val properties = gameItemList[i].split(" ")
                                val owner = properties[0]
                                val itemId = properties[1]
                                val rarity = properties[2]
                                val hp = properties[3]
                                val damage = properties[4]
                                items.add(GameItem(owner, itemId.toInt(), rarity.toInt(), hp.toInt(), damage.toInt()))
                            }

                            _skinItems.postValue(items)

                        }
                    }
                }

            }
        }




    }




}



