package io.getstream.feedstutorial.feeds

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import io.getstream.feeds.android.client.api.FeedsClient
import io.getstream.feeds.android.client.api.file.FeedUploadPayload
import io.getstream.feeds.android.client.api.file.FileType
import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.model.FeedAddActivityRequest
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.state.Feed
import io.getstream.feeds.android.network.models.AddReactionRequest
import io.getstream.feedstutorial.ClientProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class FeedsViewModel(application: Application) : AndroidViewModel(application) {
    val state: StateFlow<State?> =
        flow { emit(createState()) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    data class State(
        val client: FeedsClient,
        val userFeed: Feed,
        val timelineFeed: Feed,
        val exploreFeed: Feed,
    )

    private suspend fun createState(): State {
        val client = ClientProvider.get(application)
        val state = State(
            client = client,
            userFeed = client.feed("user", client.user.id),
            timelineFeed = client.feed("timeline", client.user.id),
            exploreFeed = client.feed("foryou", client.user.id),
        )
        loadFeeds(state)
        return state
    }

    private suspend fun loadFeeds(state: State) {
        state.userFeed.getOrCreate()
        state.timelineFeed.getOrCreate()
        state.exploreFeed.getOrCreate()

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
            val imageFile: File? = imageUri?.let { application.copyToCache(it) }?.getOrElse {
                // Failed to copy the file to cache
                return@launch
            }
            val attachment = imageFile?.let { listOf(FeedUploadPayload(it, FileType.Image)) }

            val request = FeedAddActivityRequest(
                type = "post",
                text = text.trim(),
                feeds = listOf(state.userFeed.fid.rawValue),
                attachmentUploads = attachment,
            )
            state.userFeed.addActivity(request = request)

            imageFile?.let { deleteFile(it) }
        }
    }

    private suspend fun Context.copyToCache(uri: Uri) = withContext(Dispatchers.IO) {
        runCatching {
            val outputFile = File(cacheDir, "attachment_${System.currentTimeMillis()}.tmp")

            contentResolver.openInputStream(uri).use { inputStream ->
                checkNotNull(inputStream) { "Error opening input stream for URI: $uri" }

                FileOutputStream(outputFile).use(inputStream::copyTo)
            }
            outputFile
        }
    }

    fun onLikeClick(activity: ActivityData) {
        val state = state.value ?: return
        val hasOwnReaction = activity.ownReactions.any { it.type == "like" }

        viewModelScope.launch {
            if (hasOwnReaction) {
                state.timelineFeed.deleteActivityReaction(activity.id, "like")
            } else {
                val request = AddReactionRequest("like", createNotificationActivity = true)
                state.timelineFeed.addActivityReaction(activity.id, request)
            }
        }
    }

    private suspend fun deleteFile(file: File) = runCatching {
        withContext(Dispatchers.IO) { file.delete() }
    }

    fun onFollowClick(activity: ActivityData) {
        val state = state.value ?: return

        viewModelScope.launch {
            val targetFeedId = FeedId("user", activity.user.id)
            val isFollowing = activity.currentFeed?.ownFollows.isNullOrEmpty().not()

            val result = if (isFollowing) {
                state.timelineFeed.unfollow(targetFeedId)
            } else {
                state.timelineFeed.follow(targetFeedId)
            }
            // Ensure the feeds are up to date after follow/unfollow
            result.onSuccess {
                launch { state.timelineFeed.getOrCreate() }
                launch { state.exploreFeed.getOrCreate() }
            }
        }
    }
}
