package macc.AR.domain.usecase.settings

import macc.AR.compose.authentication.UserProfileBundle
import macc.AR.domain.manager.SettingsManager


class FetchUserProfile(
    private val settingsUsecase: SettingsManager
) {
     suspend operator fun invoke() : UserProfileBundle? {
        return settingsUsecase.fetch()
    }
}