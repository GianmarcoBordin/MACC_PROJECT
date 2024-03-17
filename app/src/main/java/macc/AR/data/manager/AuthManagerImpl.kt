package macc.AR.data.manager

import com.google.firebase.auth.FirebaseAuth
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

    override suspend fun signUp(): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun logOut(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setUpdateListener(ref: UpdateListener) {
       updateListener=ref
    }


}
