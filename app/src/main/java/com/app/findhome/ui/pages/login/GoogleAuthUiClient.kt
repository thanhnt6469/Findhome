package com.app.findhome.ui.pages.login

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.findhome.R
import com.app.findhome.domain.LoginResult
import com.app.findhome.domain.UserData
import com.app.findhome.ui.pages.signup.SignupViewModel
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class GoogleAuthUiClient(
    private val context: Context,
    private val oneTapClient: SignInClient,
) {
    private val auth = FirebaseAuth.getInstance()
    private var role: String = ""
    private var cachedUserData: UserData? = null

    fun setRole(role: String) {
        this.role = role
    }

    @RequiresApi(Build.VERSION_CODES.P)
    suspend fun logIn(): IntentSender? {
        return try {
            val result = oneTapClient.beginSignIn(buildLoginRequest()).await()
            result.pendingIntent.intentSender
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            null
        }
    }

    suspend fun loginWithIntent(intent: Intent): LoginResult {
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)
        Log.d("Role", role)

        return try {
            val user = auth.signInWithCredential(googleCredentials).await().user
            user?.let {
                val userId = it.uid
                val username = it.displayName ?: ""
                val email = it.email ?: ""
                val profilePictureUrl = it.photoUrl?.toString() ?: ""
                val role = this.role
                Log.d("ROLE", role)
                val userData = UserData(
                    userId = userId,
                    username = username,
                    gmail = email,
                    profilePictureUrl = profilePictureUrl,
                    fullName = username,
                    role = role
                )

                val database = FirebaseDatabase.getInstance().reference
                val userRef = database.child("users").child(userId)
                val userMap = hashMapOf(
                    "fullName" to username,
                    "role" to role
                )
                Log.d("UserData", "$userData $userMap")

                userRef.setValue(userMap).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("Realtime Database", "User data added successfully")
                    } else {
                        Log.d("Realtime Database", "Failed to add user data")
                    }
                }

                LoginResult(data = userData, errorMessage = null)
            } ?: LoginResult(data = null, errorMessage = "User is null")
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            LoginResult(data = null, errorMessage = e.message)
        }
    }

    suspend fun signOut() {
        try {
            oneTapClient.signOut().await()
            auth.signOut()
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
    }

    private fun fetchUserProfileFromFirebase(userId: String): Flow<UserData?> = callbackFlow {
        val database = FirebaseDatabase.getInstance().reference
        val userRef = database.child("users").child(userId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.getValue(UserData::class.java)).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        userRef.addValueEventListener(listener)

        awaitClose { userRef.removeEventListener(listener) }
    }
//    fun getLoggedInUser(): UserData? = auth.currentUser?.run {
//        UserData(
//            userId = uid,
//            username = displayName,
//            gmail = email,
//            profilePictureUrl = photoUrl?.toString(),
//            fullName = displayName,
//            role = displayName
//        )
//    }
@Composable
fun getLoggedInUser(): UserData? {
    val auth = Firebase.auth
    val currentUser = auth.currentUser
    val isGoogleSignIn = currentUser?.providerData?.any { it.providerId == GoogleAuthProvider.PROVIDER_ID } ?: false

    val (cachedUserData, setCachedUserData) = remember { mutableStateOf<UserData?>(null) }

    if (isGoogleSignIn) {
        currentUser?.run {
            val userId = uid
            val username = displayName
            val email = email
            val profilePictureUrl = photoUrl?.toString()

            if (cachedUserData == null) {
                LaunchedEffect(userId) {
                    fetchUserProfileFromFirebase(userId).collect { userData ->
                        setCachedUserData(userData?.copy(
                            username = username,
                            gmail = email,
                            profilePictureUrl = profilePictureUrl,
                            fullName = username,
                            role = userData.role
                        ))
                    }
                }
            }

            val cache = cachedUserData ?: UserData(
                userId = userId,
                username = username,
                gmail = email,
                profilePictureUrl = profilePictureUrl,
                fullName = username,
                role = cachedUserData?.role
            )
            Log.d("UserData Cache", "$cache")
            return cache
        }
    } else {
        currentUser?.run {
            val userId = uid

            if (cachedUserData == null) {
                LaunchedEffect(userId) {
                    fetchUserProfileFromFirebase(userId).collect { userData ->
                        setCachedUserData(userData)
                    }
                }
            }

            val dataFetch = cachedUserData
            val cache = dataFetch ?: UserData(
                userId = userId,
                username = displayName,
                gmail = email,
                profilePictureUrl = dataFetch?.profilePictureUrl ?: "",
                fullName = dataFetch?.fullName,
                role = dataFetch?.role
            )
            Log.d("UserData Cache", "$cache")
            return cache
        }
    }

    return null
}


    private fun buildLoginRequest(): BeginSignInRequest {
        return BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(context.getString(R.string.web_client_id))
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }
}
