package com.mygdx.game.domain.usecase.settings


import com.mygdx.game.data.dao.UserProfileBundle
import com.mygdx.game.domain.manager.SettingsManager


class FetchUserProfile(
    private val settingsManager: SettingsManager
) {
     suspend operator fun invoke() : UserProfileBundle? {
        return settingsManager.fetch()
    }
}