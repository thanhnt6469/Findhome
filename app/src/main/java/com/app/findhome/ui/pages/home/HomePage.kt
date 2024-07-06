package com.app.findhome.ui.pages.home

import android.annotation.SuppressLint
import android.graphics.Color.parseColor
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.app.findhome.ui.components.*
import com.app.findhome.ui.navigation.AppPage
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.app.findhome.ui.pages.favorite.FavoriteViewModel
import com.app.findhome.R
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun Prev(){
    val navController = rememberNavController()
    val favoriteViewModel = remember { FavoriteViewModel() }
    val homeViewModel: HomeViewModel = hiltViewModel()
    HomePage(navController, favoriteViewModel, homeViewModel)
}

@SuppressLint("SuspiciousIndentation")
@Composable
@ExperimentalMaterial3Api
fun HomePage(
    navController: NavController,
    favoriteViewModel: FavoriteViewModel,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    Log.d("HOME PAGE", "HOME PAGE")
    val recommendedListState = rememberLazyListState()
    val nearbyListState = rememberLazyListState()
    val reversedItems = homeViewModel.properties.value.reversed()
    var showLoading by remember { mutableStateOf(false) }
    var showNotFound by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var notifications by remember { mutableStateOf(false) }
    var recommendedList by remember { mutableStateOf(false) }
    var nearbyList by remember { mutableStateOf(false) }
    val getData = homeViewModel.properties.value

    LaunchedEffect(searchQuery) {
        Log.d("SEARCH QUERY!", searchQuery)
        if (searchQuery.isNotEmpty()) {
            showLoading = true
            delay(1000)
            showLoading = false
            showNotFound = homeViewModel.filteredProperties.value.isEmpty()
        }
    }

    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .background(Color.White))
    {

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = dimensionResource(id = R.dimen.global_margin),
                        end = dimensionResource(id = R.dimen.global_margin),
                        top = dimensionResource(id = R.dimen.global_margin)
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Vị trí")
                    MenuWithScrollState()
                }

                IconButton(onClick = {notifications = true}) {
                    Icon(
                        painter = painterResource(id = R.drawable.bell),
                        contentDescription = "Notification",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    )
                }
            }
        }

        item {
            SearchEditText(query = searchQuery, onSearchQueryChanged = { query ->
                searchQuery = query
                showNotFound = false
                homeViewModel.onSearchQueryChanged(query)
            })
        }
        Log.d("SEARCH QUERY", searchQuery)
        if (searchQuery.isNotEmpty()) {
            if (showLoading) item { Box(Modifier.fillMaxWidth(), Alignment.Center) {LoadingAnimation()}}
            else if (homeViewModel.filteredProperties.value.isNotEmpty()) {
                items(homeViewModel.filteredProperties.value) { property ->
                    PropertyItem(
                        property = property,
                        favoriteViewModel = favoriteViewModel,
                        itemClick = {
                            favoriteViewModel.addPropertyToFavorites(property.id.toString())
                        },
                        detailClick = {
                            navController.navigate("${AppPage.DetailPage.route}/${property.id}")
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp, 5.dp)
                    )
                }
            } else if (showNotFound) {
                item {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 15.dp), contentAlignment = Alignment.Center) {
                        Text("Không tìm thấy")
                    }
                }
            }
        }

        if (searchQuery.isEmpty() || homeViewModel.filteredProperties.value.isEmpty()) {
            item { CategoryButtons(onCategoryClick = { type ->
                searchQuery = type
                showNotFound = false
                homeViewModel.filterPropertiesByType(type)
            }) }

            item {
                SectionTitle("Đề xuất cho bạn", onClick = {recommendedList = true})
                LazyRow(
                    modifier = Modifier.padding(
                        start = dimensionResource(id = R.dimen.global_margin),
                        end = dimensionResource(id = R.dimen.global_margin)
                    ), state = recommendedListState
                ) {
                    Log.d("RECOMMENDED LIST", "RECOMMENDED LIST")
                    items(getData) { item: com.app.findhome.data.model.PropertyDomain ->
                        Log.d("RECOMMENDED LIST1", "RECOMMENDED LIST1")
                        //Log.d("ITEM1", item.toString())
                        PropertyItem(
                            property = item,
                            favoriteViewModel = favoriteViewModel,
                            itemClick = {
                                favoriteViewModel.addPropertyToFavorites(item.id.toString())
                            },
                            detailClick = {
                                navController.navigate("${AppPage.DetailPage.route}/${item.id}")
                            },
                            modifier = Modifier
                                .width(284.dp)
                                .height(244.dp)
                                .padding(5.dp, 5.dp)
                        )
                    }
                }
            }

            item {
                SectionTitle("Gần bạn", onClick = {nearbyList = true})
                LazyRow(
                    modifier = Modifier.padding(
                        start = dimensionResource(id = R.dimen.global_margin),
                        end = dimensionResource(id = R.dimen.global_margin)
                    ), state = nearbyListState
                ) {
                    Log.d("NEARBY LIST", "NEARBY LIST")
                    items(reversedItems) { item: com.app.findhome.data.model.PropertyDomain ->
                        Log.d("ITEM2", item.toString())
                        PropertyItem(
                            property = item,
                            favoriteViewModel = favoriteViewModel,
                            itemClick = {
                                favoriteViewModel.addPropertyToFavorites(item.id.toString())
                            },
                            detailClick = {
                                navController.navigate("${AppPage.DetailPage.route}/${item.id}")
                            },
                            modifier = Modifier
                                .width(284.dp)
                                .height(244.dp)
                                .padding(5.dp, 5.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
    if (notifications) NotificationsDialog(navController)
    if (recommendedList) RecommendedPage(navController, favoriteViewModel, homeViewModel, getData)
    if (nearbyList) NearbyPage(navController, favoriteViewModel, homeViewModel, reversedItems)
}

@Composable
fun MenuWithScrollState() {
    var expanded by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val items = listOf("Đà Nẵng")
    var selectedItem by remember { mutableStateOf(items[0]) }
    Box(
        modifier = Modifier
            .wrapContentSize(Alignment.Center)
    ) {

        IconButton(onClick = { expanded = true }, modifier = Modifier
            .fillMaxWidth()
            .height(25.dp)) {
            Row {
                Image(
                    painter = painterResource(id = R.drawable.location),
                    contentDescription = "Location Icon",
                    modifier = Modifier.padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = selectedItem)
            }
        }
        Box(
            modifier = Modifier

                .wrapContentSize(Alignment.CenterStart)
        ){
            DropdownMenu(
                expanded = expanded,
                offset = DpOffset(120.dp, 50.dp),
                onDismissRequest = { expanded = false },
                //scrollState = scrollState
            ) {
                items.forEachIndexed { _, item ->
                    DropdownMenuItem(
                        text = { Text(text = item) },
                        onClick = {
                            selectedItem = item
                            expanded = false }
                    )
                }
            }
        }

        LaunchedEffect(expanded) {
            if (expanded) {
                scrollState.scrollTo(scrollState.maxValue)
            }
        }
    }
}

@Composable
fun SearchEditText(query: String, onSearchQueryChanged: (String) -> Unit) {
    val textState = remember { mutableStateOf("") }

    LaunchedEffect(query) {
        textState.value = query
    }

    OutlinedTextField(
        value = textState.value,
        onValueChange = { query ->
            textState.value = query
            onSearchQueryChanged(query)
        },
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(24.dp)
            .background(
                color = Color(parseColor("#F4F6F9")),
                shape = RoundedCornerShape(40.dp)
            ),
        placeholder = {
            Text(text = "Tìm kiếm...", color = Color.Black)
        },
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.search_icon),
                contentDescription = null
            )
        },
        trailingIcon = {
            if (textState.value.isNotEmpty()) {
                IconButton(onClick = {
                    textState.value = ""
                    onSearchQueryChanged("")
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.close),
                        contentDescription = "Clear search text"
                    )
                }
            } else {
                IconButton(onClick = {}) {
                    Icon(
                        painter = painterResource(id = R.drawable.settings),
                        contentDescription = "Settings"
                    )
                }
            }
        },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            cursorColor = Color.Black,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
        ),
        textStyle = TextStyle(
            color = Color.Black,
            fontSize = 16.sp
        )
    )
}

