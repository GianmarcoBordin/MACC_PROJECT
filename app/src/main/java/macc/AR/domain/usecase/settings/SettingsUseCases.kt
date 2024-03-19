package macc.AR.domain.usecase.settings

data class SettingsUseCases (
    val update: Update,
    val fetch: FetchUserProfile,
    val signOut: SignOut,
    val subscribe: Subscribe
) {
}