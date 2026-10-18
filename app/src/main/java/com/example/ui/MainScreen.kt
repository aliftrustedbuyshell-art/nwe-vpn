package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SecurityScreen
import com.example.ui.screens.ServersScreen
import com.example.ui.screens.SpeedDiagnosticsScreen
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberPrimary
import com.example.ui.theme.CyberSecondary
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary

enum class NavDestination(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    HOME("Shield", Icons.Default.Shield),
    SERVERS("Servers", Icons.Default.Public),
    SECURITY("Security", Icons.Default.Security),
    DIAGNOSTICS("Speed Test", Icons.Default.Speed)
}

@Composable
fun MainScreen() {
    var currentDestination by rememberSaveable { mutableStateOf(NavDestination.HOME) }

    // Handle system back navigation from sub-screens to Home
    if (currentDestination != NavDestination.HOME) {
        BackHandler {
            currentDestination = NavDestination.HOME
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 600.dp

        if (isWideScreen) {
            // Tablet / Foldable layout with NavigationRail on side
            Row(modifier = Modifier.fillMaxSize()) {
                NavigationRail(
                    containerColor = CyberSurface,
                    contentColor = CyberTextPrimary,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    NavDestination.values().forEach { destination ->
                        val selected = currentDestination == destination
                        NavigationRailItem(
                            selected = selected,
                            onClick = { currentDestination = destination },
                            icon = {
                                Icon(
                                    imageVector = destination.icon,
                                    contentDescription = destination.label
                                )
                            },
                            label = { Text(destination.label) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = CyberPrimary,
                                selectedTextColor = CyberPrimary,
                                unselectedIconColor = CyberTextSecondary,
                                unselectedTextColor = CyberTextSecondary,
                                indicatorColor = CyberPrimary.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier.testTag("nav_rail_${destination.name.lowercase()}")
                        )
                    }
                }

                // Main Content Pane
                ScreenContent(
                    destination = currentDestination,
                    onNavigateTo = { currentDestination = it },
                    modifier = Modifier.weight(1f)
                )
            }
        } else {
            // Phone Portrait layout with Bottom NavigationBar
            Scaffold(
                contentWindowInsets = WindowInsets.safeDrawing,
                containerColor = MaterialTheme.colorScheme.background,
                bottomBar = {
                    NavigationBar(
                        containerColor = CyberSurface,
                        contentColor = CyberTextPrimary,
                        tonalElevation = 8.dp,
                        modifier = Modifier.testTag("bottom_nav_bar")
                    ) {
                        NavDestination.values().forEach { destination ->
                            val selected = currentDestination == destination
                            NavigationBarItem(
                                selected = selected,
                                onClick = { currentDestination = destination },
                                icon = {
                                    Icon(
                                        imageVector = destination.icon,
                                        contentDescription = destination.label
                                    )
                                },
                                label = {
                                    Text(
                                        text = destination.label,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = CyberPrimary,
                                    selectedTextColor = CyberPrimary,
                                    unselectedIconColor = CyberTextSecondary,
                                    unselectedTextColor = CyberTextSecondary,
                                    indicatorColor = CyberPrimary.copy(alpha = 0.2f)
                                ),
                                modifier = Modifier.testTag("nav_item_${destination.name.lowercase()}")
                            )
                        }
                    }
                }
            ) { innerPadding ->
                ScreenContent(
                    destination = currentDestination,
                    onNavigateTo = { currentDestination = it },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Composable
private fun ScreenContent(
    destination: NavDestination,
    onNavigateTo: (NavDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    when (destination) {
        NavDestination.HOME -> HomeScreen(
            onNavigateToServers = { onNavigateTo(NavDestination.SERVERS) },
            onNavigateToSecurity = { onNavigateTo(NavDestination.SECURITY) },
            modifier = modifier
        )
        NavDestination.SERVERS -> ServersScreen(
            onServerSelected = {
                // Navigate back to Home on selection
                onNavigateTo(NavDestination.HOME)
            },
            modifier = modifier
        )
        NavDestination.SECURITY -> SecurityScreen(
            modifier = modifier
        )
        NavDestination.DIAGNOSTICS -> SpeedDiagnosticsScreen(
            modifier = modifier
        )
    }
}
