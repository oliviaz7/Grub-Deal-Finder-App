import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.grub.R
import com.example.grub.model.Deal
import com.example.grub.model.DealType
import com.example.grub.model.RestaurantDeal
import com.example.grub.ui.navigation.Destinations


/**
 * Composable function to display a restaurant item, including the restaurant's image,
 * name, address, and a list of deals associated with that restaurant.
 *
 * This component is intended to be used in list screens where restaurants are shown with
 * their deals. It accepts a `restaurant` object (of type `RestaurantDeal`) and displays:
 * 1. A restaurant image
 * 2. The restaurant's name and address
 * 3. A list of deals under that restaurant, indented for visual clarity
 *
 * The function also supports navigation, where clicking on a deal will navigate to
 * the `DealDetail` screen and pass the selected deal.
 *
 * Usage:
 * - Reusable across screens where restaurant and deal data need to be displayed.
 * - Pass in a `RestaurantDeal` object and a `NavController` for handling navigation.
 *
 * @param restaurant The `RestaurantDeal` object representing the restaurant and its deals.
 * @param navController The `NavController` used to handle navigation to the deal details.
 */
@Composable
fun RestaurantItem(
    restaurant: RestaurantDeal,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .drawBehind {
                val shadowSize = 2.dp.toPx()
                drawRoundRect(
                    color = Color.Black.copy(alpha = 0.2f),
                    size = size.copy(
                        height = size.height + shadowSize,
                        width = size.width + shadowSize,
                    ),
                    topLeft = Offset(8f, 8f),
                    cornerRadius = CornerRadius(x = 20f, y = 20f)
                )
            }
            .background(
                MaterialTheme.colorScheme.background.copy(alpha = 1f),
                MaterialTheme.shapes.large
            )

    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                val image =
                    painterResource(R.drawable.post_1_thumb)
                Image(
                    painter = image,
                    contentDescription = "${restaurant.restaurantName} Image",
                    modifier = Modifier
                        .size(48.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(2f)) {
                    Text(
                        text = restaurant.restaurantName,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black
                    )
                    Text(
                        text = restaurant.placeId,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Black
                    )
                }
            }

            Column() {
                restaurant.deals.forEach { deal ->
                    DealItem(deal = deal, navController = navController)
                }
            }
        }

    }
}


@Composable
fun DealItem(
    deal: Deal,
    navController: NavController
) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .drawBehind {
                val shadowSize = 2.dp.toPx()
                drawRoundRect(
                    color = Color.Gray.copy(alpha = 0.2f),
                    size = size.copy(
                        height = size.height + shadowSize,
                        width = size.width + shadowSize,
                    ),
                    topLeft = Offset(6f, 6f),
                    cornerRadius = CornerRadius(x = 20f, y = 20f)
                )
            }
            .background(Color.White, MaterialTheme.shapes.medium)

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 8.dp)
                .clickable {
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        key = "deal",
                        value = deal
                    )
                    navController.navigate(Destinations.DEAL_DETAIL_ROUTE)
                }
        ) {
            DealImage(deal.imageUrl)
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = deal.type.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = when (deal.type) {
                    DealType.BOGO -> MaterialTheme.colorScheme.primary
                    DealType.DISCOUNT -> MaterialTheme.colorScheme.primary
                    DealType.FREE -> MaterialTheme.colorScheme.primary
                    DealType.OTHER -> MaterialTheme.colorScheme.primary
                }
            )
        }
    }

}

@Composable
fun DealImage(imageUrl: String?, modifier: Modifier = Modifier) {
    imageUrl?.let { url ->
        AsyncImage(
            model = url,
            modifier = modifier
                .size(32.dp)
                .clip(MaterialTheme.shapes.small),
            contentDescription = "Deal Image",
            // TODO: replace with a better loading placeholder
            placeholder = painterResource(R.drawable.placeholder_4_3),
            contentScale = ContentScale.Crop
        )
    } ?: Image(
        painter = painterResource(R.drawable.placeholder_1_1),
        contentDescription = null,
        modifier = modifier
            .size(32.dp)
            .clip(MaterialTheme.shapes.large)
    )
}
