package com.app.findhome.ui.pages.profile

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class ProfileViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    fun updateUserProfile(firstName: String, lastName: String, userPictureUrl: String, profilePictureUri: Uri?, context: Context, onComplete: (Boolean, String?) -> Unit) {
        val user = auth.currentUser
        if (user != null) {
            val userId = user.uid

            if (profilePictureUri != null) {
                val profilePictureRef = storage.reference.child("profile_pictures/$userId.jpg")
                profilePictureRef.putFile(profilePictureUri)
                    .addOnSuccessListener {
                        profilePictureRef.downloadUrl.addOnSuccessListener { uri ->
                            val profilePictureUrl = uri.toString()
                            updateUserInDatabase(userId, firstName, lastName, profilePictureUrl, onComplete)
                        }
                    }
                    .addOnFailureListener { exception ->
                        onComplete(false, exception.message)
                    }
            } else {
                updateUserInDatabase(userId, firstName, lastName, userPictureUrl, onComplete)
            }
        } else {
            onComplete(false, "User is not authenticated")
        }
    }

    private fun updateUserInDatabase(userId: String, firstName: String, lastName: String, profilePictureUrl: String?, onComplete: (Boolean, String?) -> Unit) {
        val userUpdates = mutableMapOf<String, Any>(
            "fullName" to "$lastName $firstName"
        )
        profilePictureUrl?.let {
            userUpdates["profilePictureUrl"] = it
        }

        database.child("users").child(userId).updateChildren(userUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, null)
                } else {
                    onComplete(false, task.exception?.message)
                }
            }
    }

    fun reauthenticateAndChangePassword(currentPassword: String, newPassword: String, onComplete: (Boolean, String?) -> Unit) {
        val user = auth.currentUser
        if (user != null) {
            val credential = EmailAuthProvider.getCredential(user.email ?: "", currentPassword)
            user.reauthenticate(credential)
                .addOnCompleteListener { reauthTask ->
                    if (reauthTask.isSuccessful) {
                        user.updatePassword(newPassword)
                            .addOnCompleteListener { updateTask ->
                                if (updateTask.isSuccessful) {
                                    onComplete(true, null)
                                } else {
                                    onComplete(false, updateTask.exception?.message)
                                }
                            }
                    } else {
                        onComplete(false, reauthTask.exception?.message)
                    }
                }
        } else {
            onComplete(false, "User is not authenticated")
        }
    }

    fun isCurrentUserGoogleUser(): Boolean {
        val user = auth.currentUser
        return user?.providerData?.any { it.providerId == GoogleAuthProvider.PROVIDER_ID } ?: false
    }
}
