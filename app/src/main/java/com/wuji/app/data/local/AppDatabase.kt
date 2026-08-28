package com.wuji.app.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@androidx.room.Entity(tableName = "download_tasks")
data class DownloadTaskEntity(
    @androidx.room.PrimaryKey @androidx.room.ColumnInfo(name = "id") val id: String = "",
    @androidx.room.ColumnInfo(name = "source_id") val sourceId: String = "",
    @androidx.room.ColumnInfo(name = "title") val title: String = "",
    @androidx.room.ColumnInfo(name = "url") val url: String = "",
    @androidx.room.ColumnInfo(name = "save_path") val savePath: String = "",
    @androidx.room.ColumnInfo(name = "category") val category: String = "",
    @androidx.room.ColumnInfo(name = "status") val status: String = "",
    @androidx.room.ColumnInfo(name = "total_size") val totalSize: Long = 0L,
    @androidx.room.ColumnInfo(name = "downloaded_size") val downloadedSize: Long = 0L,
    @androidx.room.ColumnInfo(name = "total_chunks") val totalChunks: Int = 0,
    @androidx.room.ColumnInfo(name = "completed_chunks") val completedChunks: Int = 0,
    @androidx.room.ColumnInfo(name = "headers_json") val headersJson: String = "{}",
    @androidx.room.ColumnInfo(name = "extra_json") val extraJson: String = "{}",
    @androidx.room.ColumnInfo(name = "error") val error: String = "",
    @androidx.room.ColumnInfo(name = "created_at") val createdAt: Long = 0L,
    @androidx.room.ColumnInfo(name = "updated_at") val updatedAt: Long = 0L,
)

@androidx.room.Dao
interface DownloadDao {
    @androidx.room.Query("SELECT * FROM download_tasks ORDER BY created_at DESC")
    suspend fun getAll(): List<DownloadTaskEntity>

    @androidx.room.Query("SELECT * FROM download_tasks WHERE id = :id")
    suspend fun getById(id: String): DownloadTaskEntity?

    @androidx.room.Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insert(task: DownloadTaskEntity)

    @androidx.room.Update
    suspend fun update(task: DownloadTaskEntity)

    @androidx.room.Query("DELETE FROM download_tasks WHERE id = :id")
    suspend fun delete(id: String)

    @androidx.room.Query("DELETE FROM download_tasks")
    suspend fun deleteAll()
}

@Database(entities = [DownloadTaskEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun downloadDao(): DownloadDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "wuji_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
