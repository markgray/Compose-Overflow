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

package com.example.jetsnack.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetsnack.R
import com.example.jetsnack.ui.MainContainer
import com.example.jetsnack.ui.components.JetsnackScaffold
import com.example.jetsnack.ui.theme.JetsnackTheme

/**
 * This is just a placeholder for the [HomeSections.PROFILE] screen. Our root Composable is a
 * [Column], whose `horizontalAlignment` argument is set to [Alignment.CenterHorizontally] to
 * center its children horizontally, and  whose `modifier` argument chains a [Modifier.fillMaxSize]
 * to our [Modifier] parameter [modifier] (causes it to occupy its entire incoming size constraints)
 * with a [Modifier.wrapContentSize] that allows it to measure at its desired size without regard
 * for the incoming minimum size constraints, and with a [Modifier.padding] chained to that that
 * adds `24.dp` padding all its sides.
 *
 * In the [Column]'s `content` [ColumnScope] Composable lambda argument we compose:
 *  - an [Image] whose `painter` argument is a [painterResource] whose `id` argument is set to
 *  the `R.drawable.empty_state_search` drawable resource (which is a flashlight shining on an
 *  android cap with a question mark on it), and whose `contentDescription` argument is `null`.
 *  - a [Spacer] whose `modifier` argument is a [Modifier.height] with a `height` of 24.dp.
 *  - a [Text]
 *
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller passes us one that traces back to [MainContainer] where a [Modifier.padding]
 * with the [PaddingValues] that are passed by [JetsnackScaffold] to its `content` lambda is
 * chained to a [Modifier.consumeWindowInsets] that consumes those [PaddingValues] as insets.
 */
@Composable
fun Profile(
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .wrapContentSize()
            .padding(all = 24.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.empty_state_search),
            contentDescription = null
        )
        Spacer(modifier = Modifier.height(height = 24.dp))
        Text(
            text = stringResource(R.string.work_in_progress),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(height = 16.dp))
        Text(
            text = stringResource(R.string.grab_beverage),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Three Previews of our [Profile] Composable using different configurations.
 */
@Preview("default")
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("large font", fontScale = 2f)
@Composable
fun ProfilePreview() {
    JetsnackTheme {
        Profile()
    }
}
