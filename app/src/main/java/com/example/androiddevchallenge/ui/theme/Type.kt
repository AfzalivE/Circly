/*
 * Copyright 2021 The Android Open Source Project
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
package com.example.androiddevchallenge.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.androiddevchallenge.R

val exoFontFamily = FontFamily(
    Font(
        resId = R.font.exo_regular,
        weight = FontWeight.Normal
    ),
    Font(
        resId = R.font.exo_medium,
        weight = FontWeight.Medium
    )
)

// Set of Material typography styles to start with
val typography = Typography(
    defaultFontFamily = exoFontFamily,
    h2 = TextStyle(
        fontFamily = exoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 72.sp,
        letterSpacing = (-0.5).sp

    ),
    h6 = TextStyle(
        fontFamily = exoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        letterSpacing = 0.15.sp
    ),
    body1 = TextStyle(
        fontFamily = exoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    )
        /* Other default text styles to override
    button = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
    */
)
