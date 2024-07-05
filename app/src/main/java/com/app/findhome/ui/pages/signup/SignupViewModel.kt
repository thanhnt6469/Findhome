package com.app.findhome.ui.pages.signup

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.findhome.data.model.Validator
import com.app.findhome.data.model.ValidationResult
import com.app.findhome.domain.LoginResult
import com.app.findhome.domain.UserData
import com.app.findhome.ui.pages.login.LoginState
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SignupViewModel : ViewModel() {

    private val TAG = SignupViewModel::class.simpleName
    private val _firstName = MutableStateFlow("")
    private val _lastName = MutableStateFlow("")
    private val _email = MutableStateFlow("")
    private val _password = MutableStateFlow("")
    private val _selectedRole = MutableStateFlow("")

    private val _firstNameError = MutableStateFlow<ValidationResult?>(null)
    private val _lastNameError = MutableStateFlow<ValidationResult?>(null)
    private val _emailError = MutableStateFlow<ValidationResult?>(null)
    private val _passwordError = MutableStateFlow<ValidationResult?>(null)

    val firstName: StateFlow<String> get() = _firstName
    val lastName: StateFlow<String> get() = _lastName
    val email: StateFlow<String> get() = _email
    val password: StateFlow<String> get() = _password
    val selectedRole: StateFlow<String> get() = _selectedRole

    val firstNameError: StateFlow<ValidationResult?> get() = _firstNameError
    val lastNameError: StateFlow<ValidationResult?> get() = _lastNameError
    val emailError: StateFlow<ValidationResult?> get() = _emailError
    val passwordError: StateFlow<ValidationResult?> get() = _passwordError

    private val _isFormValid = MutableStateFlow(false)
    val isFormValid: StateFlow<Boolean> get() = _isFormValid

    private val _signupSuccess = MutableStateFlow(false)
    val signupSuccess: StateFlow<Boolean> get() = _signupSuccess

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    var signupProgress = mutableStateOf(false)

    private val _logout = MutableStateFlow(false)
    val logout: StateFlow<Boolean?> get() = _logout

    private val _googleState = MutableStateFlow(LoginState())
    val googleState = _googleState.asStateFlow()
    private lateinit var auth: FirebaseAuth

    fun googleLogin(result: LoginResult){
        _googleState.update { it.copy(
            isLoginSuccessful = result.data !== null,
            loginError = result.errorMessage,
        ) }
    }

    fun resetState(){
        _googleState.update { LoginState() }
    }

    fun onFirstNameChange(newFirstName: String) {
        _firstName.value = newFirstName
        _firstNameError.value = Validator.validateFirstName(newFirstName)
        updateFormValidity()
    }

    fun onLastNameChange(newLastName: String) {
        _lastName.value = newLastName
        _lastNameError.value = Validator.validateLastName(newLastName)
        updateFormValidity()
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

    fun onRoleChange(newRole: String) {
        _selectedRole.value = newRole
        updateFormValidity()
    }

    private fun updateFormValidity() {
        _isFormValid.value = _firstNameError.value?.status == true &&
                //_lastNameError.value?.status == true &&
                _emailError.value?.status == true &&
                _passwordError.value?.status == true //&&
                //_selectedRole.value.isNotEmpty()
    }

    fun validateFields() {
        viewModelScope.launch {
            _firstNameError.value = Validator.validateFirstName(_firstName.value)
            _lastNameError.value = Validator.validateLastName(_lastName.value)
            _emailError.value = Validator.validateEmail(_email.value)
            _passwordError.value = Validator.validatePassword(_password.value)
            updateFormValidity()
        }
    }

    fun createUserFireBase(email: String, password: String, role: String
    ) {
        Log.d("createUserFireBase", "createUserFireBase")
        signupProgress.value = true
        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    Log.d("isSuccessful", "User created successfully")
                    val user = task.result?.user
                    user?.let { firebaseUser ->
                        val database = FirebaseDatabase.getInstance().reference
                        val userRef = database.child("users").child(firebaseUser.uid)
                        Log.d("uid", firebaseUser.uid)
                        val userMap = HashMap<String, Any>()
                        userMap["fullName"] = "${_firstName.value} ${_lastName.value}"
                        userMap["role"] = role
                        Log.d("userMap", userMap.toString())
                        userRef.setValue(userMap)
                            .addOnCompleteListener { addTask ->
                                Log.d("isSuccessful1", "User created successfully")
                                if (addTask.isSuccessful) {
                                    Log.d("TAG", "User data added to database")
                                    signupProgress.value = false
                                    _signupSuccess.value = true
                                } else {
                                    Log.d("TAG", "Failed to add user data to database")
                                }
                            }
                    }
                } else {
                    val exception = task.exception
                    exception?.printStackTrace()
                    Log.d(TAG, "Failed to create user")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Failed to create user")
                Log.d(TAG, "Error: ${exception.message}")
                Log.d(TAG, "Error: ${exception.localizedMessage}")
                _errorMessage.value = "${exception.message}"
            }
    }

    fun resetSignupSuccess() {
        _signupSuccess.value = false
    }

    fun resetLogMessage() {
        _errorMessage.value = null
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

    private val _userData = MutableStateFlow<UserData?>(null)
    val userData = _userData.asStateFlow()

    fun fetchUserProfile(userId: String) {
        val database = FirebaseDatabase.getInstance().reference
        val userRef = database.child("users").child(userId)

        userRef.get().addOnSuccessListener { dataSnapshot ->
            val userData = dataSnapshot.getValue(UserData::class.java)
            _userData.value = userData
        }.addOnFailureListener { exception ->
            Log.e(TAG, "Error getting user data: $exception")
        }
    }
//    fun fetchUserProfile(userId: String) {
//        viewModelScope.launch {
//            val userData = fetchUserProfileFromFirebase(userId)
//            _userData.value = userData
//        }
//    }
//
//    private suspend fun fetchUserProfileFromFirebase(userId: String): UserData? {
//        return try {
//            val database = FirebaseDatabase.getInstance().reference
//            val userRef = database.child("users").child(userId)
//            val dataSnapshot = userRef.get().await()
//            dataSnapshot.getValue(UserData::class.java)
//        } catch (exception: Exception) {
//            Log.e(TAG, "Error getting user data: $exception")
//            null
//        }
//    }
//
//    fun updateUserData(userData: UserData?) {
//        _userData.value = userData
//    }
}
