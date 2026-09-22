package dev.anilbeesetti.nextplayer.feature.videopicker.screens.mediapicker

import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import dev.anilbeesetti.nextplayer.core.common.storagePermission
import dev.anilbeesetti.nextplayer.core.media.services.MediaService
import dev.anilbeesetti.nextplayer.core.model.ApplicationPreferences
import dev.anilbeesetti.nextplayer.core.model.Folder
import dev.anilbeesetti.nextplayer.core.model.MediaLayoutMode
import dev.anilbeesetti.nextplayer.core.model.MediaViewMode
import dev.anilbeesetti.nextplayer.core.model.Video
import dev.anilbeesetti.nextplayer.core.ui.R
import dev.anilbeesetti.nextplayer.core.ui.base.DataState
import dev.anilbeesetti.nextplayer.core.ui.components.CancelButton
import dev.anilbeesetti.nextplayer.core.ui.components.DoneButton
import dev.anilbeesetti.nextplayer.core.ui.components.NextDialog
import dev.anilbeesetti.nextplayer.core.ui.composables.PermissionMissingView
import dev.anilbeesetti.nextplayer.core.ui.designsystem.NextIcons
import dev.anilbeesetti.nextplayer.core.ui.extensions.copy
import dev.anilbeesetti.nextplayer.feature.videopicker.composables.CenterCircularProgressBar
import dev.anilbeesetti.nextplayer.feature.videopicker.composables.MediaView
import dev.anilbeesetti.nextplayer.feature.videopicker.composables.NoVideosFound
import dev.anilbeesetti.nextplayer.feature.videopicker.composables.RenameDialog
import dev.anilbeesetti.nextplayer.feature.videopicker.composables.VideoInfoDialog
import dev.anilbeesetti.nextplayer.feature.videopicker.state.SelectedFolder
import dev.anilbeesetti.nextplayer.feature.videopicker.state.SelectedVideo
import dev.anilbeesetti.nextplayer.feature.videopicker.state.rememberSelectionManager
import java.util.Locale

