package com.example.grub.ui.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ListAlt
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.PersonPin
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination

@Composable
fun BottomNavigation(navController: NavController, modifier: Modifier = Modifier) {

    val items = listOf(
        BottomNavItem.List,
        BottomNavItem.Map,
        BottomNavItem.Profile,
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
    var icon: ImageVector,
    val route: String,
) {
    data object Map :
        BottomNavItem(
            "Map",
            Icons.Outlined.Map,
            Destinations.HOME_ROUTE,
        )

    data object List :
        BottomNavItem(
            "List",
            Icons.Outlined.ListAlt,
            Destinations.LIST_ROUTE,
        )

    data object Profile :
        BottomNavItem(
            "Profile",
            Icons.Outlined.PersonPin,
            Destinations.PROFILE_ROUTE,
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
                screen.icon,
                contentDescription = screen.title,
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(24.dp))
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
