package macc.AR.data.manager

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import macc.AR.compose.authentication.UserProfileBundle
import macc.AR.data.BiometricState

import macc.AR.domain.manager.SettingsManager

class SettingsManagerImpl(
): SettingsManager {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var contxt: Context
    private lateinit var bioState: BiometricState
    private var updateListener: UpdateListener? = null


    override suspend fun update(name: String, email: String, password: String) {
        // Get Firestore instance
        firestore = FirebaseFirestore.getInstance()
        // Get user ID
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        // Update user data in Firestore
        if (userId != null) {
            val userDocRef = firestore.collection("users").document(userId)
            userDocRef.update(
                mapOf(
                    "name" to name,
                    "email" to email,
                    "password" to password
                    // Add other fields as needed
                )
            )
                .addOnSuccessListener {
                    // User data updated successfully
                    bioState.setCredentials(email,password)
                    updateListener?.onUpdate("Update Success")

                }
                .addOnFailureListener { e ->
                    // Failed to update user data
                    updateListener?.onUpdate(e.message.toString())

                }
        }else{
            updateListener?.onUpdate("Something went wrong, please retry later")
        }
    }

    override suspend fun fetch(): UserProfileBundle? {
        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser=firebaseAuth.currentUser
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
        firebaseAuth=FirebaseAuth.getInstance()
        try {
            firebaseAuth.signOut()

            updateListener?.onUpdate("SignOut Success")

        } catch (e: Exception) {
            updateListener?.onUpdate(e.message.toString())
        }
    }

    override fun setUpdateListener(ref: UpdateListener) {
        updateListener=ref    }

}






