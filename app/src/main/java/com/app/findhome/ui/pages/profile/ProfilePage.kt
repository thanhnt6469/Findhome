package com.app.findhome.ui.pages.profile

import android.content.Context
import android.content.Intent
import android.graphics.Color.parseColor
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.room.Delete
import coil.compose.AsyncImage
import com.app.findhome.ui.navigation.AppPage
import com.app.findhome.R
import com.app.findhome.domain.UserData
import com.app.findhome.ui.components.PropertyItem
import com.app.findhome.ui.pages.article.ArticleViewModel
import com.app.findhome.ui.pages.favorite.FavoriteViewModel
import com.app.findhome.ui.pages.home.HomeViewModel
import com.app.findhome.ui.pages.signup.SignupViewModel
import com.google.firebase.auth.FirebaseAuth
import com.shashank.sony.fancytoastlib.FancyToast

@Composable
fun ProfilePage(
    navController: NavController,
    userData: UserData?,
    favoriteViewModel: FavoriteViewModel,
    profileViewModel: ProfileViewModel,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val viewModel: SignupViewModel = viewModel()
    val logout by viewModel.logout.collectAsState()
    val context = LocalContext.current
    var roomPosted by remember { mutableStateOf(false) }
    var changeProfile by remember { mutableStateOf(false) }
    var changePassword by remember { mutableStateOf(false) }
    var about by remember { mutableStateOf(false) }
    val isAccountOptionEnabled = userData?.fullName?.isNotBlank() == true && userData.username.isNullOrBlank()
    Log.d("ProfilePage", "userData: $userData")
    LaunchedEffect(logout) {
        if (logout == true) {
            viewModel.resetLogout()
            navController.navigate(AppPage.LoginPage.route)
            FancyToast.makeText(
                context,
                "Logout successful",
                FancyToast.LENGTH_LONG,
                FancyToast.DEFAULT,
                true
            ).show()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(color = Color(parseColor("#f2f1f6"))),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            ConstraintLayout(
                modifier = Modifier
                    .height(250.dp)
                    .background(color = Color(parseColor("#32357a")))
            ) {
                val (topImg, profile, title, back, pen) = createRefs()
                Image(
                    painterResource(id = R.drawable.arc_3), contentDescription = null, modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(topImg) {
                            bottom.linkTo(parent.bottom)
                        }
                )
                AsyncImage(
                    model = userData?.profilePictureUrl ?: R.drawable.avatar,
                    contentDescription = null,
                    modifier = Modifier
                        .size(105.dp)
                        .clip(shape = CircleShape)
                        .fillMaxWidth()
                        .constrainAs(profile) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(topImg.bottom)
                        },
                    contentScale = Crop
                )
                Text(
                    text = "Hồ sơ",
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 30.sp
                    ),
                    modifier = Modifier
                        .constrainAs(title) {
                            top.linkTo(parent.top, margin = 32.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                )
                Image(
                    painterResource(id = R.drawable.back), contentDescription = null, modifier = Modifier
                        .clickable { navController.navigateUp() }
                        .constrainAs(back) {
                            top.linkTo(parent.top, margin = 24.dp)
                            start.linkTo(parent.start, margin = 24.dp)
                        })
//                Image(
//                    painterResource(id = R.drawable.write), contentDescription = null, modifier = Modifier
//                        .constrainAs(pen) {
//                            top.linkTo(profile.top)
//                            start.linkTo(profile.end)
//                        }
//                )
            }
        }

        item {
            Text(
                text = userData?.username ?: userData?.fullName ?: "",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp),
                color = Color(parseColor("#32357a"))
            )
        }
        item {
            Text(
                text = userData?.role ?: "",
                fontSize = 18.sp,
                color = Color(parseColor("#747679"))
            )
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
        item {
            ProfileOption(
                icon = Icons.Filled.Home,
                text = "Phòng đã đăng",
                showArrow = true,
                enabled = true,
                onClick = {roomPosted = true}
            )
        }

        item {
            ProfileOption(
                icon = Icons.Filled.AccountCircle,
                text = "Tài khoản",
                showArrow = isAccountOptionEnabled,
                enabled = true,
                onClick = {
                    if (!isAccountOptionEnabled){
                        FancyToast.makeText(
                            context,
                            "Cannot be modified",
                            FancyToast.LENGTH_LONG,
                            FancyToast.WARNING,
                            true
                        ).show()
                    } else {
                        changeProfile = true
                    }
                }
            )
        }

        item {
            ProfileOption(
                icon = Icons.Filled.Lock,
                text = "Đổi mật khẩu",
                onClick = {
                    if (!isAccountOptionEnabled){
                        FancyToast.makeText(
                            context,
                            "Cannot be modified",
                            FancyToast.LENGTH_LONG,
                            FancyToast.WARNING,
                            true
                        ).show()
                    } else {
                        changePassword = true
                    }
                },
                showArrow = isAccountOptionEnabled,
                enabled = true,
            )
        }

        item {
            ProfileOption(
                icon = Icons.Filled.Info,
                text = "Thông tin",
                showArrow = true,
                enabled = true,
                onClick = { about = true }
            )
        }

        item {
            ProfileOption(
                icon = Icons.Filled.ExitToApp,
                text = "Đăng xuất",
                showArrow = false,
                enabled = true,
                onClick = {
                    viewModel.logout()
                }
            )
        }
    }
    if (roomPosted) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid
        userId?.let { homeViewModel.getPropertyByUserId(it) }?.let {
            RoomPosted(navController, favoriteViewModel, it)
        }
    }
    if (changePassword) ChangePassword(navController,profileViewModel)
    if (changeProfile) ChangeProfile(navController, userData, profileViewModel)
    if (about) About(navController)
}

