package io.getstream.feedstutorial.feeds

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import io.getstream.feeds.android.client.api.FeedsClient
import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.model.FeedAddActivityRequest
import io.getstream.feeds.android.client.api.state.Feed
import io.getstream.feedstutorial.ClientProvider
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FeedsViewModel(application: Application) : AndroidViewModel(application) {
    val state: StateFlow<State?> =
        flow { emit(createState()) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    data class State(
        val client: FeedsClient,
        val userFeed: Feed,
        val timelineFeed: Feed,
    )

    private suspend fun createState(): State {
        val client = ClientProvider.get(application)
        val state = State(
            client = client,
            userFeed = client.feed("user", client.user.id),
            timelineFeed = client.feed("timeline", client.user.id),
        )
        loadFeeds(state)
        return state
    }

    private suspend fun loadFeeds(state: State) {
        state.userFeed.getOrCreate()
        state.timelineFeed.getOrCreate()

        viewModelScope.launch { followSelfIfNeeded(state) }
    }

    // By default, `timeline:user_id` doesn't follow `user:user_id` so the timeline doesn't
    // show the user's own posts. For tutorial purposes we create the follow relationship
    // here, but this logic is usually implemented in the backend.
    private suspend fun followSelfIfNeeded(state: State) {
        val followsSelf = state.timelineFeed.state.following.first()
            .any { it.targetFeed.fid == state.userFeed.fid }
        if (!followsSelf) {
            state.timelineFeed.follow(
                targetFid = state.userFeed.fid,
                createNotificationActivity = false
            )
        }
    }

    fun onPost(text: String, imageUri: Uri?) {
        val state = state.value ?: return

        viewModelScope.launch {
            val request = FeedAddActivityRequest(
                type = "post",
                text = text.trim(),
                feeds = listOf(state.userFeed.fid.rawValue),
            )
            state.userFeed.addActivity(request = request)
        }
    }

    fun onFollowClick(activity: ActivityData) {
        // TODO: Implement as part of the tutorial
    }

    fun onLikeClick(activity: ActivityData) {
        // TODO: Implement as part of the tutorial
    }
}