@Composable
fun MediaPickerRoute(
    viewModel: MediaPickerViewModel = hiltViewModel(),
    onPlayVideo: (uri: Uri) -> Unit,
    onPlayVideos: (uris: List<Uri>) -> Unit,
    onFolderClick: (folderPath: String) -> Unit,
    onSettingsClick: () -> Unit,
    onSearchClick: () -> Unit,
    onNavigateUp: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MediaPickerScreen(
        uiState = uiState,
        onPlayVideo = onPlayVideo,
        onPlayVideos = onPlayVideos,
        onNavigateUp = onNavigateUp,
        onFolderClick = onFolderClick,
        onSettingsClick = onSettingsClick,
        onSearchClick = onSearchClick,
        onEvent = viewModel::onEvent,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class, ExperimentalPermissionsApi::class)
@Composable
internal fun MediaPickerScreen(
    uiState: MediaPickerUiState,
    onNavigateUp: () -> Unit = {},
    onPlayVideo: (Uri) -> Unit = {},
    onPlayVideos: (List<Uri>) -> Unit = {},
    onFolderClick: (String) -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onEvent: (MediaPickerUiEvent) -> Unit = {},
) {
    val selectionManager = rememberSelectionManager()
    val permissionState = rememberPermissionState(permission = storagePermission)
    val lazyGridState = rememberLazyGridState()

    var showUrlDialog by rememberSaveable { mutableStateOf(false) }
    var showRenameActionFor: Video? by rememberSaveable { mutableStateOf(null) }
    var showInfoActionFor: Video? by rememberSaveable { mutableStateOf(null) }
    var showDeleteVideosConfirmation by rememberSaveable { mutableStateOf(false) }

    // 4 Main Cyberpunk Cinema Navigation Tabs
    var activeTab by rememberSaveable { mutableStateOf("Cinema") }
    var selectedCategory by rememberSaveable { mutableStateOf("All") }
    val themeColor = Color(0xFFFF385C) // Signature Neon Coral / Ember Flame

    val selectedItemsSize = selectionManager.selectedFolders.size + selectionManager.selectedVideos.size
    val totalItemsSize = (uiState.mediaDataState as? DataState.Success)?.value?.run { folderList.size + mediaList.size } ?: 0

    Scaffold(
        bottomBar = {
            if (selectionManager.isInSelectionMode) {
                SelectionActionsSheet(
                    show = selectionManager.isInSelectionMode && selectionManager.allSelectedVideos.isNotEmpty(),
                    showRenameAction = selectionManager.isSingleVideoSelected,
                    showInfoAction = selectionManager.isSingleVideoSelected,
                    onPlayAction = {
                        val videoUris = selectionManager.allSelectedVideos.map { it.uriString.toUri() }
                        onPlayVideos(videoUris)
                        selectionManager.clearSelection()
                    },
                    onRenameAction = {
                        val selectedVideo = selectionManager.selectedVideos.firstOrNull() ?: return@SelectionActionsSheet
                        val video = (uiState.mediaDataState as? DataState.Success)?.value?.mediaList
                            ?.find { it.uriString == selectedVideo.uriString } ?: return@SelectionActionsSheet
                        showRenameActionFor = video
                    },
                    onInfoAction = {
                        val selectedVideo = selectionManager.selectedVideos.firstOrNull() ?: return@SelectionActionsSheet
                        val video = (uiState.mediaDataState as? DataState.Success)?.value?.mediaList
                            ?.find { it.uriString == selectedVideo.uriString } ?: return@SelectionActionsSheet
                        showInfoActionFor = video
                        selectionManager.clearSelection()
                    },
                    onShareAction = {
                        onEvent(MediaPickerUiEvent.ShareVideos(selectionManager.allSelectedVideos.map { it.uriString }))
                    },
                    onDeleteAction = {
                        if (MediaService.willSystemAsksForDeleteConfirmation()) {
                            onEvent(MediaPickerUiEvent.DeleteVideos(selectionManager.allSelectedVideos.map { it.uriString }))
                            selectionManager.clearSelection()
                        } else {
                            showDeleteVideosConfirmation = true
                        }
                    },
                )
            } else {
                EmberCinemaBottomNav(
                    selectedTab = activeTab,
                    onTabSelected = { activeTab = it },
                    themeColor = themeColor
                )
            }
        },
        containerColor = Color(0xFF08090D),
    ) { scaffoldPadding ->
        val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF140B10),
                            Color(0xFF08090D),
                            Color(0xFF06070A)
                        )
                    )
                )
        ) {
            when (activeTab) {
                "Home" -> {
                    EmberHomeDiscoveryView(
                        paddingValues = scaffoldPadding,
                        onPlayVideo = onPlayVideo,
                        onSearchClick = onSearchClick
                    )
                }

                "Cinema" -> {
                    EmberCinemaHubView(
                        uiState = uiState,
                        paddingValues = scaffoldPadding,
                        onPlayVideo = onPlayVideo,
                        onFolderClick = onFolderClick,
                        onOpenUrl = { showUrlDialog = true }
                    )
                }

                "Enhance" -> {
                    AuraStudioEnhanceView(
                        paddingValues = scaffoldPadding,
                        onApply = {}
                    )
                }

                "Library" -> {
                    Box(modifier = Modifier.padding(top = statusBarPadding + 64.dp, bottom = scaffoldPadding.calculateBottomPadding())) {
                        when (uiState.mediaDataState) {
                            is DataState.Error -> {}
                            is DataState.Loading -> {
                                CenterCircularProgressBar()
                            }
                            is DataState.Success -> {
                                val rootFolder = uiState.mediaDataState.value
                                if (rootFolder == null || (rootFolder.folderList.isEmpty() && rootFolder.mediaList.isEmpty())) {
                                    NoVideosFound(contentPadding = PaddingValues(top = 80.dp, bottom = 80.dp))
                                } else {
                                    PermissionMissingView(
                                        isGranted = permissionState.status.isGranted,
                                        showRationale = permissionState.status.shouldShowRationale,
                                        permission = permissionState.permission,
                                        launchPermissionRequest = { permissionState.launchPermissionRequest() },
                                    ) {
                                        MediaView(
                                            rootFolder = rootFolder,
                                            preferences = uiState.preferences,
                                            selectedCategory = selectedCategory,
                                            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
                                            selectionManager = selectionManager,
                                            lazyGridState = lazyGridState,
                                            onFolderClick = onFolderClick,
                                            onVideoClick = { onPlayVideo(it) },
                                            onVideoLoaded = { onEvent(MediaPickerUiEvent.AddToSync(it)) },
                                            onRenameClick = { showRenameActionFor = it },
                                            onShareClick = { onEvent(MediaPickerUiEvent.ShareVideos(listOf(it.uriString))) },
                                            onInfoClick = { showInfoActionFor = it },
                                            onDeleteClick = {
                                                if (MediaService.willSystemAsksForDeleteConfirmation()) {
                                                    onEvent(MediaPickerUiEvent.DeleteVideos(listOf(it.uriString)))
                                                } else {
                                                    selectionManager.selectVideo(it)
                                                    showDeleteVideosConfirmation = true
                                                }
                                            },
                                            themeColor = themeColor
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Top Bar for Library tab
                    DarkTopAppBar(
                        title = uiState.folderName ?: "Library",
                        showBackButton = uiState.folderName != null,
                        onNavigateUp = onNavigateUp,
                        onMusicToggle = { activeTab = "Home" },
                        onLayoutToggle = {
                            val currentMode = uiState.preferences.mediaLayoutMode
                            onEvent(
                                MediaPickerUiEvent.UpdateMenu(
                                    uiState.preferences.copy(
                                        mediaLayoutMode = if (currentMode == MediaLayoutMode.LIST) MediaLayoutMode.GRID else MediaLayoutMode.LIST
                                    )
                                )
                            )
                        },
                        onSearchClick = onSearchClick,
                        isInSelectionMode = selectionManager.isInSelectionMode,
                        selectedCount = selectedItemsSize,
                        totalCount = totalItemsSize,
                        onClearSelection = { selectionManager.exitSelectionMode() },
                        onSelectAllToggle = {
                            if (selectedItemsSize != totalItemsSize) {
                                (uiState.mediaDataState as? DataState.Success)?.value?.let { folder ->
                                    folder.folderList.forEach { selectionManager.selectFolder(it) }
                                    folder.mediaList.forEach { selectionManager.selectVideo(it) }
                                }
                            } else {
                                selectionManager.clearSelection()
                            }
                        },
                        isAllSelected = selectedItemsSize == totalItemsSize
                    )
                }
            }
        }
    }

    if (showUrlDialog) {
        NetworkUrlDialog(
            onDismiss = { showUrlDialog = false },
            onDone = { onPlayVideo(it.toUri()) },
        )
    }

    showRenameActionFor?.let { video ->
        RenameDialog(
            name = video.displayName,
            onDismiss = { showRenameActionFor = null },
            onDone = {
                onEvent(MediaPickerUiEvent.RenameVideo(video.uriString.toUri(), it))
                showRenameActionFor = null
                selectionManager.clearSelection()
            },
        )
    }

    showInfoActionFor?.let { video ->
        VideoInfoDialog(
            video = video,
            onDismiss = { showInfoActionFor = null },
        )
    }

    if (showDeleteVideosConfirmation) {
        DeleteConfirmationDialog(
            selectedVideos = selectionManager.selectedVideos,
            selectedFolders = selectionManager.selectedFolders,
            onConfirm = {
                onEvent(MediaPickerUiEvent.DeleteVideos(selectionManager.allSelectedVideos.map { it.uriString }))
                selectionManager.clearSelection()
                showDeleteVideosConfirmation = false
            },
            onCancel = { showDeleteVideosConfirmation = false },
        )
    }
}

// -------------------------------------------------------------------------------------------------
// TAB 1: EMBER / AURA HOME DISCOVERY VIEW (Image 2)
// -------------------------------------------------------------------------------------------------
@Composable
fun EmberHomeDiscoveryView(
    paddingValues: PaddingValues,
    onPlayVideo: (Uri) -> Unit,
    onSearchClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    var selectedMood by remember { mutableStateOf("Cyberpunk Chill") }
    val moods = listOf("Cyberpunk Chill", "Deep Focus Beats", "Spatial Cinema", "Anime & 4K")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .statusBarsPadding()
            .padding(bottom = paddingValues.calculateBottomPadding() + 80.dp)
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1E1420))
                    .border(1.dp, Color(0xFFFF385C), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = null,
                    tint = Color(0xFFFF385C),
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Ember",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
                    )
                    Text(
                        text = "HOME",
                        color = Color(0xFFFF385C),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = onSearchClick) {
                Icon(imageVector = Icons.Filled.Search, contentDescription = "Search", tint = Color.White)
            }
            IconButton(onClick = {}) {
                Box {
                    Icon(imageVector = Icons.Filled.Notifications, contentDescription = "Notifications", tint = Color.White)
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFF385C))
                            .align(Alignment.TopEnd)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF262B3D))
                    .border(1.dp, Color(0xFF06B6D4), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("👤", fontSize = 16.sp)
            }
        }

        // Mood Filter Pills
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(moods) { mood ->
                val isSelected = selectedMood == mood
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) Color(0x33FF385C) else Color(0xFF141722))
                        .border(
                            width = 1.dp,
                            color = if (isSelected) Color(0xFFFF385C) else Color(0x33262B3D),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable { selectedMood = mood }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = if (isSelected) "● $mood" else mood,
                        color = if (isSelected) Color.White else Color(0xFF9CA3AF),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Hero Premiere Card (Image 2)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF1B182B),
                            Color(0xFF111422),
                            Color(0xFF1A121A)
                        )
                    )
                )
                .border(1.dp, Color(0x3306B6D4), RoundedCornerShape(24.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0x3306B6D4))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text("✦ AI Enhanced", color = Color(0xFF06B6D4), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0x22FFFFFF))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text("4K HDR", color = Color.White, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0x33FF385C))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text("Spatial 3D", color = Color(0xFFFF385C), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Big Center Glowing Play Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(Color(0xFFFF385C), Color(0xFFEF4444), Color(0xFF06B6D4))
                                )
                            )
                            .border(2.dp, Color.White.copy(alpha = 0.8f), CircleShape)
                            .clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("● FEATURED PREMIERE", color = Color(0xFFFF385C), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    Text("4:18 duration", color = Color(0xFF9CA3AF), style = MaterialTheme.typography.labelSmall)
                }

                Text(
                    text = "Cyber City Nocturne",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
                )
                Text(
                    text = "4K Remastered • Spatial 3D Audio Synthesis • Kaele...",
                    color = Color(0xFF9CA3AF),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color(0xFFFF385C), Color(0xFF06B6D4))
                                )
                            )
                            .clickable { }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            Text("Play Now", color = Color.White, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF161826))
                            .border(1.dp, Color(0x33262B3D), RoundedCornerShape(12.dp))
                            .clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Filled.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }

                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF161826))
                            .border(1.dp, Color(0x33262B3D), RoundedCornerShape(12.dp))
                            .clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Filled.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Trending Remasters
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_enhance_magic),
                    contentDescription = null,
                    tint = Color(0xFF06B6D4),
                    modifier = Modifier.size(18.dp)
                )
                Text("Trending Remasters", color = Color.White, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }
            Text("See All →", color = Color(0xFFFF385C), style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                TrendingRemasterCard(title = "Neon Cathedral Live", subtitle = "Vocal Clarity v3.2 • 60 FPS", duration = "3:45", badge = "1080p → 4K HDR")
            }
            item {
                TrendingRemasterCard(title = "Ether Resonance", subtitle = "Neural Stem Split • Atmos", duration = "4:20", badge = "✦ Vocal Isolate")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Aura Recommended
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("▦ Aura Recommended", color = Color.White, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Text("Synced with mood", color = Color(0xFF9CA3AF), style = MaterialTheme.typography.labelSmall)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                RecommendedMediaCard(title = "Subway Lumina", artist = "Mira & The Wave", duration = "3:24", views = "142k", match = "99%", badge = "4K")
                RecommendedMediaCard(title = "Pulse Synthesis Lab", artist = "Nexus One", duration = "4:56", views = "230k", match = "94%", badge = "3D Audio")
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                RecommendedMediaCard(title = "Liquid Horizons", artist = "Orbit Echoes", duration = "6:40", views = "88k", match = "96%", badge = "HQ")
                RecommendedMediaCard(title = "Midnight Rooftops", artist = "Sora Low-Fi", duration = "2:58", views = "67k", match = "91%", badge = "Lossless")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Neural Audio Engine Latency Pill
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF141722))
                .border(1.dp, Color(0x33FF385C), RoundedCornerShape(16.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("⚙", fontSize = 16.sp)
                    Column {
                        Text("Neural Audio Engine", color = Color.White, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                        Text("Active Remaster Latency: 4ms", color = Color(0xFF9CA3AF), style = MaterialTheme.typography.labelSmall)
                    }
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0x33FF385C))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("● Ultra HD", color = Color(0xFFFF385C), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Floating Mini Player Bar
        EmberMiniPlayerBar(title = "Cyber Resonance • Aura Mix", subtitle = "Spatial AI Remaster")
    }
}

