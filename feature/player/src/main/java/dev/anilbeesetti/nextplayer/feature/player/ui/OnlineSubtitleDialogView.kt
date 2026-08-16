package dev.anilbeesetti.nextplayer.feature.player.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class OnlineSubtitleResult(
    val id: String,
    val title: String,
    val language: String,
    val downloadUrl: String,
)

import androidx.compose.foundation.layout.BoxScope

@Composable
fun BoxScope.OnlineSubtitleDialogView(
    show: Boolean,
    videoTitle: String,
    onSubtitleSelected: (OnlineSubtitleResult) -> Unit,
    onDismiss: () -> Unit,
) {
    if (!show) return

    var searchQuery by remember(videoTitle) { mutableStateOf(videoTitle.substringBeforeLast(".")) }
    var isLoading by remember { mutableStateOf(false) }
    var searchResults by remember { mutableStateOf<List<OnlineSubtitleResult>>(emptyList()) }

    OverlayView(
        show = show,
        title = "Online Subtitle Downloader",
        onDismiss = onDismiss,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Movie / Episode Title") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                )
                Button(
                    onClick = {
                        isLoading = true
                        // Simulated OpenSubtitles search result set
                        searchResults = listOf(
                            OnlineSubtitleResult("1", "$searchQuery (English - Official SRT)", "English", "https://example.com/sub1.srt"),
                            OnlineSubtitleResult("2", "$searchQuery (Spanish - Latino SRT)", "Spanish", "https://example.com/sub2.srt"),
                            OnlineSubtitleResult("3", "$searchQuery (French - Multi-sub)", "French", "https://example.com/sub3.srt"),
                        )
                        isLoading = false
                    },
                ) {
                    Text("Search")
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            } else if (searchResults.isNotEmpty()) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    items(searchResults) { result ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF2A2A2A))
                                .clickable {
                                    onSubtitleSelected(result)
                                    onDismiss()
                                }
                                .padding(14.dp),
                        ) {
                            Column {
                                Text(text = result.title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.padding(2.dp))
                                Text(text = "Language: ${result.language}", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                            }
                        }
                    }
                }
            } else {
                Text(
                    text = "Search OpenSubtitles network for matching subtitle files.",
                    fontSize = 13.sp,
                    color = Color.Gray,
                )
            }
        }
    }
}
