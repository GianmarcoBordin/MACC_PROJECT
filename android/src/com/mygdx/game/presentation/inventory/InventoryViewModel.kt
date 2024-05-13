package com.mygdx.game.presentation.inventory

import android.util.Log
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mygdx.game.data.dao.GameItem
import com.mygdx.game.domain.usecase.inventory.InventoryUseCases
import com.mygdx.game.presentation.inventory.events.GameItemEvent
import com.mygdx.game.presentation.inventory.events.ItemEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val inventoryUseCases: InventoryUseCases
) : ViewModel() {

    var items : MutableList<GameItem> = mutableListOf()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    private var username : String? = null
    private var completed: Boolean = false

    init {
        retrieveItems()
    }

    fun onItemEvent(event: ItemEvent) {
        when (event) {
            is ItemEvent.RetrieveItems -> {
                retrieveItems()
            }
        }
    }

    fun onGameItemEvent(event: GameItemEvent) {
        when (event) {
            is GameItemEvent.UpdateBitmap -> {
                // create a new object with the updated bitmap and replace the old one with the new one
                items[event.index] = GameItem(
                    items[event.index].id,
                    items[event.index].rarity,
                    items[event.index].hp,
                    items[event.index].damage,
                    event.bitmap.asAndroidBitmap())
            }
        }
    }

    private fun retrieveItems() {
        viewModelScope.launch {
            // Set loading state
            if(!completed) {
                _isLoading.value = true
            }
            _isError.value=false
            delay(1000)
            try {
                // fetch user profile
                username = inventoryUseCases.fetchUserProfile().displayName

                // Fetch data asynchronously
                val result = inventoryUseCases.getGameItemsUser(username!!)

                result.observeForever { gameItemList ->
                    if (gameItemList != null && !completed) {
                        for (i in gameItemList.indices) {
                            val properties = gameItemList[i].split(" ")
                            val id = properties[0]
                            val rarity = properties[1]
                            val hp = properties[2]
                            val damage = properties[3]
                            items.add(GameItem(id, rarity.toInt(), hp.toInt(), damage.toInt()))
                        }
                        completed = true
                        _isLoading.value = false
                    }
                }
            } catch (e: Exception) {
                Log.e("ERROR", items.toString())
                // Handle error
                _isError.value=true
            } finally {
                Log.e("DEBUG", "${isError.value} $items")
                if(completed) {
                    // Set loading state to false regardless of success or failure
                    _isLoading.value = false
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        release()
    }

    fun release() {
        items = emptyList<GameItem>().toMutableList()
    }

    fun resume() {
        retrieveItems()
    }
}