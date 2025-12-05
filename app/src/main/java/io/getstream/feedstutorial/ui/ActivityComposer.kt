package io.getstream.feedstutorial.ui

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ActivityComposer(onPost: (String, Uri?) -> Unit) {
    var text by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.fillMaxSize(),
            placeholder = { Text("What is happening?") },
        )

        Row(Modifier.align(Alignment.End), Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = {
                    onPost(text, null)
                    text = ""
                },
                enabled = text.isNotBlank(),
                content = { Text("Post") },
            )
        }
        HorizontalDivider(Modifier.fillMaxWidth())
    }
}
