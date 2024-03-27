package macc.AR.domain.usecase.settings


import macc.AR.data.UserProfileBundle
import macc.AR.domain.manager.SettingsManager


class FetchUserProfile(
    private val settingsManager: SettingsManager
) {
     suspend operator fun invoke() : UserProfileBundle? {
        return settingsManager.fetch()
    }
}