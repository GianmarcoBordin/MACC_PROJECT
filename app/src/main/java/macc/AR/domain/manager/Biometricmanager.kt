package macc.AR.domain.manager

import android.content.Context
import macc.AR.data.BiometricState

interface Biometricmanager {
    suspend fun saveFirebaseUserIdAndBiometricCredentials(context: Context, biometricState: BiometricState)
    suspend fun retrieveFirebaseUserIdAndBiometricCredentials(context: Context): Pair<String,String>
}