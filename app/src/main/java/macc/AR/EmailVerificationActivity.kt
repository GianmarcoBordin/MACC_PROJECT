package macc.AR

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class EmailVerificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.email_verification)

        val generateotp = findViewById<Button>(R.id.buttonGenerateOTP)
        generateotp.setOnClickListener {
            // send email
            FirebaseAuth.getInstance().currentUser?.sendEmailVerification()
        }
    }
}