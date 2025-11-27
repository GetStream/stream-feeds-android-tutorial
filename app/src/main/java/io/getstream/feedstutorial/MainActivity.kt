package io.getstream.feedstutorial

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.getstream.feedstutorial.comments.CommentsScreen
import io.getstream.feedstutorial.feeds.ExploreScreen
import io.getstream.feedstutorial.feeds.HomeScreen
import io.getstream.feedstutorial.ui.theme.TutorialTheme
import io.getstream.feeds.android.client.api.model.FeedId

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TutorialTheme {
                RootScreen()
            }
        }
    }
}

enum class NavBarTab(val title: String, val icon: ImageVector, val route: String) {
    Home("Home", Icons.Default.Home, "home"),
    Explore("Explore", Icons.Default.Search, "explore"),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RootScreen() {
    val navController = rememberNavController()
    var selectedTab by remember { mutableStateOf(NavBarTab.Home) }

    Scaffold(
        modifier = Modifier.statusBarsPadding(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Stream Feeds Tutorial") },
                modifier = Modifier.shadow(4.dp)
            )
        },
        bottomBar = {
            MainNavBar(
                selectedTab = selectedTab,
                onTabClick = { clickedTab ->
                    selectedTab = clickedTab
                    navController.navigate(clickedTab.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = NavBarTab.Home.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            composable(NavBarTab.Home.route) { HomeScreen(navController) }
            composable(NavBarTab.Explore.route) { ExploreScreen(navController) }
            composable(
                route = "comments/{feedId}/{activityId}",
                enterTransition = { slideInVertically { it } },
                exitTransition = { slideOutVertically { it } },
            ) { backStackEntry ->
                val activityId = backStackEntry.arguments?.getString("activityId").orEmpty()
                val feedId = FeedId(backStackEntry.arguments?.getString("feedId").orEmpty())
                CommentsScreen(activityId, feedId)
            }
        }
    }
}

@Composable
private fun MainNavBar(selectedTab: NavBarTab, onTabClick: (NavBarTab) -> Unit) {
    NavigationBar {
        NavBarTab.entries.forEach { tab ->
            NavigationBarItem(
                selected = selectedTab == tab,
                onClick = { onTabClick(tab) },
                icon = { Icon(tab.icon, contentDescription = tab.title) },
                label = { Text(tab.title) },
            )
        }
    }
}
