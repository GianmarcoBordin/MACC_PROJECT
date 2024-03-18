package macc.AR.domain.usecase.settings
import macc.AR.data.manager.UpdateListener
import macc.AR.domain.manager.SettingsManager

class Subscribe(
    private val settingsUseCases: SettingsManager
){
    operator fun invoke(ref: UpdateListener){
        settingsUseCases.setUpdateListener(ref)
    }

}