package uk.ac.tees.mad.w9639109

import com.google.firebase.auth.FirebaseUser

sealed class AuthResult {
    data class SignInSuccess(val user: FirebaseUser?) : AuthResult()
    data class SignInFailure(val errorMessage: String) : AuthResult()

    data class RegistrationSuccess(val user: FirebaseUser?) : AuthResult()
    data class RegistrationFailure(val errorMessage: String) : AuthResult()
}