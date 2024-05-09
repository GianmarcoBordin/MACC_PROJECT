package com.mygdx.game.domain.usecase.appEntry


import androidx.lifecycle.LiveData
import com.mygdx.game.domain.manager.RankManager

class ReadSkin(
    private val rankManager: RankManager
){
    operator suspend fun invoke(user:String,rarity:String): LiveData<List<String>> {
        return rankManager.fetchGameItem(user,rarity)
    }
}