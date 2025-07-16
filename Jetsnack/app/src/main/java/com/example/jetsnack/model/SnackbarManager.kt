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

package com.example.jetsnack.model

import androidx.annotation.StringRes
import com.example.jetsnack.ui.home.cart.CartViewModel
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Data class that holds a message to be displayed by our [SnackbarManager].
 *
 * @param id a unique ID number for the [Message].
 * @param messageId the resource ID for the [String] to display in the Snackbar.
 */
data class Message(val id: Long, @param:StringRes val messageId: Int)

/**
 * Class responsible for managing Snackbar messages to show on the screen
 */
object SnackbarManager {

    /**
     * Private [MutableStateFlow] wrapped [List] of [Message], it is added to by our [showMessage]
     * method. [messages] provides public read-only access to it.
     */
    private val _messages: MutableStateFlow<List<Message>> = MutableStateFlow(emptyList())

    /**
     * Read-only access to our [MutableStateFlow] wrapped [List] of [Message] field [_messages].
     * It is collected by a coroutine launched in the `init` block of the
     * [com.example.jetsnack.ui.components.JetsnackScaffoldState] Composable, where the
     * [androidx.compose.material3.SnackbarHostState.showSnackbar] method is called to show it.
     */
    val messages: StateFlow<List<Message>> get() = _messages.asStateFlow()

    /**
     * Called by the [CartViewModel.increaseSnackCount] and [CartViewModel.decreaseSnackCount] methods
     * randomly, to show an error every few calls with the resource ID's `R.string.cart_increase_error`
     * and `R.string.cart_decrease_error` respectively ("There was an error and the quantity couldn't
     * be increased. Please try again" and "There was an error and the quantity couldn't be decreased.
     * Please try again"). We call the [MutableStateFlow.update] method of [MutableStateFlow] wrapped
     * [List] of [Message] field [_messages] and in its lambda argument we append a [Message] whose
     * [Message.id] is the most Significant Bits of a random [UUID], and whose [Message.messageId]
     * is our [Int] parameter [messageTextId].
     *
     * @param messageTextId the resource ID that should be used as the [Message.messageId] field of
     * the [Message] we append to the [MutableStateFlow] wrapped [List] of [Message] field [_messages].
     */
    fun showMessage(@StringRes messageTextId: Int) {
        _messages.update { currentMessages: List<Message> ->
            currentMessages + Message(
                id = UUID.randomUUID().mostSignificantBits,
                messageId = messageTextId
            )
        }
    }

    /**
     * Called to remove a [Message] from our [MutableStateFlow] wrapped [List] of [Message] field
     * [_messages]. It is called from the same coroutine launched in the `init` block of the
     * [com.example.jetsnack.ui.components.JetsnackScaffoldState] Composable that collected the
     * [Message] from our [StateFlow] wrapped [List] of [Message] field [messages] with its
     * [Message.id] just before it shows the [String] whose resource ID is [Message.messageId]
     * in a snackbar. We call the [MutableStateFlow.update] method of [MutableStateFlow] wrapped
     * [List] of [Message] field [_messages] and in its lambda argument we return a [List] of
     * [Message] containing all elements whose [Message.id] is not our [messageId] parameter.
     *
     * @param messageId the [Message.id] of the [Message] we are to remove from our [MutableStateFlow]
     * wrapped [List] of [Message] field [_messages].
     */
    fun setMessageShown(messageId: Long) {
        _messages.update { currentMessages: List<Message> ->
            currentMessages.filterNot { it.id == messageId }
        }
    }
}