@Composable
fun TrendingRemasterCard(title: String, subtitle: String, duration: String, badge: String) {
    Box(
        modifier = Modifier
            .width(220.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF141722))
            .border(1.dp, Color(0x33262B3D), RoundedCornerShape(16.dp))
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF1F1230), Color(0xFF0F1E2E))
                        )
                    )
                    .padding(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                        .align(Alignment.TopStart)
                ) {
                    Text(badge, color = Color(0xFF06B6D4), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black.copy(alpha = 0.7f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                        .align(Alignment.BottomEnd)
                ) {
                    Text(duration, color = Color.White, style = MaterialTheme.typography.labelSmall)
                }
            }
            Column(modifier = Modifier.padding(10.dp)) {
                Text(title, color = Color.White, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), maxLines = 1)
                Text(subtitle, color = Color(0xFF9CA3AF), style = MaterialTheme.typography.labelSmall, maxLines = 1)
            }
        }
    }
}

@Composable
fun RecommendedMediaCard(title: String, artist: String, duration: String, views: String, match: String, badge: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF141722))
            .border(1.dp, Color(0x33262B3D), RoundedCornerShape(16.dp))
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF26182E), Color(0xFF0E121E))
                        )
                    )
                    .padding(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                        .align(Alignment.BottomEnd)
                ) {
                    Text(duration, color = Color.White, style = MaterialTheme.typography.labelSmall)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0x3306B6D4))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                        .align(Alignment.BottomStart)
                ) {
                    Text(badge, color = Color(0xFF06B6D4), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }
            }
            Column(modifier = Modifier.padding(10.dp)) {
                Text(title, color = Color.White, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), maxLines = 1)
                Text(artist, color = Color(0xFF9CA3AF), style = MaterialTheme.typography.labelSmall, maxLines = 1)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("⚡ $views", color = Color(0xFF06B6D4), style = MaterialTheme.typography.labelSmall)
                    Text("$match Match", color = Color(0xFFFF385C), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// TAB 2: EMBER CINEMA HUB VIEW (Image 4)
// -------------------------------------------------------------------------------------------------
@Composable
fun EmberCinemaHubView(
    uiState: MediaPickerUiState,
    paddingValues: PaddingValues,
    onPlayVideo: (Uri) -> Unit,
    onFolderClick: (String) -> Unit,
    onOpenUrl: () -> Unit
) {
    val scrollState = rememberScrollState()
    var activeFilter by remember { mutableStateOf("All Media") }
    val filters = listOf("All Media", "Internal Storage", "SD Card")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .statusBarsPadding()
            .padding(bottom = paddingValues.calculateBottomPadding() + 80.dp)
    ) {
        // Cinema Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1E1420))
                    .border(1.dp, Color(0xFFFF385C), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = null,
                    tint = Color(0xFFFF385C),
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Ember", color = Color.White, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFFF385C))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text("CINEMA", color = Color.White, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp))
                    }
                }
                Text("Now Playing", color = Color(0xFF9CA3AF), style = MaterialTheme.typography.labelSmall)
            }

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF262B3D))
                    .border(1.dp, Color(0xFF06B6D4), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("👤", fontSize = 16.sp)
            }
        }

        // Hardware Engine Active Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF385C))
                )
                Text("HARDWARE ENGINE ACTIVE", color = Color.White, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp))
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF141722))
                    .border(1.dp, Color(0x3306B6D4), RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text("⚙ Vulkan 1.3", color = Color(0xFF06B6D4), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Quick Resume Hero Card (Image 4)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF201625), Color(0xFF0F1422), Color(0xFF141824))
                    )
                )
                .border(1.dp, Color(0x44FF385C), RoundedCornerShape(24.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0x33FF385C))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text("4K HDR • 60 FPS", color = Color(0xFFFF385C), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0x3306B6D4))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text("Dolby Atmos", color = Color(0xFF06B6D4), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                    Text("48m left (72%)", color = Color(0xFF9CA3AF), style = MaterialTheme.typography.labelSmall)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFF385C))
                            .border(2.dp, Color.White.copy(alpha = 0.8f), CircleShape)
                            .clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text("QUICK RESUME", color = Color(0xFFFF385C), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp))
                Text("Cyber City: Tokyo 2099", color = Color.White, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold))
                Text("Director's Cut • Chapter 8: Neural ...", color = Color(0xFF9CA3AF), style = MaterialTheme.typography.bodySmall)

                Spacer(modifier = Modifier.height(10.dp))

                // Dual-Gradient Progress Scrubber Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFF262B3D))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.72f)
                            .fillMaxHeight()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF06B6D4), Color(0xFFFF385C))
                                )
                            )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("💬", fontSize = 14.sp)
                        Text("English [CC] & DTS-HD Master", color = Color(0xFF9CA3AF), style = MaterialTheme.typography.labelSmall)
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1A1F30))
                            .border(1.dp, Color(0x3306B6D4), RoundedCornerShape(12.dp))
                            .clickable { }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text("Resume ⏩", color = Color.White, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Filter Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filters.forEach { filter ->
                val isSelected = activeFilter == filter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) Color(0xFFFF385C) else Color(0xFF141722))
                        .border(1.dp, if (isSelected) Color(0xFFFF385C) else Color(0x33262B3D), RoundedCornerShape(20.dp))
                        .clickable { activeFilter = filter }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = filter,
                        color = if (isSelected) Color.White else Color(0xFF9CA3AF),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Device Storage Summary
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Device Storage", color = Color.White, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Text("Internal 128GB • Scanned 5m ago", color = Color(0xFF9CA3AF), style = MaterialTheme.typography.labelSmall)
            }
            Row(
                modifier = Modifier.clickable { },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("Refresh", color = Color(0xFF06B6D4), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                Text("🔄", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2x2 Storage Grid (Downloads, Camera & Clips, Anime & Series, 4K HDR Demos)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StorageCategoryCard(
                modifier = Modifier.weight(1f),
                icon = "📥",
                title = "Downloads",
                size = "42.8 GB",
                files = "14 video files",
                accentColor = Color(0xFFFF385C)
            )
            StorageCategoryCard(
                modifier = Modifier.weight(1f),
                icon = "📷",
                title = "Camera & Clips",
                size = "18.2 GB",
                files = "38 recordings",
                accentColor = Color(0xFF06B6D4)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StorageCategoryCard(
                modifier = Modifier.weight(1f),
                icon = "🎬",
                title = "Anime & Series",
                size = "64.1 GB",
                files = "26 episodes",
                accentColor = Color(0xFFFF7A00)
            )
            StorageCategoryCard(
                modifier = Modifier.weight(1f),
                icon = "🎞️",
                title = "4K HDR Demos",
                size = "24.5 GB",
                files = "6 master files",
                accentColor = Color(0xFF00E5FF)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Ember Neural Core Upscale Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF1A162B), Color(0xFF101B2B))
                    )
                )
                .border(1.dp, Color(0x33FF385C), RoundedCornerShape(18.dp))
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0x33FF385C)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✨", fontSize = 18.sp)
                    }
                    Column {
                        Text("EMBER NEURAL CORE", color = Color(0xFF06B6D4), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        Text("AI Super-Res Upscaler", color = Color.White, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                        Text("Convert 1080p SDR to 4K HDR...", color = Color(0xFF9CA3AF), style = MaterialTheme.typography.labelSmall)
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFF385C))
                        .clickable { }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("Upscale", color = Color.White, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Ember Cinematic Showcase List
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Ember Cinematic Showcase", color = Color.White, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Text("Mastered for high refresh rate OLED displays", color = Color(0xFF9CA3AF), style = MaterialTheme.typography.labelSmall)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0x3306B6D4))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("HDR+", color = Color(0xFF06B6D4), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            CinematicShowcaseItem(
                title = "The Orbital Horizon",
                studio = "AstroCinema Labs • 4K HDR",
                duration = "02:14:10",
                badge1 = "120 FPS MEMC",
                badge2 = "4K HEVC"
            )
            Spacer(modifier = Modifier.height(10.dp))
            CinematicShowcaseItem(
                title = "Bioluminescence Abyss",
                studio = "DeepOcean Ultra Studios...",
                duration = "44:18",
                badge1 = "HDR 1400 Nits",
                badge2 = "Dolby Vision"
            )
            Spacer(modifier = Modifier.height(10.dp))
            CinematicShowcaseItem(
                title = "Synthetica: Live Stadium Tour",
                studio = "World Arena Tour Master",
                duration = "01:45:00",
                badge1 = "Spatial 360° Audio",
                badge2 = "FLAC 96kHz"
            )
        }
    }
}

