package io.getstream.feedstutorial

import android.content.Context
import io.getstream.android.core.api.model.value.StreamApiKey
import io.getstream.android.core.api.model.value.StreamToken
import io.getstream.feeds.android.client.api.FeedsClient
import io.getstream.feeds.android.client.api.model.User
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

object ClientProvider {
    private val mutex = Mutex()
    private var instance: FeedsClient? = null

    // In a real app, you'll likely have a factory to create a new client instance on user sign in
    suspend fun get(context: Context): FeedsClient {
        instance?.let { return it }

        mutex.withLock {
            instance?.let { return@get it }

            val feedsClient = createClient(context)

            feedsClient.connect().fold(
                onSuccess = { /* Connected successfully */ },
                onFailure = { /* We're skipping error handling for tutorial purposes */ }
            )

            instance = feedsClient
            return feedsClient
        }
    }

    private fun createClient(context: Context): FeedsClient = FeedsClient(
        context = context.applicationContext,
        apiKey = StreamApiKey.fromString(Credentials.API_KEY),
        user = User(id = Credentials.USER_ID, name = Credentials.USER_NAME),
        tokenProvider = { StreamToken.fromString(Credentials.USER_TOKEN) },
    )
}
