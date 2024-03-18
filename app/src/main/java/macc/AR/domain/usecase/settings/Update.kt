package macc.AR.domain.usecase.settings

import macc.AR.domain.manager.SettingsManager

class Update(
    private val settingsUsecase: SettingsManager
) {
    suspend operator fun invoke(name:String, email:String, password:String) {
       settingsUsecase.update(name,email,password)
    }
}