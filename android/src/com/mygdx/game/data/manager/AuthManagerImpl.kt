package com.mygdx.game.data.manager

import android.content.ContentValues.TAG
import android.content.Context
import android.hardware.biometrics.BiometricPrompt
import android.location.Location
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.mygdx.game.data.dao.Biometric
import com.mygdx.game.data.dao.GameItem
import com.mygdx.game.data.dao.Message
import com.mygdx.game.data.dao.Player
import com.mygdx.game.data.dao.Rank
import com.mygdx.game.data.dao.UserProfileBundle
import com.mygdx.game.domain.api.DataRepository
import com.mygdx.game.domain.manager.AuthManager
import com.mygdx.game.domain.manager.LocalUserManager
import com.mygdx.game.framework.postPlayerToFirestore
import com.mygdx.game.util.Constants.BIO_AUTH_FAILED
import com.mygdx.game.util.Constants.BIO_AUTH_SUCCESS
import com.mygdx.game.util.Constants.EMAIL_IS_IN_USE
import com.mygdx.game.util.Constants.LOGIN_FAILED
import com.mygdx.game.util.Constants.LOGIN_SUCCESS
import com.mygdx.game.util.Constants.SIGN_UP_SUCCESS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

interface UpdateListener {
    fun onUpdate(data: Location)
    fun onUpdate(data: String)
    fun onUpdate(data: Message)
}
class AuthManagerImpl @Inject constructor (private val firebaseAuth: FirebaseAuth?,
                                           private val firestore: FirebaseFirestore,
                                           private val dataRepository: DataRepository,
                                           private val localUserManager: LocalUserManager
) : AuthManager {
    private var updateListener: UpdateListener? = null
    private lateinit var contxt: Context

    override suspend fun signIn(email: String, password: String, update: Boolean) : Boolean{
        val success :Boolean = doSignIn(email, password)
        if (success) {
            try {
                // Sign-in was successful,maybe I did a signOut and lost the local data so we fetch those data
                val name = firebaseAuth?.currentUser?.displayName ?: email
                // save user profile application state
                val userProfileBundle =
                    UserProfileBundle(displayName = name, email = email)
                localUserManager.saveUserProfile(userProfileBundle)
                // fetch rank data
                val result = dataRepository.fetchUserData(name).value
                val rank:Rank
                if(result?.isNotEmpty() == true){
                    val rankData = dataRepository.fetchUserData(name).value?.get(0)?.split(" ")
                    rank = Rank(rankData?.get(0) ?: name, rankData?.get(1)?.toInt() ?: 0)

                } else {
                    rank = Rank( name,  0)
                }
                // save user rank
                localUserManager.saveScore(rank)

                // save bio application state
                val bio = Biometric(userEmail = email, userPass = password)
                localUserManager.saveBio(bio)
                if(update) {
                    // Notify listener of successful sign-up
                    updateListener?.onUpdate(LOGIN_SUCCESS)
                }
                return true
            } catch (e: Exception) {
                // Handle exceptions within runBlocking block
                Log.e("AUTH_MANAGER",e.toString())
                if(update) {
                    // Notify listener of successful sign-up
                    updateListener?.onUpdate("Error: ${e.message}")
                }
                return false
            }
        } else {
            // Sign-in failed or fields were empty, handle accordingly
            if(update) {
                // Notify listener of successful sign-up
                updateListener?.onUpdate(LOGIN_FAILED)
            }
            return false
        }
    }

    override suspend fun bioSignIn(context: Context, callbacks: (String) -> Unit) {

        // User is logged in
        contxt=context
        val biometricPrompt = BiometricPrompt.Builder(context)
            .setTitle("Biometric Authentication")
            .setSubtitle("Please authenticate to continue")
            .setNegativeButton(
                "Cancel",
                context.mainExecutor
            ) { _, _ -> callbacks.invoke("Bio Auth failed") }
            .build()

        biometricPrompt.authenticate(
            android.os.CancellationSignal(),
            context.mainExecutor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    CoroutineScope(Dispatchers.IO).launch {
                        // Use withContext to switch to a background thread for blocking operation
                        val bio = localUserManager.readBio()

                        Log.d("DEBUG", "bio from local user manager: $bio")
                        // Perform the authentication
                        val resultSignIn = doSignIn(bio.first, bio.second)

                        // Invoke the callback with the authentication result
                        if (resultSignIn) {
                            // Sign-in was successful,maybe I did a signOut and lost the local data so we fetch those data
                            val name = firebaseAuth?.currentUser?.displayName ?: bio.first
                            // save user profile application state
                            val userProfileBundle =
                                UserProfileBundle(displayName = name, email = bio.first)
                            localUserManager.saveUserProfile(userProfileBundle)
                            // fetch rank data
                            val userDataResult = dataRepository.fetchUserData(name).value
                            val rank: Rank = if (userDataResult?.isNotEmpty() == true) {
                                val rankData =
                                    dataRepository.fetchUserData(name).value?.get(0)?.split(" ")
                                Rank(rankData?.get(0) ?: name, rankData?.get(1)?.toInt() ?: 0)

                            } else {
                                Rank(name, 0)
                            }

                            // save user rank
                            localUserManager.saveScore(rank)
                            callbacks.invoke(BIO_AUTH_SUCCESS)
                        } else {
                            callbacks.invoke(BIO_AUTH_FAILED)
                        }
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    callbacks.invoke(BIO_AUTH_FAILED)
                    updateListener?.onUpdate(BIO_AUTH_FAILED)
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    callbacks.invoke(BIO_AUTH_FAILED)
                    updateListener?.onUpdate(BIO_AUTH_FAILED)
                }
            }
        )
    }


    override suspend fun signUp(name: String, email: String, password: String, confirmPass: String) {
        if (password != confirmPass) {
            updateListener?.onUpdate("Two passwords must coincide")
            return // Return early if passwords don't match
        }
        firebaseAuth?.createUserWithEmailAndPassword(email, password)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign-up success
                    Log.d(TAG, "New User $email,$password created successfully")
                    // Update user profile
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        //.setPhotoUri(createDummyPhotoUri())
                        .build()

                    firebaseAuth.currentUser?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                // Display name updated successfully
                                Log.d(TAG, "Display name: $name updated successfully")
                                try {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        // Save user profile application state and so player local state
                                        val userProfileBundle = UserProfileBundle(
                                            displayName = name,
                                            email = email
                                        )
                                        localUserManager.saveUserProfile(userProfileBundle)
                                        // Save user rank
                                        val rank = Rank(name, 0)
                                        localUserManager.saveScore(rank)

                                        // Post player data
                                        val player= Player(
                                            username = name,
                                            location = localUserManager.readLocation() ?: Location("provider"),
                                            distance = 0.0
                                        )
                                        localUserManager.saveFirestoreDocumentId(name)
                                        postPlayerToFirestore(firestore,player)
                                        // Save bio application state
                                        val bio = Biometric(userEmail = email, userPass = password)
                                        localUserManager.saveBio(bio)
                                    }
                                    // Notify listener of successful sign-up
                                    updateListener?.onUpdate(SIGN_UP_SUCCESS)
                                } catch (e: Exception) {
                                    Log.d(TAG, "Failed to set user state: ${e.message}")
                                    // Handle exceptions within runBlocking block
                                    updateListener?.onUpdate("Error: ${e.message}")
                                }
                            } else {
                                // Display name update failed
                                Log.d(TAG, "Failed to update display name: ${updateTask.exception?.message}")
                                // Notify listener of failure
                                updateListener?.onUpdate("Failed to update display name: ${updateTask.exception?.message}")
                            }
                        }
                } else {
                    // Sign-up failed
                    val errorMessage = task.exception?.message ?: "Unknown error"
                    updateListener?.onUpdate(errorMessage)
                }
            }
            ?.addOnFailureListener { exception ->
                // Handle exceptions
                if (exception is FirebaseAuthUserCollisionException) {
                    // User already exists with the same email
                    updateListener?.onUpdate(EMAIL_IS_IN_USE)
                } else {
                    // Other exceptions
                    updateListener?.onUpdate(exception.message.toString())
                }
            }
    }

    override fun authCheck(): Boolean {
        return firebaseAuth?.currentUser != null && localUserManager.getUserProfile().email.isNotEmpty()
    }

    override fun setUpdateListener(ref: UpdateListener) {
        updateListener=ref
    }

    override suspend fun updatePlayerFirestore(name: String, newName: String): String {
        return com.mygdx.game.framework.updatePlayerFirestore(firestore, name, newName)
    }

    private suspend fun doSignIn(email: String, password: String): Boolean {
        return if (email.isNotEmpty() && password.isNotEmpty()) {
            try {
                val task = firebaseAuth?.signInWithEmailAndPassword(email, password)?.await()
                task?.user != null // Check if the user is not null to determine success
            } catch (e: Exception) {
                // Handle any exceptions
                Log.e("AUTH_MANAGER",e.message.toString())
                false
            }
        } else {
            false
        }
    }
}