package com.app.findhome.ui.pages.article

import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.app.findhome.data.model.PropertyDomain
import com.app.findhome.ui.components.ArticleItem
import com.app.findhome.ui.pages.details.DetailViewModel

@Composable
fun EditArticlePage(
    navController: NavController,
    detailViewModel: DetailViewModel = hiltViewModel(),
    propertyId: String
) {
    val propertyList by detailViewModel.property.observeAsState()
    val property = remember { mutableStateOf<PropertyDomain?>(null) }

    LaunchedEffect(propertyList) {
        val fetchedProperty = detailViewModel.getPropertyById(propertyId)
        property.value = fetchedProperty
        fetchedProperty?.let {
            detailViewModel.setProperty(it)
        }
    }

    property.value?.let {
        ArticleItem(
            property = it,
            navController = navController
        )
    }
}
