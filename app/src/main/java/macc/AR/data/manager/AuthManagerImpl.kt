package macc.AR.data.manager

import android.content.ContentValues.TAG
import android.content.Context
import android.hardware.biometrics.BiometricPrompt
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.UserProfileChangeRequest
import macc.AR.data.BiometricState
import macc.AR.domain.manager.AuthManager
import javax.inject.Inject

interface UpdateListener {
    fun onUpdate(data:String)
}
class AuthManagerImpl @Inject constructor (private val firebaseAuth: FirebaseAuth?,
                                           private val biometricState: BiometricState) : AuthManager {
    private var updateListener: UpdateListener? = null
    private lateinit var contxt: Context

    override suspend fun signIn(email: String,password: String) {
        doSignIn(email,password)
    }

    override suspend fun bioSignIn(context: Context, callbacks: (String) -> Unit) {
        // Check if a user is currently logged in
        val currentUser = firebaseAuth?.currentUser
        if (currentUser != null) {
            // User is logged in
            contxt=context
            biometricState.setBio(context)
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
                        val bio=biometricState.getBio()
                        doSignIn(bio.first,bio.second)
                        callbacks.invoke("Bio Auth Success")
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        callbacks.invoke("Bio Auth failed")
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        callbacks.invoke("Bio Auth failed")
                    }
                }
            )
        } else {
            // User is not logged in
            Log.d(TAG,"User does not exists cannot bio sign in")
            callbacks.invoke("User does not exist, please first register")

        }

    }


    override suspend fun signUp(name:String,email: String,password: String,confirmPass:String) {
        if (password!=confirmPass){
            updateListener?.onUpdate("Two passwords must coincide")
        }
        firebaseAuth?.createUserWithEmailAndPassword(email, password)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign-up success
                    Log.d(TAG,"Display name updated successfully")
                    // Update user profile
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        //.setPhotoUri(createDummyPhotoUri())
                        .build()

                    firebaseAuth?.currentUser?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Display name updated successfully
                                Log.d(TAG,"Display name updated successfully")
                            } else {
                                // Display name update failed
                                Log.d(TAG,"Failed to update display name: ${task.exception?.message}")
                            }
                        }
                    biometricState.setCredentials(email,password)
                    updateListener?.onUpdate("SignUp Success")

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
                    updateListener?.onUpdate("Email is already in use")
                } else {
                    // Other exceptions
                    updateListener?.onUpdate(exception.message.toString())

                }
            }
    }

    override fun authCheck(): Boolean {
        return firebaseAuth?.currentUser != null
    }


    override fun setUpdateListener(ref: UpdateListener) {
       updateListener=ref
    }


     private fun doSignIn(email: String,password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            // fetch
            firebaseAuth?.signInWithEmailAndPassword(email, password)?.addOnCompleteListener {
                // Notify UI about data update
                if (it.isSuccessful) {
                    updateListener?.onUpdate("Login Success")
                } else {
                    updateListener?.onUpdate("Login Failed")
                }
            }
        } else {
            updateListener?.onUpdate("Fields must not be empty")
        }
    }

}
