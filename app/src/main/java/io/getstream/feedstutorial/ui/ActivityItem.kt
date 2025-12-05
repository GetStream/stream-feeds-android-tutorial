package io.getstream.feedstutorial.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.UserData

@Composable
fun ActivityItem(
    activity: ActivityData,
    feedId: FeedId,
    currentUserId: String,
    navController: NavController,
    onFollowClick: (ActivityData) -> Unit,
    onLikeClick: (ActivityData) -> Unit,
) {
    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            ActivityHeader(activity, currentUserId, onFollowClick)

            Text(
                text = activity.text.orEmpty(),
                fontSize = 14.sp,
                lineHeight = 20.sp,
                modifier = Modifier.padding(start = 56.dp, bottom = 8.dp),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            ) {
                val reactionCount = activity.reactionGroups["like"]?.count ?: 0
                val hasOwnReaction = activity.ownReactions.any { it.type == "like" }
                ActionButton(
                    icon = if (hasOwnReaction) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    count = reactionCount,
                    contentDescription = "Like",
                    onClick = { onLikeClick(activity) },
                )
            }
        }
    }
}

@Composable
private fun ActivityHeader(
    activity: ActivityData,
    currentUserId: String,
    onFollowClick: (ActivityData) -> Unit,
) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Avatar(activity.user)

        Text(
            text = activity.user.name ?: "Unknown",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
        )

        if (activity.user.id != currentUserId) {
            TextButton(onClick = { onFollowClick(activity) }) {
                if (activity.currentFeed?.ownFollows.isNullOrEmpty()) {
                    Text("Follow")
                } else {
                    Text("Unfollow")
                }
            }
        }
    }
}

@Composable
private fun Avatar(user: UserData) {
    Box(contentAlignment = Alignment.Center) {
        AsyncImage(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondary),
            model = user.image,
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )
        Text(
            text = user.name?.firstOrNull()?.uppercase() ?: "?",
            color = MaterialTheme.colorScheme.onSecondary,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun ActionButton(
    icon: ImageVector,
    count: Int,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(20.dp),
        )
        Text(
            text = count.toString(),
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(start = 4.dp),
        )
    }
}
