package com.example.grub.ui.dealDetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.grub.R
import com.example.grub.ui.navigation.Destinations


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DealDetailScreen(
    uiState: DealDetailUiState,
    navController: NavController,
    modifier: Modifier = Modifier
) {

    println("Deal Detail SCREEN ui state: ${uiState.deal}")
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.deal_detail_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            )
            IconButton(
                onClick = { navController.popBackStack() },
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = null,
                )
            }
        }
    ) { innerPadding ->
        val screenModifier = Modifier.padding(innerPadding)

        Column(modifier = screenModifier) {
            val deal = uiState.deal!!

            Text(text = "Deal Type: ${deal.type}", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Posted On: ${deal.datePosted}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Posted By: ${deal.userId}",
                style = MaterialTheme.typography.bodyMedium
            )

        }
    }
}