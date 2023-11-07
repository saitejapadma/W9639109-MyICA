package uk.ac.tees.mad.w9639109

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

class SignUp : AppCompatActivity() {

    private lateinit var authViewModel: AuthViewModel;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)


        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)


        authViewModel.authResult.observe(this, Observer { result ->
            when (result) {
                is AuthResult.SignInSuccess -> {

                }
                is AuthResult.SignInFailure -> {

                }
                is AuthResult.RegistrationFailure -> {
                    println(result.toString())
                    Toast.makeText(this,result.toString(), Toast.LENGTH_SHORT).show()
                }

                is AuthResult.RegistrationSuccess -> {
                    Toast.makeText(this,"Registation Done, Please Login", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this,SignIn::class.java))
                }
            }
        })




        findViewById<Button>(R.id.signupbtnsignup).setOnClickListener{
            val email = findViewById<EditText>(R.id.emailsignup)
            val password= findViewById<EditText>(R.id.passwordsignup)

            authViewModel.signupUser(email.text.toString(),password.text.toString())
        }

        findViewById<Button>(R.id.siginbtnup).setOnClickListener{
            startActivity(Intent(applicationContext,SignIn::class.java))
        }

    }
}