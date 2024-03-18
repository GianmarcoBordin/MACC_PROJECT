package macc.AR.data.manager

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import macc.AR.domain.manager.AuthManager

interface UpdateListener {
    fun onUpdate(data:String)
}
class AuthManagerImpl(
): AuthManager {
    private lateinit var firebaseAuth: FirebaseAuth
    private var updateListener: UpdateListener? = null

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

    override suspend fun signUp(name:String,email: String,password: String,confirmPass:String) {
        firebaseAuth = FirebaseAuth.getInstance()
        if (password!=confirmPass){
            updateListener?.onUpdate("Two passwords must coincide")
        }
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign-up success
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

    override suspend fun signOut() {
       firebaseAuth=FirebaseAuth.getInstance()
        try {
            firebaseAuth.signOut()
            updateListener?.onUpdate("SignOut Success")

        } catch (e: Exception) {
            updateListener?.onUpdate(e.message.toString())
        }
    }

    override fun setUpdateListener(ref: UpdateListener) {
       updateListener=ref
    }


}
