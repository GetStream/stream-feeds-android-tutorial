package io.getstream.feedstutorial.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun ActivityComposer(onPost: (String, Uri?) -> Unit) {
    var text by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        imageUri = it
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.fillMaxSize(),
            placeholder = { Text("What is happening?") },
        )

        imageUri?.let {
            AsyncImage(
                model = it,
                contentDescription = "Selected image",
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(25)),
                contentScale = ContentScale.Crop
            )
        }

        Row(Modifier.align(Alignment.End), Arrangement.spacedBy(16.dp)) {
            IconButton(
                onClick = {
                    launcher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                content = { Icon(Icons.Outlined.Image, contentDescription = "Add image") }
            )
            Button(
                onClick = {
                    onPost(text, imageUri)
                    imageUri = null
                    text = ""
                },
                enabled = text.isNotBlank() || imageUri != null,
                content = { Text("Post") },
            )
        }
        HorizontalDivider(Modifier.fillMaxWidth())
    }
}
