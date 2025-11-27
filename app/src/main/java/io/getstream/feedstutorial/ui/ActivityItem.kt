package io.getstream.feedstutorial.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.model.FeedId

@Composable
fun ActivityItem(
    activity: ActivityData,
    currentUserId: String,
    onFollowClick: (ActivityData) -> Unit,
    onLikeClick: (ActivityData) -> Unit,
    feedId: FeedId,
    navController: NavController,
) {
    // TODO: Implement as part of the tutorial
}
