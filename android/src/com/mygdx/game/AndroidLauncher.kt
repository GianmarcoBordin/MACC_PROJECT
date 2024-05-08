package com.mygdx.game

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.mygdx.game.dto.CharacterType
import com.mygdx.game.util.serializable
import java.io.Serializable

class AndroidLauncher: AndroidApplication() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val config = AndroidApplicationConfiguration()

        val deviceID = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID).substring(0, 2)

        //deactivate compass and Accelerometer
        config.useAccelerometer = false
        config.useCompass = false


        val receivedCharacters = intent.serializable<ArrayList<CharacterType>>("PLAYER_LIST")

        // TODO deviceID should be the player ID
        initialize(receivedCharacters?.let { GameManager(it,deviceID) }, config)
    }
}

