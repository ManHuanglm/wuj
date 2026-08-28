package com.wuji.app.ui.navigation

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.wuji.app.ui.screens.*
import com.wuji.app.ui.viewmodel.*

data class TabItem(val route: String, val label: String, val icon: ImageVector, val selectedIcon: ImageVector)

val photoTab = TabItem("photo", "??", Icons.Outlined.Photo, Icons.Filled.Photo)
val songTab = TabItem("song", "??", Icons.Outlined.MusicNote, Icons.Filled.MusicNote)
val bookTab = TabItem("book", "??", Icons.Outlined.Book, Icons.Filled.Book)
val comicTab = TabItem("comic", "??", Icons.Outlined.Collections, Icons.Filled.Collections)
val videoTab = TabItem("video", "??", Icons.Outlined.VideoLibrary, Icons.Filled.VideoLibrary)

val bottomTabs = listOf(photoTab, songTab, bookTab, comicTab, videoTab)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WujiApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = bottomTabs.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomTabs.forEach { tab ->
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    if (currentRoute == tab.route) tab.selectedIcon else tab.icon,
                                    contentDescription = tab.label,
                                )
                            },
                            label = { Text(tab.label) },
                            selected = currentRoute == tab.route,
                            onClick = {
                                if (currentRoute != tab.route) {
                                    navController.navigate(tab.route) {
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                        )
                    }
                }
            }
        },
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Route.Photo.route,
            modifier = Modifier.padding(paddingValues),
            enterTransition = { fadeIn() + slideInHorizontally { it / 4 } },
            exitTransition = { fadeOut() + slideOutHorizontally { -it / 4 } },
            popEnterTransition = { fadeIn() + slideInHorizontally { -it / 4 } },
            popExitTransition = { fadeOut() + slideOutHorizontally { it / 4 } },
        ) {
            // Photo
            composable(Route.Photo.route) { PhotoScreen(navController) }
            composable(Route.PhotoShelf.route) { PhotoShelfScreen(navController) }
            composable(
                Route.PhotoDetail.route,
                arguments = listOf(
                    navArgument("sourceId") { type = NavType.StringType },
                    navArgument("id") { type = NavType.StringType },
                ),
            ) { PhotoDetailScreen(navController) }

            // Song
            composable(Route.Song.route) { SongScreen(navController) }
            composable(Route.SongShelf.route) { SongShelfScreen(navController) }
            composable(
                Route.SongShelfDetail.route,
                arguments = listOf(navArgument("shelfId") { type = NavType.StringType }),
            ) { SongShelfDetailScreen(navController) }
            composable(
                Route.PlaylistDetail.route,
                arguments = listOf(
                    navArgument("sourceId") { type = NavType.StringType },
                    navArgument("playlistId") { type = NavType.StringType },
                ),
            ) { PlaylistDetailScreen(navController) }
            composable(Route.SongPlayView.route) { SongPlayViewScreen(navController) }

            // Book
            composable(Route.Book.route) { BookScreen(navController) }
            composable(Route.BookShelf.route) { BookShelfScreen(navController) }
            composable(
                Route.BookDetail.route,
                arguments = listOf(
                    navArgument("sourceId") { type = NavType.StringType },
                    navArgument("bookId") { type = NavType.StringType },
                ),
            ) { BookDetailScreen(navController) }
            composable(
                Route.BookRead.route,
                arguments = listOf(
                    navArgument("sourceId") { type = NavType.StringType },
                    navArgument("bookId") { type = NavType.StringType },
                    navArgument("chapterId") { type = NavType.StringType },
                ),
            ) { BookReadScreen(navController) }

            // Comic
            composable(Route.Comic.route) { ComicScreen(navController) }
            composable(Route.ComicShelf.route) { ComicShelfScreen(navController) }
            composable(
                Route.ComicDetail.route,
                arguments = listOf(
                    navArgument("sourceId") { type = NavType.StringType },
                    navArgument("comicId") { type = NavType.StringType },
                ),
            ) { ComicDetailScreen(navController) }
            composable(
                Route.ComicRead.route,
                arguments = listOf(
                    navArgument("sourceId") { type = NavType.StringType },
                    navArgument("comicId") { type = NavType.StringType },
                    navArgument("chapterId") { type = NavType.StringType },
                ),
            ) { ComicReadScreen(navController) }

            // Video
            composable(Route.Video.route) { VideoScreen(navController) }
            composable(Route.VideoShelf.route) { VideoShelfScreen(navController) }
            composable(
                Route.VideoDetail.route,
                arguments = listOf(
                    navArgument("sourceId") { type = NavType.StringType },
                    navArgument("videoId") { type = NavType.StringType },
                ),
            ) { VideoDetailScreen(navController) }

            // Other
            composable(Route.Download.route) { DownloadManagerScreen(navController) }
            composable(Route.Setting.route) { SettingScreen(navController) }
            composable(Route.About.route) { AboutScreen(navController) }
            composable(Route.SourceManage.route) { ManageSourceScreen(navController) }
            composable(Route.SourceMarket.route) { SourceMarketScreen(navController) }
            composable(Route.SourceCreate.route) { CreateSourceScreen(navController) }
            composable(Route.SourceMy.route) { MySourceScreen(navController) }
            composable(
                Route.SourceMyEdit.route,
                arguments = listOf(navArgument("sourceId") { type = NavType.StringType }),
            ) { MySourceEditScreen(navController) }

            composable(Route.User.route) { UserScreen(navController) }
            composable(Route.Login.route) { LoginScreen(navController) }
            composable(Route.VipDetail.route) { VipDetailScreen(navController) }
            composable(Route.ManageSync.route) { ManageSyncScreen(navController) }
            composable(Route.SyncToServer.route) { SyncToServerScreen(navController) }
            composable(Route.SyncFromServer.route) { SyncFromServerScreen(navController) }
        }
    }
}
