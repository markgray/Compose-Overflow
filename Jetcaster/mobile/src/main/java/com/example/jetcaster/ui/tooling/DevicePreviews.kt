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

package com.example.jetcaster.ui.tooling

import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

/**
 * [DevicePreviews] is an annotation class that groups together a set of common device
 * configurations for Compose Previews. It provides quick access to previews for various
 * form factors and screen sizes, including:
 *
 * - **small-phone:** A preview representing a smaller phone screen, such as the Pixel 4a.
 * - **phone:** A preview representing a standard phone screen with dimensions of 411dp x 891dp.
 * - **landscape:** A preview representing a landscape orientation with dimensions of 640dp x 360dp
 * and 480 dpi.
 * - **foldable:** A preview representing a foldable device with dimensions of 673dp x 841dp.
 * - **tablet:** A preview representing a tablet device with dimensions of 1280dp x 800dp and
 * 240 dpi.
 *
 * By using `@DevicePreviews` as a meta-annotation, you can easily add these predefined
 * previews to your Composable functions, improving the efficiency and consistency of
 * your previewing workflow.
 *
 * Example Usage:
 *
 * ```kotlin
 * @DevicePreviews
 * @Composable
 * fun MyComposablePreview() {
 *     MyComposable()
 * }
 * ```
 *
 * In this example, `MyComposablePreview` will have five different previews generated, each
 * representing one of the device configurations defined in `DevicePreviews`.
 */
@Preview(name = "small-phone", device = Devices.PIXEL_4A)
@Preview(name = "phone", device = "spec:width=411dp,height=891dp")
@Preview(name = "landscape", device = "spec:width=640dp,height=360dp,dpi=480")
@Preview(name = "foldable", device = "spec:width=673dp,height=841dp")
@Preview(name = "tablet", device = "spec:width=1280dp,height=800dp,dpi=240")
annotation class DevicePreviews
