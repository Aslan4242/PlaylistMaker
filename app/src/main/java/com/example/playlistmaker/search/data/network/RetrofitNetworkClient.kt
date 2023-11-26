package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.Response
import com.example.playlistmaker.search.data.dto.TracksSearchRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class RetrofitNetworkClient(private val itunesService: ITunesApi) : NetworkClient {
    override suspend fun doRequest(dto: Any): Response {
        if (dto !is TracksSearchRequest) {
            return Response().apply { resultCode = 400 }
        }
        return withContext(Dispatchers.IO) {
            try {
                val response = itunesService.search(dto.expression)
                response.apply { resultCode = 200 }
            } catch (e: Exception) {
                Response().apply {
                    resultCode = 500
                    message = e.message.toString()
                }
            }
        }
    }
}
