package macc.AR.data

import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import macc.AR.EmailVerificationActivity
import macc.AR.SignUpActivity

fun registerFirebase(username : String, email : String, pass : String, confirmPass : String, activity: SignUpActivity) {
    // getting firebase auth
    var firebaseAuth = FirebaseAuth.getInstance()

    if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
        if (pass == confirmPass) {

            firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                if (it.isSuccessful) {
                    val intent = Intent(activity, EmailVerificationActivity::class.java)
                    ContextCompat.startActivity(activity, intent, null)
                } else {
                    Toast.makeText(activity, it.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(activity, "Password is not matching", Toast.LENGTH_SHORT).show()
        }
    } else {
        Toast.makeText(activity, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()
    }
}