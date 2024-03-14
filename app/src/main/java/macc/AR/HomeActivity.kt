package macc.AR

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import macc.signinup.R


class HomeActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

        firebaseAuth = FirebaseAuth.getInstance()

        val test = findViewById<TextView>(R.id.titleTextView)
        // get username
        val user = firebaseAuth.currentUser
        val username = user?.displayName
        test.text = "Hello $username"

        val logout = findViewById<Button>(R.id.logout)
        logout.setOnClickListener {
            firebaseAuth.signOut()

            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        val delete = findViewById<Button>(R.id.delete)
        delete.setOnClickListener {
            firebaseAuth.signOut()
            user?.delete()

            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }


}