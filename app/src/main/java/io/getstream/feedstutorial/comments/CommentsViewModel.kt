package io.getstream.feedstutorial.comments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.getstream.feeds.android.client.api.model.FeedId

class CommentsViewModel(
    private val activityId: String,
    private val feedId: FeedId,
    application: Application
) : AndroidViewModel(application) {
    fun onComment(text: String) {
        // TODO: Implement as part of the tutorial
    }
}
