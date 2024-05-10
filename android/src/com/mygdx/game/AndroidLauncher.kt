package com.mygdx.game

import android.os.Bundle
import android.provider.Settings
import android.util.Log

import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.mygdx.game.dto.CharacterType
import com.mygdx.game.util.serializable



class AndroidLauncher: AndroidApplication(), GameTerminationListener {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val config = AndroidApplicationConfiguration()


        //deactivate compass and Accelerometer
        config.useAccelerometer = false
        config.useCompass = false


        val receivedCharacters = intent.serializable<ArrayList<CharacterType>>("PLAYER_LIST")
        val userId = intent.getStringExtra("USERNAME")
        Log.d("DEBUG","USERRRRR :$userId")

        if (userId != null){
            val gameManager = receivedCharacters?.let { GameManager(it,userId) }
            // set the call back
            gameManager?.setTerminationListener(this)

            initialize(gameManager, config)
        }


    }

    override fun onGameTerminated() {
        finish()
    }
}

