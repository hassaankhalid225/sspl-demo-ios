package com.sspl.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.sspl.NavItem
import org.jetbrains.compose.resources.stringResource

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 10/07/2024.
 * se.muhammadimran@gmail.com
 */

@Composable
fun AppBottomBar(bottomNavItems: List<NavItem>, navController: NavHostController) {
    BottomAppBar(
        windowInsets = WindowInsets(bottom = 0.dp, top = 0.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp, 16.dp, 0.dp, 0.dp)),
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        bottomNavItems.forEach { navItem ->
            NavigationBarItem(
                selected = navBackStackEntry?.destination?.route == navItem.screen.route,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.outlineVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.outlineVariant,
                    selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    indicatorColor = Color.Transparent
                ),

                onClick = {
                    navController.navigate(navItem.screen.route) {
                        popUpTo(navController.graph.startDestinationRoute!!){
                //avoiding new screen instances on bottom item click
                            saveState=true
                        }
                        launchSingleTop = true
                        restoreState=true
                    }
                },
                icon = {
                    navItem.screen.icon?.let {
                        Icon(
                            painter = it.icon(),
                            contentDescription = null
                        )
                    }
                },
                label = {
                    navItem.screen.title?.let {
                        AppTextLabel(
                            text = stringResource(it),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = if (navBackStackEntry?.destination?.route == navItem.screen.route) FontWeight.Medium else FontWeight.Normal
                        )
                    }
                }
            )
        }
    }
}