@Composable
fun CategoryButtons(onCategoryClick: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        CategoryButton(R.drawable.cat_1, "Trọ", onClick = onCategoryClick)
        CategoryButton(R.drawable.cat_2, "Nguyên căn", onClick = onCategoryClick)
        CategoryButton(R.drawable.cat_3, "Chung cư", onClick = onCategoryClick)
        CategoryButton(R.drawable.cat_4, "Căn hộ", onClick = onCategoryClick)
        CategoryButton(R.drawable.cat_5, "Homestay", onClick = onCategoryClick)
    }
}

@Composable
fun CategoryButton(imageRes: Int, text: String, onClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .clickable { onClick(text) }
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(Color(parseColor("#F4F6F9")), RoundedCornerShape(40.dp))
                .align(Alignment.CenterHorizontally)
        ) {

            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
            )
        }
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = Color.Black,
            modifier = Modifier
                .padding(top = 8.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun SectionTitle(title: String, onClick: () -> Unit) {
    Spacer(modifier = Modifier.height(16.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "Tất cả",
            color = Color(parseColor("#39ACA6")),
            modifier = Modifier.clickable {onClick()}
        )
    }
}

@Composable
fun NotificationsDialog(navController: NavController) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color(parseColor("#F7F7F7")))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp), contentAlignment = Alignment.Center
            ) {
                IconButton(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(40.dp),
                    onClick = { navController.navigate(AppPage.HomePage.route) }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_left),
                        contentDescription = "Back",
                        tint = Color.Black,
                        modifier = Modifier.size(33.dp)
                    )
                }
                Text(
                    text = "Thông báo",
                    modifier = Modifier.align(Alignment.Center),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(parseColor("#32357a"))
                )
                IconButton(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(40.dp),
                    onClick = {}
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.tick),
                        contentDescription = "Read All",
                        modifier = Modifier
                            .size(23.dp)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(enabled = false) {},
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painterResource(id = R.drawable.notifications),
                    contentDescription = null,
                )
            }
        }
    }
}

