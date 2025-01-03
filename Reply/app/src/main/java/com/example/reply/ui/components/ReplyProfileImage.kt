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

package com.example.reply.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

/**
 * This is just a convenience function for composing an [Image]. Our root Composable is an [Image]
 * whose [Modifier] `modifier` argument chains to our [Modifier] parameter [modifier] a
 * [Modifier.size] with a `size` of `40.dp`, followed by a [Modifier.clip] with its [Shape] `shape`
 * argument a [CircleShape]. Its [Painter] `painter` argument is a [painterResource] with its `id`
 * argument the jpg whose resource ID is our [Int] parameter [drawableResource], and its [String]
 * `contentDescription` argument is our [String] parameter [description].
 *
 * @param drawableResource the resource ID of the drawable to be used for the [Image]
 * @param description the content description of the [Image]
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Note that two of our callers pass in a [Modifier.size] so the [Modifier.size] that
 * we chain to it is ignored for those two.
 */
@Composable
fun ReplyProfileImage(
    drawableResource: Int,
    description: String,
    modifier: Modifier = Modifier
) {
    Image(
        modifier = modifier
            .size(size = 40.dp)
            .clip(shape = CircleShape),
        painter = painterResource(id = drawableResource),
        contentDescription = description,
    )
}
