/*
 * Copyright 2024 The Android Open Source Project
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

package com.example.jetcaster.tv.model

import androidx.compose.runtime.Immutable
import com.example.jetcaster.core.player.model.PlayerEpisode

/**
 * Represents an immutable list of [PlayerEpisode] objects.
 *
 * This class is a data class that wraps a List<PlayerEpisode> and delegates its list operations to
 * the underlying list. It provides a type-safe way to represent a collection of player episodes and
 * ensures immutability.
 *
 * The class implements the [List] interface through delegation, meaning you can use all the
 * standard List methods (like `get`, `size`, `contains`, `iterator`, etc.) directly on an
 * `EpisodeList` instance.
 *
 * @property member The underlying immutable list of [PlayerEpisode] objects.
 * This is a read-only property.
 * @constructor Creates an [EpisodeList] with the specified [member] list.
 */
@Immutable
data class EpisodeList(val member: List<PlayerEpisode>) : List<PlayerEpisode> by member
