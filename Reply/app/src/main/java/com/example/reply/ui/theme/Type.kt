/*
 * Copyright 2022 The Android Open Source Project
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

package com.example.reply.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Material 3 typography
 */
val replyTypography: Typography = Typography(
    /**
     * `headlineLarge` is the largest headline, reserved for short, important text or numerals. For
     * headlines, you can choose an expressive font, such as a display, handwritten, or script style.
     * These unconventional font designs have details and intricacy that help attract the eye.
     *  - `fontWeight` = [FontWeight.SemiBold],
     *  - `fontSize` = 32.sp,
     *  - `lineHeight` = 40.sp,
     *  - `letterSpacing` = 0.sp
     */
    headlineLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    /**
     * headlineMedium is the second largest headline, reserved for short, important text or numerals.
     * For headlines, you can choose an expressive font, such as a display, handwritten, or script
     * style. These unconventional font designs have details and intricacy that help attract the eye.
     *  - fontWeight = FontWeight.SemiBold,
     *  - fontSize = 28.sp,
     *  - lineHeight = 36.sp,
     *  - letterSpacing = 0.sp
     */
    headlineMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    /**
     * headlineSmall is the smallest headline, reserved for short, important text or numerals. For
     * headlines, you can choose an expressive font, such as a display, handwritten, or script style.
     * These unconventional font designs have details and intricacy that help attract the eye.
     *  - fontWeight = FontWeight.SemiBold,
     *  - fontSize = 24.sp,
     *  - lineHeight = 32.sp,
     *  - letterSpacing = 0.sp
     */
    headlineSmall = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    /**
     * titleLarge is the largest title, and is typically reserved for medium-emphasis text that is
     * shorter in length. Serif or sans serif typefaces work well for subtitles.
     */
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    /**
     * titleMedium is the second largest title, and is typically reserved for medium-emphasis text
     * that is shorter in length. Serif or sans serif typefaces work well for subtitles.
     *  - fontWeight = FontWeight.SemiBold,
     *  - fontSize = 16.sp,
     *  - lineHeight = 24.sp,
     *  - letterSpacing = 0.15.sp
     */
    titleMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    /**
     * titleSmall is the smallest title, and is typically reserved for medium-emphasis text that is
     * shorter in length. Serif or sans serif typefaces work well for subtitles.
     *  - fontWeight = FontWeight.Bold,
     *  - fontSize = 14.sp,
     *  - lineHeight = 20.sp,
     *  - letterSpacing = 0.1.sp
     */
    titleSmall = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    /**
     * bodyLarge is the largest body, and is typically used for long-form writing as it works well
     * for small text sizes. For longer sections of text, a serif or sans serif typeface is recommended.
     *  - fontWeight = FontWeight.Normal,
     *  - fontSize = 16.sp,
     *  - lineHeight = 24.sp,
     *  - letterSpacing = 0.15.sp
     */
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    /**
     * bodyMedium is the second largest body, and is typically used for long-form writing as it
     * works well for small text sizes. For longer sections of text, a serif or sans serif typeface
     * is recommended.
     *  - fontWeight = FontWeight.Medium,
     *  - fontSize = 14.sp,
     *  - lineHeight = 20.sp,
     *  - letterSpacing = 0.25.sp
     */
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    /**
     * bodySmall is the smallest body, and is typically used for long-form writing as it works well
     * for small text sizes. For longer sections of text, a serif or sans serif typeface is recommended.
     *  - fontWeight = FontWeight.Bold,
     *  - fontSize = 12.sp,
     *  - lineHeight = 16.sp,
     *  - letterSpacing = 0.4.sp
     */
    bodySmall = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    /**
     * labelLarge text is a call to action used in different types of buttons (such as text, outlined
     * and contained buttons) and in tabs, dialogs, and cards. Button text is typically sans serif,
     * using all caps text.
     *  - fontWeight = FontWeight.SemiBold,
     *  - fontSize = 14.sp,
     *  - lineHeight = 20.sp,
     *  - letterSpacing = 0.1.sp
     */
    labelLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    /**
     * labelMedium is one of the smallest font sizes. It is used sparingly to annotate imagery or to
     * introduce a headline.
     *  - fontWeight = FontWeight.SemiBold,
     *  - fontSize = 12.sp,
     *  - lineHeight = 16.sp,
     *  - letterSpacing = 0.5.sp
     */
    labelMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    /**
     * labelSmall is one of the smallest font sizes. It is used sparingly to annotate imagery or to
     * introduce a headline.
     *  - fontWeight = FontWeight.SemiBold,
     *  - fontSize = 11.sp,
     *  - lineHeight = 16.sp,
     *  - letterSpacing = 0.5.sp
     */
    labelSmall = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)
