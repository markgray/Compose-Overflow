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

package com.example.jetcaster.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * This object defines the typography used throughout the Jetcaster application.
 *
 * It uses the [Montserrat] font family for all text styles and provides a
 * comprehensive set of text styles that conform to Material Design 3
 * typography guidelines. These styles include display, headline, title, label, and body variations
 * in large, medium, and small sizes. Each style specifies font size, weight, line height,
 * and letter spacing, allowing for consistent and visually appealing text rendering across the app.
 *
 *  The defined typography options are:
 *  - **displayLarge**: For the largest text elements, such as prominent titles.
 *  - **displayMedium**: For slightly smaller, but still significant, display text.
 *  - **displaySmall**: For smaller display text.
 *  - **headlineLarge**: For large headlines.
 *  - **headlineMedium**: For medium headlines.
 *  - **headlineSmall**: For small headlines.
 *  - **titleLarge**: For large titles.
 *  - **titleMedium**: For medium titles.
 *  - **titleSmall**: For small titles.
 *  - **labelLarge**: For large labels, often used for buttons and other interactive elements.
 *  - **labelMedium**: For medium labels.
 *  - **labelSmall**: For small labels.
 *  - **bodyLarge**: For large body text.
 *  - **bodyMedium**: For medium body text.
 *  - **bodySmall**: For small body text.
 *
 * @see [Typography.displayLarge]
 * @see [Typography.displayMedium]
 * @see [Typography.displaySmall]
 * @see [Typography.headlineLarge]
 * @see [Typography.headlineMedium]
 * @see [Typography.headlineSmall]
 * @see [Typography.titleLarge]
 * @see [Typography.titleMedium]
 * @see [Typography.titleSmall]
 * @see [Typography.labelLarge]
 * @see [Typography.labelMedium]
 * @see [Typography.labelSmall]
 * @see [Typography.bodyLarge]
 * @see [Typography.bodyMedium]
 * @see [Typography.bodySmall]
 */
val JetcasterTypography: Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = Montserrat,
        fontSize = 57.sp,
        fontWeight = FontWeight.W400,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = Montserrat,
        fontSize = 45.sp,
        fontWeight = FontWeight.W400,
        lineHeight = 52.sp
    ),
    displaySmall = TextStyle(
        fontFamily = Montserrat,
        fontSize = 36.sp,
        fontWeight = FontWeight.W400,
        lineHeight = 44.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = Montserrat,
        fontSize = 32.sp,
        fontWeight = FontWeight.W500,
        lineHeight = 40.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = Montserrat,
        fontSize = 28.sp,
        fontWeight = FontWeight.W500,
        lineHeight = 36.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = Montserrat,
        fontSize = 24.sp,
        fontWeight = FontWeight.W500,
        lineHeight = 32.sp
    ),
    titleLarge = TextStyle(
        fontFamily = Montserrat,
        fontSize = 22.sp,
        fontWeight = FontWeight.W400,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontFamily = Montserrat,
        fontSize = 16.sp,
        fontWeight = FontWeight.W500,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = Montserrat,
        fontSize = 14.sp,
        fontWeight = FontWeight.W500,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelLarge = TextStyle(
        fontFamily = Montserrat,
        fontSize = 14.sp,
        fontWeight = FontWeight.W500,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = Montserrat,
        fontSize = 12.sp,
        fontWeight = FontWeight.W500,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = Montserrat,
        fontSize = 11.sp,
        fontWeight = FontWeight.W500,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = Montserrat,
        fontSize = 16.sp,
        fontWeight = FontWeight.W500,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Montserrat,
        fontSize = 14.sp,
        fontWeight = FontWeight.W500,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = Montserrat,
        fontSize = 12.sp,
        fontWeight = FontWeight.W500,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
)
