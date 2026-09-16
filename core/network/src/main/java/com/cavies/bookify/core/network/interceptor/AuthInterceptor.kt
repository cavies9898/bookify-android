package com.cavies.bookify.core.network.interceptor

import com.cavies.bookify.core.network.dto.RefreshTokenRequest
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.Authenticator
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

interface TokenProvider {
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun saveTokens(accessToken: String, refreshToken: String)
    suspend fun clearTokens()
}

@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenProvider: TokenProvider,
    private val json: Json
) : Authenticator {

    override fun authenticate(route: okhttp3.Route?, response: Response): Request? {
        if (response.code != 401) return null
        if (response.request.header("Authorization") == null) return null

        val newToken = runBlocking {
            try {
                val refreshToken = tokenProvider.getRefreshToken() ?: return@runBlocking null
                val client = OkHttpClient.Builder().build()
                val body = json.encodeToString(
                    RefreshTokenRequest.serializer(),
                    RefreshTokenRequest(refreshToken)
                ).toRequestBody("application/json".toMediaType())

                val refreshRequest = Request.Builder()
                    .url("${response.request.url.scheme}://${response.request.url.host}:${response.request.url.port}/api/auth/refresh")
                    .post(body)
                    .build()

                val refreshResponse = client.newCall(refreshRequest).execute()
                if (refreshResponse.isSuccessful) {
                    val responseBody = refreshResponse.body?.string()
                    val authDto = json.decodeFromString(
                        com.cavies.bookify.core.network.dto.AuthResponseDto.serializer(),
                        responseBody ?: return@runBlocking null
                    )
                    tokenProvider.saveTokens(authDto.accessToken, authDto.refreshToken)
                    authDto.accessToken
                } else null
            } catch (_: Exception) {
                null
            }
        } ?: return null

        return response.request.newBuilder()
            .header("Authorization", "Bearer $newToken")
            .build()
    }
}
