package com.example.grub.ui.shared.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.PersonPin
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
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

    val currentRoute = navController.currentDestination?.route

    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
    ) {
        AddItem(
            screen = BottomNavItem.List,
            navController = navController,
            selected = currentRoute?.startsWith(BottomNavItem.List.route) == true
        )
        AddItem(
            screen = BottomNavItem.Map,
            navController = navController,
            selected = currentRoute?.startsWith(BottomNavItem.Map.route) == true
        )
        AddItem(
            screen = BottomNavItem.Profile,
            navController = navController,
            selected = currentRoute?.startsWith(BottomNavItem.Profile.route) == true,
            _restoreState = false
        )
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
            Icons.AutoMirrored.Outlined.ListAlt,
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
    navController: NavController,
    selected: Boolean,
    _restoreState: Boolean = true,
) {
    NavigationBarItem(
        label = {
            Text(text = screen.title)
        },
        icon = {
            Icon(
                screen.icon,
                contentDescription = screen.title,
                tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary.copy(
                    alpha = 0.8f
                ),
                modifier = Modifier
                    .size(36.dp)
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
                restoreState = _restoreState
            }
        },
    )
}
