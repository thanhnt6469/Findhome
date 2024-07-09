package com.app.findhome.ui.components

import android.content.Intent
import android.graphics.Color.parseColor
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Scale
import coil.size.Size
import com.app.findhome.R
import com.app.findhome.ui.navigation.AppPage
import com.app.findhome.ui.pages.details.DetailViewModel
import com.app.findhome.ui.pages.favorite.FavoriteViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.NumberFormat
import java.util.Locale

@Preview
@Composable
fun Prev(){
    val itemList = com.app.findhome.data.model.PropertyDomain(1,"Villa", "Royal Villa", "Nam Ky Khoi Nghia, Ngu Hanh Son", "house_3", 999, 2, 400, 4.7, "This 2 bed /1 bath home boasts an enormous,open-living plan, accented by striking architectural features and high-end finishes. Feel inspired by open sight lines that embrace the outdoors, crowned by stunning offered ceilings.", "", mapOf("wifi" to true, "garage" to true))
    val favoriteViewModel = remember { FavoriteViewModel() }
    val navController = rememberNavController()
    DetailItem(property = itemList, favoriteViewModel = favoriteViewModel, navController)
}

@Composable
fun DetailItem(property: com.app.findhome.data.model.PropertyDomain, favoriteViewModel: FavoriteViewModel, navController: NavController) {
    var isFavorite by remember { mutableStateOf(favoriteViewModel.isPropertyFavorite(property.id.toString())) }
    val detailViewModel: DetailViewModel = hiltViewModel()
    val userName by detailViewModel.userName.observeAsState()
    val profilePictureUrl by detailViewModel.profilePictureUrl.observeAsState()
    Log.d("PROPERTY ID", property.userId)
    LaunchedEffect(property.userId) {
        detailViewModel.fetchUserName(property.userId)
    }

    Box(modifier = Modifier
        .fillMaxWidth()
        .background(Color.White)) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(420.dp)
                ) {
                    Image(
                        painter = loadImageFromURL(property.pickPath),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Image(
                        painter = painterResource(id = R.drawable.back1),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = 16.dp, top = 48.dp)
                            .align(Alignment.TopStart)
                            .clickable {
                                navController.popBackStack()
                            }
                    )
                    IconButton(
                        onClick = {
                            isFavorite = !isFavorite
                            if (isFavorite) {
                                favoriteViewModel.addPropertyToFavorites(property.id.toString())
                            } else {
                                favoriteViewModel.removePropertyFromFavorites(property.id.toString())
                            }
                        },
                        modifier = Modifier
                            .padding(end = 16.dp, top = 48.dp)
                            .align(Alignment.TopEnd)
                            .background(Color.White, RoundedCornerShape(40.dp))
                    ) {
                        val icon: Painter = if (isFavorite) {
                            painterResource(id = R.drawable.ic_fav)
                        } else {
                            painterResource(id = R.drawable.ic_not_fav)
                        }

                        Icon(
                            painter = icon,
                            contentDescription = null,
                            tint = Color(parseColor("#FF6856"))
                        )
                    }
                }
            }
            item {
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    val (typeTxt, scoreTxt, starImg) = createRefs()

                    Text(
                        text = property.type,
                        color = Color(parseColor("#39ACA6")),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(
                                color = Color(parseColor("#ECF4FB")),
                                RoundedCornerShape(40.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 3.dp)
                            .constrainAs(typeTxt) {
                                top.linkTo(parent.top)
                                start.linkTo(parent.start)
                                bottom.linkTo(parent.bottom)
                            }
                    )
                    Text(
                        text = property.score.toString(),
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.constrainAs(scoreTxt) {
                            top.linkTo(parent.top)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                        }
                    )
                    Image(
                        painter = painterResource(id = R.drawable.star),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .constrainAs(starImg) {
                                top.linkTo(parent.top)
                                end.linkTo(scoreTxt.start)
                                bottom.linkTo(parent.bottom)
                            }
                    )
                }
            }
            item {
                Text(
                    text = "${formatPrice(property.price)}đ/tháng",
                    color = Color.Black,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp)
                )
            }
            item {
                Text(
                    text = property.title,
                    color = Color.Black,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, top = 8.dp, end = 24.dp)
                )
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, top = 16.dp, end = 24.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.location),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = 3.dp, top = 2.dp),
                        colorFilter = ColorFilter.tint(Color(parseColor("#39ACA6")))
                    )

                    Text(
                        text = property.address,
                        color = Color.Black,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 4.dp, bottom = 8.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, top = 16.dp, end = 24.dp)
                ) {
                    Text(
                        text = "Dịch vụ và nội thất",
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 6.dp)
                    )
                    Row(
                        modifier = Modifier
                            .background(
                                color = Color(parseColor("#ECF4FB")),
                                shape = RoundedCornerShape(40.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .height(23.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.cube),
                            contentDescription = null,
                            modifier = Modifier
                                .size(24.dp)
                                .padding(end = 8.dp)
                        )
                        Text(
                            text = "Ask for virtual tour",
                            color = Color(parseColor("#39ACA6")),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, top = 16.dp, end = 24.dp)
                ) {

                    if (property.facilities["wifi"] == true) {
                        FacilityItem(
                            iconRes = R.drawable.wifi,
                            text = "Wifi"
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    FacilityItem(
                        iconRes = R.drawable.user,
                        text = "${property.member} người"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    if (property.facilities["garage"] == true) {
                        FacilityItem(
                            iconRes = R.drawable.garage,
                            text = "Nhà xe"
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                    FacilityItem(
                        iconRes = R.drawable.size,
                        text = "${property.size} m²"
                    )
                }
            }
            item {
                Text(
                    text = "Mô tả",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, top = 16.dp)
                )
            }
            item {
                Text(
                    text = property.description,
                    color = Color.Black,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, top = 16.dp, end = 24.dp)
                )
            }
            item {
                Text(
                    text = "Người đăng",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, top = 16.dp)
                )
            }
            item {
                OwnerInformation(property.userId ,name = userName ?: "Khách", avt = profilePictureUrl ?: R.drawable.avatar, navController)
            }
        }
        StickyBottomBar(property.userId, nameGuest = userName ?: "", modifier = Modifier.align(Alignment.BottomCenter), navController)
    }
}

