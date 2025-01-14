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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.reply.R
import com.example.reply.data.Account
import com.example.reply.data.Email
import com.example.reply.ui.ReplyEmailDetail

/**
 * This Composable is used by [ReplyEmailDetail] to display each of the [Email] in the [List] of
 * [Email] property [Email.threads] of the [Email] it is displaying. Our root Composable is a [Card]
 * whose [Modifier] `modifier` argument chains to our [Modifier] parameter [modifier] a
 * [Modifier.padding] that adds `16.dp` padding to each `horizontal` side and `4.dp` padding to each
 * `vertical` side, and whose [CardColors] `colors` argument is a [CardDefaults.cardColors] with
 * the [CardColors.containerColor] overridden to be [ColorScheme.surfaceContainerHigh] of our custom
 * [MaterialTheme.colorScheme] (A surface variant for containers with higher emphasis than
 * `surfaceContainer`). In the [ColumnScope] `content` Composable lambda argument of the [Card] we
 * compose a [Column] whose [Modifier] `modifier` argument is a [Modifier.fillMaxWidth] with a
 * [Modifier.padding] that adds `20.dp` to `all` sides chained to that. In the [ColumnScope] `content`
 * Composable lambda argument of the [Column] we compose:
 *  - a [Row] whose [Modifier] `modifier` argument is a [Modifier.fillMaxWidth] to have it take up the
 *  entire incoming width constraint. In the [RowScope] `content` Composable lambda argument of the
 *  [Row] we compose:
 *  1. a [ReplyProfileImage] whose [Int] `drawableResource` argument is the [Account.avatar] of the
 *  [Email.sender] of our [Email] parameter [email], and whose [String] `description` argument is
 *  the [Account.fullName] of the [Email.sender] of our [Email] parameter [email].
 *  2. a [Column] whose [Modifier] `modifier` argument is a [RowScope.weight] with a `weight` of `1f`,
 *  with a [Modifier.padding] chained to that which adds `12.dp` padding to each `horizontal` side
 *  and `4.dp` padding to each `vertical` side, and whose `verticalArrangement` argument is
 *  [Arrangement.Center] In the [ColumnScope] `content` Composable lambda of the [Column] we have
 *  two [Text], the `text` argument of the first is the [Account.firstName] of the [Email.sender]
 *  of our [Email] parameter [email], with its [TextStyle] `style` argument the [Typography.labelMedium]
 *  of our custom [MaterialTheme.typography], and the `text` argument of the second is the string
 *  "20 mins ago", with its [TextStyle] `style` argument the [Typography.labelMedium] of our custom
 *  [MaterialTheme.typography], and with its [Color] `color` argument the [ColorScheme.outline]
 *  of our custom [MaterialTheme.colorScheme].
 *  3. an [IconButton] whose `onClick` lambda argument is an empty lambda, and whose [Modifier]
 *  `modifier` argument is a [Modifier.clip] with a [Shape] `shape` argument of [CircleShape], to which
 *  is chained a [Modifier.background] with its [Color] `color` argument the
 *  [ColorScheme.surfaceContainer] of our custom [MaterialTheme.colorScheme]. In the [IconButton]
 *  `content` Composable lambda argument we compose an [Icon] whose [ImageVector] `imageVector`
 *  argument is the [ImageVector] drawn by [Icons.Filled.StarBorder], whose [String]
 *  `contentDescription` argument is the string "Favorite", and whose [Color] `tint` argument is the
 *  [ColorScheme.outline] of our custom [MaterialTheme.colorScheme].
 *  - Next in the [Column] is a [Text] whose `text` argument is the [Email.subject] of our [Email]
 *  parameter [email], whose [TextStyle] `style` argument is the [Typography.bodyMedium] of our custom
 *  [MaterialTheme.typography], whose [Color] `color` argument is the [ColorScheme.outline] of our
 *  custom [MaterialTheme.colorScheme], and whose [Modifier] `modifier` argument is a [Modifier.padding]
 *  that adds `12.dp` to the `top` and `8.dp` to the `bottom`.
 *  - Next in the [Column] is a [Text] whose `text` argument is the [Email.body] of our [Email]
 *  parameter [email], whose [TextStyle] `style` argument is the [Typography.bodyLarge] of our custom
 *  [MaterialTheme.typography], and whose [Color] `color` argument is the [ColorScheme.onSurfaceVariant]
 *  of our custom [MaterialTheme.colorScheme].
 *  - at the bottom of the [Column] is a [Row] whose [Modifier] `modifier` argument is a
 *  [Modifier.fillMaxWidth], with a [Modifier.padding] chained to that which adds `20.dp` padding to
 *  the `top` and `8.dp` padding to the `bottom`, and whose `horizontalArrangement` argument is
 *  an [Arrangement.spacedBy] whose `space` argument is `12.dp`. In the [RowScope] `content` Composable
 *  lambda argument of the [Row] we have:
 *  1. a [Button] whose `onClick` lambda argument is an empty lambda, whose [Modifier] `modifier`
 *  argument is a [RowScope.weight] whose `weight` is `1f`, and whose [ButtonColors] `colors` argument
 *  is a [ButtonDefaults.buttonColors] with its [ButtonColors.containerColor] overridden by the
 *  [ColorScheme.surfaceBright] of our custom [MaterialTheme.colorScheme]. And in the [RowScope]
 *  `content` Composable lambda argument of the [Button] we compose a [Text] whose `text` argument
 *  is the [String] with resource ID `R.string.reply` ("Reply"), and whose [Color] `color` argument
 *  is the [ColorScheme.onSurface] of our custom [MaterialTheme.colorScheme].
 *  2. a [Button] whose `onClick` lambda argument is an empty lambda, whose [Modifier] `modifier`
 *  argument is a [RowScope.weight] whose `weight` is `1f`, and whose [ButtonColors] `colors` argument
 *  is a [ButtonDefaults.buttonColors] with its [ButtonColors.containerColor] overridden by the
 *  [ColorScheme.surfaceBright] of our custom [MaterialTheme.colorScheme]. And in the [RowScope]
 *  `content` Composable lambda argument of the [Button] we compose a [Text] whose `text` argument
 *  is the [String] with resource ID `R.string.reply_all` ("Reply All"), and whose [Color] `color`
 *  argument is the [ColorScheme.onSurface] of our custom [MaterialTheme.colorScheme].
 *
 * @param email The [Email] to display.
 * @param modifier A [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller [ReplyEmailDetail] does not pass us one so the empty, default, or starter
 * [Modifier] that contains no elements is used.
 */
@Composable
fun ReplyEmailThreadItem(
    email: Email,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 20.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                ReplyProfileImage(
                    drawableResource = email.sender.avatar,
                    description = email.sender.fullName,
                )
                Column(
                    modifier = Modifier
                        .weight(weight = 1f)
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = email.sender.firstName,
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = "20 mins ago",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                IconButton(
                    onClick = { /*TODO*/ },
                    modifier = Modifier
                        .clip(shape = CircleShape)
                        .background(color = MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    Icon(
                        imageVector = Icons.Default.StarBorder,
                        contentDescription = "Favorite",
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Text(
                text = email.subject,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp),
            )

            Text(
                text = email.body,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(space = 12.dp),
            ) {
                Button(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.weight(weight = 1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceBright
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.reply),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Button(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.weight(weight = 1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceBright
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.reply_all),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
