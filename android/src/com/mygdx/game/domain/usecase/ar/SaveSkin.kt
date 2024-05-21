package com.mygdx.game.domain.usecase.ar

import androidx.lifecycle.LiveData
import com.mygdx.game.domain.manager.ARManager


class SaveSkin(
    private val arManager: ARManager
) {
    suspend operator fun invoke(){
        return arManager.saveSkin()
    }
}