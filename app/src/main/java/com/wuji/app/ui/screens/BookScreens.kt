package com.wuji.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookScreen(navController: NavController, viewModel: BookViewModel = hiltViewModel()) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val recommendState by viewModel.recommendState.collectAsState()
    val searchState by viewModel.searchState.collectAsState()
    val history by viewModel.history.collectAsState()
    val isSearching = searchQuery.isNotEmpty()

    LaunchedEffect(Unit) { viewModel.loadHistory() }

    Scaffold(topBar = {
        Column {
            WujiTopBar(title = "??", actions = {
                IconButton(onClick = { navController.navigate(Route.BookShelf.route) }) { Icon(Icons.Default.LibraryBooks, contentDescription = "??") }
                IconButton(onClick = { navController.navigate(Route.SourceManage.route) }) { Icon(Icons.Default.Source, contentDescription = "???") }
                IconButton(onClick = { navController.navigate(Route.Setting.route) }) { Icon(Icons.Default.Settings, contentDescription = "??") }
            })
            SearchBar(value = searchQuery, onValueChange = { viewModel.updateQuery(it) }, onSearch = { viewModel.search() }, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
        }
    }) { padding ->
        val state = if (isSearching) searchState else recommendState
        Column(Modifier.padding(padding).fillMaxSize()) {
            if (!isSearching && history.isNotEmpty()) {
                Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("????", style = MaterialTheme.typography.titleMedium)
                    TextButton(onClick = { viewModel.clearHistory() }) { Text("??") }
                }
                LazyColumn(Modifier.heightIn(max = 200.dp)) {
                    items(history.take(5)) { h ->
                        ListItem(
                            headlineContent = { Text(h.item.title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                            supportingContent = { Text("??: ${h.lastReadChapter.title}", maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 12.sp) },
                            leadingContent = { Box(Modifier.size(40.dp).clip(androidx.compose.foundation.shape.RoundedCornerShape(4.dp)).background(MaterialTheme.colorScheme.surfaceVariant)) { coil.compose.AsyncImage(model = h.item.cover, contentDescription = null, modifier = Modifier.fillMaxSize()) } },
                            modifier = Modifier.clickable { navController.navigate(Route.BookRead.createRoute(h.sourceId, h.item.id, h.lastReadChapter.id)) }
                        )
                    }
                }
                HorizontalDivider()
            }
            when {
                state.isLoading -> LoadingIndicator()
                state.error != null -> ErrorState(state.error!!) { if (isSearching) viewModel.search() else viewModel.loadRecommend() }
                state.data != null -> {
                    val list = state.data!!.list
                    if (list.isEmpty()) EmptyState(if (isSearching) "?????" else "????,??????")
                    else LazyVerticalGrid(columns = GridCells.Fixed(3), modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(list) { item -> CardItem(title = item.title, subtitle = item.author, cover = item.cover, onClick = { navController.navigate(Route.BookDetail.createRoute(item.sourceId, item.id)) }) }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookShelfScreen(navController: NavController, viewModel: BookViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) { viewModel.loadShelves() }
    val shelves by viewModel.shelves.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    Scaffold(topBar = { WujiTopBar(title = "??", onBack = { navController.popBackStack() }) }, floatingActionButton = { FloatingActionButton(onClick = { showCreateDialog = true }) { Icon(Icons.Default.Add, contentDescription = "??") } }) { padding ->
        if (shelves.isEmpty()) EmptyState("????", Modifier.padding(padding))
        else LazyColumn(Modifier.padding(padding)) { items(shelves) { shelf -> ListCardItem(title = shelf.name, subtitle = "${shelf.items.size} ?", onClick = { }); HorizontalDivider() } }
    }
    if (showCreateDialog) { var name by remember { mutableStateOf("") }; AlertDialog(onDismissRequest = { showCreateDialog = false }, title = { Text("????") }, text = { OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("??") }) }, confirmButton = { TextButton(onClick = { if (name.isNotBlank()) { viewModel.createShelf(name); showCreateDialog = false } }) { Text("??") } }, dismissButton = { TextButton(onClick = { showCreateDialog = false }) { Text("??") } }) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(navController: NavController, viewModel: BookViewModel = hiltViewModel()) {
    val sourceId = navController.previousBackStackEntry?.arguments?.getString("sourceId") ?: ""
    val bookId = navController.previousBackStackEntry?.arguments?.getString("bookId") ?: ""
    var detail by remember { mutableStateOf<BookDetail?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    LaunchedEffect(sourceId, bookId) { isLoading = true; viewModel.getBookDetail(sourceId, BookItem(id = bookId, sourceId = sourceId)) { detail = it; isLoading = false } }
    Scaffold(topBar = { WujiTopBar(title = detail?.item?.title ?: "????", onBack = { navController.popBackStack() }) }) { padding ->
        if (isLoading) LoadingIndicator(Modifier.padding(padding))
        else detail?.let { d ->
            Column(Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState())) {
                Row(Modifier.padding(16.dp)) {
                    Box(Modifier.size(100.dp).clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.surfaceVariant)) { coil.compose.AsyncImage(model = d.item.cover, contentDescription = null, modifier = Modifier.fillMaxSize()) }
                    Spacer(Modifier.width(16.dp))
                    Column { Text(d.item.title, style = MaterialTheme.typography.titleMedium); Text("??: ${d.item.author}", fontSize = 14.sp, color = MaterialTheme.colorScheme.outline); Text("??: ${d.item.status}", fontSize = 14.sp, color = MaterialTheme.colorScheme.outline); Text("??: ${d.item.lastChapter}", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline) }
                }
                Text(d.item.intro, modifier = Modifier.padding(16.dp), fontSize = 14.sp, maxLines = 5, overflow = TextOverflow.Ellipsis)
                HorizontalDivider()
                Text("??", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
                LazyColumn(Modifier.heightIn(max = 400.dp)) {
                    items(d.chapters) { chapter ->
                        ListItem(headlineContent = { Text(chapter.title, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 14.sp) }, modifier = Modifier.clickable { viewModel.updateReadProgress(d.item, sourceId, chapter); navController.navigate(Route.BookRead.createRoute(sourceId, d.item.id, chapter.id)) })
                    }
                }
            }
        } ?: EmptyState("????", Modifier.padding(padding))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookReadScreen(navController: NavController, viewModel: BookViewModel = hiltViewModel()) {
    val sourceId = navController.previousBackStackEntry?.arguments?.getString("sourceId") ?: ""
    val bookId = navController.previousBackStackEntry?.arguments?.getString("bookId") ?: ""
    val chapterId = navController.previousBackStackEntry?.arguments?.getString("chapterId") ?: ""
    var content by remember { mutableStateOf<BookContent?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showSettings by remember { mutableStateOf(false) }
    LaunchedEffect(sourceId, bookId, chapterId) { isLoading = true; viewModel.getContent(sourceId, BookItem(id = bookId, sourceId = sourceId), BookChapter(id = chapterId)) { content = it; isLoading = false } }
    Scaffold(topBar = { WujiTopBar(title = content?.chapter?.title ?: "??", onBack = { navController.popBackStack() }, actions = { IconButton(onClick = { showSettings = !showSettings }) { Icon(Icons.Default.TextFields, contentDescription = "??") } }) }) { padding ->
        if (isLoading) LoadingIndicator(Modifier.padding(padding))
        else content?.let { c -> Column(Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) { Text(c.content, fontSize = 18.sp, lineHeight = androidx.compose.ui.unit.TextUnit(1.6f, androidx.compose.ui.unit.TextUnitType.Sp), style = MaterialTheme.typography.bodyLarge) } } ?: EmptyState("????", Modifier.padding(padding))
    }
    if (showSettings) { AlertDialog(onDismissRequest = { showSettings = false }, title = { Text("????") }, text = { Column { Text("??"); Row { Slider(value = 18f, onValueChange = {}, valueRange = 12f..32f); Text("18") } } }, confirmButton = { TextButton(onClick = { showSettings = false }) { Text("??") } }) }
}