@Composable
fun StorageCategoryCard(
    modifier: Modifier = Modifier,
    icon: String,
    title: String,
    size: String,
    files: String,
    accentColor: Color
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF141722))
            .border(1.dp, Color(0x33262B3D), RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(icon, fontSize = 18.sp)
                Text(size, color = accentColor, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(title, color = Color.White, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), maxLines = 1)
            Text(files, color = Color(0xFF9CA3AF), style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun CinematicShowcaseItem(
    title: String,
    studio: String,
    duration: String,
    badge1: String,
    badge2: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF141722))
            .border(1.dp, Color(0x33262B3D), RoundedCornerShape(16.dp))
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(width = 90.dp, height = 64.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF261230), Color(0xFF0C1E30))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.Black.copy(alpha = 0.7f))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                        .align(Alignment.BottomEnd)
                ) {
                    Text(duration, color = Color.White, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp))
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(badge1, color = Color(0xFF06B6D4), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp))
                    Text(badge2, color = Color(0xFF9CA3AF), style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp))
                }
                Text(title, color = Color.White, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), maxLines = 1)
                Text(studio, color = Color(0xFF9CA3AF), style = MaterialTheme.typography.labelSmall, maxLines = 1)
            }

            IconButton(onClick = {}) {
                Text("⋮", color = Color.White, fontSize = 20.sp)
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// TAB 3: AURA / EMBER STUDIO ENHANCE VIEW (Image 3)
// -------------------------------------------------------------------------------------------------
@Composable
fun AuraStudioEnhanceView(
    paddingValues: PaddingValues,
    onApply: () -> Unit
) {
    val scrollState = rememberScrollState()

    var activeTab by remember { mutableStateOf("Color LUTs") }
    var selectedLut by remember { mutableStateOf("Cyberpunk 2099") }
    val luts = listOf("Cyberpunk 2099", "Neon Dream", "Vintage VHS 90s", "Obsidian Noir")

    var vibranceSlider by remember { mutableFloatStateOf(0.72f) }
    var hdrSlider by remember { mutableFloatStateOf(0.88f) }
    var grainSlider by remember { mutableFloatStateOf(0.35f) }

    var eq60Hz by remember { mutableFloatStateOf(8f) }
    var eq250Hz by remember { mutableFloatStateOf(4f) }
    var eq1kHz by remember { mutableFloatStateOf(0f) }
    var eq4kHz by remember { mutableFloatStateOf(3f) }
    var eq16kHz by remember { mutableFloatStateOf(6f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .statusBarsPadding()
            .padding(bottom = paddingValues.calculateBottomPadding() + 80.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("● AURA PRO CORE V4.2", color = Color(0xFF06B6D4), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                Text("Aura Studio Remaster", color = Color.White, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold))
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFF385C))
                    .clickable { }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text("📹 Export 4K HD", color = Color.White, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Split Screen Live Compare View
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(190.dp)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, Color(0x3306B6D4), RoundedCornerShape(20.dp))
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                // Raw Left Side
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Color(0xFF181A24))
                        .padding(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Black.copy(alpha = 0.6f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                            .align(Alignment.TopStart)
                    ) {
                        Text("RAW 10-BIT", color = Color(0xFF9CA3AF), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }
                    Text("● Slide to compare live grade", color = Color(0xFF06B6D4), style = MaterialTheme.typography.labelSmall, modifier = Modifier.align(Alignment.BottomStart))
                }

                // Enhanced Right Side
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF2E1236), Color(0xFF0C2438))
                            )
                        )
                        .padding(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFFF385C))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                            .align(Alignment.TopEnd)
                    ) {
                        Text("✦ AURA HDR", color = Color.White, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }
                    Text("120 FPS Real-time", color = Color.White, style = MaterialTheme.typography.labelSmall, modifier = Modifier.align(Alignment.BottomEnd))
                }
            }

            // Split Center Slider Thumb
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1E2436))
                    .border(2.dp, Color(0xFF06B6D4), CircleShape)
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                Text("⯈⯇", color = Color.White, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Studio Mode Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Color LUTs", "Sound FX & EQ", "AI Filters").forEach { tab ->
                val isSelected = activeTab == tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) Color(0x33FF385C) else Color(0xFF141722))
                        .border(1.dp, if (isSelected) Color(0xFFFF385C) else Color(0x33262B3D), RoundedCornerShape(14.dp))
                        .clickable { activeTab = tab }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab,
                        color = if (isSelected) Color.White else Color(0xFF9CA3AF),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Neural Cinematic LUTs Carousel
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Neural Cinematic LUTs", color = Color.White, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0x3306B6D4))
                        .padding(horizontal = 6.dp, vertical = 1.dp)
                ) {
                    Text("Active: $selectedLut", color = Color(0xFF06B6D4), style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold))
                }
            }
            Text("Reset", color = Color(0xFFFF385C), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(luts) { lut ->
                val isSelected = selectedLut == lut
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { selectedLut = lut }
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = if (isSelected) listOf(Color(0xFFFF385C), Color(0xFF1E1424)) else listOf(Color(0xFF262B3D), Color(0xFF12141D))
                                )
                            )
                            .border(2.dp, if (isSelected) Color(0xFFFF385C) else Color(0x33262B3D), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(imageVector = Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("🎬", fontSize = 20.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(lut, color = if (isSelected) Color(0xFFFF385C) else Color(0xFF9CA3AF), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Neural Grade Sliders Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF141722))
                .border(1.dp, Color(0x33262B3D), RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("▦ Neural Grade Sliders", color = Color.White, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1E2436))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text("✨ Auto Balance", color = Color(0xFF06B6D4), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }
                }

                // Slider 1: AI Color Grading Vibrance
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("AI Color Grading Vibrance", color = Color.White, style = MaterialTheme.typography.labelMedium)
                        Text("${(vibranceSlider * 100).toInt()}%", color = Color(0xFFFF385C), style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                    }
                    Slider(
                        value = vibranceSlider,
                        onValueChange = { vibranceSlider = it },
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFFFF385C),
                            activeTrackColor = Color(0xFFFF385C),
                            inactiveTrackColor = Color(0xFF262B3D)
                        )
                    )
                }

                // Slider 2: Dynamic Range (HDR)
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Dynamic Range (HDR)", color = Color.White, style = MaterialTheme.typography.labelMedium)
                        Text("${(hdrSlider * 100).toInt()}%", color = Color(0xFF06B6D4), style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                    }
                    Slider(
                        value = hdrSlider,
                        onValueChange = { hdrSlider = it },
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF06B6D4),
                            activeTrackColor = Color(0xFF06B6D4),
                            inactiveTrackColor = Color(0xFF262B3D)
                        )
                    )
                }

                // Slider 3: Film Grain & Glow Bloom
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Film Grain & Glow Bloom", color = Color.White, style = MaterialTheme.typography.labelMedium)
                        Text("${(grainSlider * 100).toInt()}%", color = Color(0xFFFF7A00), style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                    }
                    Slider(
                        value = grainSlider,
                        onValueChange = { grainSlider = it },
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFFFF7A00),
                            activeTrackColor = Color(0xFFFF7A00),
                            inactiveTrackColor = Color(0xFF262B3D)
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Spatial Audio EQ & Remix
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF141722))
                .border(1.dp, Color(0x33262B3D), RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("📊 Spatial Audio EQ & Remix", color = Color.White, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Text("Real-time dynamic bass boost", color = Color(0xFF9CA3AF), style = MaterialTheme.typography.labelSmall)
                    }
                    Text("🔄", fontSize = 16.sp)
                }

                // Equalizer Presets
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0x3306B6D4))
                            .border(1.dp, Color(0xFF06B6D4), RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text("⚡ Club Sub-Bass", color = Color(0xFF06B6D4), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF1A1E2D))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text("Vocal Crystal", color = Color(0xFF9CA3AF), style = MaterialTheme.typography.labelSmall)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF1A1E2D))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text("Surround", color = Color(0xFF9CA3AF), style = MaterialTheme.typography.labelSmall)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // 5-Band Equalizer Columns (60Hz, 250Hz, 1kHz, 4kHz, 16kHz)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    EqBarColumn(freq = "60Hz", value = "+8dB", heightFraction = 0.85f, color = Color(0xFF06B6D4))
                    EqBarColumn(freq = "250Hz", value = "+4dB", heightFraction = 0.65f, color = Color(0xFFFF385C))
                    EqBarColumn(freq = "1kHz", value = "0dB", heightFraction = 0.45f, color = Color(0xFF9CA3AF))
                    EqBarColumn(freq = "4kHz", value = "+3dB", heightFraction = 0.60f, color = Color(0xFFFF385C))
                    EqBarColumn(freq = "16kHz", value = "+6dB", heightFraction = 0.75f, color = Color(0xFF06B6D4))
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Big Apply Action Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFFFF385C), Color(0xFFEF4444), Color(0xFFDC2626))
                    )
                )
                .clickable { onApply() }
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(imageVector = Icons.Filled.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                Text("Apply & Save to Library", color = Color.White, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "⚙ Hardware Accelerated via Neural Engine • 0.4s latency",
            color = Color(0xFF06B6D4),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(14.dp))

        EmberMiniPlayerBar(title = "Cyber Resonance • Aura Mix", subtitle = "Spatial AI Remaster")
    }
}

