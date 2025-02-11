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

package com.example.grub.ui.list

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * Displays the List route.
 *
 * @param listViewModel ViewModel that handles the business logic of this screen
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ListRoute(
    listViewModel: ListViewModel,
) {
    // UiState of the HomeScreen
    val uiState by listViewModel.uiState.collectAsStateWithLifecycle()

    ListRoute(
        uiState = uiState,
    )
}

/**
 * Displays the Home route.
 *
 * This composable is not coupled to any specific state management.
 *
 * @param uiState (state) the data to show on the screen
 * @param snackbarHostState (state) state for the [Scaffold] component on this screen
 */
@Composable
fun ListRoute(
    uiState: ListUiState,
) {
    ListScreen(uiState)
}
