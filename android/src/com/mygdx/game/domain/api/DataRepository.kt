package com.mygdx.game.domain.api

import androidx.lifecycle.LiveData
import com.mygdx.game.data.dao.GameItem
import com.mygdx.game.data.dao.Player
import com.mygdx.game.data.dao.Rank


interface DataRepository {
    suspend fun fetchData(): LiveData<List<String>>
    suspend fun postRank(request: Rank): LiveData<String>
    suspend fun postPlayer(request: Player): LiveData<String>
    suspend fun postGameItem(request: GameItem): LiveData<String>
    suspend fun getGameItem(user: String,rarity:String): LiveData<List<String>>
    suspend fun fetchUserData(user : String): LiveData<List<String>>
    suspend fun getGameItemsUser(user: String): LiveData<List<String>>
    suspend fun mergeItems(itemId1: Int, itemId2: Int): LiveData<String>
}

