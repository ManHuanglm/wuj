package com.wuji.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.wuji.app.ui.components.*
import com.wuji.app.ui.navigation.Route
import com.wuji.app.ui.viewmodel.PhotoViewModel
import com.wuji.app.ui.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoScreen(navController: NavController, viewModel: PhotoViewModel = hiltViewModel()) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val recommendState by viewModel.recommendState.collectAsState()
    val searchState by viewModel.searchState.collectAsState()
    val isSearching = searchQuery.isNotEmpty()

    Scaffold(
        topBar = {
            Column {
                WujiTopBar(
                    title = "??",
                    actions = {
                        IconButton(onClick = { navController.navigate(Route.PhotoShelf.route) }) {
                            Icon(Icons.Default.Favorite, contentDescription = "??")
                        }
                        IconButton(onClick = { navController.navigate(Route.SourceManage.route) }) {
                            Icon(Icons.Default.Source, contentDescription = "???")
                        }
                        IconButton(onClick = { navController.navigate(Route.Setting.route) }) {
                            Icon(Icons.Default.Settings, contentDescription = "??")
                        }
                    },
                )
                SearchBar(
                    value = searchQuery,
                    onValueChange = { viewModel.updateQuery(it) },
                    onSearch = { viewModel.search() },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                )
            }
        },
    ) { padding ->
        val state = if (isSearching) searchState else recommendState
        when {
            state.isLoading -> LoadingIndicator(Modifier.padding(padding))
            state.error != null -> ErrorState(state.error!!) { if (isSearching) viewModel.search() else viewModel.loadRecommend() }
            state.data != null -> {
                val list = state.data!!.list
                if (list.isEmpty()) {
                    EmptyState(if (isSearching) "???????" else "????,??????", Modifier.padding(padding))
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier.padding(padding).fillMaxSize(),
                        contentPadding = PaddingValues(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(list) { item ->
                            CardItem(
                                title = item.title,
                                cover = item.cover,
                                onClick = { navController.navigate(Route.PhotoDetail.createRoute(item.sourceId, item.id)) },
                            )
                        }
                        item {
                            LaunchedEffect(Unit) {
                                if (isSearching) viewModel.loadMoreSearch() else viewModel.loadMoreRecommend()
                            }
                            Box(Modifier.fillMaxWidth().padding(16.dp), androidx.compose.ui.Alignment.Center) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoShelfScreen(navController: NavController, viewModel: PhotoViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) { viewModel.loadShelves() }
    val shelves by viewModel.shelves.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { WujiTopBar(title = "????", onBack = { navController.popBackStack() }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "?????")
            }
        },
    ) { padding ->
        if (shelves.isEmpty()) {
            EmptyState("?????", Modifier.padding(padding))
        } else {
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(shelves.size) { index ->
                    val shelf = shelves[index]
                    ListCardItem(
                        title = shelf.name,
                        subtitle = "${shelf.items.size} ???",
                        onClick = { },
                    )
                    HorizontalDivider()
                }
            }
        }
    }

    if (showCreateDialog) {
        var name by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("?????") },
            text = { OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("??") }) },
            confirmButton = {
                TextButton(onClick = {
                    if (name.isNotBlank()) { viewModel.createShelf(name); showCreateDialog = false }
                }) { Text("??") }
            },
            dismissButton = { TextButton(onClick = { showCreateDialog = false }) { Text("??") } },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoDetailScreen(navController: NavController, viewModel: PhotoViewModel = hiltViewModel()) {
    val sourceId = navController.previousBackStackEntry?.arguments?.getString("sourceId") ?: ""
    val id = navController.previousBackStackEntry?.arguments?.getString("id") ?: ""

    var detail by remember { mutableStateOf<com.wuji.app.data.model.PhotoDetail?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var currentPage by remember { mutableStateOf(1) }

    LaunchedEffect(sourceId, id) {
        isLoading = true
        viewModel.getPhotoDetail(sourceId, com.wuji.app.data.model.PhotoItem(id = id, sourceId = sourceId), currentPage) { result ->
            detail = result
            isLoading = false
        }
    }

    Scaffold(
        topBar = { WujiTopBar(title = detail?.item?.title ?: "????", onBack = { navController.popBackStack() }) },
    ) { padding ->
        if (isLoading) {
            LoadingIndicator(Modifier.padding(padding))
        } else {
            detail?.let { d ->
                LazyColumn(modifier = Modifier.padding(padding).fillMaxSize()) {
                    items(d.images.size) { index ->
                        val imageUrl = d.images[index]
                        coil.compose.AsyncImage(
                            model = imageUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            } ?: EmptyState("????", Modifier.padding(padding))
        }
    }
}
