package com.ead.project.dreamer.app.data.util

import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import kotlin.jvm.Throws

object HttpUtil {

    private lateinit var httpURLConnection : HttpURLConnection
    private const val GET = "GET"

    const val BLANK_BROWSER = "about:blank"

    @Throws(IOException::class)
    private fun openConnection(url : String)  {
        val urlObject = URL(url)
        httpURLConnection = urlObject.openConnection() as HttpURLConnection
        httpURLConnection.requestMethod = GET
        httpURLConnection.connect()
    }

    private fun closeConnection() {
        httpURLConnection.disconnect()
    }

    private fun getResponseCode(url: String) : Int {
        return try {
            openConnection(url)
            val responseCode = httpURLConnection.responseCode
            closeConnection()
            return responseCode
        } catch (exception : IOException) {
            exception.printStackTrace()
            -1
        }
    }

    fun isConnectionAvailableInt(url: String): Int {
        return when(getResponseCode(url)) {
            200 -> 1
            -1 -> -1
            else -> 0
        }
    }

    fun connectionAvailable(url: String) : Boolean {
        return getResponseCode(url) in 200..300
    }
}