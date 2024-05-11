package com.mygdx.game.presentation.inventory

import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mygdx.game.data.dao.GameItem
import com.mygdx.game.domain.manager.LocalUserManager
import com.mygdx.game.domain.usecase.inventory.InventoryUseCases
import com.mygdx.game.presentation.inventory.events.GameItemEvent
import com.mygdx.game.presentation.inventory.events.ItemEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val localUserManager: LocalUserManager,
    private val inventoryUseCases: InventoryUseCases
) : ViewModel() {
    var items : MutableList<GameItem> = mutableListOf()

    private val _retrieved = MutableLiveData(false)
    val retrieved: LiveData<Boolean> = _retrieved

    fun onItemEvent(event: ItemEvent) {
        when (event) {
            ItemEvent.RetrieveItems -> {
                viewModelScope.launch {
                    val username = localUserManager.getUserProfile().displayName
                    val listOfLists = inventoryUseCases.retrieveItems(username)
                    for (i in 0 until listOfLists.size) {
                        items.add(GameItem(listOfLists[i][0], listOfLists[i][1].toInt(), listOfLists[i][2].toInt(), listOfLists[i][3].toInt()))
                    }
                    _retrieved.value = true
                }
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
}