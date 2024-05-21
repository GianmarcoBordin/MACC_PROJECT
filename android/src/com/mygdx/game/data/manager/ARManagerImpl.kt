package com.mygdx.game.data.manager

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mygdx.game.data.dao.GameItem
import com.mygdx.game.domain.api.DataRepository
import com.mygdx.game.domain.manager.ARManager
import com.mygdx.game.domain.manager.LocalUserManager
import javax.inject.Inject

class ARManagerImpl @Inject constructor(private val dataRepository: DataRepository, private val localUserManager: LocalUserManager): ARManager {
    override suspend fun addGameItem(gameItem: GameItem) {
        dataRepository.postGameItem(gameItem)
    }

    override suspend fun getGameItem(username: String, rarity: String): LiveData<List<String>> {
        val gameItemLiveData = MutableLiveData<List<String>>()

        dataRepository.getGameItem(username, rarity).observeForever { gameItemList ->
            val stringGameItem = if (gameItemList.isEmpty()) emptyList()
                                else gameItemList[0].split(" ")
            gameItemLiveData.value = stringGameItem
        }

        return gameItemLiveData
    }

    override suspend fun saveSkin(){
        val name = localUserManager.getUserProfile().displayName

        for (i in 1..5){

            this.getGameItem(name, i.toString()).value.let {res ->
                Log.d("DEBUG","$name, $res")
                if (res?.isNotEmpty() == true) {
                    val skin = res[0].split(" ")

                    val gameItem = GameItem(
                        owner = skin[0],
                        itemId = skin[1].toInt(),
                        rarity = skin[2].toInt(),
                        hp = skin[3].toInt(),
                        damage = skin[4].toInt()
                    )

                    // save user skin
                    localUserManager.saveObject("SKIN_${i}", gameItem)
                }
            }

        }
    }


}