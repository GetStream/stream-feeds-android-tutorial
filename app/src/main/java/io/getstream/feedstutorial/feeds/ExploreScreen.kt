package io.getstream.feedstutorial.feeds

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feedstutorial.ui.ActivityItem
import io.getstream.feedstutorial.ui.EmptyContent
import io.getstream.feedstutorial.ui.LoadingScreen

@Composable
fun ExploreScreen(navController: NavController) {
    val viewModel: FeedsViewModel = viewModel(LocalActivity.current as ComponentActivity)
    val state by viewModel.state.collectAsStateWithLifecycle()

    Surface {
        when (val state = state) {
            null -> LoadingScreen()
            else -> ExploreContent(
                state = state,
                navController = navController,
                onLikeClick = { activity -> viewModel.onLikeClick(activity) },
                onFollowClick = { activity -> viewModel.onFollowClick(activity) }
            )
        }
    }
}

@Composable
fun ExploreContent(
    state: FeedsViewModel.State,
    navController: NavController,
    onLikeClick: (ActivityData) -> Unit,
    onFollowClick: (ActivityData) -> Unit,
) {
    val activities by state.exploreFeed.state.activities.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (activities.isEmpty()) {
            item {
                EmptyContent(
                    "Popular activities will show up here once your application has more content"
                )
            }
        } else {
            items(activities) { activity ->
                ActivityItem(
                    activity = activity,
                    feedId = state.exploreFeed.fid,
                    currentUserId = state.client.user.id,
                    navController = navController,
                    onLikeClick = onLikeClick,
                    onFollowClick = onFollowClick
                )
            }
        }
    }
}
