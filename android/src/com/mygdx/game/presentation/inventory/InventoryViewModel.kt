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
import com.mygdx.game.presentation.inventory.events.UpdateItemsEvent
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
                    items[event.index].owner,
                    items[event.index].itemId,
                    items[event.index].rarity,
                    items[event.index].hp,
                    items[event.index].damage,
                    event.bitmap.asAndroidBitmap()
                )
            }
        }
    }

    fun onUpdateMergedItemEvent(event: UpdateItemsEvent) {

        when (event) {
            UpdateItemsEvent.UpdateMergedItems -> {

                // group items by rarity
                val itemsToMerge = items.groupBy { it.rarity }
                Log.d("DEBUG","$itemsToMerge")
                val newMergedItems = mutableListOf<GameItem>()
                itemsToMerge.forEach { (_, groupedItems) ->

                    // if the group contains one item then add it to the new item list
                    if (groupedItems.size == 1){
                        newMergedItems.add(groupedItems[0])
                    }
                    // otherwise merge all the item in the same list
                    else if (groupedItems.size > 1) {

                        // save the item that has the most damage among the captured ones
                        var max = 0
                        var index = 0
                        for (i in groupedItems.indices) {
                            if (groupedItems[i].damage > max) {
                                max = groupedItems[i].damage
                                index = i
                            }
                        }

                        val mergedItem = groupedItems.reduce { acc, item ->
                            GameItem(
                                owner = item.owner,
                                // select the itemId of the item that has the most damage
                                itemId = if (item.damage == groupedItems[index].damage) item.itemId else acc.itemId,
                                hp = acc.hp + item.hp,
                                damage = acc.damage + item.damage,
                                rarity = item.rarity
                            )
                        }
                        newMergedItems.add(mergedItem)
                    }
                }
                viewModelScope.launch {
                    // before assignment, delete previous items (items list) from db
                    items = newMergedItems
                    val itemsToDelete: List<GameItem> = items.filter { item1 ->
                        newMergedItems.none { item2 -> item2.itemId == item1.itemId }
                    }
                    itemsToDelete.forEach{ deleteGameItem ->
                        inventoryUseCases.deleteGameItem(deleteGameItem)
                    }
                    inventoryUseCases.saveOldItems(itemsToDelete)
                }
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
                            val owner = properties[0]
                            val id = properties[1]
                            val rarity = properties[2]
                            val hp = properties[3]
                            val damage = properties[4]
                            items.add(GameItem(owner, id.toInt(), rarity.toInt(), hp.toInt(), damage.toInt()))
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

    private fun release() {
        items = emptyList<GameItem>().toMutableList()
    }

    fun resume() {
        retrieveItems()
    }
}