package com.app.findhome.ui.components

import android.annotation.SuppressLint
import android.graphics.Color.parseColor
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Scale
import com.app.findhome.R
import com.app.findhome.ui.pages.favorite.FavoriteViewModel
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale

//@Preview
//@Composable
//fun Prev(){
//val itemList = PropertyDomain("Villa", "Royal Villa", "LosAngles La", "house_3", 999, 2, 1, 400, true, 4.7, "This 2 bed /1 bath home boasts an enormous,open-living plan, accented by striking architectural features and high-end finishes. Feel inspired by open sight lines that embrace the outdoors, crowned by stunning coffered ceilings. ")
//    PropertyItem(itemList, {})
//}

@SuppressLint("DiscouragedApi")
@Composable
fun PropertyItem(
    property: com.app.findhome.data.model.PropertyDomain,
    favoriteViewModel: FavoriteViewModel,
    itemClick: () -> Unit,
    detailClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFavorite by remember { mutableStateOf(favoriteViewModel.isPropertyFavorite(property.id.toString())) }

    Surface(
        modifier = modifier.clickable(onClick = detailClick),
        color = Color.White,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Color(0xFFD8D8D8)),
    ){
        ConstraintLayout(
            modifier = Modifier
//                .width(284.dp)
                //.height(244.dp)
                .padding(8.dp)
        ) {
            val (pic, favoriteIcon, typeTxt, titleTxt, scoreTxt, starIcon, locationIcon, addressTxt, bedIcon, bedTxt, bathIcon, bathTxt, priceTxt) = createRefs()

            Image(
                painter = loadImageFromUrl(property.pickPath),
                contentDescription = "Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(136.dp)
                    .constrainAs(pic) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)

                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    }
                    .clip(RoundedCornerShape(8.dp))

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
                    .size(60.dp)
                    .constrainAs(favoriteIcon) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                    }
                    .padding(16.dp)
                    .background(Color.White, RoundedCornerShape(40.dp))
                    .padding(0.dp)
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

            Text(
                text = property.type,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(parseColor("#39ACA6")),
                modifier = Modifier
                    .background(Color(parseColor("#ECF4FB")), RoundedCornerShape(40.dp))
                    //.background(painterResource(id = R.drawable.light_blue_bg))
                    .padding(start = 8.dp, end = 8.dp)
                    .constrainAs(typeTxt) {
                        top.linkTo(parent.top, margin = 10.dp)
                        start.linkTo(parent.start, margin = 10.dp)
                    }
                    .padding(5.dp)
            )

            Text(
                text = property.title,
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .constrainAs(titleTxt) {
                        top.linkTo(pic.bottom, margin = 8.dp)
                        start.linkTo(pic.start)
                        end.linkTo(starIcon.start)
                        width = Dimension.fillToConstraints
                    }
            )

            Text(
                text = property.score.toString(),
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier
                    .constrainAs(scoreTxt) {
                        top.linkTo(titleTxt.top)
                        bottom.linkTo(titleTxt.bottom)
                        end.linkTo(parent.end, margin = 8.dp)
                    }
            )

            Image(
                painter = painterResource(id = R.drawable.star),
                contentDescription = null,
                modifier = Modifier
                    .constrainAs(starIcon) {
                        top.linkTo(scoreTxt.top)
                        bottom.linkTo(scoreTxt.bottom)
                        end.linkTo(scoreTxt.start, margin = 8.dp)
                    }
            )

            Image(
                painter = painterResource(id = R.drawable.location),
                contentDescription = null,
                modifier = Modifier
                    .constrainAs(locationIcon) {
                        top.linkTo(titleTxt.bottom, margin = 8.dp)
                        start.linkTo(titleTxt.start)
                    }
            )

            Text(
                text = property.address,
                color = Color.Gray,
                modifier = Modifier
                    .constrainAs(addressTxt) {
                        top.linkTo(locationIcon.top)
                        bottom.linkTo(locationIcon.bottom)
                        start.linkTo(locationIcon.end, margin = 8.dp)
                        end.linkTo(parent.end, margin = 8.dp)
                        width = Dimension.fillToConstraints
                    },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Image(
                painter = painterResource(id = R.drawable.user),
                colorFilter = ColorFilter.tint(Color.Black),
                contentDescription = null,
                modifier = Modifier
                    .size(19.dp)
                    .constrainAs(bedIcon) {
                        top.linkTo(locationIcon.bottom, margin = 8.dp)
                        start.linkTo(parent.start)
                    }
            )

            Text(
                text = "${property.member}",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier
                    .constrainAs(bedTxt) {
                        top.linkTo(bedIcon.top)
                        bottom.linkTo(bedIcon.bottom)
                        start.linkTo(bedIcon.end, margin = 8.dp)
                    }
            )

            Image(
                painter = painterResource(id = R.drawable.size),
                colorFilter = ColorFilter.tint(Color.Black),
                contentDescription = null,
                modifier = Modifier
                    .size(19.dp)
                    .constrainAs(bathIcon) {
                        top.linkTo(bedTxt.top)
                        bottom.linkTo(bedTxt.bottom)
                        start.linkTo(bedTxt.end, margin = 8.dp)
                    }
            )

            Text(
                text = "${property.size}m²",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier
                    .constrainAs(bathTxt) {
                        top.linkTo(bathIcon.top)
                        bottom.linkTo(bathIcon.bottom)
                        start.linkTo(bathIcon.end, margin = 8.dp)
                    }
            )

            Text(
                text = "${formatCurrency(property.price)}/tháng",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier
                    .constrainAs(priceTxt) {
                        top.linkTo(bathIcon.top)
                        bottom.linkTo(bathIcon.bottom)
                        end.linkTo(parent.end, margin = 8.dp)
                    }
            )
        }
    }
}

fun formatCurrency(amount: Int): String {
    val million = 1_000_000
    val thousand = 1_000
    val bigAmount = amount.toBigDecimal()

    return when {
        bigAmount >= million.toBigDecimal() -> {
            val formattedValue = bigAmount.divide(million.toBigDecimal(), 1, RoundingMode.HALF_UP)
            "${formattedValue.stripTrailingZeros().toPlainString()} triệu"
        }
        bigAmount >= thousand.toBigDecimal() -> {
            val formattedValue = bigAmount.divide(thousand.toBigDecimal(), 1, RoundingMode.HALF_UP)
            "${formattedValue.stripTrailingZeros().toPlainString()} nghìn"
        }
        else -> "$amount đ"
    }
}


@Composable
fun loadImageFromUrl(imageUrl: String): AsyncImagePainter {
    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .apply {
                size(coil.size.Size.ORIGINAL)
                scale(Scale.FILL)
                memoryCachePolicy(CachePolicy.ENABLED)
                diskCachePolicy(CachePolicy.ENABLED)
            }
            .build()
    )
    return painter
}
