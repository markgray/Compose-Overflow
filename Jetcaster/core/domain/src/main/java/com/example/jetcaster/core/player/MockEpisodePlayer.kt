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

package com.example.jetcaster.core.player

import com.example.jetcaster.core.player.model.PlayerEpisode
import java.time.Duration
import kotlin.reflect.KProperty
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * A mock implementation of the [EpisodePlayer] interface for testing and development purposes.
 *
 * This class provides a simulated episode player that allows you to control playback, queue
 * management, and time manipulation without relying on a real media player.
 *
 * The [MockEpisodePlayer] uses Kotlin Coroutines and Flows to manage its state and simulate
 * time progression.
 *
 * @property mainDispatcher The coroutine dispatcher used for managing coroutines within the player.
 */
class MockEpisodePlayer(
    private val mainDispatcher: CoroutineDispatcher
) : EpisodePlayer {

    /**
     * Represents the state of the episode player.
     *
     * This could include information such as:
     * - Is the player currently playing?
     * - What is the current playback position?
     * - Is the player paused?
     * - Is the player buffering?
     * - What is the current episode being played?
     * - ...etc.
     */
    private val _playerState = MutableStateFlow(value = EpisodePlayerState())

    /**
     * The currently playing episode in the player.
     *
     * This is a [MutableStateFlow] that emits the [PlayerEpisode] representing the
     * episode that is currently being played. If no episode is currently playing,
     * the value will be `null`.
     *
     * This property is intended for internal use by the player to manage its state.
     * External components should observe this state via the public property [currentEpisode]
     *
     * Updates to this property will cause recomposition if observed in a composable context.
     */
    private val _currentEpisode = MutableStateFlow<PlayerEpisode?>(value = null)

    /**
     * The queue of episodes to be played.
     *
     * This is a [MutableStateFlow] that holds a list of [PlayerEpisode] objects.
     * Changes to this list will be emitted as new states to any collectors of this flow.
     * The initial state is an empty list.
     *
     * Use cases:
     * - Adding episodes to the playback queue.
     * - Removing episodes from the playback queue.
     * - Reordering episodes in the playback queue.
     * - Observing the current state of the playback queue.
     * - Displaying the current queue to the user.
     *
     * Thread Safety:
     * Access and modifications to the list within the flow are thread-safe as guaranteed by
     * [MutableStateFlow].
     */
    private val queue = MutableStateFlow<List<PlayerEpisode>>(value = emptyList())

    /**
     * Indicates whether the media is currently playing.
     *
     * This is a [MutableStateFlow] that emits `true` when the media playback has started
     * and is actively playing, and `false` when the playback is paused, stopped, or
     * hasn't started yet.
     */
    private val isPlaying = MutableStateFlow(value = false)

    /**
     * Represents the elapsed time since playback started.
     *
     * This property holds the current elapsed time as a [Duration] and is updated
     * over time.  It's represented as a [MutableStateFlow] to allow for reactive
     * updates and observation of the changing elapsed time.
     */
    private val timeElapsed = MutableStateFlow(value = Duration.ZERO)

    /**
     * The current playback speed of the player.
     *
     * This is a [MutableStateFlow] that holds a [Duration] representing the player's speed.
     * A playback speed of:
     *  - `1 second` (1.0x) indicates normal playback speed.
     *  - `2 seconds` (2.0x) indicates playback at double speed.
     *  - `0.5 seconds` (0.5x) indicates playback at half speed.
     *
     * Changes to this value will be reflected in the playback rate of the media.
     *
     * The initial value is [DefaultPlaybackSpeed].
     *
     * @see DefaultPlaybackSpeed
     */
    private val _playerSpeed: MutableStateFlow<Duration> =
        MutableStateFlow(value = DefaultPlaybackSpeed)

    /**
     * The [CoroutineScope] used for managing coroutines within the player.
     */
    private val coroutineScope = CoroutineScope(context = mainDispatcher)

    /**
     * The [Job] representing the currently running timer.
     *
     * When the timer is started, a new [Job] is created and assigned to this property.
     * If a timer is already running, the existing [Job] will be cancelled before
     * creating a new one.
     *
     * When the timer is stopped or completed, this property should be set to `null`
     * to indicate that no timer is currently active.
     *
     * This property should be accessed and modified from the coroutine context.
     */
    private var timerJob: Job? = null

    init {
        coroutineScope.launch {
            // Combine streams here
            combine(
                flow = _currentEpisode,
                flow2 = queue,
                flow3 = isPlaying,
                flow4 = timeElapsed,
                flow5 = _playerSpeed
            ) { currentEpisode: PlayerEpisode?,
                queue: List<PlayerEpisode>,
                isPlaying: Boolean,
                timeElapsed: Duration,
                playerSpeed: Duration ->

                EpisodePlayerState(
                    currentEpisode = currentEpisode,
                    queue = queue,
                    isPlaying = isPlaying,
                    timeElapsed = timeElapsed,
                    playbackSpeed = playerSpeed
                )
            }.catch {
                // TODO handle error state
                throw it
            }.collect {
                _playerState.value = it
            }
        }
    }

    /**
     * The current speed at which the player is playing.
     *
     * This property represents the playback speed as a [Duration].
     * A value of `Duration.seconds(1)` means normal speed.
     * Values greater than `Duration.seconds(1)` indicate faster speeds.
     * Values less than `Duration.seconds(1)` and greater than [Duration.ZERO] indicate slower speeds.
     * Values of [Duration.ZERO] or less are invalid and will likely result in undefined behavior.
     *
     * Note that this is a derived property based on the underlying [_playerSpeed] value.
     */
    override var playerSpeed: Duration = _playerSpeed.value

    /**
     * Represents the current state of the episode player.
     *
     * This property provides a read-only [StateFlow] reflecting the player's
     * current state, including loading, playing, paused, error, and completed states.
     *
     * You can collect updates from this [StateFlow] to react to changes in the player's
     * state, such as updating the UI or triggering other actions.
     *
     * Note: This is a read-only view of the internal [_playerState]. To modify the state,
     * use the appropriate methods within the player implementation.
     */
    override val playerState: StateFlow<EpisodePlayerState> = _playerState.asStateFlow()

    /**
     * The currently playing episode.
     *
     * This property represents the episode that is currently being played by the player.
     * It can be `null` if no episode is currently loaded or playing.
     *
     * Changes to this property will trigger updates to the player's state and UI.
     */
    override var currentEpisode: PlayerEpisode? by _currentEpisode

    /**
     * Adds a [PlayerEpisode] to the end of the playback queue.
     *
     * This function appends the given [episode] to the current queue of episodes.
     * The queue is managed internally and updated atomically.
     *
     * @param episode The [PlayerEpisode] to be added to the queue.
     */
    override fun addToQueue(episode: PlayerEpisode) {
        queue.update {
            it + episode
        }
    }

    /**
     * Removes all items from the queue, effectively emptying it.
     *
     * This function sets the underlying queue's value to an empty list,
     * discarding all currently enqueued items.
     */
    override fun removeAllFromQueue() {
        queue.value = emptyList()
    }

    /**
     * Starts or resumes playback of the currently selected episode.
     *
     * This function manages the playback state and updates the elapsed time.
     * It performs the following actions:
     *
     * 1. **Checks if already playing:** If [isPlaying] is true, it does nothing and returns early,
     * preventing multiple playback instances.
     * 2. **Retrieves the current episode:** It attempts to get the current episode from
     * [_currentEpisode]. If no episode is found (`null`), it returns early.
     * 3. **Sets playing state:** It sets [isPlaying] to true, indicating that playback has started.
     * 4. **Launches a coroutine:** It starts a coroutine using [coroutineScope] to handle the timer
     * and playback logic.
     * 5. **Increments the timer:** Inside the coroutine, it enters a loop that continues as long as:
     *     - The coroutine is active (`isActive`).
     *     - The `timeElapsed` is less than the episode's duration.
     *     - Within the loop a delay is set based on the `playerSpeed` using `playerSpeed.toMillis()`.
     *     - After the delay the `timeElapsed` is incremented by the `playerSpeed` amount.
     * 6. **Handles playback completion:** When the loop finishes (either the coroutine is canceled
     * or the elapsed time equals/exceeds the episode duration):
     *     - `isPlaying` is set to false, indicating playback has stopped.
     *     - `timeElapsed` is reset to `Duration.ZERO`.
     *     - It checks if there is a next episode using `hasNext()`.
     *     - If there is a next episode, `next()` is called to automatically start playing it.
     *
     * **Note:** This function relies on several other properties and functions, including:
     *  - [isPlaying]: A `MutableStateFlow<Boolean>` representing the current playback state.
     *  - [_currentEpisode]: A `StateFlow<Episode?>` holding the currently selected episode.
     *  - [timeElapsed]: A `MutableStateFlow<Duration>` representing the elapsed time in the
     *  current episode.
     */
    override fun play() {
        // Do nothing if already playing
        if (isPlaying.value) {
            return
        }

        val episode = _currentEpisode.value ?: return

        isPlaying.value = true
        timerJob = coroutineScope.launch {
            // Increment timer by a second
            while (isActive && timeElapsed.value < episode.duration) {
                delay(playerSpeed.toMillis())
                timeElapsed.update { it + playerSpeed }
            }

            // Once done playing, see if
            isPlaying.value = false
            timeElapsed.value = Duration.ZERO

            if (hasNext()) {
                next()
            }
        }
    }

    /**
     * Plays a single [PlayerEpisode].
     *
     * This function initiates playback of the specified [PlayerEpisode]. It essentially wraps the
     * single episode in a list and delegates the actual playback to the overload of [play] that
     * takes a [List] of [PlayerEpisode] (sure wish I could link to it but that's the way it is for
     * now).
     *
     * @param playerEpisode The [PlayerEpisode] to be played.
     *
     * @see play(playerEpisodes: List<PlayerEpisode>) the overload that takes a [List] of [PlayerEpisode]
     */
    override fun play(playerEpisode: PlayerEpisode) {
        play(playerEpisodes = listOf(playerEpisode))
    }

    /**
     * Plays a list of [PlayerEpisode]s.
     *
     * This function handles the following:
     * 1. **Pauses current playback:** If an episode is currently playing, it pauses it.
     * 2. **Manages the queue:**
     *    - It updates the internal queue of episodes to be played.
     *    - If an episode from the provided `playerEpisodes` list is already in the queue, it
     *    removes it from its current position and places it at the beginning of the new queue.
     *    - It preserves the currently playing episode (if any) by adding it after the provided
     *    `playerEpisodes` in the queue.
     *    - It keeps the order of the previously existing episode not in the input list, after the
     *    added playing episode (if any)
     * 3. **Starts playback:** After updating the queue, it initiates playback by calling `next()`,
     * which typically starts playing the first episode in the updated queue.
     *
     * @param playerEpisodes The list of [PlayerEpisode]s to play. These episodes will be added to
     * the beginning of the updated queue.
     *
     * Example Scenarios:
     *   - If `queue` is [A, B, C], `_currentEpisode` is B, and `playerEpisodes` is [D, E], the
     *   new `queue` will be [D, E, B, A, C].
     *   - If `queue` is [A, B, C], `_currentEpisode` is null, and `playerEpisodes` is [D, E],
     *   the new `queue` will be [D, E, A, B, C].
     *   - If `queue` is [A, B, C], `_currentEpisode` is B, and `playerEpisodes` is [B, E],
     *   the new `queue` will be [B, E, B, A, C] and the first B is the B input
     *   - If `queue` is [A, B, C], `_currentEpisode` is A, and `playerEpisodes` is [B, E],
     *   the new `queue` will be [B, E, A, C]
     *   - If `queue` is [A, B, C], `_currentEpisode` is B, and `playerEpisodes` is [A, B],
     *   the new `queue` will be [A, B, B, C]
     */
    override fun play(playerEpisodes: List<PlayerEpisode>) {
        if (isPlaying.value) {
            pause()
        }

        // Keep the currently playing episode in the queue
        val playingEpisode: PlayerEpisode? = _currentEpisode.value
        var previousList: List<PlayerEpisode> = emptyList()
        queue.update { queue: List<PlayerEpisode> ->
            playerEpisodes.map { episode: PlayerEpisode ->
                if (queue.contains(episode)) {
                    val mutableList: MutableList<PlayerEpisode> = queue.toMutableList()
                    mutableList.remove(episode)
                    previousList = mutableList
                } else {
                    previousList = queue
                }
            }
            if (playingEpisode != null) {
                playerEpisodes + listOf(playingEpisode) + previousList
            } else {
                playerEpisodes + previousList
            }
        }

        next()
    }

    /**
     * Pauses the ongoing timer.
     *
     * This function sets the [isPlaying] state to `false`, indicating that the timer is no longer
     * running. It also cancels the currently active timer job ([timerJob]) and sets it to `null`.
     *
     * After calling this function, the timer will be in a paused state, and no further time will be
     * elapsed until the timer is resumed (if applicable).
     */
    override fun pause() {
        isPlaying.value = false

        timerJob?.cancel()
        timerJob = null
    }

    /**
     * Stops the timer and resets its state.
     *
     * This function performs the following actions:
     * 1. Sets the [isPlaying] state to `false`, indicating that the timer is no longer running.
     * 2. Resets the [timeElapsed] state to [Duration.ZERO], effectively setting the elapsed time
     * back to zero.
     * 3. Cancels the currently running [timerJob], if any.
     * 4. Sets [timerJob] to `null`, clearing the reference to the cancelled job.
     *
     * This method should be called when the timer needs to be stopped and reset.
     */
    override fun stop() {
        isPlaying.value = false
        timeElapsed.value = Duration.ZERO

        timerJob?.cancel()
        timerJob = null
    }

    /**
     * Advances the playback time by the specified duration.
     *
     * This function updates the [timeElapsed] state, effectively simulating the passage of time
     * during playback. The advancement is limited to the duration of the currently playing episode.
     * If there is no current episode, this function does nothing.
     *
     * @param duration The duration to advance the playback time by. This should be a non-negative
     * duration. If a negative duration is provided the elapsed time will actually go back, but will
     * be clamped to 0. The advance is clamped to the current episode duration, so the elapsed time
     * will never be greater than the duration of the current episode.
     */
    override fun advanceBy(duration: Duration) {
        val currentEpisodeDuration = _currentEpisode.value?.duration ?: return
        timeElapsed.update {
            (it + duration).coerceAtMost(currentEpisodeDuration)
        }
    }

    /**
     * Rewinds the elapsed time by the specified duration.
     *
     * This function decrements the current elapsed time by the given [duration].
     * If the resulting time would be negative, it is clamped to zero, preventing the time from
     * going backward beyond the start.
     *
     * @param duration The duration to rewind by. Must be a non-negative duration.
     *
     * @throws IllegalArgumentException if duration is negative
     */
    override fun rewindBy(duration: Duration) {
        timeElapsed.update {
            (it - duration).coerceAtLeast(Duration.ZERO)
        }
    }

    /**
     * Called when the user starts seeking (e.g., dragging the seek bar thumb).
     *
     * This method pauses the player to prevent it from competing with the timeline
     * progression during the seeking operation. This ensures that the visual
     * feedback of the seek operation (like scrubbing) is smooth and accurate,
     * without interference from the underlying playback.
     *
     * Note: The player should be resumed via [onSeekingFinished] once the seeking operation is
     * complete.
     */
    override fun onSeekingStarted() {
        // Need to pause the player so that it doesn't compete with timeline progression.
        pause()
    }

    /**
     * Called when a seeking operation (e.g., fast-forwarding or rewinding) has finished.
     *
     * This function updates the [timeElapsed] state to reflect the new position reached after
     * seeking and then resumes playback.
     *
     * Behavior:
     * 1. **Retrieve Current Episode Duration:** It first attempts to get the duration of the
     * currently playing episode from the [_currentEpisode] state. If no episode is currently
     * loaded (i.e., `_currentEpisode.value` is `null`), the function returns early without
     * doing anything.
     * 2. **Clamp Duration:** It then ensures that the provided [duration] is within the valid
     * range of the current episode's duration. It uses `coerceIn` to restrict the duration:
     *     - If [duration] is less than [Duration.ZERO] (0), it's set to [Duration.ZERO].
     *     - If [duration] is greater than the [currentEpisodeDuration], it's set to
     *     [currentEpisodeDuration].
     *     - Otherwise, it remains unchanged.
     * 3. **Update Time Elapsed:** It updates the [timeElapsed] state with the clamped duration.
     * This makes sure that UI elements and other components that rely on [timeElapsed] reflect the
     * correct playback position.
     * 4. **Resume Playback:** Finally, it calls the [play] function to resume playback from the
     * newly set position.
     *
     * @param duration The new duration representing the time elapsed after the seek operation.
     * This duration can be anywhere within the episode, including the beginning (0) or the end.
     */
    override fun onSeekingFinished(duration: Duration) {
        val currentEpisodeDuration = _currentEpisode.value?.duration ?: return
        timeElapsed.update { duration.coerceIn(Duration.ZERO, currentEpisodeDuration) }
        play()
    }

    /**
     * Increases the current player speed by the specified duration.
     *
     * This function adds the given [speed] duration to the current [_playerSpeed].
     * It's used to accelerate the player's speed by a certain increment.
     *
     * @param speed The [Duration] representing the amount by which to increase the player's speed.
     * This value is added to the current speed.
     */
    override fun increaseSpeed(speed: Duration) {
        _playerSpeed.value += speed
    }

    /**
     * Decreases the current player speed by the specified duration.
     *
     * This function subtracts the provided [speed] (a [Duration]) from the internal
     * [_playerSpeed] value, effectively slowing down the player.
     *
     * @param speed The duration representing the amount by which to decrease the speed. A positive
     * duration will reduce the speed, while a negative or zero duration will have no or opposite
     * effect. For example, if the current speed is 2x and you decrease by a duration representing
     * 0.5x, the resulting speed would be 1.5x
     * @throws IllegalArgumentException if the resulting speed would be negative, since speeds are
     * expected to be non-negative.
     */
    override fun decreaseSpeed(speed: Duration) {
        _playerSpeed.value -= speed
    }

    /**
     * Advances to the next episode in the queue.
     *
     * This function performs the following actions:
     * 1. Checks if the queue is empty. If it is, the function returns, doing nothing.
     * 2. Resets the [timeElapsed] to zero, effectively starting the timer for the new episode.
     * 3. Retrieves the next episode from the front of the queue.
     * 4. Sets the [currentEpisode] to the retrieved episode.
     * 5. Removes the selected episode from the queue.
     * 6. Initiates playback of the newly selected [currentEpisode] by calling the [play] function.
     *
     * This method assumes that [queue] is a mutable list-like structure that maintains the order
     * of episodes, [timeElapsed] is a value that tracks how long the current episode has been
     * playing, and [currentEpisode] represents the episode currently being played. The [play]
     * function is assumed to start or resume the playback of an episode.
     *
     * @throws NoSuchElementException if the queue is modified concurrently and becomes empty
     * between the check and the retrieval of the next episode.
     */
    override fun next() {
        val q: List<PlayerEpisode> = queue.value
        if (q.isEmpty()) {
            return
        }

        timeElapsed.value = Duration.ZERO
        val nextEpisode: PlayerEpisode = q[0]
        currentEpisode = nextEpisode
        queue.value = q - nextEpisode
        play()
    }

    /**
     * Resets the timer to its initial state, stopping any ongoing timer.
     *
     * This function performs the following actions:
     * 1. **Resets [timeElapsed]:** Sets the [timeElapsed] value to [Duration.ZERO], effectively
     * resetting the elapsed time to zero.
     * 2. **Stops playback:** Sets the [isPlaying] value to `false`, indicating that the timer is
     * no longer running.
     * 3. **Cancels the timer job:** If a timer job ([timerJob]) is currently running, it is
     * canceled using `timerJob?.cancel()`.
     * 4. **Clears the timer job reference:** Sets [timerJob] to `null`, ensuring that no reference
     * to the canceled job is retained.
     *
     * This function is typically used to stop and reset a timer when the user navigates to a
     * previous state or when the timer needs to be completely reset.
     */
    override fun previous() {
        timeElapsed.value = Duration.ZERO
        isPlaying.value = false
        timerJob?.cancel()
        timerJob = null
    }

    private fun hasNext(): Boolean {
        return queue.value.isNotEmpty()
    }
}

// Used to enable property delegation
private operator fun <T> MutableStateFlow<T>.setValue(
    thisObj: Any?,
    property: KProperty<*>,
    value: T
) {
    this.value = value
}

private operator fun <T> MutableStateFlow<T>.getValue(thisObj: Any?, property: KProperty<*>): T =
    this.value
