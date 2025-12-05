package io.getstream.feedstutorial.comments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.getstream.feedstutorial.ui.LoadingScreen
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.ThreadedCommentData
import io.getstream.feeds.android.client.api.state.Activity

@Composable
fun CommentsScreen(activityId: String, feedId: FeedId) {
    val viewModel = viewModel {
        CommentsViewModel(
            activityId = activityId,
            feedId = feedId,
            application = get(APPLICATION_KEY)!!
        )
    }

    val activity by viewModel.activity.collectAsStateWithLifecycle()

    Surface {
        when (val activity = activity) {
            null -> LoadingScreen()
            else -> CommentsContent(
                activity = activity,
                onComment = { text -> viewModel.onComment(text) }
            )
        }
    }
}

@Composable
private fun CommentsContent(activity: Activity, onComment: (String) -> Unit) {
    val comments by activity.state.comments.collectAsStateWithLifecycle()

    Column {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(comments) { comment ->
                CommentItem(comment)
            }
        }

        HorizontalDivider(Modifier.fillMaxWidth())

        CommentComposer(onComment = onComment)
    }
}

@Composable
private fun CommentItem(data: ThreadedCommentData) {
    Card {
        Column(
            Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(data.user.name.orEmpty(), fontWeight = FontWeight.SemiBold)
            Text(data.text.orEmpty())
        }
    }
}

@Composable
fun CommentComposer(
    onComment: (String) -> Unit,
) {
    var text by remember { mutableStateOf("") }

    OutlinedTextField(
        value = text,
        onValueChange = { text = it },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        placeholder = { Text("Write a comment...") },
        maxLines = 4,
        trailingIcon = {
            IconButton(
                onClick = {
                    onComment(text)
                    text = ""
                },
                enabled = text.isNotBlank(),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.Send,
                    contentDescription = "Send comment",
                )
            }
        }
    )
}
