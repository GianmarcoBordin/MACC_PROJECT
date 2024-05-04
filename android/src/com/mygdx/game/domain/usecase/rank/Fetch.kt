package com.mygdx.game.domain.usecase.rank

import androidx.lifecycle.LiveData
import com.mygdx.game.domain.manager.RankManager


class Fetch(
    private val rankManager: RankManager
){
    suspend operator fun invoke(): LiveData<List<String>> {
       return rankManager.fetch()
    }

}