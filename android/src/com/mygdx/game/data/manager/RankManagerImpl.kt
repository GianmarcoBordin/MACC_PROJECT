

package com.mygdx.game.data.manager

import androidx.lifecycle.LiveData
import com.mygdx.game.domain.api.DataRepository
import com.mygdx.game.domain.manager.RankManager
import javax.inject.Inject

class RankManagerImpl @Inject constructor(private val dataRepository: DataRepository) :
    RankManager {
    private var updateListener: UpdateListener? = null


    override suspend fun fetch(): LiveData<List<String>> {
        return dataRepository.fetchData()
    }


    override fun setUpdateListener(ref: UpdateListener) {
        updateListener=ref
    }


}

