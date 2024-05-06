package com.mygdx.game.domain.api

import androidx.lifecycle.LiveData
import com.mygdx.game.data.dao.GameItem
import com.mygdx.game.data.dao.Ownership
import com.mygdx.game.data.dao.Player
import com.mygdx.game.data.dao.Rank


interface DataRepository {
    suspend fun fetchData(): LiveData<List<String>>

    suspend fun postRank(request: Rank): LiveData<String>

    suspend fun postPlayer(request: Player): LiveData<String>

    suspend fun postGameItem(request: GameItem): LiveData<String>
    suspend fun getGameItem(): LiveData<List<String>>
    suspend fun postOwnership(request: Rank): LiveData<String>
    suspend fun getOwnership(): LiveData<List<String>>
    suspend fun postOwnership(request: Ownership): LiveData<String>
    suspend fun fetchUserData(user : String): LiveData<List<String>>
}

