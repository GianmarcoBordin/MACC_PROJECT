package com.mygdx.game.domain.api

import androidx.lifecycle.LiveData
import com.mygdx.game.data.dao.Player
import com.mygdx.game.data.dao.Rank


interface DataRepository {
    suspend fun fetchData(): LiveData<List<String>>

    suspend fun postRank(request: Rank): LiveData<String>

    suspend fun postPlayer(request: Player): LiveData<String>

}

