package dev.anilbeesetti.nextplayer.feature.videopicker.composables

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import androidx.compose.ui.composed
import androidx.compose.animation.core.Animatable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.graphicsLayer
import dev.anilbeesetti.nextplayer.core.common.Utils
import kotlinx.coroutines.launch
import dev.anilbeesetti.nextplayer.core.model.ApplicationPreferences
import dev.anilbeesetti.nextplayer.core.model.MediaLayoutMode
import dev.anilbeesetti.nextplayer.core.model.Video
import dev.anilbeesetti.nextplayer.core.ui.designsystem.NextIcons

@Composable
fun VideoItem(
    video: Video,
    isRecentlyPlayedVideo: Boolean,
    preferences: ApplicationPreferences,
    modifier: Modifier = Modifier,
    isFirstItem: Boolean = false,
    isLastItem: Boolean = false,
    selected: Boolean = false,
    onClick: () -> Unit = {},
    onLongClick: (() -> Unit)? = null,
    onRenameClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onInfoClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    themeColor: Color = Color(0xFFFF5722)
) {
    VideoListItem(
        video = video,
        isRecentlyPlayedVideo = isRecentlyPlayedVideo,
        preferences = preferences,
        modifier = modifier,
        selected = selected,
        onClick = onClick,
        onLongClick = onLongClick,
        onRenameClick = onRenameClick,
        onShareClick = onShareClick,
        onInfoClick = onInfoClick,
        onDeleteClick = onDeleteClick,
        themeColor = themeColor
    )
}

@Composable
fun AnimatedEqualizer(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFFFF5722)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "equalizer")
    
    val heightScale1 by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar1"
    )
    val heightScale2 by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar2"
    )
    val heightScale3 by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 700, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar3"
    )
    
    Row(
        modifier = modifier
            .height(14.dp)
            .width(14.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        Box(modifier = Modifier.weight(1f).fillMaxHeight(heightScale1).background(color, CircleShape))
        Box(modifier = Modifier.weight(1f).fillMaxHeight(heightScale2).background(color, CircleShape))
        Box(modifier = Modifier.weight(1f).fillMaxHeight(heightScale3).background(color, CircleShape))
    }
}

@Composable
private fun VideoListItem(
    video: Video,
    isRecentlyPlayedVideo: Boolean,
    preferences: ApplicationPreferences,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: () -> Unit = {},
    onLongClick: (() -> Unit)? = null,
    onRenameClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onInfoClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    themeColor: Color = Color(0xFFFF5722)
) {
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }

    val isNew = remember(video.dateModified) {
        val time = if (video.dateModified < 20000000000L) video.dateModified * 1000 else video.dateModified
        (System.currentTimeMillis() - time) < 7 * 24 * 60 * 60 * 1000L
    }

    Row(
        modifier = modifier
            .animateItemEntry(preferences.appAnimations)
            .fillMaxWidth()
            .background(if (selected) themeColor.copy(alpha = 0.15f) else Color.Transparent)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Left Side: 16:9 Thumbnail Box
        Box(
            modifier = Modifier
                .width(130.dp)
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF1E1E1E))
                .border(
                    width = if (selected) 2.dp else 0.dp,
                    color = if (selected) themeColor else Color.Transparent,
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            Icon(
                imageVector = NextIcons.Video,
                contentDescription = null,
                tint = Color(0xFF333333),
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(32.dp)
            )

            // Video Thumbnail
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(video.uriString)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Bottom Gradient Shadow overlay for duration readability
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                        )
                    )
            )

            // Duration Badge (bottom-left)
            Text(
                text = video.formattedDuration,
                color = Color.White,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            )

            // Animated Equalizer (bottom-right) if active/recently played
            if (isRecentlyPlayedVideo) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    AnimatedEqualizer(color = themeColor)
                }
            }
        }

        // Right Side: Title & Metadata Column
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = Utils.sanitizeFileName(video.displayName),
                color = if (isRecentlyPlayedVideo) themeColor else Color.White,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = video.formattedFileSize,
                    color = Color(0xFFA0AAB5),
                    style = MaterialTheme.typography.bodyMedium
                )

                if (isNew) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFE53935), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "New",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }

        // Far Right: Three-dot dropdown menu
        Box {
            IconButton(onClick = { showMenu = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More Options",
                    tint = Color.White
                )
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                modifier = Modifier.background(Color(0xFF1E1E1E))
            ) {
                DropdownMenuItem(
                    text = { Text("Play", color = Color.White) },
                    onClick = {
                        showMenu = false
                        onClick()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Rename", color = Color.White) },
                    onClick = {
                        showMenu = false
                        onRenameClick()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Share", color = Color.White) },
                    onClick = {
                        showMenu = false
                        onShareClick()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Info", color = Color.White) },
                    onClick = {
                        showMenu = false
                        onInfoClick()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Delete", color = Color.White) },
                    onClick = {
                        showMenu = false
                        onDeleteClick()
                    }
                )
            }
        }
    }
}

fun Modifier.animateItemEntry(enabled: Boolean): Modifier = composed {
    if (!enabled) return@composed this

    val alpha = remember { Animatable(0f) }
    val yOffset = remember { Animatable(20f) } // Slide up by 20dp

    LaunchedEffect(Unit) {
        // Run both animations concurrently
        launch {
            alpha.animateTo(1f, animationSpec = tween(durationMillis = 350, easing = LinearEasing))
        }
        launch {
            yOffset.animateTo(0f, animationSpec = tween(durationMillis = 350, easing = LinearEasing))
        }
    }

    this.graphicsLayer {
        this.alpha = alpha.value
        this.translationY = yOffset.value * density
    }
}

