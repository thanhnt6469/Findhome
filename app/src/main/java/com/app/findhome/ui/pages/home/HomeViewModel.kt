package com.app.findhome.ui.pages.home

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.findhome.domain.PropertyDomain
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(): ViewModel() {

    private val _state = mutableStateOf(HomeState())
    val state : State<HomeState>
        get() = _state

    private val _properties = mutableStateOf<List<com.app.findhome.data.model.PropertyDomain>>(emptyList())
    val properties: State<List<com.app.findhome.data.model.PropertyDomain>> = _properties

    private val _filteredProperties = mutableStateOf(emptyList<com.app.findhome.data.model.PropertyDomain>())
    val filteredProperties: State<List<com.app.findhome.data.model.PropertyDomain>> = _filteredProperties

    private val  db = FirebaseFirestore.getInstance()
    val stateData = MutableLiveData<List<com.app.findhome.data.model.PropertyDomain>>()

    private val _stateDataByUserId = MutableStateFlow<List<com.app.findhome.data.model.PropertyDomain>>(emptyList())
    val stateDataByUserId: StateFlow<List<com.app.findhome.data.model.PropertyDomain>> = _stateDataByUserId

    init {
        getData()
    }

    private fun getData() {
        viewModelScope.launch {
            try {
                val querySnapshot = db.collection("rooms").get().await()
                val itemList = ArrayList<com.app.findhome.data.model.PropertyDomain>()
                for (document in querySnapshot.documents) {
                    val property = document.toObject(com.app.findhome.data.model.PropertyDomain::class.java)
                    property?.let { itemList.add(it) }
                }
                stateData.value = itemList
                _stateDataByUserId.value = itemList
                _properties.value = itemList
                _filteredProperties.value = itemList
            } catch (e: FirebaseFirestoreException) {
                Log.e("error", "getDataFromFireStore: ", e)
            }
        }
    }

    fun setItems(): List<PropertyDomain> {
        return emptyList()
    }

    fun getPropertyById(id: String): com.app.findhome.data.model.PropertyDomain? {
        return stateData.value?.find { it.id.toString() == id }
    }

    fun getPropertyByUserId(userId: String): List<com.app.findhome.data.model.PropertyDomain> {
        val data = _stateDataByUserId.value.filter { it.userId == userId }
        Log.d("getPropertyByUserId", "getPropertyByUserId: $data")
        return data
    }

    private fun filterPropertiesByAddress(query: String): List<com.app.findhome.data.model.PropertyDomain> {
        return stateData.value?.filter { property ->
            property.address.contains(query, ignoreCase = true)
        } ?: emptyList()
    }

    fun filterPropertiesByType(type: String) {
        viewModelScope.launch {
            _filteredProperties.value = stateData.value?.filter { property ->
                property.type.contains(type, ignoreCase = true)
            } ?: emptyList()
        }
    }


    fun onSearchQueryChanged(query: String) {
        viewModelScope.launch {
            _filteredProperties.value = filterPropertiesByAddress(query)
        }
    }
}

suspend fun getDataFromFireStore(): ArrayList<com.app.findhome.data.model.PropertyDomain> {
    val itemList = ArrayList<com.app.findhome.data.model.PropertyDomain>()
    val db = FirebaseFirestore.getInstance()
    var propertyDomain = com.app.findhome.data.model.PropertyDomain()
    try {
        db.collection("rooms").get().await().map {
            val result = it.toObject(com.app.findhome.data.model.PropertyDomain::class.java)
            Log.d("GET DATA1", "Property object: $result")
            //propertyDomain = result
            itemList.addAll(listOf(result))
        }
    } catch (e: FirebaseFirestoreException) {
        Log.e("error", "getDataFromFireStore: ", e)
    }
    Log.d("GET DATA2", "Property object: $itemList")
    return itemList
}