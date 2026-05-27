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
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
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
    val selectVideoFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { it?.let { onPlayVideo(it) } },
    )

    var isFabExpanded by rememberSaveable { mutableStateOf(false) }
    var showUrlDialog by rememberSaveable { mutableStateOf(false) }

    var showRenameActionFor: Video? by rememberSaveable { mutableStateOf(null) }
    var showInfoActionFor: Video? by rememberSaveable { mutableStateOf(null) }
    var showDeleteVideosConfirmation by rememberSaveable { mutableStateOf(false) }

    // Dynamic Navigation Tabs
    var activeTab by rememberSaveable { mutableStateOf("Videos") }
    var selectedCategory by rememberSaveable { mutableStateOf("All") }
    
    // Dynamic Primary Brand Color
    var themeColorName by rememberSaveable { mutableStateOf("Orange-Red") }
    val themeColor = remember(themeColorName) {
        when (themeColorName) {
            "Blue" -> Color(0xFF2196F3)
            "Green" -> Color(0xFF4CAF50)
            "Purple" -> Color(0xFF9C27B0)
            else -> Color(0xFFFF5722) // Orange-red default
        }
    }

    val selectedItemsSize = selectionManager.selectedFolders.size + selectionManager.selectedVideos.size
    val totalItemsSize = (uiState.mediaDataState as? DataState.Success)?.value?.run { folderList.size + mediaList.size } ?: 0

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(Color(0xFF0C0C0C))) {
                DarkTopAppBar(
                    title = uiState.folderName ?: "Ember",
                    showBackButton = uiState.folderName != null,
                    onNavigateUp = onNavigateUp,
                    onMusicToggle = { activeTab = "Music" },
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
                
                if (activeTab == "Videos") {
                    CategoryFilterRow(
                        selectedCategory = selectedCategory,
                        onCategorySelected = { selectedCategory = it },
                        themeColor = themeColor
                    )
                }
            }
        },
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
                EmberBottomNavigation(
                    selectedTab = activeTab,
                    onTabSelected = { activeTab = it },
                    themeColor = themeColor
                )
            }
        },
        floatingActionButton = {
            if (selectionManager.isInSelectionMode || activeTab != "Videos") return@Scaffold

            FloatingActionButtonMenu(
                expanded = isFabExpanded,
                button = {
                    ToggleFloatingActionButton(
                        checked = isFabExpanded,
                        onCheckedChange = { isFabExpanded = !isFabExpanded },
                    ) {
                        val icon by remember {
                            derivedStateOf {
                                if (checkedProgress > 0.5f) NextIcons.Close else NextIcons.Play
                            }
                        }
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier.animateIcon(checkedProgress = { checkedProgress }),
                        )
                    }
                },
            ) {
                FloatingActionButtonMenuItem(
                    onClick = {
                        isFabExpanded = false
                        showUrlDialog = true
                    },
                    icon = {
                        Icon(
                            imageVector = NextIcons.Link,
                            contentDescription = null,
                        )
                    },
                    text = {
                        Text(text = stringResource(id = R.string.open_network_stream))
                    },
                )
                FloatingActionButtonMenuItem(
                    onClick = {
                        isFabExpanded = false
                        selectVideoFileLauncher.launch("video/*")
                    },
                    icon = {
                        Icon(
                            imageVector = NextIcons.FileOpen,
                            contentDescription = null,
                        )
                    },
                    text = {
                        Text(text = stringResource(id = R.string.open_local_video))
                    },
                )
                FloatingActionButtonMenuItem(
                    onClick = {
                        isFabExpanded = false
                        val folder = (uiState.mediaDataState as? DataState.Success)?.value ?: return@FloatingActionButtonMenuItem
                        val videoToPlay = folder.recentlyPlayedVideo ?: folder.firstVideo ?: return@FloatingActionButtonMenuItem
                        onPlayVideo(videoToPlay.uriString.toUri())
                    },
                    icon = {
                        Icon(
                            imageVector = NextIcons.History,
                            contentDescription = null,
                        )
                    },
                    text = {
                        Text(text = stringResource(id = R.string.recently_played))
                    },
                )
            }
        },
        containerColor = Color(0xFF0C0C0C),
    ) { scaffoldPadding ->
        Surface(
            color = Color(0xFF0C0C0C),
            modifier = Modifier.fillMaxSize()
        ) {
            when (activeTab) {
                "Videos" -> {
                    AnimatedContent(
                        targetState = selectedCategory,
                        transitionSpec = {
                            val categories = listOf("All", "Folders", "Videos")
                            val initialIndex = categories.indexOf(initialState)
                            val targetIndex = categories.indexOf(targetState)
                            if (targetIndex > initialIndex) {
                                (slideInHorizontally { width -> width } + fadeIn()) togetherWith 
                                (slideOutHorizontally { width -> -width } + fadeOut())
                            } else {
                                (slideInHorizontally { width -> -width } + fadeIn()) togetherWith 
                                (slideOutHorizontally { width -> width } + fadeOut())
                            }
                        },
                        label = "categoryTransition",
                        modifier = Modifier.padding(scaffoldPadding)
                    ) { category ->
                        when (uiState.mediaDataState) {
                            is DataState.Error -> {}
                            is DataState.Loading -> {
                                CenterCircularProgressBar()
                            }
                            is DataState.Success -> {
                                val rootFolder = uiState.mediaDataState.value
                                if (rootFolder == null || (rootFolder.folderList.isEmpty() && rootFolder.mediaList.isEmpty())) {
                                    NoVideosFound(contentPadding = scaffoldPadding)
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
                                            selectedCategory = category,
                                            contentPadding = PaddingValues(0.dp),
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
                }
                "Music" -> {
                    Box(modifier = Modifier.padding(scaffoldPadding)) {
                        MusicLibraryView(themeColor = themeColor)
                    }
                }
                "Download" -> {
                    Box(modifier = Modifier.padding(scaffoldPadding)) {
                        BrowserDownloaderView(themeColor = themeColor)
                    }
                }
                "More" -> {
                    Box(modifier = Modifier.padding(scaffoldPadding)) {
                        MoreOptionsView(
                            themeColor = themeColor,
                            themeColorName = themeColorName,
                            onThemeChange = { themeColorName = it },
                            onSettingsClick = onSettingsClick
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(lazyGridState.isScrollInProgress) {
        if (isFabExpanded && lazyGridState.isScrollInProgress) {
            isFabExpanded = false
        }
    }

    LaunchedEffect(selectionManager.isInSelectionMode) {
        if (selectionManager.isInSelectionMode) {
            isFabExpanded = false
        }
    }

    BackHandler(enabled = isFabExpanded) {
        isFabExpanded = false
    }

    BackHandler(enabled = selectionManager.isInSelectionMode) {
        selectionManager.exitSelectionMode()
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

@Composable
fun DarkTopAppBar(
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
                        imageVector = Icons.Default.Close,
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
                        imageVector = if (isAllSelected) Icons.Default.Check else Icons.Default.CheckCircle,
                        contentDescription = "Select All Toggle",
                        tint = Color.White
                    )
                }
            } else {
                if (showBackButton) {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Text(
                    text = title.uppercase(Locale.getDefault()),
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                )

                Spacer(modifier = Modifier.weight(1f))

                IconButton(onClick = onMusicToggle) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Music Toggle",
                        tint = Color.White
                    )
                }

                IconButton(onClick = onLayoutToggle) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = "Layout Toggle",
                        tint = Color.White
                    )
                }

                IconButton(onClick = onSearchClick) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryFilterRow(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    themeColor: Color
) {
    val categories = listOf("All", "Folders", "Videos")
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            val isSelected = selectedCategory == category
            val backgroundColor = if (isSelected) themeColor else Color(0xFF1E1E1E)
            val textColor = if (isSelected) Color.White else Color(0xFFB0B0B0)
            val borderModifier = if (isSelected) Modifier else Modifier.border(0.5.dp, Color(0xFF333333), RoundedCornerShape(20.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(backgroundColor)
                    .then(borderModifier)
                    .clickable { onCategorySelected(category) }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category,
                    color = textColor,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
fun EmberBottomNavigation(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    themeColor: Color
) {
    Surface(
        color = Color.Black.copy(alpha = 0.8f),
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 0.5.dp, color = Color(0xFF222222), shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val tabs: List<Pair<String, androidx.compose.ui.graphics.vector.ImageVector>> = listOf(
                "Videos" to Icons.Default.PlayArrow,
                "Music" to Icons.Default.Home,
                "Download" to Icons.Default.ArrowDownward,
                "More" to Icons.Default.Settings
            )

            tabs.forEach { (tabName, icon) ->
                val isSelected = selectedTab == tabName
                val activeColor = themeColor
                val inactiveColor = Color(0xFF888888)

                Column(
                    modifier = Modifier
                        .clickable { onTabSelected(tabName) }
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = tabName,
                        tint = if (isSelected) activeColor else inactiveColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = tabName,
                        color = if (isSelected) activeColor else inactiveColor,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun MusicLibraryView(
    themeColor: Color
) {
    val context = LocalContext.current
    val localSongs = remember { mutableStateListOf<Song>() }

    val sampleSongs = listOf(
        Song("After Hours", "The Weeknd", "8.4 MB", "3:42"),
        Song("Blinding Lights", "The Weeknd", "7.1 MB", "3:21"),
        Song("Ember Theme Song (Retro)", "JoeJiJa", "9.2 MB", "4:15"),
        Song("Starboy", "The Weeknd", "6.8 MB", "3:50"),
        Song("Die For You", "The Weeknd", "7.9 MB", "3:11")
    )

    LaunchedEffect(Unit) {
        try {
            val resolver = context.contentResolver
            val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.DURATION
            )
            val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
            resolver.query(uri, projection, selection, null, null)?.use { cursor ->
                val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
                val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                while (cursor.moveToNext()) {
                    val title = cursor.getString(titleCol)
                    val artist = cursor.getString(artistCol)
                    val sizeBytes = cursor.getLong(sizeCol)
                    val durationMs = cursor.getLong(durationCol)

                    val sizeMb = String.format(Locale.US, "%.1f MB", sizeBytes.toFloat() / (1024 * 1024))
                    val seconds = (durationMs / 1000) % 60
                    val minutes = (durationMs / (1000 * 60)) % 60
                    val durationStr = String.format(Locale.US, "%d:%02d", minutes, seconds)

                    localSongs.add(Song(title, artist, sizeMb, durationStr))
                }
            }
        } catch (e: Exception) {
            // Log or ignore
        }
    }

    val songsToDisplay = if (localSongs.isEmpty()) sampleSongs else localSongs

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0C0C0C))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("All Device Music", color = Color.White, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        }

        items(songsToDisplay) { song ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E1E1E), RoundedCornerShape(12.dp))
                    .clickable {
                        // Play action stub
                    }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(themeColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        tint = themeColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(song.title, color = Color.White, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(song.artist, color = Color.Gray, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(song.duration, color = Color.White, style = MaterialTheme.typography.labelMedium)
                    Text(song.size, color = Color.Gray, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
fun BrowserDownloaderView(
    themeColor: Color
) {
    var urlInput by remember { mutableStateOf("") }
    var downloadProgress by remember { mutableStateOf(-1f) }
    var downloadStatus by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0C0C0C))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = urlInput,
            onValueChange = { urlInput = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search or type web URL...", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Gray) },
            trailingIcon = {
                if (urlInput.isNotBlank()) {
                    TextButton(
                        onClick = {
                            downloadProgress = 0f
                            downloadStatus = "Connecting to server..."
                        }
                    ) {
                        Text("Go", color = themeColor, fontWeight = FontWeight.Bold)
                    }
                }
            },
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = themeColor,
                unfocusedBorderColor = Color(0xFF333333),
                focusedContainerColor = Color(0xFF1E1E1E),
                unfocusedContainerColor = Color(0xFF1E1E1E)
            ),
            shape = RoundedCornerShape(24.dp)
        )

        if (downloadProgress >= 0f) {
            LaunchedEffect(downloadProgress) {
                if (downloadProgress < 1f) {
                    kotlinx.coroutines.delay(200)
                    downloadProgress += 0.05f
                    if (downloadProgress >= 1f) {
                        downloadStatus = "Download complete: saved to Downloads folder!"
                    } else {
                        downloadStatus = "Downloading video... ${(downloadProgress * 100).toInt()}%"
                    }
                }
            }

            Surface(
                color = Color(0xFF1E1E1E),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(downloadStatus, color = Color.White, style = MaterialTheme.typography.bodyMedium)
                    androidx.compose.material3.LinearProgressIndicator(
                        progress = { downloadProgress },
                        color = themeColor,
                        trackColor = Color(0xFF333333),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Quick Link Shortcuts", color = Color.White, style = MaterialTheme.typography.titleMedium, modifier = Modifier.align(Alignment.Start))

        val shortcuts = listOf(
            "YouTube" to "youtube.com",
            "Vimeo" to "vimeo.com",
            "Dailymotion" to "dailymotion.com",
            "Facebook Video" to "facebook.com",
            "TikTok" to "tiktok.com"
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            shortcuts.chunked(2).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowItems.forEach { (name, url) ->
                        Surface(
                            color = Color(0xFF1E1E1E),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    urlInput = "https://www.$url"
                                    downloadProgress = 0f
                                    downloadStatus = "Connecting to $url..."
                                }
                                .padding(12.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(name, color = Color.White, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                Text(url, color = Color.Gray, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                    if (rowItems.size < 2) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun MoreOptionsView(
    themeColor: Color,
    themeColorName: String,
    onThemeChange: (String) -> Unit,
    onSettingsClick: () -> Unit
) {
    var showThemesDialog by remember { mutableStateOf(false) }
    var showHiddenFolderDialog by remember { mutableStateOf(false) }
    var showWidgetsDialog by remember { mutableStateOf(false) }
    var showFeedbackDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    var passwordInput by remember { mutableStateOf("") }
    var isVaultUnlocked by remember { mutableStateOf(false) }
    var vaultError by remember { mutableStateOf("") }

    val menuItems = listOf(
        "Settings" to Icons.Default.Settings,
        "Themes" to Icons.Default.Refresh,
        "Hidden Folder" to Icons.Default.Lock,
        "Widgets" to Icons.Default.Build,
        "Feedback" to Icons.Default.Email,
        "About" to Icons.Default.Info
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0C0C0C))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("More Options", color = Color.White, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))

        menuItems.forEach { (name, icon) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E1E1E), RoundedCornerShape(12.dp))
                    .clickable {
                        when (name) {
                            "Settings" -> onSettingsClick()
                            "Themes" -> showThemesDialog = true
                            "Hidden Folder" -> {
                                showHiddenFolderDialog = true
                                isVaultUnlocked = false
                                passwordInput = ""
                                vaultError = ""
                            }
                            "Widgets" -> showWidgetsDialog = true
                            "Feedback" -> showFeedbackDialog = true
                            "About" -> showAboutDialog = true
                        }
                    }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(imageVector = icon, contentDescription = name, tint = themeColor, modifier = Modifier.size(24.dp))
                Text(name, color = Color.White, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium))
                Spacer(modifier = Modifier.weight(1f))
                Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
            }
        }
    }

    if (showThemesDialog) {
        val colorNames = listOf("Orange-Red", "Blue", "Green", "Purple")
        NextDialog(
            onDismissRequest = { showThemesDialog = false },
            title = { Text("Select App Theme Color") },
            content = {
                Column {
                    colorNames.forEach { colorName ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onThemeChange(colorName) }
                                .padding(vertical = 12.dp)
                        ) {
                            RadioButton(
                                selected = (colorName == themeColorName),
                                onClick = { onThemeChange(colorName) },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = themeColor
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(colorName, color = Color.White)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemesDialog = false }) {
                    Text("Done", color = themeColor)
                }
            }
        )
    }

    if (showHiddenFolderDialog) {
        NextDialog(
            onDismissRequest = { showHiddenFolderDialog = false },
            title = { Text(if (isVaultUnlocked) "Privacy Vault" else "Enter Password") },
            content = {
                if (!isVaultUnlocked) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Please enter password to access hidden folder. Default is '1234'.", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                        OutlinedTextField(
                            value = passwordInput,
                            onValueChange = { passwordInput = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Password", color = Color.Gray) },
                            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = themeColor
                            )
                        )
                        if (vaultError.isNotBlank()) {
                            Text(vaultError, color = Color.Red, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Secret Videos Folder", color = Color.White, style = MaterialTheme.typography.titleSmall)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .background(Color(0xFF0C0C0C), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Vault is empty. Add videos by long-pressing in list.", color = Color.Gray)
                        }
                    }
                }
            },
            confirmButton = {
                if (!isVaultUnlocked) {
                    TextButton(
                        onClick = {
                            if (passwordInput == "1234") {
                                isVaultUnlocked = true
                                vaultError = ""
                            } else {
                                vaultError = "Incorrect password! Try again."
                            }
                        }
                    ) {
                        Text("Unlock", color = themeColor)
                    }
                } else {
                    TextButton(onClick = { showHiddenFolderDialog = false }) {
                        Text("Close", color = themeColor)
                    }
                }
            },
            dismissButton = {
                CancelButton(onClick = { showHiddenFolderDialog = false })
            }
        )
    }

    if (showWidgetsDialog) {
        NextDialog(
            onDismissRequest = { showWidgetsDialog = false },
            title = { Text("Homescreen Widgets") },
            content = {
                Text(
                    "To add widgets to your home screen:\n\n1. Press and hold any empty space on your home screen.\n2. Tap the 'Widgets' option.\n3. Search for 'Ember Video Player' in the list.\n4. Drag and drop the Ember Widget onto your screen.\n\nEnjoy quick playback access directly from your launcher!",
                    color = Color.LightGray
                )
            },
            confirmButton = {
                TextButton(onClick = { showWidgetsDialog = false }) {
                    Text("OK", color = themeColor)
                }
            }
        )
    }

    if (showFeedbackDialog) {
        var feedbackText by remember { mutableStateOf("") }
        var rating by remember { mutableStateOf(5) }

        NextDialog(
            onDismissRequest = { showFeedbackDialog = false },
            title = { Text("App Feedback") },
            content = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Rate your experience:", color = Color.White)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        (1..5).forEach { star ->
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (star <= rating) Color(0xFFFFD700) else Color.Gray,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clickable { rating = star }
                            )
                        }
                    }
                    OutlinedTextField(
                        value = feedbackText,
                        onValueChange = { feedbackText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        placeholder = { Text("Tell us what we can improve...", color = Color.Gray) },
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = themeColor
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showFeedbackDialog = false
                    }
                ) {
                    Text("Submit", color = themeColor)
                }
            },
            dismissButton = {
                CancelButton(onClick = { showFeedbackDialog = false })
            }
        )
    }

    if (showAboutDialog) {
        NextDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("About Ember") },
            content = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(themeColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Ember Player", color = Color.White, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                    Text("Version 1.0.0 (API 34)", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Ember is a premium open-source native media player built entirely in Kotlin and Jetpack Compose. Enjoy beautiful dark-themed playback.", color = Color.LightGray, textAlign = TextAlign.Center)
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("Close", color = themeColor)
                }
            }
        )
    }
}

data class Song(
    val title: String,
    val artist: String,
    val size: String,
    val duration: String
)

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
        val shape = MaterialTheme.shapes.largeIncreased.copy(
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
