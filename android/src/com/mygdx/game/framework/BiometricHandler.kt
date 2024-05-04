package com.mygdx.game.framework

// BiometricHandler.kt
import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.mygdx.game.data.dao.Biometric

// Function to save Firebase User ID and biometric credentials in secure storage
fun saveFirebaseUserIdAndBiometricCredentials(context: Context, biometric: Biometric) {
    val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        "secure_prefs",
        masterKeyAlias,
        context.applicationContext,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    // Save biometric credentials (email and password)
    sharedPreferences.edit().putString("biometricEmail", biometric.userEmail).apply()
    sharedPreferences.edit().putString("biometricPassword", biometric.userPass).apply()
}

// Function to retrieve Firebase User ID and biometric credentials from secure storage
fun retrieveFirebaseUserIdAndBiometricCredentials(context: Context): Pair<String,String> {
    val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        "secure_prefs",
        masterKeyAlias,
        context.applicationContext,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    // Retrieve Firebase User ID
    sharedPreferences.getString("firebaseUserId", null)

    // Retrieve biometric credentials (email and password)
    val email = sharedPreferences.getString("biometricEmail", null)
    val password = sharedPreferences.getString("biometricPassword", null)

    val biometricCredentials = if (email != null && password != null) {
        Pair(email, password)
    } else {
        Pair("","")
    }

    return  biometricCredentials
}
