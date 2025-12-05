package io.getstream.feedstutorial.feeds

import android.net.Uri
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
fun HomeScreen(navController: NavController) {
    val viewModel: FeedsViewModel = viewModel(LocalActivity.current as ComponentActivity)
    val state by viewModel.state.collectAsStateWithLifecycle()

    Surface {
        when (val state = state) {
            null -> LoadingScreen()
            else -> HomeContent(
                state = state,
                navController = navController,
                onLikeClick = { activity -> viewModel.onLikeClick(activity) },
                onPost = { text, imageUri -> viewModel.onPost(text, imageUri) },
                onFollowClick = { activity -> viewModel.onFollowClick(activity) }
            )
        }
    }
}

@Composable
fun HomeContent(
    state: FeedsViewModel.State,
    navController: NavController,
    onLikeClick: (ActivityData) -> Unit,
    onPost: (String, Uri?) -> Unit,
    onFollowClick: (ActivityData) -> Unit,
) {
    val timelineActivities by state.timelineFeed.state.activities.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (timelineActivities.isEmpty()) {
            item { EmptyContent("Write something to start your timeline ✨") }
        } else {
            items(timelineActivities) { activity ->
                ActivityItem(
                    activity = activity,
                    feedId = state.timelineFeed.fid,
                    currentUserId = state.client.user.id,
                    navController = navController,
                    onLikeClick = onLikeClick,
                    onFollowClick = onFollowClick
                )
            }
        }
    }
}
