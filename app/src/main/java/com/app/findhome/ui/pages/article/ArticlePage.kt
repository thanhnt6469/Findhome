package com.app.findhome.ui.pages.article

import android.graphics.Color.parseColor
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.app.findhome.domain.UserData
import com.app.findhome.ui.components.LoadingAnimation
import com.app.findhome.ui.navigation.AppPage
import com.shashank.sony.fancytoastlib.FancyToast

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ArticlePage(navController = rememberNavController(), userData = null)
}

@Composable
fun ArticlePage(navController: NavController, userData: UserData?) {
    val viewModel: ArticleViewModel = viewModel()
    var currentScreen by remember { mutableIntStateOf(1) }
    val context = LocalContext.current
    val isLoading = viewModel.isLoading
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Color(parseColor("#6750a4")))
        ) {
            Text(
                text = "Đăng bài viết",
                modifier = Modifier.align(Alignment.Center),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(modifier = Modifier.fillMaxSize()) {
                when (currentScreen) {
                    1 -> LocationScreen(
                        viewModel,
                        onBackClicked = {
                            navController.popBackStack()
                        },
                        onContinueClicked = { currentScreen = 2 }
                    )

                    2 -> RoomDetailsScreen(
                        viewModel,
                        onBackClicked = { currentScreen = 1 },
                        onContinueClicked = { currentScreen = 3 },
                        userData?.role ?: ""
                    )

                    3 -> ImageUploadScreen(
                        viewModel,
                        onBackClicked = { currentScreen = 2 },
                        onPostClicked = {
                            viewModel.postArticle(
                                onSuccess = {
                                    viewModel.resetData()
                                    FancyToast.makeText(
                                        context,
                                        "Article posted successfully",
                                        FancyToast.LENGTH_SHORT,
                                        FancyToast.SUCCESS,
                                        true
                                    ).show()
                                    navController.navigate(AppPage.HomePage.route)
                                },
                                onFailure = { e ->
                                    FancyToast.makeText(
                                        context,
                                        "Error posting article: ${e.message}",
                                        FancyToast.LENGTH_SHORT,
                                        FancyToast.ERROR,
                                        true
                                    ).show()
                                }
                            )
                        }
                    )
                }
            }
            if (isLoading) {
                LoadingAnimation()
            }
        }
    }

}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationScreen(viewModel: ArticleViewModel, onBackClicked: () -> Unit, onContinueClicked: () -> Unit) {
    val context = LocalContext.current
    val city = arrayOf("Đà Nẵng")
    val town = arrayOf("Ngũ Hành Sơn", "Liên Chiểu", "Hải Châu", "Thanh Khê", "Cẩm Lệ", "Sơn Trà", "Hòa Vang")
    var expandedCity by remember { mutableStateOf(false) }
    var expandedTown by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("Tỉnh/Thành Phố")
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp))
            {
                ExposedDropdownMenuBox(
                    expanded = expandedCity,
                    onExpandedChange = {
                        expandedCity = !expandedCity
                    }
                ) {
                    TextField(
                        value = viewModel.selectedCity,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCity) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expandedCity,
                        onDismissRequest = { expandedCity = false }
                    ) {
                        city.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(text = item) },
                                onClick = {
                                    viewModel.selectedCity = item
                                    expandedCity = false
                                    Toast.makeText(context, item, Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("Quận/Huyện/Thị trấn")
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp))
            {
                ExposedDropdownMenuBox(
                    expanded = expandedTown,
                    onExpandedChange = {
                        expandedTown = !expandedTown
                    }
                ) {
                    TextField(
                        value = viewModel.selectedTown,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTown) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expandedTown,
                        onDismissRequest = { expandedTown = false }
                    ) {
                        town.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(text = item) },
                                onClick = {
                                    viewModel.selectedTown = item
                                    expandedTown = false
                                    Toast.makeText(context, item, Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("Địa chỉ")
            TextField(
                value = viewModel.address,
                onValueChange = { viewModel.address = it },
                label = { Text("Số nhà, tên đường") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                singleLine = true
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onBackClicked) {
                Text("QUAY LẠI")
            }
            Button(
                onClick = onContinueClicked
            ) {
                Text("TIẾP TỤC")
            }
        }
    }
}

@Composable
fun RoomDetailsScreen(viewModel: ArticleViewModel, onBackClicked: () -> Unit, onContinueClicked: () -> Unit, role: String) {
    val newsTypes = listOf("Cho thuê", "Tìm người ở ghép")
    val housingTypes = listOf("Trọ", "Nguyên căn", "Chung cư", "Căn hộ", "Homestay")
    val facilityOptions = listOf("WiFi", "Nhà xe")

    @Composable
    fun NewsTypeRadioButton(newsType: String) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = viewModel.title == newsType,
                onClick = { viewModel.title = newsType }
            )
            Text(newsType)
        }
    }

    @Composable
    fun HousingTypeRadioButton(text: String) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = viewModel.type == text,
                onClick = { viewModel.type = text }
            )
            Text(text)
        }
    }

    @Composable
    fun FacilityCheckbox(text: String) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = when (text) {
                    "WiFi" -> viewModel.wifi
                    "Nhà xe" -> viewModel.garage
                    else -> false
                },
                onCheckedChange = {
                    when (text) {
                        "WiFi" -> viewModel.wifi = it
                        "Nhà xe" -> viewModel.garage = it
                    }
                }
            )
            Text(text)
        }
    }

    @Composable
    fun BedCounter() {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Số lượng người: ${viewModel.member}", modifier = Modifier.weight(1f))
            Button(onClick = { if (viewModel.member > 0) viewModel.member-- }) {
                Text("-")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { viewModel.member++ }) {
                Text("+")
            }
        }
    }

    Box(modifier = Modifier
        .fillMaxWidth()
        .background(Color.White)
        .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text("Loại tin")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (role == "Chủ") NewsTypeRadioButton(newsTypes[0])
                    else if (role == "Khách") NewsTypeRadioButton(newsTypes[1])
                    else {
                        NewsTypeRadioButton(newsTypes[0])
                        NewsTypeRadioButton(newsTypes[1])
                    }
                }
            }
            item {

                Text("Loại phòng")
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row{
                        HousingTypeRadioButton(housingTypes[0])
                        HousingTypeRadioButton(housingTypes[1])
                        HousingTypeRadioButton(housingTypes[2])
                    }
                    Row{
                        HousingTypeRadioButton(housingTypes[3])
                        HousingTypeRadioButton(housingTypes[4])
                    }
                }
            }
            item {
                TextField(
                    value = viewModel.price.toString(),
                    onValueChange = { viewModel.price = it.toIntOrNull() ?: 0 },
                    label = { Text("Giá phòng (VND)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = viewModel.size.toString(),
                    onValueChange = {  viewModel.size = it.toIntOrNull() ?: 0  },
                    label = { Text("Diện tích (m²)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )
            }
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Cơ sở vật chất")
                    facilityOptions.forEach { option ->
                        FacilityCheckbox(option)
                    }
                }
                BedCounter()
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                TextField(
                    value = viewModel.description,
                    onValueChange = { viewModel.description = it },
                    label = { Text("Mô tả") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item { Spacer(modifier = Modifier.height(50.dp)) }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .align(Alignment.BottomCenter)
                .background(Color.White)
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onBackClicked) {
                Text("QUAY LẠI")
            }
            Button(onClick = onContinueClicked) {
                Text("TIẾP TỤC")
            }
        }
    }
}

@Composable
fun ImageUploadScreen(viewModel: ArticleViewModel, onBackClicked: () -> Unit, onPostClicked: () -> Unit) {
    val isLoading = viewModel.isLoading
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.imageUri = it
        } ?: run {
            Toast.makeText(context, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            viewModel.imageUri?.let {
                Image(
                    painter = rememberAsyncImagePainter(model = it),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            } ?: run {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color.Gray)
                ) {
                    Text("Không có hình ảnh", modifier = Modifier.align(Alignment.Center))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    launcher.launch("image/*")
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Chọn ảnh")
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Button(onClick = onBackClicked) {
                Text("QUAY LẠI")
            }
            Button(onClick = {
                if (!isLoading) onPostClicked()
            },
                enabled = viewModel.isDataValid
            ) {
                Text("ĐĂNG BÀI")
            }
        }
    }
}
