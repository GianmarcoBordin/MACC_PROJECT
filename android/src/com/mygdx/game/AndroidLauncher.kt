package com.mygdx.game

import android.os.Bundle
import android.provider.Settings
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration


class AndroidLauncher : AndroidApplication() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val config = AndroidApplicationConfiguration()

        val deviceID = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID).substring(0,2)

        //deactivate compass and Accelerometer
        config.useAccelerometer = false
        config.useCompass = false

        initialize(GameManager(deviceID), config)
    }
}
