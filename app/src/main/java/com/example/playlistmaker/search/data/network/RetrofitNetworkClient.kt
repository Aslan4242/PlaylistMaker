package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.Response
import com.example.playlistmaker.search.data.dto.TracksSearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class RetrofitNetworkClient(private val itunesService: ITunesApi) : NetworkClient {
    override fun doRequest(dto: Any): Response {
        return if (dto is TracksSearchRequest) {
            try {
                val response = itunesService.search(dto.expression).execute()
                val body = response.body() ?: Response()
                body.apply { resultCode = response.code() }
            } catch (e: Exception) {
                Response().apply {
                    resultCode = 400
                    message = e.message.toString()
                }
            }
        } else {
            Response().apply { resultCode = 400 }
        }
    }
}
