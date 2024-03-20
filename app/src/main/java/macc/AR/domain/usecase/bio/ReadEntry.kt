package macc.AR.domain.usecase.bio

import android.content.Context
import macc.AR.domain.manager.Biometricmanager

class ReadEntry(
    private val biometricmanager: Biometricmanager
) {
    suspend operator fun invoke(context: Context): Pair<String,String> {
        return biometricmanager.retrieveFirebaseUserIdAndBiometricCredentials(context)
    }
}