@Composable
fun ProfileOption(
    icon: ImageVector,
    text: String,
    showArrow: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 25.dp, end = 25.dp, top = 10.dp, bottom = 10.dp)
            .height(55.dp)
            .clip(RoundedCornerShape(25.dp))
            .clickable(enabled = enabled) {
                if (enabled) {
                    onClick()
                } else {
                    FancyToast
                        .makeText(
                            context,
                            "Cannot be modified",
                            FancyToast.LENGTH_LONG,
                            FancyToast.WARNING,
                            true
                        )
                        .show()
                }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon,
            contentDescription = null,
            modifier = Modifier
                .padding(start = 13.dp, end = 5.dp)
                .size(28.dp),
            tint = Color(parseColor("#32357a"))
        )
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            modifier = Modifier
                .padding(start = 16.dp)
                .weight(1f)
        )

        if (showArrow) {
            Image(
                painter = painterResource(id = R.drawable.arrow),
                contentDescription = null,
                modifier = Modifier.padding(end = 17.dp)
            )
        }
    }
}

@Composable
fun RoomPosted(
    navController: NavController,
    favoriteViewModel: FavoriteViewModel,
    propertiesByUserId: List<com.app.findhome.data.model.PropertyDomain>
) {
    val articleViewModel: ArticleViewModel = viewModel()
    val context = LocalContext.current

    Surface(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()) {
            item {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Bài viết đã đăng (${propertiesByUserId.size})",
                        modifier = Modifier.align(Alignment.Center),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(parseColor("#32357a"))
                    )
                    Image(
                        painterResource(id = R.drawable.back),
                        contentDescription = null,
                        modifier = Modifier
                            .clickable { navController.navigate(AppPage.ProfilePage.route) }
                            .align(Alignment.CenterStart)
                            .size(35.dp)
                    )
                }
            }
            items(propertiesByUserId) { item ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    PropertyItem(
                        property = item,
                        favoriteViewModel = favoriteViewModel,
                        itemClick = {},
                        detailClick = { navController.navigate("${AppPage.DetailPage.route}/${item.id}") },
                        modifier = Modifier
                            .fillMaxSize(1f)
                            .padding(0.dp, 5.dp)
                    )
                    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp), horizontalArrangement = Arrangement.Center)
                    {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {navController.navigate("${AppPage.EditArticlePage.route}/${item.id}")}
                                .weight(1f)
                                .border(
                                    1.dp,
                                    Color(parseColor("#E6E6E6")),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ){
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Chỉnh sửa",
                                tint = Color.Gray,
                                modifier = Modifier
                                    .padding(4.dp)
                            )
                            Spacer(modifier = Modifier.width(9.dp))
                            Text(
                                text = "Chỉnh sửa",
                                modifier = Modifier,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    articleViewModel.deleteArticle(
                                        item.id,
                                        onSuccess = {
                                            FancyToast.makeText(
                                                context,
                                                "Article delete successfully",
                                                FancyToast.LENGTH_SHORT,
                                                FancyToast.SUCCESS,
                                                true
                                            ).show()
                                            navController.navigate(AppPage.HomePage.route)
                                        },
                                        onFailure = { e ->
                                            FancyToast.makeText(
                                                context,
                                                "Error deleting article: ${e.message}",
                                                FancyToast.LENGTH_SHORT,
                                                FancyToast.ERROR,
                                                true
                                            ).show()
                                        }
                                    )
                                }
                                .weight(1f)
                                .border(
                                    1.dp,
                                    Color(parseColor("#E6E6E6")),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Xoá",
                                tint = Color.Red,
                                modifier = Modifier
                                    .padding(4.dp)
                            )
                            Spacer(modifier = Modifier.width(9.dp))
                            Text(
                                text = "Xoá",
                                modifier = Modifier,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Red
                            )
                        }

                    }
                }
            }
        }
    }
}

