package com.app.findhome.ui.pages.details

import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.app.findhome.ui.components.DetailItem
import com.app.findhome.ui.pages.favorite.FavoriteViewModel

@Composable
fun DetailPage(
    navController: NavController,
    favoriteViewModel: FavoriteViewModel,
    detailViewModel: DetailViewModel = hiltViewModel(),
    propertyId: String
) {
    val propertyList by detailViewModel.property.observeAsState()
    val property = remember { mutableStateOf<com.app.findhome.data.model.PropertyDomain?>(null) }

    Log.d("PROPERTY ID", propertyId)
    LaunchedEffect(propertyList) {
        val fetchedProperty = detailViewModel.getPropertyById(propertyId)
        Log.d("DATA1 DP", fetchedProperty.toString())
        property.value = fetchedProperty
        fetchedProperty?.let {
            detailViewModel.setProperty(it)
        }
        Log.d("DATA 2 DP", fetchedProperty.toString())
    }
    Log.d("DATA 3 DP", property.toString())
    property.value?.let {
        DetailItem(
            property = it,
            favoriteViewModel = favoriteViewModel,
            navController = navController
        )
    }
    Log.d("PROPERTY DATA2 DP", propertyList.toString())
    Log.d("PROPERTY DATA3 DP", property.toString())
}


@Preview(showBackground = true)
@Composable
fun DetailPagePreview() {
    val dummyProperty = com.app.findhome.data.model.PropertyDomain(1,"House", "Beautiful House", "123 Street, City", "https://firebasestorage.googleapis.com/v0/b/findhomeapp-f35c6.appspot.com/o/images%2F3aa65979-d150-4154-a7f4-50d16f4cb10f?alt=media&token=39522fcf-9b94-4659-8974-5f5eb7a6a3bd", 1200, 3, 500, 4.5, "A beautiful house with all amenities.", "", mapOf("wifi" to true, "garage" to true))
    val properties = listOf(dummyProperty)
    val favoriteViewModel = remember { FavoriteViewModel() }
    val detailViewModel: DetailViewModel = hiltViewModel()
    Log.d("PROPERTY ID", dummyProperty.id.toString())

    val propertyList by detailViewModel.property.observeAsState()
    var property by remember { mutableStateOf<com.app.findhome.data.model.PropertyDomain?>(null) }

    LaunchedEffect(propertyList) {
        property = detailViewModel.getPropertyById(dummyProperty.id.toString())
        Log.d("PROPERTY ID", dummyProperty.id.toString())
        Log.d("PROPERTY DATA2", property.toString())

    }
    property?.let {
        DetailItem(
            property = it,
            favoriteViewModel = favoriteViewModel,
            navController = rememberNavController()
        )
    }

    Log.d("PROPERTY DATA1", propertyList.toString())
    Log.d("PROPERTY DATA2", property.toString())
}