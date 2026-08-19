package dev.anilbeesetti.nextplayer.feature.player.ui

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.input.pointer.PointerEventTimeoutCancellationException
import androidx.compose.ui.input.pointer.withTimeout
import dev.anilbeesetti.nextplayer.feature.player.extensions.detectCustomHorizontalDragGestures
import dev.anilbeesetti.nextplayer.feature.player.extensions.detectCustomTransformGestures
import dev.anilbeesetti.nextplayer.feature.player.extensions.detectCustomVerticalDragGestures
import dev.anilbeesetti.nextplayer.feature.player.state.ControlsVisibilityState
import dev.anilbeesetti.nextplayer.feature.player.state.PictureInPictureState
import dev.anilbeesetti.nextplayer.feature.player.state.SeekGestureState
import dev.anilbeesetti.nextplayer.feature.player.state.TapGestureState
import dev.anilbeesetti.nextplayer.feature.player.state.VideoZoomAndContentScaleState
import dev.anilbeesetti.nextplayer.feature.player.state.VolumeAndBrightnessGestureState

@Composable
fun PlayerGestures(
    modifier: Modifier = Modifier,
    controlsVisibilityState: ControlsVisibilityState,
    tapGestureState: TapGestureState,
    pictureInPictureState: PictureInPictureState,
    seekGestureState: SeekGestureState,
    videoZoomAndContentScaleState: VideoZoomAndContentScaleState,
    volumeAndBrightnessGestureState: VolumeAndBrightnessGestureState,
) {
    BoxWithConstraints {
        Box(
            modifier = modifier
                .fillMaxSize()
                .pointerInput(
                    controlsVisibilityState.controlsLocked,
                    pictureInPictureState.isInPictureInPictureMode,
                ) {
                    if (pictureInPictureState.isInPictureInPictureMode) return@pointerInput

                    detectUnifiedPlayerGestures(
                        controlsLocked = controlsVisibilityState.controlsLocked,
                        onToggleControls = controlsVisibilityState::toggleControlsVisibility,
                        onDoubleTap = { offset, size ->
                            tapGestureState.handleDoubleTap(offset, size)
                        },
                        onLongPressStart = { offset ->
                            tapGestureState.handleLongPress(offset)
                        },
                        onLongPressDrag = { dragX ->
                            tapGestureState.handleLongPressDrag(dragX)
                        },
                        onLongPressRelease = {
                            tapGestureState.handleOnLongPressRelease()
                        },
                        onSpeedUnlock = {
                            tapGestureState.unlockSpeed()
                        },
                        isSpeedLocked = { tapGestureState.isSpeedLocked },
                        seekMillis = { tapGestureState.seekMillis },
                    )
                }
                .pointerInput(
                    controlsVisibilityState.controlsLocked,
                    pictureInPictureState.isInPictureInPictureMode,
                ) {
                    if (controlsVisibilityState.controlsLocked) return@pointerInput
                    if (pictureInPictureState.isInPictureInPictureMode) return@pointerInput

                    detectCustomHorizontalDragGestures(
                        onDragStart = { offset ->
                            if (!tapGestureState.isLongPressGestureInAction) {
                                seekGestureState.onDragStart(offset)
                            }
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            if (tapGestureState.isLongPressGestureInAction) {
                                tapGestureState.handleLongPressDrag(dragAmount)
                            } else {
                                seekGestureState.onDrag(change, dragAmount)
                            }
                        },
                        onDragCancel = {
                            if (!tapGestureState.isLongPressGestureInAction) {
                                seekGestureState.onDragEnd()
                            }
                        },
                        onDragEnd = {
                            if (!tapGestureState.isLongPressGestureInAction) {
                                seekGestureState.onDragEnd()
                            }
                        },
                    )
                }
                .pointerInput(
                    controlsVisibilityState.controlsLocked,
                    pictureInPictureState.isInPictureInPictureMode,
                ) {
                    if (controlsVisibilityState.controlsLocked) return@pointerInput
                    if (pictureInPictureState.isInPictureInPictureMode) return@pointerInput

                    detectCustomVerticalDragGestures(
                        onDragStart = { volumeAndBrightnessGestureState.onDragStart(it, size) },
                        onVerticalDrag = volumeAndBrightnessGestureState::onDrag,
                        onDragCancel = volumeAndBrightnessGestureState::onDragEnd,
                        onDragEnd = volumeAndBrightnessGestureState::onDragEnd,
                    )
                }
                .pointerInput(
                    controlsVisibilityState.controlsLocked,
                    pictureInPictureState.isInPictureInPictureMode,
                ) {
                    if (controlsVisibilityState.controlsLocked) return@pointerInput
                    if (pictureInPictureState.isInPictureInPictureMode) return@pointerInput

                    detectCustomTransformGestures(
                        onGestureStart = {
                            videoZoomAndContentScaleState.onPinchStart()
                        },
                        onGesture = { _, _, zoomChange, _ ->
                            if (tapGestureState.isLongPressGestureInAction) return@detectCustomTransformGestures
                            if (zoomChange != 1f) {
                                videoZoomAndContentScaleState.onPinchGesture(zoomChange)
                            }
                        },
                        onGestureEnd = {
                            videoZoomAndContentScaleState.onZoomPanGestureEnd()
                        },
                    )
                },
        )
    }
}

