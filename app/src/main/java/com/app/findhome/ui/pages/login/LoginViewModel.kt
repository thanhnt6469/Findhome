package com.app.findhome.ui.pages.login

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.findhome.data.model.ValidationResult
import com.app.findhome.data.model.Validator
import com.app.findhome.domain.LoginResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthCredential
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel(){
    private val TAG = LoginViewModel::class.simpleName
    private val _email = MutableStateFlow("")
    private val _password = MutableStateFlow("")

    private val _emailError = MutableStateFlow<ValidationResult?>(null)
    private val _passwordError = MutableStateFlow<ValidationResult?>(null)

    val email: StateFlow<String> get() = _email
    val password: StateFlow<String> get() = _password

    val emailError: StateFlow<ValidationResult?> get() = _emailError
    val passwordError: StateFlow<ValidationResult?> get() = _passwordError

    private val _isFormValid = MutableStateFlow(false)
    val isFormValid: StateFlow<Boolean> get() = _isFormValid

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> get() = _loginSuccess

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    var loginProgress = mutableStateOf(false)

    private val _logout = MutableStateFlow(false)
    val logout: StateFlow<Boolean?> get() = _logout

    private val _googleState = MutableStateFlow(LoginState())
    val googleState = _googleState.asStateFlow()

    fun googleLogin(result: LoginResult){
        _googleState.update { it.copy(
            isLoginSuccessful = result.data !== null,
            loginError = result.errorMessage,
        ) }
    }

    fun resetState(){
        _googleState.update { LoginState() }
    }

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
        _emailError.value = Validator.validateEmail(newEmail)
        updateFormValidity()
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
        _passwordError.value = Validator.validatePassword(newPassword)
        updateFormValidity()
    }

    private fun updateFormValidity() {
        _isFormValid.value = _emailError.value?.status == true && _passwordError.value?.status == true
    }

    fun validateFields() {
        viewModelScope.launch {
            _emailError.value = Validator.validateEmail(_email.value)
            _passwordError.value = Validator.validatePassword(_password.value)
            updateFormValidity()
        }
    }

    fun login(){
        loginProgress.value = true
        Log.d("TAG", "Login started")
        FirebaseAuth
            .getInstance()
            .signInWithEmailAndPassword(_email.value, _password.value)
            .addOnCompleteListener(){
                loginProgress.value = false
                Log.d("TAG", "Login success")
                Log.d(TAG, "login: ${it.isSuccessful}")

                if(it.isSuccessful){
                    _loginSuccess.value = true
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "Login failure")
                Log.d(TAG, "Error: ${it.message}")
                Log.d(TAG, "Error: ${it.localizedMessage}")
                _errorMessage.value = "${it.message}"
            }
    }

    fun logout(){
        FirebaseAuth.getInstance().signOut()

        val authStateListener = FirebaseAuth.AuthStateListener {
            if (it.currentUser == null) {
                Log.d(TAG, "User is logged out")
                _logout.value = true
            } else {
                Log.d(TAG, "User is not logged out")
            }
        }

        FirebaseAuth.getInstance().addAuthStateListener(authStateListener)
    }

    fun resetLogout() {
        _logout.value = false
    }

    fun resetLoginSuccess() {
        _loginSuccess.value = false
    }

    fun resetLogMessage() {
        _errorMessage.value = null
    }
}