@Composable
fun RecommendedPage(
    navController: NavController,
    favoriteViewModel: FavoriteViewModel,
    homeViewModel: HomeViewModel,
    getData: List<com.app.findhome.data.model.PropertyDomain>
) {
    var searchQuery by remember { mutableStateOf("") }
    var showLoading by remember { mutableStateOf(false) }
    var showNotFound by remember { mutableStateOf(false) }
    //val properties = homeViewModel.properties.collectAsState()
    LaunchedEffect(searchQuery) {
        Log.d("SEARCH QUERY!", searchQuery)
        if (searchQuery.isNotEmpty()) {
            showLoading = true
            delay(1000)
            showLoading = false
            showNotFound = homeViewModel.filteredProperties.value.isEmpty()
        }
    }
    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .background(Color.White))
        {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp), contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .size(40.dp),
                        onClick = { navController.navigate(AppPage.HomePage.route) }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_left),
                            contentDescription = "Back",
                            tint = Color(parseColor("#32357a")),
                            modifier = Modifier.size(33.dp)
                        )
                    }
                    Text(
                        text = "Đề xuất cho bạn",
                        modifier = Modifier.align(Alignment.Center),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(parseColor("#32357a"))
                    )
                }
            }
            item {
                SearchEditText(query = searchQuery, onSearchQueryChanged = { query ->
                    searchQuery = query
                    homeViewModel.onSearchQueryChanged(query)
                })
            }
            if (searchQuery.isNotEmpty()) {
                if (showLoading) item { Box(Modifier.fillMaxWidth(), Alignment.Center) {LoadingAnimation()}}
                else if (homeViewModel.filteredProperties.value.isNotEmpty()) {
                    items(homeViewModel.filteredProperties.value) { property ->
                        PropertyItem(
                            property = property,
                            favoriteViewModel = favoriteViewModel,
                            itemClick = {
                                favoriteViewModel.addPropertyToFavorites(property.id.toString())
                            },
                            detailClick = {
                                navController.navigate("${AppPage.DetailPage.route}/${property.id}")
                            },
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp, 5.dp)
                        )
                    }
                } else if (showNotFound) {
                    item {
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 15.dp), contentAlignment = Alignment.Center) {
                            Text("Không tìm thấy")
                        }
                    }
                }
            }
            if (searchQuery.isEmpty() || homeViewModel.filteredProperties.value.isEmpty()) {
                item {
                    CategoryButtons(onCategoryClick = { type ->
                        searchQuery = type
                        homeViewModel.filterPropertiesByType(type)
                    })
                }

                items(getData) { item: com.app.findhome.data.model.PropertyDomain ->
                    Log.d("ITEM2", item.toString())
                    PropertyItem(
                        property = item,
                        favoriteViewModel = favoriteViewModel,
                        itemClick = {
                            favoriteViewModel.addPropertyToFavorites(item.id.toString())
                        },
                        detailClick = {
                            navController.navigate("${AppPage.DetailPage.route}/${item.id}")
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp, 5.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun NearbyPage(
    navController: NavController,
    favoriteViewModel: FavoriteViewModel,
    homeViewModel: HomeViewModel,
    reversedItems: List<com.app.findhome.data.model.PropertyDomain>
) {
    var searchQuery by remember { mutableStateOf("") }
    var showLoading by remember { mutableStateOf(false) }
    var showNotFound by remember { mutableStateOf(false) }
    //val properties = homeViewModel.properties.collectAsState()
    LaunchedEffect(searchQuery) {
        Log.d("SEARCH QUERY!", searchQuery)
        if (searchQuery.isNotEmpty()) {
            showLoading = true
            delay(1000)
            showLoading = false
            showNotFound = homeViewModel.filteredProperties.value.isEmpty()
        }
    }
    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .background(Color.White))
        {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp), contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .size(40.dp),
                        onClick = { navController.navigate(AppPage.HomePage.route) }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_left),
                            contentDescription = "Back",
                            tint = Color(parseColor("#32357a")),
                            modifier = Modifier.size(33.dp)
                        )
                    }
                    Text(
                        text = "Gần bạn",
                        modifier = Modifier.align(Alignment.Center),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(parseColor("#32357a"))
                    )
                }
            }
            item {
                SearchEditText(query = searchQuery, onSearchQueryChanged = { query ->
                    searchQuery = query
                    homeViewModel.onSearchQueryChanged(query)
                })
            }
            if (searchQuery.isNotEmpty()) {
                if (showLoading) item { Box(Modifier.fillMaxWidth(), Alignment.Center) {LoadingAnimation()}}
                else if (homeViewModel.filteredProperties.value.isNotEmpty()) {
                    items(homeViewModel.filteredProperties.value) { property ->
                        PropertyItem(
                            property = property,
                            favoriteViewModel = favoriteViewModel,
                            itemClick = {
                                favoriteViewModel.addPropertyToFavorites(property.id.toString())
                            },
                            detailClick = {
                                navController.navigate("${AppPage.DetailPage.route}/${property.id}")
                            },
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp, 5.dp)
                        )
                    }
                } else if (showNotFound) {
                    item {
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 15.dp), contentAlignment = Alignment.Center) {
                            Text("Không tìm thấy")
                        }
                    }
                }
            }
            if (searchQuery.isEmpty() || homeViewModel.filteredProperties.value.isEmpty()) {
                item {
                    CategoryButtons(onCategoryClick = { type ->
                        searchQuery = type
                        homeViewModel.filterPropertiesByType(type)
                    })
                }
                items(reversedItems) { item: com.app.findhome.data.model.PropertyDomain ->
                    Log.d("ITEM2", item.toString())
                    PropertyItem(
                        property = item,
                        favoriteViewModel = favoriteViewModel,
                        itemClick = {
                            favoriteViewModel.addPropertyToFavorites(item.id.toString())
                        },
                        detailClick = {
                            navController.navigate("${AppPage.DetailPage.route}/${item.id}")
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp, 5.dp)
                    )
                }
            }
        }
    }
}