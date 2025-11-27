package io.getstream.feedstutorial.comments

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.compose.viewModel
import io.getstream.feeds.android.client.api.model.FeedId

@Composable
fun CommentsScreen(activityId: String, feedId: FeedId) {
    val viewModel = viewModel {
        CommentsViewModel(
            activityId = activityId,
            feedId = feedId,
            application = get(APPLICATION_KEY)!!
        )
    }

    Surface {
    }
}
