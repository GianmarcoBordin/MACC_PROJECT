package macc.AR.data.manager

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

import macc.AR.data.BiometricState
import macc.AR.data.UserProfileBundle
import macc.AR.domain.manager.SettingsManager
import javax.inject.Inject

class SettingsManagerImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth?,
    private val biometricState: BiometricState
) : SettingsManager {
    private var updateListener: UpdateListener? = null

    override suspend fun update(name: String, email: String, password: String) {

        val user = firebaseAuth?.currentUser

        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(name)
            .build()

        if (password != "New Password"){
            user?.updateEmail(email)?.continueWithTask { emailUpdateTask ->
                if (emailUpdateTask.isSuccessful) {
                    return@continueWithTask user.updatePassword(password)
                } else {
                    throw emailUpdateTask.exception!!
                }
            }?.continueWithTask { passwordUpdateTask ->
                if (passwordUpdateTask.isSuccessful) {
                    return@continueWithTask user.updateProfile(profileUpdates)
                } else {
                    throw passwordUpdateTask.exception!!
                }
            }?.addOnCompleteListener { combinedTask ->
                if (combinedTask.isSuccessful) {
                    // All updates were successful
                    // User data updated successfully
                    biometricState.setCredentials(email,password)
                    updateListener?.onUpdate("Update Success")
                } else {
                    // Handle the failure, you can access the exception from combinedTask.exception
                    updateListener?.onUpdate("Something went wrong, retry later")
                }
            }
        }
        else {
            user?.updateEmail(email)?.continueWithTask { emailUpdateTask ->
                if (emailUpdateTask.isSuccessful) {
                    return@continueWithTask user.updateProfile(profileUpdates)
                } else {
                    throw emailUpdateTask.exception!!
                }
            }?.addOnCompleteListener { combinedTask ->
                if (combinedTask.isSuccessful) {
                    // All updates were successful
                    // User data updated successfully
                    biometricState.setCredentials(email,password)
                    updateListener?.onUpdate("Update Success")
                } else {
                    // Handle the failure, you can access the exception from combinedTask.exception
                    updateListener?.onUpdate("Something went wrong, retry later")
                }
            }
        }
    }

    override suspend fun fetch(): UserProfileBundle? {
        val currentUser= firebaseAuth?.currentUser
        val userProfile: UserProfileBundle?
        if (currentUser != null) {
            val displayName = currentUser.displayName
            val email = currentUser.email
            userProfile = UserProfileBundle(displayName, email)
        }else{
            userProfile=null
        }
        return userProfile
    }
    override suspend fun signOut() {
        try {
            // clear firebase
            firebaseAuth?.signOut()
            // clear bio state
            biometricState.setCredentials("","")
            // update listener
            updateListener?.onUpdate("SignOut Success")

        } catch (e: Exception) {
            updateListener?.onUpdate(e.message.toString())
        }
    }

    override fun setUpdateListener(ref: UpdateListener) {
        updateListener=ref    }

}






