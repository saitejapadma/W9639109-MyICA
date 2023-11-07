package uk.ac.tees.mad.w9639109

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth

class Intro : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private val delayMillis = 2000L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        auth = FirebaseAuth.getInstance()

        Handler().postDelayed({
            val targetClass = if (auth.currentUser != null) {
                MainActivity::class.java
            } else {
                SignIn::class.java
            }
            startActivity(Intent(this, targetClass))
        }, delayMillis)
    }
}