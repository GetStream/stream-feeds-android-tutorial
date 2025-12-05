package io.getstream.feedstutorial.comments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.request.ActivityAddCommentRequest
import io.getstream.feeds.android.client.api.state.Activity
import io.getstream.feedstutorial.ClientProvider
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CommentsViewModel(
    private val activityId: String,
    private val feedId: FeedId,
    application: Application
) : AndroidViewModel(application) {
    val activity: StateFlow<Activity?> =
        flow { emit(createActivity()) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private suspend fun createActivity(): Activity {
        return ClientProvider.get(application)
            .activity(activityId, feedId)
            .apply { get() }
    }

    fun onComment(text: String) {
        val activity = activity.value ?: return

        viewModelScope.launch {
            val request = ActivityAddCommentRequest(
                comment = text,
                activityId = activity.activityId
            )
            activity.addComment(request)
        }
    }
}
