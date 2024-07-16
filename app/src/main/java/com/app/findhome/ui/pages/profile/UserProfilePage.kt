package com.app.findhome.ui.pages.profile

import android.content.Intent
import android.graphics.Color.parseColor
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.app.findhome.R
import com.app.findhome.ui.components.PropertyItem
import com.app.findhome.ui.navigation.AppPage
import com.app.findhome.ui.pages.details.DetailViewModel
import com.app.findhome.ui.pages.favorite.FavoriteViewModel
import com.app.findhome.ui.pages.home.HomeViewModel

@Composable
fun UserProfilePage(
    userId: String,
    navController: NavController,
    favoriteViewModel: FavoriteViewModel,
    detailViewModel: DetailViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val userName by detailViewModel.userName.observeAsState()
    val profilePictureUrl by detailViewModel.profilePictureUrl.observeAsState()
    val role by detailViewModel.role.observeAsState()
    LaunchedEffect(userId) { detailViewModel.fetchUserName(userId) }
    Item(userName,profilePictureUrl,role,navController, favoriteViewModel, homeViewModel.getPropertyByUserId(userId))
}

@Composable
fun Item(
    userName: String?,
    profilePictureUrl: String?,
    role: String?,
    navController: NavController,
    favoriteViewModel: FavoriteViewModel,
    propertiesByUserId: List<com.app.findhome.data.model.PropertyDomain>
) {
    val phoneNumber = "+84394281107"
    val context = LocalContext.current
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
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
                    painterResource(id = R.drawable.arc_3),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(topImg) {
                            bottom.linkTo(parent.bottom)
                        }
                )
                AsyncImage(
                    model = profilePictureUrl ?: R.drawable.avatar,
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
                    contentScale = ContentScale.Crop
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
                    painterResource(id = R.drawable.back),
                    contentDescription = null,
                    modifier = Modifier
                        .clickable { navController.popBackStack() }
                        .constrainAs(back) {
                            top.linkTo(parent.top, margin = 24.dp)
                            start.linkTo(parent.start, margin = 24.dp)
                        })
            }
        }
        item {
            Text(
                text = userName ?: "",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp),
                color = Color(android.graphics.Color.parseColor("#32357a"))
            )
        }
        item {
            Text(
                text = role ?: "",
                fontSize = 18.sp,
                color = Color(parseColor("#747679"))
            )
        }
        item {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp), horizontalArrangement = Arrangement.Center)
            {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
                            ContextCompat.startActivity(context, intent, null)
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
                ){
                    Image(
                        painter = painterResource(id = R.drawable.call),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(4.dp)
                    )
                    Spacer(modifier = Modifier.width(9.dp))
                    Text(
                        text = "Điện thoại",
                        modifier = Modifier,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(parseColor("#747679"))
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            val intent =
                                Intent(Intent.ACTION_VIEW, Uri.parse("sms:$phoneNumber"))
                            ContextCompat.startActivity(context, intent, null)
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
                    Image(
                        painter = painterResource(id = R.drawable.message),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(4.dp)

                    )
                    Spacer(modifier = Modifier.width(9.dp))
                    Text(
                        text = "Nhắn tin",
                        modifier = Modifier,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(parseColor("#747679"))
                    )
                }

            }
        }
        item {
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp), contentAlignment = Alignment.Center)
            {
                Text(
                    text = "Tin đã đăng (${propertiesByUserId.size})",
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 16.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(parseColor("#32357a"))
                )
            }
        }
        items(propertiesByUserId) { item ->
            PropertyItem(
                property = item,
                favoriteViewModel = favoriteViewModel,
                itemClick = {},
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
