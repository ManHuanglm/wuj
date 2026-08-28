package com.wuji.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.wuji.app.data.local.DataStoreManager
import com.wuji.app.data.repository.SourceRepository
import com.wuji.app.ui.components.*
import com.wuji.app.ui.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(val dataStore: DataStoreManager) : ViewModel()

@HiltViewModel
class SourceViewModel @Inject constructor(private val sourceRepo: SourceRepository) : ViewModel() {
    val sources: StateFlow<List<com.wuji.app.data.model.SubscribeSource>> = sourceRepo.subscribeSources
    fun loadSources() { viewModelScope.launch { sourceRepo.loadSources() } }
    fun importSource(url: String) { viewModelScope.launch { sourceRepo.addSubscribeSource(url) } }
    fun removeSource(id: String) { viewModelScope.launch { sourceRepo.removeSubscribeSource(id) } }
    fun updateSource(id: String) { viewModelScope.launch { sourceRepo.updateSubscribeSource(id) } }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(navController: NavController) {
    val vm: SettingViewModel = hiltViewModel()
    val dataStore = vm.dataStore
    val scope = rememberCoroutineScope()
    val darkMode by dataStore.darkMode.collectAsState(initial = false)
    val showHistory by dataStore.showHistory.collectAsState(initial = true)
    val songAutoSwitch by dataStore.songAutoSwitch.collectAsState(initial = true)
    val autoUpdateSource by dataStore.autoUpdateSource.collectAsState(initial = true)
    val keepScreenOn by dataStore.keepScreenOn.collectAsState(initial = false)

    Scaffold(topBar = { WujiTopBar(title = "??", onBack = { navController.popBackStack() }) }) { padding ->
        LazyColumn(Modifier.padding(padding)) {
            item { HorizontalDivider() }
            item { ListItem(headlineContent = { Text("????") }, trailingContent = { Switch(checked = darkMode, onCheckedChange = { scope.launch { dataStore.setDarkMode(it) } }) }) }
            item { ListItem(headlineContent = { Text("??????") }, trailingContent = { Switch(checked = showHistory, onCheckedChange = { scope.launch { dataStore.setShowHistory(it) } }) }) }
            item { ListItem(headlineContent = { Text("??????") }, trailingContent = { Switch(checked = songAutoSwitch, onCheckedChange = { scope.launch { dataStore.setSongAutoSwitch(it) } }) }) }
            item { ListItem(headlineContent = { Text("???????") }, trailingContent = { Switch(checked = autoUpdateSource, onCheckedChange = { scope.launch { dataStore.setAutoUpdateSource(it) } }) }) }
            item { ListItem(headlineContent = { Text("?????????") }, trailingContent = { Switch(checked = keepScreenOn, onCheckedChange = { scope.launch { dataStore.setKeepScreenOn(it) } }) }) }
            item { HorizontalDivider() }
            item { ListItem(headlineContent = { Text("????") }, leadingContent = { Icon(Icons.Default.Download, contentDescription = null) }, modifier = Modifier.clickable { navController.navigate(Route.Download.route) }) }
            item { ListItem(headlineContent = { Text("?????") }, leadingContent = { Icon(Icons.Default.Source, contentDescription = null) }, modifier = Modifier.clickable { navController.navigate(Route.SourceManage.route) }) }
            item { ListItem(headlineContent = { Text("?????") }, leadingContent = { Icon(Icons.Default.Store, contentDescription = null) }, modifier = Modifier.clickable { navController.navigate(Route.SourceMarket.route) }) }
            item { ListItem(headlineContent = { Text("?????") }, leadingContent = { Icon(Icons.Default.AddCircle, contentDescription = null) }, modifier = Modifier.clickable { navController.navigate(Route.SourceCreate.route) }) }
            item { ListItem(headlineContent = { Text("?????") }, leadingContent = { Icon(Icons.Default.Person, contentDescription = null) }, modifier = Modifier.clickable { navController.navigate(Route.SourceMy.route) }) }
            item { ListItem(headlineContent = { Text("?????") }, leadingContent = { Icon(Icons.Default.Cloud, contentDescription = null) }, modifier = Modifier.clickable { navController.navigate(Route.User.route) }) }
            item { ListItem(headlineContent = { Text("??") }, leadingContent = { Icon(Icons.Default.Info, contentDescription = null) }, modifier = Modifier.clickable { navController.navigate(Route.About.route) }) }
            item { HorizontalDivider() }
            item { ListItem(headlineContent = { Text("????") }, leadingContent = { Icon(Icons.Default.Cached, contentDescription = null) }, modifier = Modifier.clickable { }) }
            item { ListItem(headlineContent = { Text("??????") }, leadingContent = { Icon(Icons.Default.DeleteForever, contentDescription = null) }, modifier = Modifier.clickable { scope.launch { dataStore.clearAll() } }) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navController: NavController) {
    Scaffold(topBar = { WujiTopBar(title = "??", onBack = { navController.popBackStack() }) }) { padding ->
        Column(Modifier.padding(padding).fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(48.dp))
            Box(Modifier.size(96.dp).clip(RoundedCornerShape(24.dp)).background(MaterialTheme.colorScheme.primaryContainer), Alignment.Center) {
                Text("??", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
            Spacer(Modifier.height(16.dp))
            Text("??", style = MaterialTheme.typography.titleLarge)
            Text("??????????????????????", fontSize = 14.sp, color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(horizontal = 32.dp))
            Spacer(Modifier.height(8.dp))
            Text("??: 0.2.7", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
            Spacer(Modifier.height(32.dp))
            Button(onClick = {}) { Text("????") }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = {}) { Text("????") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadManagerScreen(navController: NavController) {
    Scaffold(topBar = { WujiTopBar(title = "????", onBack = { navController.popBackStack() }) }) { padding ->
        EmptyState("??????", Modifier.padding(padding))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageSourceScreen(navController: NavController, viewModel: SourceViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) { viewModel.loadSources() }
    val sources by viewModel.sources.collectAsState()
    var showImportDialog by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            WujiTopBar(title = "?????", onBack = { navController.popBackStack() }, actions = {
                IconButton(onClick = { navController.navigate(Route.SourceMarket.route) }) { Icon(Icons.Default.Store, contentDescription = "??") }
                IconButton(onClick = { showImportDialog = true }) { Icon(Icons.Default.Add, contentDescription = "??") }
            })
        },
    ) { padding ->
        if (sources.isEmpty()) {
            EmptyState("?????,????????", Modifier.padding(padding))
        } else {
            LazyColumn(Modifier.padding(padding)) {
                items(sources) { source ->
                    ListItem(
                        headlineContent = { Text(source.name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        supportingContent = { Text("${source.detail.urls.size} ??", fontSize = 12.sp) },
                        trailingContent = { Switch(checked = !source.disable, onCheckedChange = { }) },
                    )
                    HorizontalDivider()
                }
            }
        }
    }
    if (showImportDialog) {
        var url by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text("?????") },
            text = { OutlinedTextField(value = url, onValueChange = { url = it }, label = { Text("?????") }, singleLine = true) },
            confirmButton = { TextButton(onClick = { if (url.isNotBlank()) { viewModel.importSource(url); showImportDialog = false } }) { Text("??") } },
            dismissButton = { TextButton(onClick = { showImportDialog = false }) { Text("??") } },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SourceMarketScreen(navController: NavController) {
    Scaffold(topBar = { WujiTopBar(title = "?????", onBack = { navController.popBackStack() }) }) { padding ->
        EmptyState("?????", Modifier.padding(padding))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSourceScreen(navController: NavController) {
    Scaffold(topBar = { WujiTopBar(title = "?????", onBack = { navController.popBackStack() }) }) { padding ->
        EmptyState("??????", Modifier.padding(padding))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MySourceScreen(navController: NavController) {
    Scaffold(topBar = { WujiTopBar(title = "?????", onBack = { navController.popBackStack() }) }) { padding ->
        EmptyState("???????", Modifier.padding(padding))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MySourceEditScreen(navController: NavController) {
    Scaffold(topBar = { WujiTopBar(title = "?????", onBack = { navController.popBackStack() }) }) { padding ->
        EmptyState("???", Modifier.padding(padding))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(navController: NavController) {
    Scaffold(topBar = { WujiTopBar(title = "??", onBack = { navController.popBackStack() }) }) { padding ->
        LazyColumn(Modifier.padding(padding)) {
            item { ListItem(headlineContent = { Text("??/??") }, leadingContent = { Icon(Icons.Default.Login, contentDescription = null) }, modifier = Modifier.clickable { navController.navigate(Route.Login.route) }) }
            item { ListItem(headlineContent = { Text("????") }, leadingContent = { Icon(Icons.Default.Star, contentDescription = null) }, modifier = Modifier.clickable { navController.navigate(Route.VipDetail.route) }) }
            item { ListItem(headlineContent = { Text("????") }, leadingContent = { Icon(Icons.Default.Sync, contentDescription = null) }, modifier = Modifier.clickable { navController.navigate(Route.ManageSync.route) }) }
            item { ListItem(headlineContent = { Text("?????") }, leadingContent = { Icon(Icons.Default.CloudUpload, contentDescription = null) }, modifier = Modifier.clickable { navController.navigate(Route.SyncToServer.route) }) }
            item { ListItem(headlineContent = { Text("?????") }, leadingContent = { Icon(Icons.Default.CloudDownload, contentDescription = null) }, modifier = Modifier.clickable { navController.navigate(Route.SyncFromServer.route) }) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isRegister by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    Scaffold(topBar = { WujiTopBar(title = if (isRegister) "??" else "??", onBack = { navController.popBackStack() }) }) { padding ->
        Column(Modifier.padding(padding).fillMaxSize().padding(32.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            if (isRegister) { OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("??") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("??") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("??") }, singleLine = true, visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
            Button(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) { Text(if (isRegister) "??" else "??") }
            TextButton(onClick = { isRegister = !isRegister }, modifier = Modifier.fillMaxWidth()) { Text(if (isRegister) "????????" else "????????") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VipDetailScreen(navController: NavController) {
    Scaffold(topBar = { WujiTopBar(title = "??", onBack = { navController.popBackStack() }) }) { padding ->
        EmptyState("??????", Modifier.padding(padding))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageSyncScreen(navController: NavController) {
    Scaffold(topBar = { WujiTopBar(title = "????", onBack = { navController.popBackStack() }) }) { padding ->
        Column(Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            Text("????", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Text("???,????????????????", fontSize = 14.sp, color = MaterialTheme.colorScheme.outline)
            Spacer(Modifier.height(16.dp))
            ListItem(headlineContent = { Text("????") }, trailingContent = { Switch(checked = false, onCheckedChange = {}) })
            ListItem(headlineContent = { Text("????") }, trailingContent = { Switch(checked = false, onCheckedChange = {}) })
            ListItem(headlineContent = { Text("????") }, trailingContent = { Switch(checked = false, onCheckedChange = {}) })
            ListItem(headlineContent = { Text("????") }, trailingContent = { Switch(checked = false, onCheckedChange = {}) })
            ListItem(headlineContent = { Text("????") }, trailingContent = { Switch(checked = false, onCheckedChange = {}) })
            ListItem(headlineContent = { Text("???") }, trailingContent = { Switch(checked = false, onCheckedChange = {}) })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyncToServerScreen(navController: NavController) {
    Scaffold(topBar = { WujiTopBar(title = "?????", onBack = { navController.popBackStack() }) }) { padding ->
        EmptyState("??????????", Modifier.padding(padding))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyncFromServerScreen(navController: NavController) {
    Scaffold(topBar = { WujiTopBar(title = "?????", onBack = { navController.popBackStack() }) }) { padding ->
        EmptyState("??????????", Modifier.padding(padding))
    }
}
