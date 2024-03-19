package macc.AR.data.manager

import android.content.ContentValues.TAG
import android.content.Context
import android.hardware.biometrics.BiometricPrompt
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.actionCodeSettings
import com.google.firebase.auth.auth
import macc.AR.data.BiometricState
import macc.AR.domain.manager.AuthManager

interface UpdateListener {
    fun onUpdate(data:String)
}
class AuthManagerImpl(
): AuthManager {
    private lateinit var firebaseAuth: FirebaseAuth
    private var updateListener: UpdateListener? = null
    private lateinit var contxt: Context
    private lateinit var bioState: BiometricState
    private lateinit var otp: String

    override suspend fun signIn(email: String,password: String) {
        // get firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        if (email.isNotEmpty() && password.isNotEmpty()) {
            // fetch
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
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

    //TODO // Function to handle biometric login and sign in with Firebase
    fun biometricLoginAndSignIn(email: String, password: String, callback: (Boolean) -> Unit) {
        // Perform biometric authentication and obtain credentials
        // Example: showBiometricPrompt { credentials ->
        //     if (credentials != null) {
        //         signInWithFirebase(credentials.email, credentials.password) { success ->
        //             callback.invoke(success)
        //         }
        //     } else {
        //         callback.invoke(false)
        //     }
        // }
    }

    override suspend fun bioSignIn(context: Context, callback: (Boolean) -> Unit) {
        contxt=context
        val biometricPrompt = BiometricPrompt.Builder(context)
            .setTitle("Biometric Authentication")
            .setSubtitle("Please authenticate to continue")
            .setNegativeButton(
                "Cancel",
                context.mainExecutor,
                { _, _ -> callback.invoke(false) }
            )
            .build()

        biometricPrompt.authenticate(
            android.os.CancellationSignal(),
            context.mainExecutor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    bioState.initBio(context)
                    callback.invoke(true)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    callback.invoke(false)
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    callback.invoke(false)
                }
            }
        )
    }


    override suspend fun signUp(name:String,email: String,password: String,confirmPass:String) {
        firebaseAuth = FirebaseAuth.getInstance()
        if (password!=confirmPass){
            updateListener?.onUpdate("Two passwords must coincide")
        }
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign-up success
                    bioState.setBio(contxt,email,password)
                    updateListener?.onUpdate("SignUp Success")

                } else {
                    // Sign-up failed
                    val errorMessage = task.exception?.message ?: "Unknown error"
                    updateListener?.onUpdate(errorMessage)
                }
            }
            .addOnFailureListener { exception ->
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

    override fun confirm(otp: String): Boolean {
        return otp==this.otp
    }

    override fun authCheck(): Boolean {
        return firebaseAuth.currentUser != null
    }

    override suspend fun sendEmail() {
        val email = FirebaseAuth.getInstance().currentUser?.email
        val name = FirebaseAuth.getInstance().currentUser?.displayName
        otp=generateOTP()
        val actionCodeSettings = actionCodeSettings {
            // URL you want to redirect back to. The domain (www.example.com) for this
            // URL must be whitelisted in the Firebase Console.
            url = "Hi $name! This is your auto-generated OTP: $otp please insert it in the app to confirm your account!"
            // This must be true
            handleCodeInApp = true
            setIOSBundleId("com.example.ios")
            setAndroidPackageName(
                "com.example.android",
                true, // installIfNotAvailable
                "12", // minimumVersion
            )
        }
        Firebase.auth.sendSignInLinkToEmail(email.toString(), actionCodeSettings)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Email sent.")
                    updateListener?.onUpdate("Confirmation Success")
                }else{
                    updateListener?.onUpdate("Something went wrong, please retry later")
                }
            }
    }

    override fun setUpdateListener(ref: UpdateListener) {
       updateListener=ref
    }

    // Generate OTP function
    private fun generateOTP(): String {
        val otpLength = 6 // Length of OTP
        val otp = StringBuilder()
        val random = java.util.Random()
        for (i in 0 until otpLength) {
            otp.append(random.nextInt(10)) // Generate random digit
        }
        return otp.toString()
    }

}
