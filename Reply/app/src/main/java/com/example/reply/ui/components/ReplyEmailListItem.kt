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
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.reply.data.Email
import com.example.reply.ui.ReplyEmailList

/**
 * This composable displays a single [Email]. It is used by the [ReplyEmailList] composable. Our root
 * Composable is a [Card] whose [Modifier] `modifier` argument chains to our [Modifier] parameter
 * [modifier] a [Modifier.padding] that adds `16.dp` padding to the `horizontal` sides and `4.dp`
 * to the `vertical` sides, followed by a [Modifier.semantics] that sets [SemanticsPropertyReceiver.selected]
 * to our [Boolean] parameter [isSelected], followed by a [Modifier.clip] that clips the [Card] to
 * the [Shape] `shape` [CardDefaults.shape], followed by a [Modifier.combinedClickable] whose `onClick`
 * argument is a lambda that calls our [navigateToDetail] function with the [Email.id] of our [Email]
 * parameter [email], and whose `onLongClick` argument is a lambda that calls our [toggleSelection]
 * with the [Email.id] of our [Email] parameter [email], and at the end of the chain is yet another
 * [Modifier.clip] that clips the [Card] to the [Shape] `shape` [CardDefaults.shape] (for some reason).
 * The [CardColors] `colors` argument is a [CardDefaults.cardColors] with the [CardColors.containerColor]
 * overridden to be one of [ColorScheme.primaryContainer] if our [Boolean] parameter [isSelected] is
 * `true`, or [ColorScheme.secondaryContainer] if our [Boolean] parameter [isOpened] is `true`, or
 * else if neither are `true` [ColorScheme.surfaceVariant].
 *
 * In the [ColumnScope] `content` Composable lambda argument of the [Card] we have a [Column] whose
 * [Modifier] `modifier` argument is a [Modifier.fillMaxWidth], followed by a [Modifier.padding] that
 * adds `20.dp` padding to all sides. In the [ColumnScope] `content` Composable lambda argument of the
 * [Column] we have:
 *  - a [Row] whose [Modifier] `modifier` argument is a [Modifier.fillMaxWidth].
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
                val clickModifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { toggleSelection(email.id) }
                AnimatedContent(targetState = isSelected, label = "avatar") { selected ->
                    if (selected) {
                        SelectedProfileImage(clickModifier)
                    } else {
                        ReplyProfileImage(
                            email.sender.avatar,
                            email.sender.fullName,
                            clickModifier
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
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
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
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
 *
 */
@Composable
fun SelectedProfileImage(modifier: Modifier = Modifier) {
    Box(
        modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Icon(
            Icons.Default.Check,
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .align(Alignment.Center),
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}
