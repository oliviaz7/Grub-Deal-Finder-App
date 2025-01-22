import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.grub.R

// https://medium.com/design-bootcamp/navigation-bar-with-jetpack-compose-32b052824b7d

@Composable
fun BottomNavigation(modifier: Modifier = Modifier) {

    val items = listOf(
        BottomNavItem.Map,
        BottomNavItem.Discover,
    )

    NavigationBar(modifier = modifier) {
        items.forEach { item ->
            AddItem(
                screen = item
            )
        }
    }
}

sealed class BottomNavItem(
    var title: String,
    var icon: Int
) {
    data object Map :
        BottomNavItem(
            "Map",
            R.drawable.ic_jetnews_bookmark // replace
        )

    data object Discover :
        BottomNavItem(
            "Discover",
            R.drawable.ic_jetnews_logo // replace
        )
}

@Composable
fun RowScope.AddItem(
    screen: BottomNavItem
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
        onClick = { /*TODO*/ },
        colors = NavigationBarItemDefaults.colors()
    )
}
