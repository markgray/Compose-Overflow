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

package com.example.jetsnack.ui.utils

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

/**
 * Converts a long to a price string. We call [NumberFormat.getCurrencyInstance] to get a currency
 * format for the current `Category.FORMAT` of the default [Locale]. Then we call its
 * [NumberFormat.format] method for the [BigDecimal] of our [Long] parameter [price], and call
 * its [BigDecimal.movePointLeft] to move the decimal point `2` places to the left, returing the
 * [String] produced by [NumberFormat.format] to the caller.
 *
 * @param price the price in cents.
 * @return the formatted price.
 */
fun formatPrice(price: Long): String {
    return NumberFormat.getCurrencyInstance().format(
        BigDecimal(price).movePointLeft(2)
    )
}
