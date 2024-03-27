package macc.AR.domain.usecase

import macc.AR.data.manager.UpdateListener
import macc.AR.domain.manager.AuthManager
import macc.AR.domain.manager.RankManager
import macc.AR.domain.manager.SettingsManager
import macc.AR.util.Constants.USER_AUTH
import macc.AR.util.Constants.USER_RANK
import macc.AR.util.Constants.USER_SETTINGS


class Subscribe(
    private val settingsManager: SettingsManager,
    private val authManager: AuthManager,
    private val rankManager: RankManager
){
    operator fun invoke(ref: UpdateListener,type:String){
        when (type) {
            USER_AUTH ->  authManager.setUpdateListener(ref)
            USER_RANK -> rankManager.setUpdateListener(ref)
            USER_SETTINGS -> settingsManager.setUpdateListener(ref)
        }
    }

}