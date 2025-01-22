import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.grub.R
import com.example.grub.ui.Destinations

@Composable
fun BottomNavigation(navController: NavController, modifier: Modifier = Modifier) {

    val items = listOf(
        BottomNavItem.Map,
        BottomNavItem.Discover,
    )

    NavigationBar(modifier = modifier) {
        items.forEach { item ->
            AddItem(
                screen = item,
                navController = navController,
            )
        }
    }
}

sealed class BottomNavItem(
    var title: String,
    var icon: Int,
    val route: String,
) {
    data object Map :
        BottomNavItem(
            "Map",
            R.drawable.ic_jetnews_bookmark, // replace
            Destinations.HOME_ROUTE,
        )

    data object Discover :
        BottomNavItem(
            "Discover",
            R.drawable.ic_jetnews_logo, // replace
            Destinations.INTERESTS_ROUTE,
        )
}

@Composable
fun RowScope.AddItem(
    screen: BottomNavItem,
    navController: NavController
) {
    NavigationBarItem(
        label = {
            Text(text = screen.title)
        },
        icon = {
            Icon(
                painterResource(id = screen.icon),
                contentDescription = screen.title
            )
        },
        selected = false,
        alwaysShowLabel = true,
        onClick = {
            navController.navigate(screen.route) {
                // Pop up to the start destination of the graph to
                // avoid building up a large stack of destinations
                // on the back stack as users select items
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                // Avoid multiple copies of the same destination when
                // re-selecting the same item
                launchSingleTop = true
                // Restore state when re-selecting a previously selected item
                restoreState = true
            }
        },
        colors = NavigationBarItemDefaults.colors()
    )
}
