package dev.anilbeesetti.nextplayer.feature.player.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import dev.anilbeesetti.nextplayer.feature.player.extensions.detectCustomHorizontalDragGestures
import dev.anilbeesetti.nextplayer.feature.player.extensions.detectCustomTapAndLongPressDragGestures
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

                    detectCustomTapAndLongPressDragGestures(
                        onTap = {
                            if (tapGestureState.isSpeedLocked) {
                                tapGestureState.unlockSpeed()
                                return@detectCustomTapAndLongPressDragGestures
                            }
                            if (tapGestureState.seekMillis != 0L) return@detectCustomTapAndLongPressDragGestures
                            controlsVisibilityState.toggleControlsVisibility()
                        },
                        onDoubleTap = { offset ->
                            if (controlsVisibilityState.controlsLocked) return@detectCustomTapAndLongPressDragGestures
                            tapGestureState.handleDoubleTap(offset = offset, size = size)
                        },
                        onLongPressStart = { offset ->
                            if (controlsVisibilityState.controlsLocked) return@detectCustomTapAndLongPressDragGestures
                            tapGestureState.handleLongPress(offset = offset)
                        },
                        onLongPressDrag = { dragAmountX ->
                            tapGestureState.handleLongPressDrag(dragAmountX)
                        },
                        onLongPressRelease = {
                            tapGestureState.handleOnLongPressRelease()
                        },
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
                            if (!tapGestureState.isLongPressGestureInAction) {
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
