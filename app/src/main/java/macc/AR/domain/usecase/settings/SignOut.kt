package macc.AR.domain.usecase.settings

import macc.AR.domain.manager.SettingsManager

class SignOut(
    private val settingsManager: SettingsManager
){
    suspend operator fun invoke(){
        settingsManager.signOut()
    }
}