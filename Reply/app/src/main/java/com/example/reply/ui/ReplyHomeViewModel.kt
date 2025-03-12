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

package com.example.reply.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reply.data.Email
import com.example.reply.data.EmailsRepository
import com.example.reply.data.EmailsRepositoryImpl
import com.example.reply.ui.utils.ReplyContentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * This is the [ViewModel] that is used to communicate between the UI and the business model
 * throughout the app.
 *
 * @param emailsRepository the [EmailsRepository] used to retrieve the list of emails.
 */
class ReplyHomeViewModel(private val emailsRepository: EmailsRepository = EmailsRepositoryImpl()) :
    ViewModel() {

    /**
     * UI state exposed to the UI by our [uiState] field.
     */
    private val _uiState = MutableStateFlow(ReplyHomeUIState(loading = true))

    /**
     * Public read-only access to our [_uiState] field.
     */
    val uiState: StateFlow<ReplyHomeUIState> = _uiState

    init {
        observeEmails()
    }

    /**
     * This function lauches a coroutine on the [CoroutineScope] tied to this [ViewModel] returned
     * by the [viewModelScope] extension function whose `block` calls the
     * [EmailsRepository.getAllEmails] method of our [EmailsRepository] field [emailsRepository] and
     * processes the [Flow] of [List] of [Email] that it returns by using the [catch] extension
     * function on it to catch any exceptions thrown in order to set the [MutableStateFlow.value] of
     * [_uiState] to a new instance of [ReplyHomeUIState] whose [ReplyHomeUIState.error] field
     * contains the contents of the [Throwable.message] field of the exception that was thrown (and
     * then returning). If no exception is thrown the [Flow.collect] method "collects" the [List] of
     * [Email] emitted by the [EmailsRepository.getAllEmails] method and sets the
     * [MutableStateFlow.value] of [_uiState] to a new instance of [ReplyHomeUIState] whose
     * [ReplyHomeUIState.emails] field is that [List] of [Email], and whose
     * [ReplyHomeUIState.openedEmail] field is the first [Email] in the [List] of [Email]s.
     */
    private fun observeEmails() {
        viewModelScope.launch {
            emailsRepository.getAllEmails()
                .catch { ex: Throwable ->
                    _uiState.value = ReplyHomeUIState(error = ex.message)
                }
                .collect { emails: List<Email> ->
                    /**
                     * We set first email selected by default for first App launch in large-screens
                     */
                    _uiState.value = ReplyHomeUIState(
                        emails = emails,
                        openedEmail = emails.first()
                    )
                }
        }
    }

    /**
     * This method sets the [Email] whose [Email.id] is our [Long] parameter [emailId] to be the
     * opened email. We start by using the [Iterable.find] method of the [List] of [Email]s in the
     * [ReplyHomeUIState.emails] property of the [StateFlow.value] of our field [uiState] to find the
     * [Email] whose [Email.id] matches our [Long] parameter [emailId] in order to initialize our
     * [Email] variable `val email` to that [Email]. Then we set the [MutableStateFlow.value] of our
     * field [_uiState] to a copy of itself with its [ReplyHomeUIState.openedEmail] property set to
     * [Email] variable `email` and its [ReplyHomeUIState.isDetailOnlyOpen] set to `true` if our
     * [ReplyContentType] parameter is equal to [ReplyContentType.SINGLE_PANE].
     *
     * @param emailId the [Email.id] of the [Email] to be opened.
     * @param contentType the [ReplyContentType] appropriate for the device we are running on, either
     * [ReplyContentType.SINGLE_PANE] or [ReplyContentType.DUAL_PANE].
     */
    fun setOpenedEmail(emailId: Long, contentType: ReplyContentType) {
        /**
         * We only set isDetailOnlyOpen to true when it's only single pane layout
         */
        val email: Email? = uiState.value.emails.find { it.id == emailId }
        _uiState.value = _uiState.value.copy(
            openedEmail = email,
            isDetailOnlyOpen = contentType == ReplyContentType.SINGLE_PANE
        )
    }

    /**
     * This method toggles the "Selected" status of the [Email] whose [Email.id] is our [Long] parameter
     * [emailId]. We start by initializing our [Set] of [Long] variable `val currentSelection` to the
     * [ReplyHomeUIState.selectedEmails] property of the [StateFlow.value] of our field [uiState].
     * Then we set the [StateFlow.value] of [ReplyHomeUIState] field [_uiState] to a copy of itself
     * with the [ReplyHomeUIState.selectedEmails] property set to the [Set] of [Long] variable
     * `currentSelection` minus [emailId] if it contains it, or the [Set] of [Long] variable
     * `currentSelection` plus [emailId] if it does not already contain it (thereby toggling its
     * selected status).
     *
     * @param emailId the [Email.id] of the [Email] whose selected status we are toggling.
     */
    fun toggleSelectedEmail(emailId: Long) {
        val currentSelection: Set<Long> = uiState.value.selectedEmails
        _uiState.value = _uiState.value.copy(
            selectedEmails = if (currentSelection.contains(emailId))
                currentSelection.minus(emailId) else currentSelection.plus(emailId)
        )
    }

    /**
     * This method causes the detail screen to close by setting the [MutableStateFlow.value] of
     * [ReplyHomeUIState] field [_uiState] to a copy of itself with its
     * [ReplyHomeUIState.isDetailOnlyOpen] property set to `false`, and its
     * [ReplyHomeUIState.openedEmail] to the first [Email] in its [List] of [Email]
     * property [ReplyHomeUIState.emails].
     */
    fun closeDetailScreen() {
        _uiState.value = _uiState
            .value.copy(
                isDetailOnlyOpen = false,
                openedEmail = _uiState.value.emails.first()
            )
    }
}

/**
 * This data class holds the UI state of the Reply app.
 */
data class ReplyHomeUIState(
    /**
     * The full [List] of [Email] in our [EmailsRepository].
     */
    val emails: List<Email> = emptyList(),
    /**
     * The set of [Email.id] of the [Email]s that the user has selected.
     */
    val selectedEmails: Set<Long> = emptySet(),
    /**
     * The [Email] that is currently being displayed in the detail screen.
     */
    val openedEmail: Email? = null,
    /**
     * `true` if only the detail screen is displayed.
     */
    val isDetailOnlyOpen: Boolean = false,
    /**
     * When `true` we are waiting for [EmailsRepository.getAllEmails] to finish loading.
     */
    val loading: Boolean = false,
    /**
     * If non-`null` this is the [Throwable.message] of an exception thrown by
     * [EmailsRepository.getAllEmails].
     */
    val error: String? = null
)
