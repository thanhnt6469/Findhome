package com.app.findhome.ui.pages.details

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.findhome.domain.PropertyDomain
import com.app.findhome.ui.pages.home.HomeViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.functions.FirebaseFunctions
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

class DetailViewModel @Inject constructor(): ViewModel() {
    private val _property = MutableLiveData<List<com.app.findhome.data.model.PropertyDomain>>()
    val property: LiveData<List<com.app.findhome.data.model.PropertyDomain>> get() = _property
    private val homeViewModel: HomeViewModel = HomeViewModel()
    private val _userName = MutableLiveData<String?>()
    val userName: MutableLiveData<String?> get() = _userName
    private val _profilePictureUrl = MutableLiveData<String?>()
    val profilePictureUrl: MutableLiveData<String?> get() = _profilePictureUrl
    private val _role = MutableLiveData<String?>()
    val role: MutableLiveData<String?> get() = _role

    init {
        homeViewModel.stateData.observeForever { propertyList ->
            Log.d("GET stateData VM", "Property object: $propertyList")
            propertyList?.let {
                _property.value = it
            }
            Log.d("GET DATA ALL VM", "Property object: ${_property.value}")
        }
    }

    fun getPropertyById(id: String): com.app.findhome.data.model.PropertyDomain? {
        val currentList = _property.value ?: return null
        Log.d("GET DATA1 VM", "$currentList")
        val getId = currentList.find { it.id.toString() == id }
        Log.d("GET DATA2 VM", "$getId")
        return getId
    }

    fun setProperty(property: com.app.findhome.data.model.PropertyDomain) {
        _property.value = listOf(property)
        Log.d("setProperty", property.toString())
    }

    fun fetchUserName(userId: String) {
        val currentUser = Firebase.auth.currentUser ?: return

        val databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId)
        val isGoogleSignIn = currentUser.providerData.any { it.providerId == GoogleAuthProvider.PROVIDER_ID }

        if (isGoogleSignIn && currentUser.uid == userId) {
            _userName.postValue(currentUser.displayName)
            _profilePictureUrl.postValue(currentUser.photoUrl?.toString())
        } else {
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    _userName.postValue(snapshot.child("fullName").getValue(String::class.java))
                    _profilePictureUrl.postValue(snapshot.child("profilePictureUrl").getValue(String::class.java))
                }
                override fun onCancelled(error: DatabaseError) {}
            })
        }

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _role.postValue(snapshot.child("role").getValue(String::class.java))
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }


}