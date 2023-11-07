package uk.ac.tees.mad.w9639109


import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthViewModel : ViewModel() {

    private val _authResult = MutableLiveData<AuthResult>()
    val authResult: LiveData<AuthResult> get() = _authResult

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun signInUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    handleSignInSuccess()
                } else {
                    handleSignInError("Check Email and password Combination")
                }
            }
    }

    fun signupUser(email: String, pass: String) {
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    handleRegistrationSuccess()
                } else {

                    handleRegistrationError(task.exception.toString())
                }
            }
    }

    private fun handleSignInSuccess() {
        val user: FirebaseUser? = auth.currentUser
        _authResult.value = AuthResult.SignInSuccess(user)
    }

    private fun handleSignInError(errorMessage: String) {
        _authResult.value = AuthResult.SignInFailure(errorMessage)
    }

    private fun handleRegistrationSuccess() {
        val user: FirebaseUser? = auth.currentUser
        _authResult.value = AuthResult.RegistrationSuccess(user)
    }

    private fun handleRegistrationError(errorMessage: String) {
        _authResult.value = AuthResult.RegistrationFailure(errorMessage)
    }

}
