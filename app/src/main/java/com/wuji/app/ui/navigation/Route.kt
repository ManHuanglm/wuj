package com.wuji.app.ui.navigation

sealed class Route(val route: String) {
    data object Photo : Route("photo")
    data object PhotoShelf : Route("photo/shelf")
    data object PhotoDetail : Route("photo/{sourceId}/{id}") {
        fun createRoute(sourceId: String, id: String) = "photo/$sourceId/$id"
    }

    data object Song : Route("song")
    data object SongShelf : Route("song/shelf")
    data object SongShelfDetail : Route("song/shelf/{shelfId}") {
        fun createRoute(shelfId: String) = "song/shelf/$shelfId"
    }
    data object PlaylistDetail : Route("song/playlist/{sourceId}/{playlistId}") {
        fun createRoute(sourceId: String, playlistId: String) = "song/playlist/$sourceId/$playlistId"
    }
    data object SongPlayView : Route("song/playing")

    data object Book : Route("book")
    data object BookShelf : Route("book/shelf")
    data object BookDetail : Route("book/{sourceId}/{bookId}") {
        fun createRoute(sourceId: String, bookId: String) = "book/$sourceId/$bookId"
    }
    data object BookRead : Route("book/{sourceId}/{bookId}/{chapterId}") {
        fun createRoute(sourceId: String, bookId: String, chapterId: String) = "book/$sourceId/$bookId/$chapterId"
    }

    data object Comic : Route("comic")
    data object ComicShelf : Route("comic/shelf")
    data object ComicDetail : Route("comic/{sourceId}/{comicId}") {
        fun createRoute(sourceId: String, comicId: String) = "comic/$sourceId/$comicId"
    }
    data object ComicRead : Route("comic/{sourceId}/{comicId}/{chapterId}") {
        fun createRoute(sourceId: String, comicId: String, chapterId: String) = "comic/$sourceId/$comicId/$chapterId"
    }

    data object Video : Route("video")
    data object VideoShelf : Route("video/shelf")
    data object VideoDetail : Route("video/{sourceId}/{videoId}") {
        fun createRoute(sourceId: String, videoId: String) = "video/$sourceId/$videoId"
    }

    data object Download : Route("download")
    data object Setting : Route("setting")
    data object About : Route("about")
    data object SourceManage : Route("source/manage")
    data object SourceMarket : Route("source/market")
    data object SourceCreate : Route("source/create")
    data object SourceMy : Route("source/my")
    data object SourceMyEdit : Route("source/my/{sourceId}") {
        fun createRoute(sourceId: String) = "source/my/$sourceId"
    }
    data object User : Route("user")
    data object Login : Route("user/login")
    data object VipDetail : Route("user/vip-detail")
    data object ManageSync : Route("user/manage-sync")
    data object SyncToServer : Route("user/toserver")
    data object SyncFromServer : Route("user/from-server")
}
