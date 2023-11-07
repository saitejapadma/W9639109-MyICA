package uk.ac.tees.mad.w9639109

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

class SignIn : AppCompatActivity() {

    private lateinit var authViewModel: AuthViewModel;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

         authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)


        authViewModel.authResult.observe(this, Observer { result ->
            when (result) {
                is AuthResult.SignInSuccess -> {
                    Toast.makeText(this,"Success", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this,MainActivity::class.java))
                }
                is AuthResult.SignInFailure -> {
                    Toast.makeText(this,"Incorrect Email or Password", Toast.LENGTH_SHORT).show()
                }
                is AuthResult.RegistrationFailure -> {}
                is AuthResult.RegistrationSuccess -> {}
            }
        })




        findViewById<Button>(R.id.signinbtn).setOnClickListener{
            val email = findViewById<EditText>(R.id.emaillogin)
            val password= findViewById<EditText>(R.id.passwordlogin)
            authViewModel.signInUser(email.text.toString(),password.text.toString())

        }

        findViewById<Button>(R.id.sigupininbtn).setOnClickListener{
           startActivity(Intent(applicationContext,SignUp::class.java))
        }


    }
}