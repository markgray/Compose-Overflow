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

package com.example.jetsnack.ui.home.cart

import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.jetsnack.R
import com.example.jetsnack.model.OrderLine
import com.example.jetsnack.model.SnackRepo
import com.example.jetsnack.model.SnackbarManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.jetsnack.model.Snack

/**
 * Holds the contents of the cart and allows changes to it.
 *
 * TODO: Move data to Repository so it can be displayed and changed consistently throughout the app.
 *
 * @param snackbarManager The singleton [SnackbarManager] of the app, responsible for managing
 * Snackbar messages to show on the screen.
 * @param snackRepository the singleton [SnackRepo] fake repo of the app.
 */
class CartViewModel(
    private val snackbarManager: SnackbarManager,
    snackRepository: SnackRepo
) : ViewModel() {

    /**
     * This private [MutableStateFlow] wrapped [List] of [OrderLine] is read using our publicly
     * accessible property [orderLines]. The list of fake data is initialized from the
     * [SnackRepo.getCart] property when our [CartViewModel] is first constructed and modified using
     * our [decreaseSnackCount] method, our [increaseSnackCount] method, our [removeSnack] method
     * and our [updateSnackCount] method.
     */
    private val _orderLines: MutableStateFlow<List<OrderLine>> =
        MutableStateFlow(value = snackRepository.getCart())

    /**
     * Publicly accessible read-only access to our [_orderLines] property. The stateful [Cart]
     * Composable override uses the [StateFlow.collectAsStateWithLifecycle] extension method to
     * initialize its [State] wrapped `orderLines` variable which it then passes to the stateless
     * [Cart] Composable override as its `orderLines` argument.
     */
    val orderLines: StateFlow<List<OrderLine>> get() = _orderLines

    /**
     * Counter used by our [shouldRandomlyFail] method to fake a failure every fifth request.
     */
    private var requestCount = 0

    /**
     * Logic to show errors every fifth request, `true` if [requestCount] is a multiple of 5.
     */
    private fun shouldRandomlyFail(): Boolean = ++requestCount % 5 == 0

    /**
     * Adds 1 to the [OrderLine.count] of the [OrderLine] whose [OrderLine.snack] has a [Snack.id]
     * that is equal to its [Long] parameter [snackId]. If our [shouldRandomlyFail] method returns
     * `false` we set [Int] variable `val currentCount` by fetching the [MutableStateFlow.value] of
     * our [MutableStateFlow] wrapped [List] of [OrderLine], then using the [List.first] method of
     * that [List] to fetch the [OrderLine] whose [OrderLine.snack] has a [Snack.id] that is equal
     * to our [Int] parameter [snackId] then set `currentCount` to the [OrderLine.count] of that
     * [OrderLine]. We then call our [updateSnackCount] method with its `snackId` argument our [Int]
     * parameter [snackId] and its `count` argument our `currentCount` variable plus 1 to have it
     * store the updated [OrderLine.count] in the [OrderLine] whose [OrderLine.snack] has a [Snack.id]
     * equal to [snackId]. If [shouldRandomlyFail] returns `true` however we call the
     * [SnackbarManager.showMessage] method of our [SnackbarManager] field [snackbarManager] with its
     * `messageTextId` argument the resource ID `R.string.cart_increase_error` ("There was an error
     * and the quantity couldn't be increased. Please try again.").
     *
     * @param snackId the [Snack.id] of the [OrderLine.snack] of the [OrderLine] whose
     * [OrderLine.count] we wish to add 1 to.
     */
    fun increaseSnackCount(snackId: Long) {
        if (!shouldRandomlyFail()) {
            val currentCount: Int = _orderLines.value.first { it.snack.id == snackId }.count
            updateSnackCount(snackId = snackId, count = currentCount + 1)
        } else {
            snackbarManager.showMessage(messageTextId = R.string.cart_increase_error)
        }
    }

    /**
     * Subtracts 1 from the [OrderLine.count] of the [OrderLine] whose [OrderLine.snack] has a
     * [Snack.id] that is equal to its [Long] parameter [snackId]. If our [shouldRandomlyFail] method
     * returns `false` we set [Int] variable `val currentCount` by fetching the [MutableStateFlow.value]
     * of our [MutableStateFlow] wrapped [List] of [OrderLine], then using the [List.first] method of
     * that [List] to fetch the [OrderLine] whose [OrderLine.snack] has a [Snack.id] that is equal
     * to our [Int] parameter [snackId] then set `currentCount` to the [OrderLine.count] of that
     * [OrderLine]. If `currentCount` is equal to 1 we call our [removeSnack] method with its
     * ``snackId` argument our [Int] parameter [snackId] to have it remove the [OrderLine] from our
     * [MutableStateFlow] wrapped [List] of [OrderLine] field [_orderLines]. Otherwise we call our
     * [updateSnackCount] method with its `snackId` argument our [Int] parameter [snackId] and its
     * `count` argument our `currentCount` variable minus 1 to have it store the updated
     * [OrderLine.count] in the [OrderLine] whose [OrderLine.snack] has a [Snack.id] equal to
     * [snackId]. If [shouldRandomlyFail] returns `true` however we call the [SnackbarManager.showMessage]
     * method of our [SnackbarManager] field [snackbarManager] with its `messageTextId` argument the
     * resource ID `R.string.cart_decrease_error` ("There was an error and the quantity couldn't be
     * decreased. Please try again.")
     *
     * @param snackId the [Snack.id] of the [OrderLine.snack] of the [OrderLine] whose
     * [OrderLine.count] we wish to subtract 1 from.
     */
    fun decreaseSnackCount(snackId: Long) {
        if (!shouldRandomlyFail()) {
            val currentCount = _orderLines.value.first { it.snack.id == snackId }.count
            if (currentCount == 1) {
                // remove snack from cart
                removeSnack(snackId = snackId)
            } else {
                // update quantity in cart
                updateSnackCount(snackId = snackId, count = currentCount - 1)
            }
        } else {
            snackbarManager.showMessage(R.string.cart_decrease_error)
        }
    }

    /**
     * This method removes the [OrderLine] whose [OrderLine.snack] has a [Snack.id] equal to its
     * [Long] parameter [snackId]. We use the [Iterable.filter] extension function of the
     * [MutableStateFlow] wrapped [List] of [OrderLine] field [_orderLines] to create a [List] of
     * [OrderLine] from that [List] whose members have an [OrderLine.snack] whose [Snack.id] is not
     * equal to our [Long] parameter [snackId] and asign the resulting [List] to the
     * [MutableStateFlow.value] of [_orderLines].
     *
     * @param snackId the [Snack.id] of the [OrderLine.snack] of the [OrderLine] we wish to remove
     * from our [MutableStateFlow] wrapped [List] of [OrderLine] field [_orderLines].
     */
    fun removeSnack(snackId: Long) {
        _orderLines.value = _orderLines.value.filter { it.snack.id != snackId }
    }

    /**
     * This method updates the [OrderLine.count] of the [OrderLine] whose [OrderLine.snack] has a
     * [Snack.id] equal to our [Long] parameter [snackId] to our [Int] parameter [count]. We use the
     * [Iterable.map] extension function of the [MutableStateFlow] wrapped [List] of [OrderLine]
     * field [_orderLines] to create a [List] of [OrderLine] from that [List] in which the
     * [OrderLine] whose [OrderLine.snack] has a [Snack.id] equal to our [Long] parameter [snackId]
     * is a copy of the of the original [OrderLine] with its [OrderLine.count] set to our [Int]
     * parameter [count] and all other [OrderLine]'s included unmodified and asign the resulting
     * [List] to the [MutableStateFlow.value] of [_orderLines].
     *
     * @param snackId the [Snack.id] of the [OrderLine.snack] of the [OrderLine] whose [OrderLine.count]
     * we should set to our [Int] parameter [count].
     * @param count the new value of [OrderLine.count] to use to update the [OrderLine] whose
     * [OrderLine.snack] has a [Snack.id] equal to our [Long] parameter [snackId].
     */
    private fun updateSnackCount(snackId: Long, count: Int) {
        _orderLines.value = _orderLines.value.map {
            if (it.snack.id == snackId) {
                it.copy(count = count)
            } else {
                it
            }
        }
    }

    /**
     * Factory for [CartViewModel] that takes [SnackbarManager] and [SnackRepo] as dependencies.
     */
    companion object {
        /**
         * The [ViewModelProvider.Factory] for our [CartViewModel].
         *
         * @param snackbarManager the app's singleton [SnackbarManager].
         * @param snackRepository the app's singleton [SnackRepo].
         */
        fun provideFactory(
            snackbarManager: SnackbarManager = SnackbarManager,
            snackRepository: SnackRepo = SnackRepo
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CartViewModel(snackbarManager, snackRepository) as T
            }
        }
    }
}
