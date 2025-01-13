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

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.StarBorder
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.reply.data.Account
import com.example.reply.data.Email
import com.example.reply.ui.ReplyEmailList

/**
 * This composable displays a single [Email]. It is used by the [ReplyEmailList] composable. Our root
 * Composable is a [Card] whose [Modifier] `modifier` argument chains to our [Modifier] parameter
 * [modifier] a [Modifier.padding] that adds `16.dp` padding to the `horizontal` sides and `4.dp`
 * to the `vertical` sides, followed by a [Modifier.semantics] that sets
 * [SemanticsPropertyReceiver.selected] to our [Boolean] parameter [isSelected], followed by a
 * [Modifier.clip] that clips the [Card] to the [Shape] `shape` [CardDefaults.shape], followed by a
 * [Modifier.combinedClickable] whose `onClick` argument is a lambda that calls our [navigateToDetail]
 * lambda parameter with the [Email.id] of our [Email] parameter [email], and whose `onLongClick`
 * argument is a lambda that calls our [toggleSelection] lambda parameter with the [Email.id] of our
 * [Email] parameter [email], and at the end of the chain is yet another [Modifier.clip] that clips
 * the [Card] to the [Shape] `shape` [CardDefaults.shape] (for some reason). The [CardColors] `colors`
 * argument is a [CardDefaults.cardColors] with the [CardColors.containerColor] overridden to be one
 * of [ColorScheme.primaryContainer] if our [Boolean] parameter [isSelected] is `true`, or
 * [ColorScheme.secondaryContainer] if our [Boolean] parameter [isOpened] is `true`, or else if neither
 * are `true` [ColorScheme.surfaceVariant].
 *
 * In the [ColumnScope] `content` Composable lambda argument of the [Card] we have a [Column] whose
 * [Modifier] `modifier` argument is a [Modifier.fillMaxWidth], followed by a [Modifier.padding] that
 * adds `20.dp` padding to all sides. In the [ColumnScope] `content` Composable lambda argument of the
 * [Column] we have:
 *  - a [Row] whose [Modifier] `modifier` argument is a [Modifier.fillMaxWidth] to have it take up
 *  its entire incoming width constraint. In the [RowScope] `content` Composable lambda argument of
 *  the [Row] we first initialize our [Modifier] variable `val clickModifier` to a [Modifier.clickable]
 *  whose `interactionSource` argument is a [remember] of a [MutableInteractionSource], and whose
 *  `indication` argument is `null`, and the `onClick` argument is a lambda that calls our lambda
 *  parameter [toggleSelection] with the [Email.id] of our [Email] parameter [email]. Next we compose
 *  an [AnimatedContent] whose `targetState` is our [Boolean] parameter [isSelected], and in its
 *  [AnimatedContentScope] `content` Composable lambda argument we accept the [Boolean] parameter
 *  passed the lambda in our variable `selected` and if `selected` is `true` we compose a
 *  [SelectedProfileImage] whose `modifier` argument is our [Modifier] variable `clickModifier`, and
 *  it `selected` is `false` we compose a [ReplyProfileImage] whose `drawableResource` argument is
 *  the [Account.avatar] of the [Email.sender] of [email], whose `contentDescription` argument is
 *  the [Account.fullName] of the [Email.sender] of [email], and whose `modifier` argument is our
 *  [Modifier] variable `clickModifier`. Next in the [Row] is a [Column] whose `modifier` argument
 *  is a [RowScope.weight] of `weight` `1f` causing it to take up all remaining space after its
 *  unweighted siblings are measured and placed, chained to a [Modifier.padding] that adds `12.dp`
 *  to each `horizontal` side and `4.dp` to each `vertical` side, and the `verticalArrangement`
 *  argument of the [Column] is [Arrangement.Center]. In the [ColumnScope] `content` Composable lambda
 *  argument of the [Column] we compose a [Text] whose `text` argument is the [Account.firstName] of
 *  the [Email.sender] of [email], and whose [TextStyle] `style` argument is the
 *  [Typography.labelMedium] of our custom [MaterialTheme.typography]. This is followed by another
 *  [Text] whose `text` argument is the [Email.createdAt] of [email], and whose [TextStyle] `style`
 *  argument is the [Typography.labelMedium] of our custom [MaterialTheme.typography]. Next in the
 *  [Row] is an [IconButton] whose `onCick` argument is a do-nothing lambda, and whose [Modifier]
 *  `modifier` argument is a [Modifier.clip] whose [Shape] `shape` argument is [CircleShape], chained
 *  to a [Modifier.background] whose [Color] `color` argument is the [ColorScheme.surfaceContainerHigh]
 *  of our custom [MaterialTheme.colorScheme]. In the [IconButton] `content` Composable lambda argument
 *  we compose an [Icon] whose [ImageVector] `imageVector` argument is the [ImageVector] drawn by
 *  [Icons.Filled.StarBorder],  whose `contentDescription` argument is "Favorite", and whose [Color]
 *  `tint` argument is the [ColorScheme.outline] of our custom [MaterialTheme.colorScheme].
 *  - Next in the [Column] is a [Text] whose `text` argument is the [Email.subject] of [email], whose
 *  [TextStyle] `style` argument is the [Typography.bodyLarge] of our custom [MaterialTheme.typography],
 *  and whose [Modifier] `modifier` argument is a [Modifier.padding] that adds `12.dp` to the `top`,
 *  and `8.dp` to the `bottom`.
 *  - This is followed by another [Text] whose `text` argument is the [Email.body] of [email], whose
 *  [TextStyle] `style` argument is the [Typography.bodyMedium] of our custom [MaterialTheme.typography],
 *  whose `maxLines` argument is `2`, and whose [TextOverflow] `overflow` argument is
 *  [TextOverflow.Ellipsis]
 *
 * @param email The [Email] to display.
 * @param navigateToDetail The function to call with the [Email.id] when the [email] is clicked.
 * @param toggleSelection The function to call with the [Email.id] when the [email] is long clicked.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller does not pass us one so the empty, default, or starter [Modifier] that
 * contains no elements is used.
 * @param isOpened if `true` this [Email] is the [Email] that is currently being displayed on a dual
 * pane device (it is meaningless on a single pane device).
 * @param isSelected if `true` this [Email] is selected.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReplyEmailListItem(
    email: Email,
    navigateToDetail: (Long) -> Unit,
    toggleSelection: (Long) -> Unit,
    modifier: Modifier = Modifier,
    isOpened: Boolean = false,
    isSelected: Boolean = false,
) {
    Card(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .semantics { selected = isSelected }
            .clip(shape = CardDefaults.shape)
            .combinedClickable(
                onClick = { navigateToDetail(email.id) },
                onLongClick = { toggleSelection(email.id) }
            )
            .clip(shape = CardDefaults.shape),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
            else if (isOpened) MaterialTheme.colorScheme.secondaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 20.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                val clickModifier: Modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { toggleSelection(email.id) }
                AnimatedContent(targetState = isSelected, label = "avatar") { selected: Boolean ->
                    if (selected) {
                        SelectedProfileImage(modifier = clickModifier)
                    } else {
                        ReplyProfileImage(
                            drawableResource = email.sender.avatar,
                            description = email.sender.fullName,
                            modifier = clickModifier
                        )
                    }
                }

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
                        text = email.createdAt,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
                IconButton(
                    onClick = { /*TODO*/ },
                    modifier = Modifier
                        .clip(shape = CircleShape)
                        .background(color = MaterialTheme.colorScheme.surfaceContainerHigh)
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
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp),
            )
            Text(
                text = email.body,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * This Composable is displayed by [ReplyEmailListItem] instead of [ReplyProfileImage] if the [Email]
 * is the currently selected [Email] and consists of just a "check mark". Our root Composable is a
 * [Box] whose [Modifier] `modifier` argument chains to our [Modifier] parameter [modifier] a
 * [Modifier.size] that sets our `size` to `40.dp`, followed by a [Modifier.clip] that clips us to
 * the [Shape] `shape` [CircleShape], with a [Modifier.background] at the end of the chain whose
 * [Color] `color` argument is the [ColorScheme.primary] of our custom [MaterialTheme.colorScheme].
 * In the [BoxScope] `content` composable lambda argument of the [Box] we compose an [Icon] whose
 * [ImageVector] `imageVector` argument is [Icons.Filled.Check], whose `contentDescription` argument
 * is `null`, whose [Modifier] `modifier` argument is a [Modifier.size] that sets its `size` to
 * `24.dp`, chained to a [BoxScope.align] that aligns its `alignment` to [Alignment.Center], and
 * whose [Color] `tint` argument is the [ColorScheme.onPrimary] of our custom
 * [MaterialTheme.colorScheme].
 *
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller passes us a [Modifier.clickable] that calls its `toggleSelection` lambda
 * parameter which traces back to a lambda which toggles the selection state of the [Email] it is
 * rendering (thus causing [ReplyEmailListItem] to display the [ReplyProfileImage] instead of us).
 */
@Composable
fun SelectedProfileImage(modifier: Modifier = Modifier) {
    Box(
        modifier
            .size(size = 40.dp)
            .clip(shape = CircleShape)
            .background(color = MaterialTheme.colorScheme.primary)
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            modifier = Modifier
                .size(size = 24.dp)
                .align(alignment = Alignment.Center),
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}
