package com.wuji.app.data.remote

import com.wuji.app.data.model.*
import kotlinx.serialization.json.JsonElement
import retrofit2.http.*

interface ServerApi {

    @GET("source/market")
    suspend fun getMarketSources(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("sort") sort: String = "time",
        @Query("keyword") keyword: String? = null,
    ): PagedMarketSource

    @GET("source/market/{id}")
    suspend fun getMarketSourceDetail(@Path("id") id: String): MarketSource

    @POST("source/market/{id}/like")
    suspend fun likeMarketSource(@Path("id") id: String): JsonElement

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): UserInfo

    @POST("auth/register")
    suspend fun register(@Body body: RegisterRequest): UserInfo

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body body: Map<String, String>): JsonElement

    @GET("user/info")
    suspend fun getUserInfo(@Header("Authorization") token: String): UserInfo

    @PUT("user/info")
    suspend fun updateUserInfo(
        @Header("Authorization") token: String,
        @Body body: Map<String, String>,
    ): UserInfo

    @GET("announcement/list")
    suspend fun getAnnouncements(): List<Announcement>

    @GET("membership/plans")
    suspend fun getMembershipPlans(): List<MembershipPlan>

    @GET("feature/list")
    suspend fun getFeatures(): List<Feature>

    @POST("sync/upload")
    suspend fun syncToServer(
        @Header("Authorization") token: String,
        @Body body: SyncUploadRequest,
    ): SyncResponse

    @POST("sync/download")
    suspend fun syncFromServer(
        @Header("Authorization") token: String,
        @Body body: SyncDownloadRequest,
    ): SyncResponse

    @POST("taichi/free-trial")
    suspend fun taichiFreeTrial(@Body body: Map<String, String>): JsonElement
}

@kotlinx.serialization.Serializable
data class LoginRequest(
    val email: String,
    val password: String,
)

@kotlinx.serialization.Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String,
)

@kotlinx.serialization.Serializable
data class SyncUploadRequest(
    val items: List<SyncUploadItem>,
)

@kotlinx.serialization.Serializable
data class SyncDownloadRequest(
    val types: List<String>,
    val incremental: Boolean = true,
)

@kotlinx.serialization.Serializable
data class SyncResponse(
    val success: Boolean = false,
    val message: String = "",
    val records: List<SyncDownloadRecord> = emptyList(),
)
