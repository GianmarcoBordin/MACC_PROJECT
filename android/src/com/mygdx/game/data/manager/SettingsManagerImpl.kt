@file:Suppress("DEPRECATION")

package com.mygdx.game.data.manager

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.runBlocking
import com.mygdx.game.data.dao.Biometric
import com.mygdx.game.data.dao.Rank
import com.mygdx.game.data.dao.UserProfileBundle
import com.mygdx.game.domain.manager.AuthManager
import com.mygdx.game.domain.manager.LocalUserManager
import com.mygdx.game.domain.manager.SettingsManager
import javax.inject.Inject

class SettingsManagerImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth?,
    private val localUserManager: LocalUserManager,
    private val authManager: AuthManager,
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

                    try {
                        runBlocking {
                            val oldName= localUserManager.getUserProfile().displayName
                            authManager.updatePlayerFirestore(oldName,name)
                            val avatarUrl = localUserManager.getUserProfile().avatarUrl
                            // save user profile application state
                            val userProfileBundle =
                                UserProfileBundle(displayName = name, email = email, avatarUrl = avatarUrl)
                            localUserManager.saveUserProfile(userProfileBundle)
                            // update local bio
                            val bio= Biometric(email,password)
                            localUserManager.saveBio(bio)
                        }
                        // All updates were successful
                        updateListener?.onUpdate("Update Success")
                    } catch (e: Exception) {
                        // Handle exceptions within runBlocking block
                        updateListener?.onUpdate("Error: ${e.message}")
                    }
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

                    runBlocking {

                        val oldName= localUserManager.getUserProfile().displayName
                        authManager.updatePlayerFirestore(oldName,name)
                        val avatarUrl = localUserManager.getUserProfile().avatarUrl
                        // save user profile application state
                        val userProfileBundle =
                            UserProfileBundle(displayName = name, email = email, avatarUrl = avatarUrl)
                        localUserManager.saveUserProfile(userProfileBundle)
                        // update local bio
                        val bio= Biometric(email,password)
                        localUserManager.saveBio(bio)
                    }
                    // All updates were successful
                    updateListener?.onUpdate("Update Success")
                } else {
                    // Handle the failure, you can access the exception from combinedTask.exception
                    updateListener?.onUpdate("Something went wrong, retry later")
                }
            }
        }
    }

    override suspend fun fetch(): UserProfileBundle {
        return localUserManager.getUserProfile()
    }
    override suspend fun signOut() {
        try {
            // clear firebase
            firebaseAuth?.signOut()
            // clear bio state
            val bio= Biometric("","")
            localUserManager.saveBio(bio)
            // clear rank state
            val rank= Rank("",0)
            localUserManager.saveScore(rank)
            // clear anchor id
            localUserManager.saveAnchorId("")
            // clear user profile state
            val userProfileBundle= UserProfileBundle("","","")
            localUserManager.saveUserProfile(userProfileBundle)
            // clear app entry state
            localUserManager.clearAppEntry()
            // update listener
            updateListener?.onUpdate("SignOut Success")

        } catch (e: Exception) {
            updateListener?.onUpdate(e.message.toString())
        }
    }

    override fun setUpdateListener(ref: UpdateListener) {
        updateListener=ref    }

}