@Composable
fun ChangePassword(navController: NavController, profileViewModel: ProfileViewModel) {
    val context = LocalContext.current
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    )  {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Đổi mật khẩu",
                    modifier = Modifier.align(Alignment.Center),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(parseColor("#32357a"))
                )
                Image(
                    painterResource(id = R.drawable.back),
                    contentDescription = null,
                    modifier = Modifier
                        .clickable { navController.navigate(AppPage.ProfilePage.route) }
                        .align(Alignment.CenterStart)
                        .size(35.dp)
                )
            }
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center)
            {
                Column {
                    TextField(
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        label = { Text("Mật khẩu hiện tại") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("Mật khẩu mới") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            profileViewModel.reauthenticateAndChangePassword(
                                currentPassword,
                                newPassword
                            ) { success, errorMessage ->
                                handlePasswordChangeResult(success, errorMessage, context)
                            }
                            navController.navigate(AppPage.ProfilePage.route)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Thay đổi mật khẩu")
                    }
                }
            }
        }
    }
}

fun handlePasswordChangeResult(success: Boolean, errorMessage: String?, context: Context) {
    if (success) {
        FancyToast.makeText(
            context,
            "Password updated successfully",
            FancyToast.LENGTH_LONG,
            FancyToast.SUCCESS,
            true
        ).show()
    } else {
        Log.e("ProfilePage", "Failed to update password: $errorMessage")
        FancyToast.makeText(
            context,
            "Failed to update password: $errorMessage",
            FancyToast.LENGTH_LONG,
            FancyToast.ERROR,
            true
        ).show()
    }
}

