package macc.AR.domain.usecase

import kotlinx.coroutines.flow.Flow
import macc.AR.domain.manager.LocalUserManager

class ReadAppEntry(
    private val localUserManager: LocalUserManager
){
    operator fun invoke(): Flow<Boolean> {
        return localUserManager.readAppEntry()
    }
}