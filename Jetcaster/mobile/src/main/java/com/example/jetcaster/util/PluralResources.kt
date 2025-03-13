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

package com.example.jetcaster.util

import android.content.Context
import androidx.annotation.PluralsRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * Load a quantity format string from resources.
 *
 * @param id the resource identifier of the `plurals` format [String]
 * @param quantity The number used to get the string for the current language's plural rules.
 * @return the string format associated with resource ID [id] for the [quantity] number of items.
 */
@Suppress("unused")
@Composable
fun quantityStringResource(@PluralsRes id: Int, quantity: Int): String {
    val context: Context = LocalContext.current
    return context.resources.getQuantityString(id, quantity)
}

/**
 * Produce a [String] from the specified [formatArgs] using the `plurals` format string associated
 * with resource ID [id] and the current locale's plural rules for [quantity] number of items.
 *
 * @param id the resource identifier of the `plurals` format [String]
 * @param quantity The number used to get the string for the current language's plural rules.
 * @param formatArgs the format arguments
 * @return the [String] formatted using the format with resource ID [id] for the [quantity] number
 * of items and the format arguments in [formatArgs].
 */
@Composable
fun quantityStringResource(@PluralsRes id: Int, quantity: Int, vararg formatArgs: Any): String {
    val context: Context = LocalContext.current
    return context.resources.getQuantityString(id, quantity, *formatArgs)
}