private suspend fun PointerInputScope.detectUnifiedPlayerGestures(
    controlsLocked: Boolean,
    onToggleControls: () -> Unit,
    onDoubleTap: (Offset, IntSize) -> Unit,
    onLongPressStart: (Offset) -> Unit,
    onLongPressDrag: (Float) -> Unit,
    onLongPressRelease: () -> Unit,
    onSpeedUnlock: () -> Unit,
    isSpeedLocked: () -> Boolean,
    seekMillis: () -> Long,
) {
    var lastTapTime = 0L
    var lastTapOffset = Offset.Zero

    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        val downOffset = down.position
        var isLongPress = false
        val longPressTimeout = viewConfiguration.longPressTimeoutMillis

        try {
            withTimeout(longPressTimeout) {
                while (true) {
                    val event = awaitPointerEvent()
                    val change = event.changes.firstOrNull { it.id == down.id } ?: break
                    if (!change.pressed) break
                    val distance = (change.position - downOffset).getDistance()
                    if (distance > viewConfiguration.touchSlop) {
                        break
                    }
                }
            }
        } catch (e: PointerEventTimeoutCancellationException) {
            if (!controlsLocked) {
                isLongPress = true
                onLongPressStart(downOffset)

                while (true) {
                    val event = awaitPointerEvent()
                    val change = event.changes.firstOrNull { it.id == down.id } ?: break
                    if (!change.pressed) break
                    val dragX = change.positionChange().x
                    if (dragX != 0f) {
                        onLongPressDrag(dragX)
                        change.consume()
                    }
                }

                onLongPressRelease()
            }
        }

        if (!isLongPress) {
            val event = awaitPointerEvent()
            val up = event.changes.firstOrNull { it.id == down.id }
            if (up != null && !up.pressed) {
                val now = up.uptimeMillis
                val isDoubleTap = (now - lastTapTime < viewConfiguration.doubleTapTimeoutMillis) &&
                        ((up.position - lastTapOffset).getDistance() < viewConfiguration.touchSlop * 2)

                if (isDoubleTap) {
                    if (!controlsLocked) {
                        onDoubleTap(up.position, size)
                    }
                    lastTapTime = 0L
                } else {
                    lastTapTime = now
                    lastTapOffset = up.position

                    if (isSpeedLocked()) {
                        onSpeedUnlock()
                    } else if (seekMillis() == 0L) {
                        onToggleControls()
                    }
                }
            }
        }
    }
}
