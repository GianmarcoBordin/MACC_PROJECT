package com.mygdx.game

import android.os.Bundle
import android.util.Log
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.mygdx.game.data.api.DataRepositoryImpl
import com.mygdx.game.data.dao.Rank
import com.mygdx.game.domain.api.RankApi
import com.mygdx.game.dto.CharacterType
import com.mygdx.game.util.Constants
import com.mygdx.game.util.Constants.PLAYER_LIST
import com.mygdx.game.util.Constants.USER
import com.mygdx.game.util.Constants.USERNAME
import com.mygdx.game.util.serializable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class AndroidLauncher: AndroidApplication(), GameTerminationListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val config = AndroidApplicationConfiguration()


        //deactivate compass and Accelerometer
        config.useAccelerometer = false
        config.useCompass = false


        val receivedCharacters = intent.serializable<ArrayList<CharacterType>>(PLAYER_LIST)
        val userId = intent.getStringExtra(USER)
        val username = intent.getStringExtra(USERNAME)

        val gameManager = receivedCharacters?.let { GameManager(it,userId!!, username!!) }
        gameManager?.setTerminationListener(this)
        initialize(gameManager, config)


    }

    override fun onGameTerminated() {
        finish()
    }

    override fun submitScore(username: String, win: Boolean) {
        Log.d("DEBUG", "submitting score")
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.RANK_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val rankApi = retrofit.create(RankApi::class.java)
        val dataRepository = DataRepositoryImpl(rankApi)

        CoroutineScope(Dispatchers.IO).launch {
            dataRepository.postRank(Rank(username, if (win) 1 else 0))
        }
        finish()


    }



}

