package com.ead.project.dreamer.data.models.server

import com.ead.project.dreamer.data.models.Player
import com.ead.project.dreamer.data.models.Server
import com.ead.project.dreamer.data.models.server_properties.ONE_FICHIER_API_TOKEN
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class Onefichier (embeddedUrl:String) : Server(embeddedUrl) {

    companion object {
        const val apiDownloadRequest = "https://api.1fichier.com/v1/download/get_token.cgi"
    }

    override fun onPreExtract() {
        player = Player.Onefichier
    }

    override fun onExtract() {
        try {
            val request = Request.Builder()
                .url(apiDownloadRequest)
                .header("Authorization", "Bearer ${getApiToken()}")
                .header("Content-Type", "application/json")
                .post(getRequestBody())
                .build()

            val client = OkHttpClient()
            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val responseBody = response.body?.string()?:return

                val source = JSONObject(responseBody)
                val status = source.getString("status")
                if (status != "OK") return
                url = source.getString("url")
                addDefaultVideo()
                endProcessing()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun getApiToken() = ONE_FICHIER_API_TOKEN

    private fun getRequestBody() : RequestBody {
        val jsonRequest = "{" +
                "\"url\":\"${url}\"," +
                "\"pretty\":1" +
                "}"
        return jsonRequest.toRequestBody(
            "application/json".toMediaTypeOrNull()
        )
    }
}