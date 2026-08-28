package com.wuji.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.wuji.app.data.model.*
import com.wuji.app.ui.components.*
import com.wuji.app.ui.navigation.Route
import com.wuji.app.ui.viewmodel.*
import androidx.compose.foundation.clickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoScreen(navController: NavController, viewModel: VideoViewModel = hiltViewModel()) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val recommendState by viewModel.recommendState.collectAsState()
    val searchState by viewModel.searchState.collectAsState()
    val history by viewModel.history.collectAsState()
    val isSearching = searchQuery.isNotEmpty()
    LaunchedEffect(Unit) { viewModel.loadHistory() }
    Scaffold(topBar = { Column { WujiTopBar(title = "??", actions = { IconButton(onClick = { navController.navigate(Route.VideoShelf.route) }) { Icon(Icons.Default.VideoLibrary, contentDescription = "??") }; IconButton(onClick = { navController.navigate(Route.SourceManage.route) }) { Icon(Icons.Default.Source, contentDescription = "???") }; IconButton(onClick = { navController.navigate(Route.Setting.route) }) { Icon(Icons.Default.Settings, contentDescription = "??") } }); SearchBar(value = searchQuery, onValueChange = { viewModel.updateQuery(it) }, onSearch = { viewModel.search() }, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) } }) { padding ->
        val state = if (isSearching) searchState else recommendState
        Column(Modifier.padding(padding).fillMaxSize()) {
            if (!isSearching && history.isNotEmpty()) { Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) { Text("????", style = MaterialTheme.typography.titleMedium); TextButton(onClick = { viewModel.clearHistory() }) { Text("??") } }; LazyColumn(Modifier.heightIn(max = 200.dp)) { items(history.take(5)) { h -> ListItem(headlineContent = { Text(h.item.title, maxLines = 1, overflow = TextOverflow.Ellipsis) }, supportingContent = { Text("??: ${h.lastWatchEpisode.title}", maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 12.sp) }, modifier = Modifier.clickable { navController.navigate(Route.VideoDetail.createRoute(h.sourceId, h.item.id)) }) } }; HorizontalDivider() }
            when { state.isLoading -> LoadingIndicator(); state.error != null -> ErrorState(state.error!!) { if (isSearching) viewModel.search() else viewModel.loadRecommend() }; state.data != null -> { val list = state.data!!.list; if (list.isEmpty()) EmptyState(if (isSearching) "?????" else "????,??????") else LazyVerticalGrid(columns = GridCells.Fixed(3), modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) { items(list) { item -> CardItem(title = item.title, subtitle = "${item.year} ${item.area}", cover = item.cover, onClick = { navController.navigate(Route.VideoDetail.createRoute(item.sourceId, item.id)) }) } } } }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoShelfScreen(navController: NavController, viewModel: VideoViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) { viewModel.loadShelves() }
    val shelves by viewModel.shelves.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    Scaffold(topBar = { WujiTopBar(title = "????", onBack = { navController.popBackStack() }) }, floatingActionButton = { FloatingActionButton(onClick = { showCreateDialog = true }) { Icon(Icons.Default.Add, contentDescription = "??") } }) { padding -> if (shelves.isEmpty()) EmptyState("????", Modifier.padding(padding)) else LazyColumn(Modifier.padding(padding)) { items(shelves) { shelf -> ListCardItem(title = shelf.name, subtitle = "${shelf.items.size} ?", onClick = {}); HorizontalDivider() } } }
    if (showCreateDialog) { var name by remember { mutableStateOf("") }; AlertDialog(onDismissRequest = { showCreateDialog = false }, title = { Text("?????") }, text = { OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("??") }) }, confirmButton = { TextButton(onClick = { if (name.isNotBlank()) { viewModel.createShelf(name); showCreateDialog = false } }) { Text("??") } }, dismissButton = { TextButton(onClick = { showCreateDialog = false }) { Text("??") } }) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoDetailScreen(navController: NavController, viewModel: VideoViewModel = hiltViewModel()) {
    val sourceId = navController.previousBackStackEntry?.arguments?.getString("sourceId") ?: ""
    val videoId = navController.previousBackStackEntry?.arguments?.getString("videoId") ?: ""
    var detail by remember { mutableStateOf<VideoDetail?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedResource by remember { mutableStateOf(0) }
    var selectedEpisode by remember { mutableStateOf<VideoEpisode?>(null) }
    var playUrl by remember { mutableStateOf<VideoUrlMap?>(null) }

    LaunchedEffect(sourceId, videoId) {
        isLoading = true
        viewModel.getVideoDetail(sourceId, VideoItem(id = videoId, sourceId = sourceId)) { d ->
            detail = d
            isLoading = false
            if (d.resources.isNotEmpty()) {
                selectedEpisode = d.resources[0].episodes.firstOrNull()
            }
        }
    }

    LaunchedEffect(selectedEpisode) {
        if (selectedEpisode != null && detail != null && detail!!.resources.isNotEmpty()) {
            val resource = detail!!.resources.getOrElse(selectedResource) { detail!!.resources[0] }
            viewModel.getPlayUrl(sourceId, detail!!.item, resource, selectedEpisode!!) { playUrl = it }
        }
    }

    Scaffold(topBar = { WujiTopBar(title = detail?.item?.title ?: "????", onBack = { navController.popBackStack() }) }) { padding ->
        if (isLoading) { LoadingIndicator(Modifier.padding(padding)) }
        else detail?.let { d ->
            Column(Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState())) {
                // Player area
                Box(Modifier.fillMaxWidth().height(220.dp).background(MaterialTheme.colorScheme.surfaceVariant), Alignment.Center) {
                    if (playUrl?.url?.isNotEmpty() == true) {
                        Text("??: ${playUrl!!.url.take(50)}...", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                    } else {
                        CircularProgressIndicator()
                    }
                }
                // Info
                Column(Modifier.padding(16.dp)) {
                    Text(d.item.title, style = MaterialTheme.typography.titleMedium)
                    Text("${d.item.year} | ${d.item.area} | ${d.item.category}", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                    Spacer(Modifier.height(8.dp))
                    Text(d.item.intro, fontSize = 14.sp, maxLines = 4, overflow = TextOverflow.Ellipsis)
                }
                HorizontalDivider()
                // Resources and episodes
                if (d.resources.isNotEmpty()) {
                    TabRow(selectedTabIndex = selectedResource) {
                        d.resources.forEachIndexed { index, res ->
                            Tab(selected = selectedResource == index, onClick = { selectedResource = index; selectedEpisode = res.episodes.firstOrNull() }, text = { Text(res.name) })
                        }
                    }
                    val episodes = d.resources.getOrElse(selectedResource) { d.resources[0] }.episodes
                    FlowRow(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        episodes.forEach { ep ->
                            FilterChip(selected = selectedEpisode?.id == ep.id, onClick = { selectedEpisode = ep; viewModel.updateWatchProgress(d.item, sourceId, ep, 0L) }, label = { Text(ep.title, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 12.sp) })
                        }
                    }
                }
            }
        } ?: EmptyState("????", Modifier.padding(padding))
    }
}
