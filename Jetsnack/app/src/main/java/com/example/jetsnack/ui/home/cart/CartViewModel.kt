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
     *
     */
    fun increaseSnackCount(snackId: Long) {
        if (!shouldRandomlyFail()) {
            val currentCount = _orderLines.value.first { it.snack.id == snackId }.count
            updateSnackCount(snackId, currentCount + 1)
        } else {
            snackbarManager.showMessage(R.string.cart_increase_error)
        }
    }

    /**
     *
     */
    fun decreaseSnackCount(snackId: Long) {
        if (!shouldRandomlyFail()) {
            val currentCount = _orderLines.value.first { it.snack.id == snackId }.count
            if (currentCount == 1) {
                // remove snack from cart
                removeSnack(snackId)
            } else {
                // update quantity in cart
                updateSnackCount(snackId, currentCount - 1)
            }
        } else {
            snackbarManager.showMessage(R.string.cart_decrease_error)
        }
    }

    /**
     *
     */
    fun removeSnack(snackId: Long) {
        _orderLines.value = _orderLines.value.filter { it.snack.id != snackId }
    }

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
     * Factory for CartViewModel that takes SnackbarManager as a dependency
     */
    companion object {
        /**
         *
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
