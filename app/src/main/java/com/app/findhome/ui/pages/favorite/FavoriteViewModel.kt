package com.app.findhome.ui.pages.favorite

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(): ViewModel() {

    val favoriteProperties = mutableStateOf<List<String>>(listOf())

    fun addPropertyToFavorites(property: String) {
        favoriteProperties.value += property
    }

    fun removePropertyFromFavorites(property: String) {
        favoriteProperties.value -= property
    }

    fun isPropertyFavorite(property: String): Boolean {
        return favoriteProperties.value.contains(property)
    }
}
