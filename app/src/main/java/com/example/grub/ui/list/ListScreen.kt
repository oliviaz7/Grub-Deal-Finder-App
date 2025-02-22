package com.example.grub.ui.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.grub.R
import com.example.grub.ui.navigation.Destinations

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(uiState: ListUiState, navController: NavController, modifier: Modifier = Modifier) {

    println("List SCREEN ui state: ${uiState.restaurantDeals}")
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.list_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            )
        }
    ) { innerPadding ->
        val screenModifier = Modifier.padding(innerPadding)

        Column(modifier = screenModifier) {
            // Iterate through each restaurant
            uiState.restaurantDeals.forEach { restaurant ->
                Text(
                    text = restaurant.restaurantName,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )

                // Iterate through each deal of the current restaurant
                restaurant.deals.forEach { deal ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .clickable {
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    key = "deal",
                                    value = deal
                                )
                                navController.navigate(Destinations.DEAL_DETAIL_ROUTE)
                            }
                    ) {
                        DealImage(deal.imageUrl)
                        Text(
                            text = "${deal.id} ${deal.type}",
                            modifier = Modifier
                                .padding(16.dp)
                                .weight(1f), // Break line if the title is too long
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.width(16.dp))
                    }

                    // Divider after each deal
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 72.dp, top = 8.dp, bottom = 8.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    )
                }
            }
        }
    }

}

@Composable
fun DealImage(imageUrl: String?, modifier: Modifier = Modifier) {
    imageUrl?.let { url ->
        AsyncImage(
            model = url,
            contentDescription = "Deal Image",
            // TODO: replace with a better loading placeholder
            placeholder = painterResource(R.drawable.placeholder_4_3),
            modifier = modifier
                .size(56.dp)
                .clip(RoundedCornerShape(4.dp))
        )
    } ?: Image(
        painter = painterResource(R.drawable.placeholder_1_1),
        contentDescription = null,
        modifier = modifier
            .size(56.dp)
            .clip(RoundedCornerShape(4.dp))
    )
}
