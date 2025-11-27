package io.getstream.feedstutorial.feeds

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import io.getstream.feeds.android.client.api.model.ActivityData

class FeedsViewModel(application: Application) : AndroidViewModel(application) {

    fun onPost(text: String, imageUri: Uri?) {
        // TODO: Implement as part of the tutorial
    }

    fun onFollowClick(activity: ActivityData) {
        // TODO: Implement as part of the tutorial
    }

    fun onLikeClick(activity: ActivityData) {
        // TODO: Implement as part of the tutorial
    }
}
