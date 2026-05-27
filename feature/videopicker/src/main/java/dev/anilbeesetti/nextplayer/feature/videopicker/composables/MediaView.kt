package dev.anilbeesetti.nextplayer.feature.videopicker.composables

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onFirstVisible
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import dev.anilbeesetti.nextplayer.core.model.ApplicationPreferences
import dev.anilbeesetti.nextplayer.core.model.Folder
import dev.anilbeesetti.nextplayer.core.model.MediaLayoutMode
import dev.anilbeesetti.nextplayer.core.model.MediaViewMode
import dev.anilbeesetti.nextplayer.core.model.Video
import dev.anilbeesetti.nextplayer.core.ui.R
import dev.anilbeesetti.nextplayer.core.ui.components.ListSectionTitle
import dev.anilbeesetti.nextplayer.core.ui.extensions.plus
import dev.anilbeesetti.nextplayer.feature.videopicker.state.SelectionManager
import dev.anilbeesetti.nextplayer.feature.videopicker.state.rememberSelectionManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, ExperimentalPermissionsApi::class)
@Composable
fun MediaView(
    rootFolder: Folder,
    preferences: ApplicationPreferences,
    selectedCategory: String = "All",
    showHeaders: Boolean = preferences.mediaViewMode == MediaViewMode.FOLDER_TREE,
    contentPadding: PaddingValues = PaddingValues(),
    selectionManager: SelectionManager = rememberSelectionManager(),
    lazyGridState: LazyGridState = rememberLazyGridState(),
    onFolderClick: (String) -> Unit,
    onVideoClick: (Uri) -> Unit,
    onVideoLoaded: (Uri) -> Unit,
    onRenameClick: (Video) -> Unit = {},
    onShareClick: (Video) -> Unit = {},
    onInfoClick: (Video) -> Unit = {},
    onDeleteClick: (Video) -> Unit = {},
    themeColor: Color = Color(0xFFFF5722)
) {
    val haptic = LocalHapticFeedback.current

    when (selectedCategory) {
        "Videos" -> {
            ChronologicalVideoList(
                rootFolder = rootFolder,
                preferences = preferences,
                selectionManager = selectionManager,
                onVideoClick = onVideoClick,
                onVideoLoaded = onVideoLoaded,
                onRenameClick = onRenameClick,
                onShareClick = onShareClick,
                onInfoClick = onInfoClick,
                onDeleteClick = onDeleteClick,
                contentPadding = contentPadding,
                themeColor = themeColor
            )
        }
        "Folders" -> {
            // Render folder format (grid/tree) but filter out video list
            val folderOnlyRoot = remember(rootFolder) {
                rootFolder.copy(mediaList = emptyList())
            }
            FolderGridOrTree(
                rootFolder = folderOnlyRoot,
                preferences = preferences,
                showHeaders = showHeaders,
                contentPadding = contentPadding,
                selectionManager = selectionManager,
                lazyGridState = lazyGridState,
                onFolderClick = onFolderClick,
                onVideoClick = onVideoClick,
                onVideoLoaded = onVideoLoaded,
                onRenameClick = onRenameClick,
                onShareClick = onShareClick,
                onInfoClick = onInfoClick,
                onDeleteClick = onDeleteClick,
                themeColor = themeColor
            )
        }
        else -> {
            // Default "All" view format: show both folders and videos using same format
            FolderGridOrTree(
                rootFolder = rootFolder,
                preferences = preferences,
                showHeaders = showHeaders,
                contentPadding = contentPadding,
                selectionManager = selectionManager,
                lazyGridState = lazyGridState,
                onFolderClick = onFolderClick,
                onVideoClick = onVideoClick,
                onVideoLoaded = onVideoLoaded,
                onRenameClick = onRenameClick,
                onShareClick = onShareClick,
                onInfoClick = onInfoClick,
                onDeleteClick = onDeleteClick,
                themeColor = themeColor
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChronologicalVideoList(
    rootFolder: Folder,
    preferences: ApplicationPreferences,
    selectionManager: SelectionManager,
    onVideoClick: (Uri) -> Unit,
    onVideoLoaded: (Uri) -> Unit,
    onRenameClick: (Video) -> Unit,
    onShareClick: (Video) -> Unit,
    onInfoClick: (Video) -> Unit,
    onDeleteClick: (Video) -> Unit,
    contentPadding: PaddingValues,
    themeColor: Color = Color(0xFFFF5722)
) {
    val haptic = LocalHapticFeedback.current

    val sortedVideos = remember(rootFolder.allMediaList) {
        rootFolder.allMediaList.sortedByDescending { it.dateModified }
    }

    val groupedVideos = remember(sortedVideos) {
        sortedVideos.groupBy { video ->
            getGroupHeader(video.dateModified)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0C0C0C)),
        contentPadding = contentPadding
    ) {
        groupedVideos.forEach { (dateHeader, videosInGroup) ->
            stickyHeader {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0C0C0C))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = dateHeader,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            items(
                items = videosInGroup,
                key = { it.uriString }
            ) { video ->
                val selected by remember { derivedStateOf { selectionManager.isVideoSelected(video) } }
                VideoItem(
                    video = video,
                    isRecentlyPlayedVideo = rootFolder.isRecentlyPlayedVideo(video),
                    preferences = preferences,
                    selected = selected,
                    onClick = {
                        if (selectionManager.isInSelectionMode) {
                            haptic.performHapticFeedback(HapticFeedbackType.VirtualKey)
                            selectionManager.toggleVideoSelection(video)
                        } else {
                            onVideoClick(video.uriString.toUri())
                        }
                    },
                    onLongClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        selectionManager.toggleVideoSelection(video)
                    },
                    onRenameClick = { onRenameClick(video) },
                    onShareClick = { onShareClick(video) },
                    onInfoClick = { onInfoClick(video) },
                    onDeleteClick = { onDeleteClick(video) },
                    themeColor = themeColor
                )
            }
        }
    }
}

@Composable
fun FolderGridOrTree(
    rootFolder: Folder,
    preferences: ApplicationPreferences,
    showHeaders: Boolean,
    contentPadding: PaddingValues,
    selectionManager: SelectionManager,
    lazyGridState: LazyGridState,
    onFolderClick: (String) -> Unit,
    onVideoClick: (Uri) -> Unit,
    onVideoLoaded: (Uri) -> Unit,
    onRenameClick: (Video) -> Unit,
    onShareClick: (Video) -> Unit,
    onInfoClick: (Video) -> Unit,
    onDeleteClick: (Video) -> Unit,
    themeColor: Color = Color(0xFFFF5722)
) {
    val haptic = LocalHapticFeedback.current

    val folderMinWidth = 90.dp
    val videoMinWidth = 130.dp
    BoxWithConstraints {
        val contentHorizontalPadding = when (preferences.mediaLayoutMode) {
            MediaLayoutMode.LIST -> 8.dp
            MediaLayoutMode.GRID -> 8.dp
        }
        val itemSpacing = when (preferences.mediaLayoutMode) {
            MediaLayoutMode.LIST -> 2.dp
            MediaLayoutMode.GRID -> 2.dp
        }
        val maxWidth = this.maxWidth - (contentHorizontalPadding * 2) - itemSpacing
        val maxFolders = (maxWidth / folderMinWidth).toInt()
        val maxVideos = (maxWidth / videoMinWidth).toInt()
        val spans = when (preferences.mediaLayoutMode) {
            MediaLayoutMode.LIST -> 1
            MediaLayoutMode.GRID -> lcm(maxFolders, maxVideos)
        }

        val singleFolderSpan = when (preferences.mediaLayoutMode) {
            MediaLayoutMode.LIST -> 1
            MediaLayoutMode.GRID -> spans / maxFolders
        }
        val singleVideoSpan = when (preferences.mediaLayoutMode) {
            MediaLayoutMode.LIST -> 1
            MediaLayoutMode.GRID -> spans / maxVideos
        }

        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0C0C0C)),
            state = lazyGridState,
            columns = GridCells.Fixed(spans),
            contentPadding = contentPadding + PaddingValues(horizontal = contentHorizontalPadding, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(itemSpacing),
            horizontalArrangement = Arrangement.spacedBy(itemSpacing),
        ) {
            if (showHeaders && rootFolder.folderList.isNotEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    ListSectionTitle(text = stringResource(id = R.string.folders) + " (${rootFolder.folderList.size})")
                }
            }
            itemsIndexed(
                items = rootFolder.folderList,
                key = { _, folder -> folder.path },
                span = { _, _ -> GridItemSpan(singleFolderSpan) },
            ) { index, folder ->
                val selected by remember { derivedStateOf { selectionManager.isFolderSelected(folder) } }
                FolderItem(
                    folder = folder,
                    isRecentlyPlayedFolder = rootFolder.isRecentlyPlayedVideo(folder.recentlyPlayedVideo),
                    preferences = preferences,
                    selected = selected,
                    isFirstItem = index == 0,
                    isLastItem = index == rootFolder.folderList.lastIndex,
                    onClick = {
                        if (selectionManager.isInSelectionMode) {
                            haptic.performHapticFeedback(HapticFeedbackType.VirtualKey)
                            selectionManager.toggleFolderSelection(folder)
                        } else {
                            onFolderClick(folder.path)
                        }
                    },
                    onLongClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        selectionManager.toggleFolderSelection(folder)
                    },
                )
            }

            if (preferences.mediaViewMode == MediaViewMode.FOLDER_TREE && rootFolder.folderList.isNotEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Spacer(modifier = Modifier.size(8.dp))
                }
            }

            if (showHeaders && rootFolder.mediaList.isNotEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    ListSectionTitle(text = stringResource(id = R.string.videos) + " (${rootFolder.mediaList.size})")
                }
            }

            itemsIndexed(
                items = rootFolder.mediaList,
                key = { _, video -> video.uriString },
                span = { _, _ -> GridItemSpan(singleVideoSpan) },
            ) { index, video ->
                val selected by remember { derivedStateOf { selectionManager.isVideoSelected(video) } }
                VideoItem(
                    video = video,
                    preferences = preferences,
                    isRecentlyPlayedVideo = rootFolder.isRecentlyPlayedVideo(video),
                    isFirstItem = index == 0,
                    isLastItem = index == rootFolder.mediaList.lastIndex,
                    selected = selected,
                    onClick = {
                        if (selectionManager.isInSelectionMode) {
                            haptic.performHapticFeedback(HapticFeedbackType.VirtualKey)
                            selectionManager.toggleVideoSelection(video)
                        } else {
                            onVideoClick(video.uriString.toUri())
                        }
                    },
                    onLongClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        selectionManager.toggleVideoSelection(video)
                    },
                    onRenameClick = { onRenameClick(video) },
                    onShareClick = { onShareClick(video) },
                    onInfoClick = { onInfoClick(video) },
                    onDeleteClick = { onDeleteClick(video) },
                    themeColor = themeColor
                )
            }
        }
    }
}

fun getGroupHeader(dateModified: Long): String {
    val timestamp = if (dateModified < 20000000000L) dateModified * 1000 else dateModified
    val date = Date(timestamp)

    val targetCal = Calendar.getInstance().apply { time = date }
    val currentCal = Calendar.getInstance()

    val targetYear = targetCal.get(Calendar.YEAR)
    val targetDayOfYear = targetCal.get(Calendar.DAY_OF_YEAR)

    val currentYear = currentCal.get(Calendar.YEAR)
    val currentDayOfYear = currentCal.get(Calendar.DAY_OF_YEAR)

    return if (targetYear == currentYear && targetDayOfYear == currentDayOfYear) {
        "Today"
    } else if (targetYear == currentYear && currentCal.get(Calendar.DAY_OF_YEAR) - targetDayOfYear == 1) {
        "Yesterday"
    } else {
        val sdf = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        sdf.format(date)
    }
}

fun lcm(a: Int, b: Int): Int {
    return abs(a * b) / gcd(a, b)
}

fun gcd(a: Int, b: Int): Int {
    return if (b == 0) a else gcd(b, a % b)
}