@Composable
fun EqBarColumn(freq: String, value: String, heightFraction: Float, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(value, color = color, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp))
        Box(
            modifier = Modifier
                .width(14.dp)
                .height(80.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(Color(0xFF0A0C14))
                .padding(2.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(heightFraction)
                    .clip(RoundedCornerShape(5.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(color, color.copy(alpha = 0.5f))
                        )
                    )
            )
        }
        Text(freq, color = Color(0xFF9CA3AF), style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp))
    }
}

// -------------------------------------------------------------------------------------------------
// FLOATING MINI PLAYER BAR (Used across Home & Enhance)
// -------------------------------------------------------------------------------------------------
@Composable
fun EmberMiniPlayerBar(title: String, subtitle: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF141722))
            .border(1.dp, Color(0x44FF385C), RoundedCornerShape(18.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0x33FF385C)),
                contentAlignment = Alignment.Center
            ) {
                Text("🎵", fontSize = 16.sp)
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Color.White, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), maxLines = 1)
                Text(subtitle, color = Color(0xFFFF385C), style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), maxLines = 1)
            }

            IconButton(onClick = {}) {
                Icon(imageVector = Icons.Filled.KeyboardArrowRight, contentDescription = "Next", tint = Color.White)
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF385C))
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// BOTTOM NAVIGATION BAR (4 Tabs: Home, Cinema, Enhance, Library)
// -------------------------------------------------------------------------------------------------
@Composable
fun EmberCinemaBottomNav(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    themeColor: Color
) {
    Surface(
        color = Color(0xFF08090D).copy(alpha = 0.96f),
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = Color(0x33FF385C), shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val tabs = listOf(
                "Home" to R.drawable.ic_home_explore,
                "Cinema" to R.drawable.ic_cinema_film,
                "Enhance" to R.drawable.ic_enhance_magic,
                "Library" to R.drawable.ic_library_folder
            )

            tabs.forEach { (tabName, iconRes) ->
                val isSelected = selectedTab == tabName
                val activeColor = Color(0xFFFF385C)
                val inactiveColor = Color(0xFF7A8090)

                Column(
                    modifier = Modifier
                        .clickable { onTabSelected(tabName) }
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = tabName,
                        tint = if (isSelected) activeColor else inactiveColor,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = tabName,
                        color = if (isSelected) activeColor else inactiveColor,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    )
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(activeColor)
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// PRIVATE COMPOSABLES (shared / required by MediaPickerScreen)
// -------------------------------------------------------------------------------------------------

@Composable
private fun DarkTopAppBar(
    title: String,
    showBackButton: Boolean,
    onNavigateUp: () -> Unit,
    onMusicToggle: () -> Unit,
    onLayoutToggle: () -> Unit,
    onSearchClick: () -> Unit,
    isInSelectionMode: Boolean,
    selectedCount: Int,
    totalCount: Int,
    onClearSelection: () -> Unit,
    onSelectAllToggle: () -> Unit,
    isAllSelected: Boolean
) {
    Surface(
        color = Color(0xFF0C0C0C),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .height(64.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isInSelectionMode) {
                IconButton(onClick = onClearSelection) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$selectedCount / $totalCount Selected",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = onSelectAllToggle) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Select All Toggle",
                        tint = Color.White
                    )
                }
            } else {
                if (showBackButton) {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Text(
                    text = title,
                    color = Color.White,
                    fontFamily = FontFamily.SansSerif,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    ),
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = onMusicToggle) {
                    Icon(
                        imageVector = Icons.Filled.Home,
                        contentDescription = "Home",
                        tint = Color.White
                    )
                }

                IconButton(onClick = onLayoutToggle) {
                    Icon(
                        imageVector = Icons.Filled.List,
                        contentDescription = "Layout Toggle",
                        tint = Color.White
                    )
                }

                IconButton(onClick = onSearchClick) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun SelectionActionsSheet(
    modifier: Modifier = Modifier,
    show: Boolean,
    showRenameAction: Boolean,
    showInfoAction: Boolean,
    onPlayAction: () -> Unit,
    onRenameAction: () -> Unit,
    onShareAction: () -> Unit,
    onInfoAction: () -> Unit,
    onDeleteAction: () -> Unit,
) {
    AnimatedVisibility(
        modifier = modifier.padding(
            start = WindowInsets.displayCutout.asPaddingValues()
                .calculateStartPadding(LocalLayoutDirection.current),
        ),
        visible = show,
        enter = slideInVertically { it },
        exit = slideOutVertically { it },
    ) {
        val shape = MaterialTheme.shapes.large.copy(
            bottomStart = ZeroCornerSize,
            bottomEnd = ZeroCornerSize,
        )
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFF1E1E1E),
                        shape = shape,
                    )
                    .clip(shape)
                    .horizontalScroll(rememberScrollState())
                    .navigationBarsPadding()
                    .padding(
                        horizontal = 8.dp,
                        vertical = 12.dp,
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                SelectionAction(
                    imageVector = NextIcons.Play,
                    title = stringResource(R.string.play),
                    onClick = onPlayAction,
                )
                if (showRenameAction) {
                    SelectionAction(
                        imageVector = NextIcons.Edit,
                        title = stringResource(R.string.rename),
                        onClick = onRenameAction,
                    )
                }
                SelectionAction(
                    imageVector = NextIcons.Share,
                    title = stringResource(R.string.share),
                    onClick = onShareAction,
                )
                if (showInfoAction) {
                    SelectionAction(
                        imageVector = NextIcons.Info,
                        title = stringResource(id = R.string.info),
                        onClick = onInfoAction,
                    )
                }
                SelectionAction(
                    imageVector = NextIcons.Delete,
                    title = stringResource(id = R.string.delete),
                    onClick = onDeleteAction,
                )
            }
        }
    }
}

@Composable
private fun SelectionAction(
    imageVector: ImageVector,
    title: String,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .defaultMinSize(
                minWidth = 75.dp,
                minHeight = 64.dp,
            )
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp,
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = title,
            modifier = Modifier.size(24.dp),
            tint = Color.White,
        )
        Spacer(modifier = Modifier.size(4.dp))
        Text(
            text = title,
            modifier = Modifier,
            style = MaterialTheme.typography.labelLarge,
            color = Color.White,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun DeleteConfirmationDialog(
    modifier: Modifier = Modifier,
    selectedVideos: Set<SelectedVideo>,
    selectedFolders: Set<SelectedFolder>,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    NextDialog(
        onDismissRequest = onCancel,
        title = {
            Text(
                text = when {
                    selectedVideos.isEmpty() -> when (selectedFolders.size) {
                        1 -> stringResource(R.string.delete_one_folder)
                        else -> stringResource(R.string.delete_folders, selectedFolders.size)
                    }

                    selectedFolders.isEmpty() -> when (selectedVideos.size) {
                        1 -> stringResource(R.string.delete_one_video)
                        else -> stringResource(R.string.delete_videos, selectedVideos.size)
                    }

                    else -> stringResource(R.string.delete_items, selectedFolders.size + selectedVideos.size)
                },
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                modifier = modifier,
            ) {
                Text(text = stringResource(R.string.delete))
            }
        },
        dismissButton = { CancelButton(onClick = onCancel) },
        modifier = modifier,
        content = {
            Text(
                text = if ((selectedFolders.size + selectedVideos.size) == 1) {
                    stringResource(R.string.delete_item_info)
                } else {
                    stringResource(R.string.delete_items_info)
                },
                style = MaterialTheme.typography.titleSmall,
            )
        },
    )
}

@Composable
private fun NetworkUrlDialog(
    onDismiss: () -> Unit,
    onDone: (String) -> Unit,
) {
    var url by rememberSaveable { mutableStateOf("") }
    NextDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.network_stream)) },
        content = {
            Text(text = stringResource(R.string.enter_a_network_url))
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = stringResource(R.string.example_url)) },
            )
        },
        confirmButton = {
            DoneButton(
                enabled = url.isNotBlank(),
                onClick = { onDone(url) },
            )
        },
        dismissButton = { CancelButton(onClick = onDismiss) },
    )
}
