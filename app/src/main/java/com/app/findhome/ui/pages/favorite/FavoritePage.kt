package com.app.findhome.ui.pages.favorite

import android.graphics.Color.parseColor
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.app.findhome.domain.PropertyDomain
import com.app.findhome.ui.components.PropertyItem
import com.app.findhome.ui.navigation.AppPage
import com.app.findhome.ui.pages.home.HomeViewModel

@Composable
fun FavoritePage(
    navController: NavController,
    favoriteViewModel: FavoriteViewModel,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val favoriteProperties = favoriteViewModel.favoriteProperties.value

    Surface(modifier = Modifier.fillMaxSize()) {
        if (favoriteProperties.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Không tìm thấy mục yêu thích", style = MaterialTheme.typography.titleLarge)
            }
        } else {
            LazyColumn(modifier = Modifier.padding(16.dp).fillMaxSize()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Yêu thích",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(parseColor("#32357a"))
                        )
                    }
                }
                items(favoriteProperties) { property ->
                    val propertyId = homeViewModel.getPropertyById(property)
                    propertyId?.let {
                        PropertyItem(
                            property = it,
                            favoriteViewModel = favoriteViewModel,
                            itemClick = {},
                            detailClick = {
                                navController.navigate("${AppPage.DetailPage.route}/${propertyId.id}")
                            },
                            modifier = Modifier.fillMaxSize(1f).padding(0.dp, 5.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FavoritePagePreview() {
    val dummyProperty = com.app.findhome.data.model.PropertyDomain(1,"House", "Beautiful House", "123 Street, City", "house_1", 1200, 3, 500, 4.5, "A beautiful house with all amenities.", "", mapOf("wifi" to true, "garage" to true))
    val properties = listOf(dummyProperty, dummyProperty, dummyProperty)
    val favoriteViewModel = remember { FavoriteViewModel() }

    Surface(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            items(properties) { property ->
                PropertyItem(
                    property = property,
                    favoriteViewModel = favoriteViewModel,
                    {},{},
                    modifier = Modifier.fillMaxSize(1f).padding(0.dp, 5.dp)
                )
            }
        }
    }
}
