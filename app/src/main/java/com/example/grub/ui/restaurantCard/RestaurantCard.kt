import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.grub.R
import com.example.grub.model.Deal
import com.example.grub.model.RestaurantDeal
import com.example.grub.ui.navigation.Destinations
import com.example.grub.utils.ImageUrlHelper
import java.time.format.DateTimeFormatter


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
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RestaurantItem(
    restaurant: RestaurantDeal,
    navController: NavController,
    modifier: Modifier = Modifier,
    showBoxShadow: Boolean = true,
) {
    Box(
        modifier = modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .drawBehind {
                if (showBoxShadow) {
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
                RestaurantImage(
                    restaurant.imageUrl,
                    Modifier
                        .weight(0.3f)
                        .aspectRatio(1f)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(2f)) {
                    Text(
                        text = restaurant.restaurantName,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black
                    )
                    if (restaurant.displayAddress != null && restaurant.displayAddress != "")
                        Text(
                            text = restaurant.displayAddress,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Black
                        )
                }
            }

            Column() {
                restaurant.deals.forEach { deal ->
                    DealItem(
                        restaurantName = restaurant.restaurantName,
                        restaurantAddress = restaurant.displayAddress,
                        deal = deal,
                        navController = navController
                    )
                }
            }
        }

    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DealItem(
    restaurantName: String,
    restaurantAddress: String?,
    deal: Deal,
    navController: NavController
) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp)
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
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        "restaurantName",
                        restaurantName
                    )
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        "restaurantAddress",
                        restaurantAddress
                    )
                    navController.navigate(Destinations.DEAL_DETAIL_ROUTE)
                }
        ) {
            DealImage(
                deal.imageUrl,
                Modifier
                    .weight(0.3f)
                    .aspectRatio(5f / 4f)
                    .clip(MaterialTheme.shapes.small)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                Modifier.weight(0.7f)
            ) {
                val dealTitle: String =
                    if (deal.price != 0.0)
                        "$" + deal.price.toString() + " " + deal.item
                    else
                        deal.item;

                Text(
                    text = deal.type.toString() + " " + dealTitle,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 2.dp)
                )
                if (!deal.description.isNullOrBlank()) {
                    Text(
                        text = deal.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Black,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }
                if (deal.expiryDate != null) {
                    Row() {
                        Icon(
                            Icons.Filled.CalendarMonth,
                            contentDescription = "expiryIcon",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(14.dp)
                                .scale(0.95f)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "Valid Until: " + deal.expiryDate.format(
                                DateTimeFormatter.ofPattern(
                                    "MMMM dd, yyyy"
                                )
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }

                }

            }

        }
    }

}

@Composable
fun DealImage(imageUrl: String?, modifier: Modifier = Modifier) {
    Log.d("deal image", imageUrl.toString())
    AsyncImage(
        model = imageUrl,
        modifier = modifier,
        contentDescription = "Deal Image",
        placeholder = painterResource(R.drawable.hot_deals),
        error = painterResource(R.drawable.hot_deals),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun RestaurantImage(imageUrl: String?, modifier: Modifier = Modifier) {
    Log.d("restaurant image", imageUrl.toString())

    AsyncImage(
        model = imageUrl,
        modifier = modifier,
        contentDescription = "restaurant Image",
        placeholder = painterResource(R.drawable.restaurant_placeholder),
        error = painterResource(R.drawable.restaurant_placeholder),
        contentScale = ContentScale.Crop
    )
}
