package macc.AR.domain.usecase.settings

import macc.AR.domain.usecase.Subscribe

data class SettingsUseCases (
    val update: Update,
    val fetch: FetchUserProfile,
    val signOut: SignOut,
    val subscribe: Subscribe
) {
}