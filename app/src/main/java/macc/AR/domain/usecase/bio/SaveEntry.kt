package macc.AR.domain.usecase.bio

import android.content.Context
import macc.AR.data.BiometricState
import macc.AR.domain.manager.Biometricmanager


class SaveEntry(
    private val biometricmanager: Biometricmanager
) {
    suspend operator fun invoke(context: Context, biometricState: BiometricState) {
        biometricmanager.saveFirebaseUserIdAndBiometricCredentials(context, biometricState)
    }
}