@Composable
fun About(navController : NavController){
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .background(Color(parseColor("#d2e8d4")))
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painterResource(id = R.drawable.back),
                    contentDescription = null,
                    modifier = Modifier
                        .clickable { navController.navigate(AppPage.ProfilePage.route) }
                        .align(Alignment.CenterStart)
                        .size(35.dp)
                )
            }
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center)
            {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 100.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Column(
                        Modifier
                            .clip(shape = CircleShape)
                            .background(Color(parseColor("#46D0D9")))
                            .size(150.dp)
                            .wrapContentHeight()
                            .wrapContentWidth()
                    ) {
                        Image(
                            painterResource(id = R.drawable.logo),
                            contentDescription = "Findhome",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.aspectRatio(1.4f)
                        )
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.findhome),
                        contentDescription = "",
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.height(7.dp))
                    Text(
                        text = "Android Developer Extraordinaire",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(parseColor("#006d3b"))
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Column {
                        Row(Modifier.padding(bottom = 10.dp)) {
                            Image(
                                imageVector = Icons.Filled.Phone,
                                contentDescription = "",
                                Modifier.padding(end = 15.dp),
                                colorFilter = ColorFilter.tint(Color(parseColor("#006d3b")))
                            )
                            Text(text = "+84 394 281 107")
                        }
                        Row(Modifier.padding(bottom = 10.dp)) {
                            Image(
                                imageVector = Icons.Filled.Share,
                                contentDescription = "",
                                Modifier.padding(end = 15.dp),
                                colorFilter = ColorFilter.tint(Color(parseColor("#006d3b")))
                            )
                            Text(text = "@FindHome")
                        }
                        Row {
                            Image(
                                imageVector = Icons.Filled.Email,
                                contentDescription = "",
                                Modifier.padding(end = 15.dp),
                                colorFilter = ColorFilter.tint(Color(parseColor("#006d3b")))
                            )
                            Text(text = "findhome.dev@android.com")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChangeProfile(navController: NavController, userData: UserData?, profileViewModel: ProfileViewModel) {
    var lastName by remember { mutableStateOf(userData?.fullName?.split(" ")?.getOrNull(0) ?: "") }
    var firstName by remember { mutableStateOf(userData?.fullName?.split(" ")?.getOrNull(1) ?: "") }
    var profilePictureUri by remember { mutableStateOf<Uri?>(null) }
    var isProfilePictureChanged by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        profilePictureUri = uri
        isProfilePictureChanged = uri != null
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(parseColor("#f2f1f6")))
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .height(250.dp)
                    .background(color = Color(parseColor("#32357a")))
            ) {
                val (topImg, profile, title, back, pen) = createRefs()

                Image(
                    painterResource(id = R.drawable.arc_3), contentDescription = null, modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(topImg) {
                            bottom.linkTo(parent.bottom)
                        }
                )
                AsyncImage(
                    model = profilePictureUri ?: userData?.profilePictureUrl ?: R.drawable.avatar,
                    contentDescription = null,
                    modifier = Modifier
                        .size(105.dp)
                        .clip(shape = CircleShape)
                        .fillMaxWidth()
                        .clickable { launcher.launch("image/*") }
                        .constrainAs(profile) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(topImg.bottom)
                        },
                    contentScale = Crop
                )
                Text(
                    text = "Thông tin",
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 30.sp
                    ),
                    modifier = Modifier
                        .constrainAs(title) {
                            top.linkTo(parent.top, margin = 32.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                )
                Image(
                    painterResource(id = R.drawable.back),
                    contentDescription = null,
                    modifier = Modifier
                        .size(45.dp)
                        .clickable { navController.navigate(AppPage.ProfilePage.route) }
                        .constrainAs(back) {
                            top.linkTo(parent.top, margin = 24.dp)
                            start.linkTo(parent.start, margin = 24.dp)
                        }
                )
            }
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(Color(parseColor("#f2f1f6"))),
                contentAlignment = Alignment.Center
            ) {
                Column {
                    TextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        label = { Text("Tên") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        label = { Text("Họ") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            profileViewModel.updateUserProfile(
                                firstName,
                                lastName,
                                userData?.profilePictureUrl.toString(),
                                if (isProfilePictureChanged) profilePictureUri else null,
                                context
                            ) { success, errorMessage ->
                                if (success) {
                                    FancyToast.makeText(
                                        context,
                                        "Cập nhật thông tin thành công",
                                        FancyToast.LENGTH_LONG,
                                        FancyToast.SUCCESS,
                                        true
                                    ).show()
                                    navController.navigate(AppPage.ProfilePage.route)
                                } else {
                                    FancyToast.makeText(
                                        context,
                                        "Cập nhật thông tin thất bại: $errorMessage",
                                        FancyToast.LENGTH_LONG,
                                        FancyToast.ERROR,
                                        true
                                    ).show()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(Color(parseColor("#32357a")))
                    ) {
                        Text("Thay đổi thông tin")
                    }
                }
            }
        }
    }
}
