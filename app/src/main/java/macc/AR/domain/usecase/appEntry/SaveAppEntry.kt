package macc.AR.domain.usecase.appEntry

import macc.AR.domain.manager.LocalUserManager

class SaveAppEntry(
    private val localUserManager: LocalUserManager
){
    suspend operator fun invoke(){
        localUserManager.saveAppEntry()
    }
}