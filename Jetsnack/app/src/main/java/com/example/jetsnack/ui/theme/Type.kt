/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jetsnack.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.jetsnack.R

/**
 * This [FontFamily] is used for the [Typography.displayLarge], [Typography.displayMedium],
 * [Typography.displaySmall], [Typography.headlineMedium], [Typography.headlineSmall],
 * [Typography.titleLarge], [Typography.titleMedium], [Typography.titleSmall],
 * [Typography.labelLarge], and [Typography.labelSmall]  as the `fontFamily` of their [TextStyle].
 */
private val Montserrat = FontFamily(
    Font(R.font.montserrat_light, FontWeight.Light),
    Font(R.font.montserrat_regular, FontWeight.Normal),
    Font(R.font.montserrat_medium, FontWeight.Medium),
    Font(R.font.montserrat_semibold, FontWeight.SemiBold)
)

/**
 * This [FontFamily] is used for the [Typography.titleSmall] [Typography.bodyLarge], and
 * [Typography.bodySmall] as the `fontFamily` of their [TextStyle].
 */
private val Karla = FontFamily(
    Font(R.font.karla_regular, FontWeight.Normal),
    Font(R.font.karla_bold, FontWeight.Bold)
)

/**
 * This is the [Typography] that our [JetsnackTheme] custom [MaterialTheme] uses.
 */
val Typography: Typography = Typography(
    /**
     * [Typography.displayLarge] is the largest display text.
     */
    displayLarge = TextStyle(
        fontFamily = Montserrat,
        fontSize = 96.sp,
        fontWeight = FontWeight.Light,
        lineHeight = 117.sp,
        letterSpacing = (-1.5).sp
    ),
    /**
     * [Typography.displayMedium] is the second largest display text.
     */
    displayMedium = TextStyle(
        fontFamily = Montserrat,
        fontSize = 60.sp,
        fontWeight = FontWeight.Light,
        lineHeight = 73.sp,
        letterSpacing = (-0.5).sp
    ),
    /**
     * [Typography.displaySmall] is the smallest display text.
     */
    displaySmall = TextStyle(
        fontFamily = Montserrat,
        fontSize = 48.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 59.sp
    ),
    /**
     * [Typography.headlineMedium] is the second largest headline, reserved for short, important
     * text or numerals. For headlines, you can choose an expressive font, such as a display,
     * handwritten, or script style. These unconventional font designs have details and intricacy
     * that help attract the eye.
     */
    headlineMedium = TextStyle(
        fontFamily = Montserrat,
        fontSize = 30.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 37.sp
    ),
    /**
     * [Typography.headlineSmall] is the smallest headline, reserved for short, important text or
     * numerals. For headlines, you can choose an expressive font, such as a display, handwritten,
     * or script style. These unconventional font designs have details and intricacy that help
     * attract the eye.
     */
    headlineSmall = TextStyle(
        fontFamily = Montserrat,
        fontSize = 24.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 29.sp
    ),
    /**
     * [Typography.titleLarge] is the largest title, and is typically reserved for medium-emphasis
     * text that is shorter in length. Serif or sans serif typefaces work well for subtitles.
     */
    titleLarge = TextStyle(
        fontFamily = Montserrat,
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 24.sp
    ),
    /**
     * [Typography.titleMedium] is the second largest title, and is typically reserved for
     * medium-emphasis text that is shorter in length. Serif or sans serif typefaces work well
     * for subtitles.
     */
    titleMedium = TextStyle(
        fontFamily = Montserrat,
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    /**
     * [Typography.titleSmall] is the smallest title, and is typically reserved for medium-emphasis
     * text that is shorter in length. Serif or sans serif typefaces work well for subtitles.
     */
    titleSmall = TextStyle(
        fontFamily = Karla,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp
    ),
    /**
     * [Typography.bodyLarge] is the largest body text, and is typically reserved for long-form
     * writing as it works well for small text sizes. For longer sections of text, a serif or sans
     * serif typeface is recommended.
     */
    bodyLarge = TextStyle(
        fontFamily = Karla,
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 28.sp,
        letterSpacing = 0.15.sp
    ),
    /**
     * [Typography.bodyMedium] is the second largest body text, and is typically used for long-form
     * writing as it works well for small text sizes. For longer sections of text, a serif or sans
     * serif typeface is recommended.
     */
    bodyMedium = TextStyle(
        fontFamily = Montserrat,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    /**
     * [Typography.labelLarge] is text is a call to action used in different types of buttons (such
     * as text, outlined and contained buttons) and in tabs, dialogs, and cards. Button text is
     * typically sans serif, using all caps text.
     */
    labelLarge = TextStyle(
        fontFamily = Montserrat,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 16.sp,
        letterSpacing = 1.25.sp
    ),
    /**
     * [Typography.bodySmall] is the smallest body, and is typically used for long-form writing as
     * it works well for small text sizes. For longer sections of text, a serif or sans serif
     * typeface is recommended.
     */
    bodySmall = TextStyle(
        fontFamily = Karla,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    /**
     * [Typography.labelSmall] is one of the smallest font sizes. It is used sparingly to annotate
     * imagery or to introduce a headline.
     */
    labelSmall = TextStyle(
        fontFamily = Montserrat,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 16.sp,
        letterSpacing = 1.sp
    )
)
