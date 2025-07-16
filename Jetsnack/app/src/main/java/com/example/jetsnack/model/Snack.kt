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

package com.example.jetsnack.model

import androidx.annotation.DrawableRes
import androidx.compose.material3.Text
import androidx.compose.runtime.Immutable
import com.example.jetsnack.R
import kotlin.random.Random

/**
 * This class holds the information need to display one of our [Snack].
 *
 * @param id a unique ID number, used as a `key` in several places in order to cause a re-composition
 * when it changes.
 * @param name a [String] naming this [Snack]. It is displayed in a [Text] in several places that
 * display a [Snack] as well as the [String] that is searched for in a [List] of [Snack] when the
 * user uses the search operation.
 * @param imageRes the resource ID of a jpeg of the [Snack].
 * @param price the "price" that the user will be charged for the [Snack].
 * @param tagline a [String] that is displayed below the [Snack.name] when the [Snack] is displayed.
 * Always the [String] "A tag line".
 * @param tags an unused [Set] of [String] (no idea why it is included).
 */
@Immutable
data class Snack(
    val id: Long,
    val name: String,
    @param:DrawableRes
    val imageRes: Int,
    val price: Long,
    val tagline: String = "",
    val tags: Set<String> = emptySet()
)

/*
 * Static data
 */

/**
 * Our [List] of [Snack]. It is searched by its [Snack.name] when the user uses the search operation
 * and split up using [List.subList] to populate the various [SnackCollection] that the app displays.
 */
val snacks: List<Snack> = listOf(
    Snack(
        id = 1L,
        name = "Cupcake",
        tagline = "A tag line",
        imageRes = R.drawable.cupcake,
        price = 299
    ),
    Snack(
        id = Random.nextLong(),
        name = "Donut",
        tagline = "A tag line",
        imageRes = R.drawable.donut,
        price = 299
    ),
    Snack(
        id = Random.nextLong(),
        name = "Eclair",
        tagline = "A tag line",
        imageRes = R.drawable.eclair,
        price = 299
    ),
    Snack(
        id = Random.nextLong(),
        name = "Froyo",
        tagline = "A tag line",
        imageRes = R.drawable.froyo,
        price = 299
    ),
    Snack(
        id = Random.nextLong(),
        name = "Gingerbread",
        tagline = "A tag line",
        imageRes = R.drawable.gingerbread,
        price = 499
    ),
    Snack(
        id = Random.nextLong(),
        name = "Honeycomb",
        tagline = "A tag line",
        imageRes = R.drawable.honeycomb,
        price = 299
    ),
    Snack(
        id = Random.nextLong(),
        name = "Ice Cream Sandwich",
        tagline = "A tag line",
        imageRes = R.drawable.ice_cream_sandwich,
        price = 1299
    ),
    Snack(
        id = Random.nextLong(),
        name = "Jellybean",
        tagline = "A tag line",
        imageRes = R.drawable.jelly_bean,
        price = 299
    ),
    Snack(
        id = Random.nextLong(),
        name = "KitKat",
        tagline = "A tag line",
        imageRes = R.drawable.kitkat,
        price = 549
    ),
    Snack(
        id = Random.nextLong(),
        name = "Lollipop",
        tagline = "A tag line",
        imageRes = R.drawable.lollipop,
        price = 299
    ),
    Snack(
        id = Random.nextLong(),
        name = "Marshmallow",
        tagline = "A tag line",
        imageRes = R.drawable.marshmallow,
        price = 299
    ),
    Snack(
        id = Random.nextLong(),
        name = "Nougat",
        tagline = "A tag line",
        imageRes = R.drawable.nougat,
        price = 299
    ),
    Snack(
        id = Random.nextLong(),
        name = "Oreo",
        tagline = "A tag line",
        imageRes = R.drawable.oreo,
        price = 299
    ),
    Snack(
        id = Random.nextLong(),
        name = "Pie",
        tagline = "A tag line",
        imageRes = R.drawable.pie,
        price = 299
    ),
    Snack(
        id = Random.nextLong(),
        name = "Chips",
        imageRes = R.drawable.chips,
        price = 299
    ),
    Snack(
        id = Random.nextLong(),
        name = "Pretzels",
        imageRes = R.drawable.pretzels,
        price = 299
    ),
    Snack(
        id = Random.nextLong(),
        name = "Smoothies",
        imageRes = R.drawable.smoothies,
        price = 299
    ),
    Snack(
        id = Random.nextLong(),
        name = "Popcorn",
        imageRes = R.drawable.popcorn,
        price = 299
    ),
    Snack(
        id = Random.nextLong(),
        name = "Almonds",
        imageRes = R.drawable.almonds,
        price = 299
    ),
    Snack(
        id = Random.nextLong(),
        name = "Cheese",
        imageRes = R.drawable.cheese,
        price = 299
    ),
    Snack(
        id = Random.nextLong(),
        name = "Apples",
        tagline = "A tag line",
        imageRes = R.drawable.apples,
        price = 299
    ),
    Snack(
        id = Random.nextLong(),
        name = "Apple sauce",
        tagline = "A tag line",
        imageRes = R.drawable.apple_sauce,
        price = 299
    ),
    Snack(
        id = Random.nextLong(),
        name = "Apple chips",
        tagline = "A tag line",
        imageRes = R.drawable.apple_chips,
        price = 299
    ),
    Snack(
        id = Random.nextLong(),
        name = "Apple juice",
        tagline = "A tag line",
        imageRes = R.drawable.apple_juice,
        price = 299
    ),
    Snack(
        id = Random.nextLong(),
        name = "Apple pie",
        tagline = "A tag line",
        imageRes = R.drawable.apple_pie,
        price = 299
    ),
    Snack(
        id = Random.nextLong(),
        name = "Grapes",
        tagline = "A tag line",
        imageRes = R.drawable.grapes,
        price = 299
    ),
    Snack(
        id = Random.nextLong(),
        name = "Kiwi",
        tagline = "A tag line",
        imageRes = R.drawable.kiwi,
        price = 299
    ),
    Snack(
        id = Random.nextLong(),
        name = "Mango",
        tagline = "A tag line",
        imageRes = R.drawable.mango,
        price = 299
    )
)
