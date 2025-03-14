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

package com.example.jetcaster.util

import androidx.window.layout.FoldingFeature
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Checks if the device is in a "table-top" posture based on the provided [FoldingFeature].
 *
 * A device is considered to be in "table-top" posture if the following conditions are met:
 *  - A [FoldingFeature] is present (not null).
 *  - The [FoldingFeature]'s state is [FoldingFeature.State.HALF_OPENED].
 *  - The [FoldingFeature]'s orientation is [FoldingFeature.Orientation.HORIZONTAL].
 *
 * This posture typically corresponds to a foldable device that is partially open, resting
 * horizontally on a flat surface, like a table.
 *
 * @param foldFeature The [FoldingFeature] representing the fold state of the device.
 * @return `true` if the device is in table-top posture, `false` otherwise.
 *         Returns `false` if [foldFeature] is null or if the feature's state or
 *         orientation don't match the table-top criteria.
 */
@OptIn(ExperimentalContracts::class)
fun isTableTopPosture(foldFeature: FoldingFeature?): Boolean {
    contract { returns(true) implies (foldFeature != null) }
    return foldFeature?.state == FoldingFeature.State.HALF_OPENED &&
        foldFeature.orientation == FoldingFeature.Orientation.HORIZONTAL
}

/**
 * Checks if a given [FoldingFeature] represents a "book posture".
 *
 * A "book posture" is defined as a [FoldingFeature] that is:
 * 1. **Half-opened:** The device is in a state where the hinge is partially open.
 * 2. **Vertically oriented:** The hinge or fold runs vertically across the device.
 *
 * This function utilizes Kotlin contracts to provide compile-time guarantees. If the function
 * returns `true`, it guarantees that the provided [foldFeature] is not null.
 *
 * @param foldFeature The [FoldingFeature] to check. Can be null.
 * @return `true` if the [foldFeature] represents a book posture (half-opened and vertically
 *         oriented), `false` otherwise.
 */
@OptIn(ExperimentalContracts::class)
fun isBookPosture(foldFeature: FoldingFeature?): Boolean {
    contract { returns(true) implies (foldFeature != null) }
    return foldFeature?.state == FoldingFeature.State.HALF_OPENED &&
        foldFeature.orientation == FoldingFeature.Orientation.VERTICAL
}

/**
 * Checks if a given [FoldingFeature] represents a separating posture.
 *
 * A separating posture is defined as a [FoldingFeature] that is in the [FoldingFeature.State.FLAT]
 * state and is also separating (i.e., [FoldingFeature.isSeparating] is true).
 *
 * This function utilizes a contract to ensure that if the function returns true,
 * then the provided [foldFeature] is guaranteed to be non-null. This allows
 * for safe access to the properties of [foldFeature] after a true result without
 * additional null checks.
 *
 * @param foldFeature The [FoldingFeature] to check for separating posture. Can be null.
 * @return `true` if the [foldFeature] represents a separating posture (FLAT and separating),
 * `false` otherwise. Returns `false` if [foldFeature] is `null`.
 * @see FoldingFeature.State.FLAT
 * @see FoldingFeature.isSeparating
 */
@OptIn(ExperimentalContracts::class)
fun isSeparatingPosture(foldFeature: FoldingFeature?): Boolean {
    contract { returns(true) implies (foldFeature != null) }
    return foldFeature?.state == FoldingFeature.State.FLAT && foldFeature.isSeparating
}
