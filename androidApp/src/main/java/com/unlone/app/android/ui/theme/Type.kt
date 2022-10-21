package com.unlone.app.android.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.unlone.app.android.R

@OptIn(ExperimentalTextApi::class)
val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

@OptIn(ExperimentalTextApi::class)
val NotoSansHK = GoogleFont(name = "Noto Sans Hong Kong")

@OptIn(ExperimentalTextApi::class)
val NotoSansHKFontFamily = FontFamily(
    Font(googleFont = NotoSansHK, fontProvider = provider),
    Font(googleFont = NotoSansHK, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = NotoSansHK, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = NotoSansHK, fontProvider = provider, weight = FontWeight.Bold)
)

@OptIn(ExperimentalTextApi::class)
val Montserrat = GoogleFont(name = "Montserrat")

@OptIn(ExperimentalTextApi::class)
val MontserratFontFamily = FontFamily(
    Font(googleFont = Montserrat, fontProvider = provider),
    Font(googleFont = Montserrat, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = Montserrat, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = Montserrat, fontProvider = provider, weight = FontWeight.Bold)
)

// Set of Material typography styles to start with
val Typography = Typography(
    h1 = TextStyle(
        fontFamily = NotoSansHKFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    h5 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        letterSpacing = 0.sp,
        fontFamily = NotoSansHKFontFamily,
    ),
    body1 = TextStyle(
//        color = Black95,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = 2.4.sp,
        fontFamily = NotoSansHKFontFamily,
    ),
    subtitle1 = TextStyle(
        fontFamily = MontserratFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)

val Typography.titleLarge: TextStyle
    get() = TextStyle(
//        color = Color.Black,
        fontFamily = NotoSansHKFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 29.sp,
        lineHeight = 42.sp,
        letterSpacing = 0.4.sp
    )

val Typography.titleMedium: TextStyle
    get() = TextStyle(
        fontFamily = NotoSansHKFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        lineHeight = 27.sp,
        letterSpacing = 0.1.sp
    )


val Typography.storyText: TextStyle
    get() = this.body1.copy(
        lineHeight = 32.sp,
    )