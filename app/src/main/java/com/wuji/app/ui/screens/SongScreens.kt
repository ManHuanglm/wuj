package com.wuji.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.wuji.app.data.model.*
import com.wuji.app.ui.components.*
import com.wuji.app.ui.navigation.Route
import com.wuji.app.ui.viewmodel.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongScreen(navController: NavController, viewModel: SongViewModel = hiltViewModel()) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val songsState by viewModel.songsState.collectAsState()
    val playlistsState by viewModel.playlistsState.collectAsState()
    val isSearching = searchQuery.isNotEmpty()
    var activeTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            Column {
                WujiTopBar(title = "音乐", actions = {
                    IconButton(onClick = { navController.navigate(Route.SongShelf.route) }) { Icon(Icons.Default.QueueMusic, contentDescription = "歌单") }
                    IconButton(onClick = { navController.navigate(Route.SourceManage.route) }) { Icon(Icons.Default.Source, contentDescription = "订阅源") }
                    IconButton(onClick = { navController.navigate(Route.Setting.route) }) { Icon(Icons.Default.Settings, contentDescription = "设置") }
                })
                SearchBar(value = searchQuery, onValueChange = { viewModel.updateQuery(it) }, onSearch = { viewModel.search() }, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
                TabRow(selectedTabIndex = activeTab) {
                    Tab(selected = activeTab == 0, onClick = { activeTab = 0 }, text = { Text("歌单") })
                    Tab(selected = activeTab == 1, onClick = { activeTab = 1 }, text = { Text("歌曲") })
                }
            }
        },
    ) { padding ->
        val state = if (activeTab == 0) playlistsState else songsState
        when {
            state.isLoading -> LoadingIndicator(Modifier.padding(padding))
            state.error != null -> ErrorState(state.error!!) { if (isSearching) viewModel.search() else viewModel.loadRecommend() }
            state.data != null -> {
                if (activeTab == 0) {
                    val playlists = (state.data as PlaylistDetail).songs
                    if (playlists.isEmpty()) EmptyState(if (isSearching) "未找到歌单" else "暂无歌单", Modifier.padding(padding))
                    else LazyColumn(Modifier.padding(padding)) { items(playlists) { p -> ListCardItem(title = p.title, subtitle = p.author, cover = p.cover, onClick = { navController.navigate(Route.PlaylistDetail.createRoute(p.sourceId, p.id)) }) } }
                } else {
                    val songs = (state.data as SongList).list
                    if (songs.isEmpty()) EmptyState(if (isSearching) "未找到歌曲" else "暂无歌曲", Modifier.padding(padding))
                    else LazyColumn(Modifier.padding(padding)) {
                        items(songs) { song ->
                            ListItem(
                                headlineContent = { Text(song.title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                                supportingContent = { Text("${song.artist} - ${song.album}", maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 12.sp) },
                                leadingContent = { Box(Modifier.size(48.dp).clip(RoundedCornerShape(4.dp)).background(MaterialTheme.colorScheme.surfaceVariant), Alignment.Center) { if (song.cover.isNotEmpty()) coil.compose.AsyncImage(model = song.cover, contentDescription = null, modifier = Modifier.fillMaxSize()) else Icon(Icons.Default.MusicNote, contentDescription = null) } },
                                trailingContent = { IconButton(onClick = { navController.navigate(Route.SongPlayView.route) }) { Icon(Icons.Default.PlayArrow, contentDescription = "播放") } },
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongShelfScreen(navController: NavController, viewModel: SongViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) { viewModel.loadShelves() }
    val shelves by viewModel.shelves.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    Scaffold(topBar = { WujiTopBar(title = "歌单收藏", onBack = { navController.popBackStack() }) }, floatingActionButton = { FloatingActionButton(onClick = { showCreateDialog = true }) { Icon(Icons.Default.Add, contentDescription = "新建") } }) { padding ->
        if (shelves.isEmpty()) EmptyState("暂无歌单", Modifier.padding(padding))
        else LazyColumn(Modifier.padding(padding)) { items(shelves) { shelf -> ListCardItem(title = shelf.name, subtitle = "${shelf.items.size} 首", onClick = { navController.navigate(Route.SongShelfDetail.createRoute(shelf.id)) }); HorizontalDivider() } }
    }
    if (showCreateDialog) { var name by remember { mutableStateOf("") }; AlertDialog(onDismissRequest = { showCreateDialog = false }, title = { Text("新建歌单") }, text = { OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("名称") }) }, confirmButton = { TextButton(onClick = { if (name.isNotBlank()) { viewModel.createShelf(name); showCreateDialog = false } }) { Text("确定") } }, dismissButton = { TextButton(onClick = { showCreateDialog = false }) { Text("取消") } }) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongShelfDetailScreen(navController: NavController, viewModel: SongViewModel = hiltViewModel()) {
    val shelfId = navController.previousBackStackEntry?.arguments?.getString("shelfId") ?: ""
    LaunchedEffect(Unit) { viewModel.loadShelves() }
    val shelves by viewModel.shelves.collectAsState()
    val shelf = shelves.find { it.id == shelfId }
    Scaffold(topBar = { WujiTopBar(title = shelf?.name ?: "歌单详情", onBack = { navController.popBackStack() }) }) { padding ->
        val songs = shelf?.items ?: emptyList()
        if (songs.isEmpty()) {
            EmptyState("暂无歌曲", Modifier.padding(padding))
        } else {
            LazyColumn(Modifier.padding(padding)) {
                items(songs) { s ->
                    ListItem(
                        headlineContent = { Text(s.song.title) },
                        supportingContent = { Text(s.song.artist) },
                        trailingContent = {
                            IconButton(onClick = { navController.navigate(Route.SongPlayView.route) }) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "播放")
                            }
                        },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistDetailScreen(navController: NavController, viewModel: SongViewModel = hiltViewModel()) {
    val sourceId = navController.previousBackStackEntry?.arguments?.getString("sourceId") ?: ""
    val playlistId = navController.previousBackStackEntry?.arguments?.getString("playlistId") ?: ""
    var detail by remember { mutableStateOf<PlaylistDetail?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    LaunchedEffect(sourceId, playlistId) { isLoading = true; viewModel.getPlaylistDetail(sourceId, PlaylistInfo(id = playlistId, sourceId = sourceId), 1) { detail = it; isLoading = false } }
    Scaffold(topBar = { WujiTopBar(title = detail?.info?.title ?: "歌单详情", onBack = { navController.popBackStack() }) }) { padding ->
        if (isLoading) {
            LoadingIndicator(Modifier.padding(padding))
        } else {
            val d = detail
            if (d == null) {
                EmptyState("加载失败", Modifier.padding(padding))
            } else if (d.songs.isEmpty()) {
                EmptyState("暂无歌曲", Modifier.padding(padding))
            } else {
                LazyColumn(Modifier.padding(padding)) {
                    items(d.songs) { song ->
                        ListItem(
                            headlineContent = { Text(song.title) },
                            supportingContent = { Text(song.artist) },
                            trailingContent = {
                                IconButton(onClick = { navController.navigate(Route.SongPlayView.route) }) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = "播放")
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongPlayViewScreen(navController: NavController, viewModel: SongViewModel = hiltViewModel()) {
    Scaffold(topBar = { WujiTopBar(title = "正在播放", onBack = { navController.popBackStack() }) }) { padding ->
        Column(Modifier.padding(padding).fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(48.dp))
            Box(Modifier.size(200.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant), Alignment.Center) { Icon(Icons.Default.MusicNote, contentDescription = null, modifier = Modifier.size(80.dp)) }
            Spacer(Modifier.height(24.dp))
            Text("暂无播放", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Text("请从列表中选择歌曲", color = MaterialTheme.colorScheme.outline)
            Spacer(Modifier.height(48.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {}) { Icon(Icons.Default.SkipPrevious, contentDescription = "上一首", modifier = Modifier.size(40.dp)) }
                FilledIconButton(onClick = {}, modifier = Modifier.size(64.dp)) { Icon(Icons.Default.PlayArrow, contentDescription = "播放", modifier = Modifier.size(32.dp)) }
                IconButton(onClick = {}) { Icon(Icons.Default.SkipNext, contentDescription = "下一首", modifier = Modifier.size(40.dp)) }
            }
            Spacer(Modifier.height(24.dp))
            Slider(value = 0f, onValueChange = {}, modifier = Modifier.padding(horizontal = 32.dp))
            Row(Modifier.fillMaxWidth().padding(horizontal = 32.dp), horizontalArrangement = Arrangement.SpaceBetween) { Text("00:00"); Text("00:00") }
        }
    }
}