fun formatPrice(amount: Int): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
    return formatter.format(amount).replace("₫","").trim()
}

@Composable
fun FacilityItem(iconRes: Int, text: String) {
    Column(
        modifier = Modifier
            .background(
                color = Color.White,
                shape = RoundedCornerShape(10.dp)
            )
            .border(1.dp, Color(parseColor("#E6E6E6")), shape = RoundedCornerShape(10.dp))
            .padding(8.dp)
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier
                .height(22.dp)
                .align(Alignment.Start)
                .padding(top = 8.dp)
        )
        Text(
            text = text,
            color = Color.Black,
            fontSize = 12.sp,
            modifier = Modifier
                .width(67.dp)
                .padding(top = 8.dp)
        )
    }
}

@Composable
fun OwnerInformation(userId: String, name: String, avt: Comparable<*>, navController: NavController) {
    val phoneNumber = "+84394281107"
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, top = 16.dp, end = 24.dp)
            .background(color = Color.White)
            .border(1.dp, Color(parseColor("#E6E6E6")), shape = RoundedCornerShape(10.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = avt,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(50.dp))
        )
        Text(
            text = name,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f)
        )
        Box(modifier = Modifier
            .clip(RoundedCornerShape(40.dp))
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:$phoneNumber"))
                startActivity(context, intent, null)
            }
        ) {
            Image(
                painter = painterResource(id = R.drawable.message),
                contentDescription = null,
                modifier = Modifier
                    .padding(4.dp)
            )
        }
        Box(Modifier
            .clip(RoundedCornerShape(40.dp))
            .clickable {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
            startActivity(context, intent, null)
            }
        ){
            Image(
                painter = painterResource(id = R.drawable.call),
                contentDescription = null,
                modifier = Modifier
                    .padding(4.dp)
            )
        }
        Spacer(modifier = Modifier.width(3.dp))
        Box(modifier = Modifier
            .clip(RoundedCornerShape(40.dp))
            .background(
                color = Color(parseColor("#ECF4FB")),
                shape = RoundedCornerShape(40.dp)
            )


            .clickable {
                navController.navigate("${AppPage.UserProfilePage.route}/${userId}")
            }
        ) {
            Text(
                text = "Xem hồ sơ",
                color = Color(parseColor("#39ACA6")),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center).padding(horizontal = 14.dp, vertical = 8.dp)
            )
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun StickyBottomBar(userId: String, nameGuest: String, modifier: Modifier = Modifier, navController: NavController) {
    val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(Color(0xFFfbfbfb))
            .padding(3.dp)
    ) {
        val (messageBtn, addBtn) = createRefs()

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(40.dp))
                .size(60.dp)
                .background(
                    color = Color(parseColor("#ECF4FB")),
                    shape = RoundedCornerShape(40.dp)
                )
                .constrainAs(messageBtn) {
                    start.linkTo(parent.start, 24.dp)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
                .clickable(userId != currentUserUid) {
                    navController.navigate("${AppPage.ChatPage.route}/${userId}/${nameGuest}")
                }
        ) {
            Image(
                painter = painterResource(id = R.drawable.message2),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.Center)
            )
        }
        Button(
            onClick = {
                  if (userId != currentUserUid) {
                      navController.navigate("${AppPage.ChatPage.route}/${userId}/${nameGuest}")
                  }
            },
            colors = ButtonDefaults
                .buttonColors(containerColor = Color(parseColor("#39ACA6"))),
            shape = RoundedCornerShape(50.dp),
            modifier = Modifier
                .fillMaxHeight(0.7f)
                .padding(horizontal = 8.dp)
                .constrainAs(addBtn) {
                    start.linkTo(messageBtn.end, 24.dp)
                    end.linkTo(parent.end, 24.dp)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.calendar),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .padding(end = 30.dp)
//                    .clickable(userId != currentUserUid) {
//                        navController.navigate("${AppPage.ChatPage.route}/${userId}/${nameGuest}")
//                    }
            )
            Text(
                text = "Đặt cuộc hẹn ngayㅤ",
                color = Color.White,
                fontSize = 17.sp
            )
        }
    }
}

@Composable
fun loadImageFromURL(imageUrl: String): AsyncImagePainter {
    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .apply {
                size(Size.ORIGINAL)
                scale(Scale.FILL)
                memoryCachePolicy(CachePolicy.ENABLED)
                diskCachePolicy(CachePolicy.ENABLED)
            }
            .build()
    )
    return painter
}
