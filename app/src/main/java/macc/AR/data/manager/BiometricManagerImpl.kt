package macc.AR.data.manager

// BiometricManagerImpl.kt
import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import macc.AR.data.BiometricState

// Function to save Firebase User ID and biometric credentials in secure storage
fun saveFirebaseUserIdAndBiometricCredentials(context: Context, biometricState: BiometricState) {
    val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        "secure_prefs",
        masterKeyAlias,
        context.applicationContext,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    // Save biometric credentials (email and password)
    sharedPreferences.edit().putString("biometricEmail", biometricState.getBio().first).apply()
    sharedPreferences.edit().putString("biometricPassword", biometricState.getBio().second).apply()
}

// Function to retrieve Firebase User ID and biometric credentials from secure storage
fun retrieveFirebaseUserIdAndBiometricCredentials(context: Context): Pair<String,String>? {
    val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        "secure_prefs",
        masterKeyAlias,
        context.applicationContext,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    // Retrieve Firebase User ID
    val firebaseUserId = sharedPreferences.getString("firebaseUserId", null)

    // Retrieve biometric credentials (email and password)
    val email = sharedPreferences.getString("biometricEmail", null)
    val password = sharedPreferences.getString("biometricPassword", null)

    val biometricCredentials = if (email != null && password != null) {
        Pair(email, password)
    } else {
        null
    }

    return  biometricCredentials
}
