package dev.anilbeesetti.nextplayer.feature.player.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.media3.common.Player
import dev.anilbeesetti.nextplayer.core.model.VideoContentScale
import dev.anilbeesetti.nextplayer.feature.player.extensions.noRippleClickable
import dev.anilbeesetti.nextplayer.feature.player.state.SubtitleOptionsEvent

@Composable
fun BoxScope.OverlayShowView(
    player: Player,
    overlayView: OverlayView?,
    videoContentScale: VideoContentScale,
    onDismiss: () -> Unit = {},
    onSelectSubtitleClick: () -> Unit = {},
    onSubtitleSettingsClick: () -> Unit = {},
    onSubtitleOptionEvent: (SubtitleOptionsEvent) -> Unit = {},
    onVideoContentScaleChanged: (VideoContentScale) -> Unit = {},
    subtitleTextSize: Int = 20,
    subtitleTextColor: String = "#FFFFFF",
    subtitleBackgroundColor: String = "#80000000",
    onSubtitleTextSizeChange: (Int) -> Unit = {},
    onSubtitleTextColorChange: (String) -> Unit = {},
    onSubtitleBackgroundColorChange: (String) -> Unit = {},
    equalizerManager: dev.anilbeesetti.nextplayer.feature.player.audio.AudioEqualizerManager = androidx.compose.runtime.remember { dev.anilbeesetti.nextplayer.feature.player.audio.AudioEqualizerManager() },
    currentAudioSyncOffsetMs: Long = 0L,
    onAudioSyncOffsetChange: (Long) -> Unit = {},
    videoTitle: String = "",
    onOnlineSubtitleSelected: (dev.anilbeesetti.nextplayer.feature.player.ui.OnlineSubtitleResult) -> Unit = {},
) {
    Box(
        modifier = Modifier
            .matchParentSize()
            .then(
                if (overlayView != null) {
                    Modifier.noRippleClickable(onClick = onDismiss)
                } else {
                    Modifier
                },
            ),
    )

    AudioTrackSelectorView(
        show = overlayView == OverlayView.AUDIO_SELECTOR,
        player = player,
        onDismiss = onDismiss,
    )

    SubtitleSelectorView(
        show = overlayView == OverlayView.SUBTITLE_SELECTOR,
        player = player,
        onSelectSubtitleClick = onSelectSubtitleClick,
        onSubtitleSettingsClick = onSubtitleSettingsClick,
        onEvent = onSubtitleOptionEvent,
        onDismiss = onDismiss,
    )

    SubtitleStyleCustomizerView(
        show = overlayView == OverlayView.SUBTITLE_STYLE_CUSTOMIZER,
        currentSize = subtitleTextSize,
        currentColorHex = subtitleTextColor,
        currentBgColorHex = subtitleBackgroundColor,
        onSizeChange = onSubtitleTextSizeChange,
        onColorChange = onSubtitleTextColorChange,
        onBgColorChange = onSubtitleBackgroundColorChange,
        onDismiss = onDismiss,
    )

    PlaybackSpeedSelectorView(
        show = overlayView == OverlayView.PLAYBACK_SPEED,
        player = player,
    )

    VideoContentScaleSelectorView(
        show = overlayView == OverlayView.VIDEO_CONTENT_SCALE,
        videoContentScale = videoContentScale,
        onVideoContentScaleChanged = onVideoContentScaleChanged,
        onDismiss = onDismiss,
    )

    PlaylistView(
        show = overlayView == OverlayView.PLAYLIST,
        player = player,
    )

    EqualizerDialogView(
        show = overlayView == OverlayView.EQUALIZER,
        equalizerManager = equalizerManager,
        onDismiss = onDismiss,
    )

    AudioSyncDialogView(
        show = overlayView == OverlayView.AUDIO_SYNC,
        currentOffsetMs = currentAudioSyncOffsetMs,
        onOffsetChange = onAudioSyncOffsetChange,
        onDismiss = onDismiss,
    )

    OnlineSubtitleDialogView(
        show = overlayView == OverlayView.ONLINE_SUBTITLES,
        videoTitle = videoTitle,
        onSubtitleSelected = onOnlineSubtitleSelected,
        onDismiss = onDismiss,
    )
}

val Configuration.isPortrait: Boolean
    get() = orientation == Configuration.ORIENTATION_PORTRAIT

enum class OverlayView {
    AUDIO_SELECTOR,
    SUBTITLE_SELECTOR,
    SUBTITLE_STYLE_CUSTOMIZER,
    PLAYBACK_SPEED,
    VIDEO_CONTENT_SCALE,
    PLAYLIST,
    EQUALIZER,
    AUDIO_SYNC,
    ONLINE_SUBTITLES,
}
