package com.ead.project.dreamer.data.network

import android.text.TextUtils
import android.util.Log
import android.webkit.WebResourceResponse
import java.io.ByteArrayInputStream
import java.net.MalformedURLException
import java.net.URL


object AdBlocker {
    private val AD_HOSTS: Set<String> = HashSet()
    fun isAd(url: String?): Boolean {
        return try {
            isAdHost(getHost(url))
        } catch (e: MalformedURLException) {
            Log.e("Devangi..", e.toString())
            false
        }
    }

    private fun isAdHost(host: String): Boolean {
        if (TextUtils.isEmpty(host)) {
            return false
        }
        val index = host.indexOf(".")
        return index >= 0 && (AD_HOSTS.contains(host) ||
                index + 1 < host.length && isAdHost(host.substring(index + 1)))
    }

    fun createEmptyResource(): WebResourceResponse {
        return WebResourceResponse("text/plain", "utf-8", ByteArrayInputStream("".toByteArray()))
    }

    @Throws(MalformedURLException::class)
    fun getHost(url: String?): String {
        return URL(url).host
    }
}