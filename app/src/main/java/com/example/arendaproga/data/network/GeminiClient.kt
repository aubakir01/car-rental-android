package com.example.arendaproga.data.network

import com.example.arendaproga.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

object AiClient {
    private val API_KEY = BuildConfig.GROQ_API_KEY

    suspend fun ask(prompt: String): String {
        val client = OkHttpClient()
        val body = JSONObject().apply {
            put("model", "llama-3.1-8b-instant")
            put("max_tokens", 1024)
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", prompt)
                })
            })
        }.toString()

        val request = Request.Builder()
            .url("https://api.groq.com/openai/v1/chat/completions")
            .addHeader("Authorization", "Bearer $API_KEY")
            .addHeader("Content-Type", "application/json")
            .post(body.toRequestBody("application/json".toMediaType()))
            .build()

        val response = withContext(Dispatchers.IO) {
            client.newCall(request).execute()
        }
        val json = JSONObject(response.body!!.string())
        return json.getJSONArray("choices")
            .getJSONObject(0)
            .getJSONObject("message")
            .getString("content")
    }
}