package com.app.findhome.ui.theme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


val md_theme_light_primary = Color(0xFF2a51d5)
val md_theme_light_onPrimary = Color(0xFFffffff)
val md_theme_light_primaryContainer = Color(0xFFdce1ff)
val md_theme_light_onPrimaryContainer = Color(0xFF001257)
val md_theme_light_secondary = Color(0xFF2a6b29)
val md_theme_light_onSecondary = Color(0xFFffffff)
val md_theme_light_secondaryContainer = Color(0xFFadf4a1)
val md_theme_light_onSecondaryContainer = Color(0xFF002201)
val md_theme_light_tertiary = Color(0xFF7D5260)
val md_theme_light_onTertiary = Color(0xFFFFFFFF)
val md_theme_light_tertiaryContainer = Color(0xFFFFD8E4)
val md_theme_light_onTertiaryContainer = Color(0xFF31111D)
val md_theme_light_error = Color(0xFFB3261E)
val md_theme_light_errorContainer = Color(0xFFF9DEDC)
val md_theme_light_onError = Color(0xFFFFFFFF)
val md_theme_light_onErrorContainer = Color(0xFF410E0B)
val md_theme_light_background = Color(0xFFfcfcfc)
val md_theme_light_onBackground = Color(0xFF1d1b1e)
val md_theme_light_surface = Color(0xFFfcfcfc)
val md_theme_light_onSurface = Color(0xFF1d1b1e)
val md_theme_light_surfaceVariant = Color(0xFFE7E0EC)
val md_theme_light_onSurfaceVariant = Color(0xFF49454F)
val md_theme_light_outline = Color(0xFF79747E)
val md_theme_light_inverseOnSurface = Color(0xFFf6eff3)
val md_theme_light_inverseSurface = Color(0xFF322f33)
val md_theme_light_inversePrimary = Color(0xFFb7c4ff)
val md_theme_light_shadow = Color(0xFF000000)

val md_theme_dark_primary = Color(0xFFb7c4ff)
val md_theme_dark_onPrimary = Color(0xFF00238a)
val md_theme_dark_primaryContainer = Color(0xFF0036bd)
val md_theme_dark_onPrimaryContainer = Color(0xFFdce1ff)
val md_theme_dark_secondary = Color(0xFF91d787)
val md_theme_dark_onSecondary = Color(0xFF003a03)
val md_theme_dark_secondaryContainer = Color(0xFF0c5212)
val md_theme_dark_onSecondaryContainer = Color(0xFFadf4a1)
val md_theme_dark_tertiary = Color(0xFFEFB8C8)
val md_theme_dark_onTertiary = Color(0xFF492532)
val md_theme_dark_tertiaryContainer = Color(0xFF633B48)
val md_theme_dark_onTertiaryContainer = Color(0xFFFFD8E4)
val md_theme_dark_error = Color(0xFFF2B8B5)
val md_theme_dark_errorContainer = Color(0xFF8C1D18)
val md_theme_dark_onError = Color(0xFF601410)
val md_theme_dark_onErrorContainer = Color(0xFFF9DEDC)
val md_theme_dark_background = Color(0xFF1d1b1e)
val md_theme_dark_onBackground = Color(0xFFe7e0e5)
val md_theme_dark_surface = Color(0xFF1d1b1e)
val md_theme_dark_onSurface = Color(0xFFe7e0e5)
val md_theme_dark_surfaceVariant = Color(0xFF49454F)
val md_theme_dark_onSurfaceVariant = Color(0xFFCAC4D0)
val md_theme_dark_outline = Color(0xFF938F99)
val md_theme_dark_inverseOnSurface = Color(0xFF1d1b1e)
val md_theme_dark_inverseSurface = Color(0xFFe7e0e5)
val md_theme_dark_inversePrimary = Color(0xFF2a51d5)
val md_theme_dark_shadow = Color(0xFF000000)


val md_theme_image_overlay = Color(0x5E000000)

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val seed = Color(0xFF6750A4)
val error = Color(0xFFB3261E)

val Black = Color(0xFF000113)
val LightBlueWhite = Color(0xFFF1F5F9) //Social media background
val BlueGray = Color(0xFF334155)

val ColorScheme.focusedTextFieldText
    @Composable
    get() = if (isSystemInDarkTheme()) Color.White else Color.Black

val ColorScheme.unfocusedTextFieldText
    @Composable
    get() = if (isSystemInDarkTheme()) Color(0xFF94A3B8) else Color(0xFF475569)

val ColorScheme.textFieldContainer
    @Composable
    get() = if (isSystemInDarkTheme()) BlueGray.copy(alpha = 0.6f) else LightBlueWhite