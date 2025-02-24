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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController

/**
 * Displays the List route.
 *
 * @param listViewModel ViewModel that handles the business logic of this screen
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ListRoute(
    listViewModel: ListViewModel,
    navController: NavController
) {
    val uiState by listViewModel.uiState.collectAsState()
    ListScreen(
        navController,
        uiState,
        onSelectCustomFilter = { type: String, filter: String ->
            listViewModel.onSelectCustomFilter(
                type,
                filter
            )
        },
        onSubmitCustomFilter = { -> listViewModel.onSubmitCustomFilter() },
        onShowFilterDialog = { bool: Boolean -> listViewModel.onShowFilterDialog(bool) },
        onFilterSelected = { filter: String -> listViewModel.onFilterSelected(filter) },
    )
}
