package com.ead.project.dreamer.data.utils

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.webkit.WebView
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.DreamerApp
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.commons.Tools
import com.ead.project.dreamer.data.commons.Tools.Companion.manageFolder
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.database.model.Server
import com.ead.project.dreamer.data.database.model.VideoChecker
import com.ead.project.dreamer.data.network.DreamerClient
import com.ead.project.dreamer.data.network.DreamerWebView
import com.ead.project.dreamer.data.network.DreamerWebView.Companion.getServerScript
import kotlinx.coroutines.*
import java.io.File

open class DownloadManager(
    private val context: Context,
    val chapterList: MutableList<Chapter>) {

    private var webView : DreamerWebView?= null
    private var downloadManager : DownloadManager?=null
    private var idAdapter =  0L
    private var chapter : Chapter?= null
    private var broadcastReceiver : BroadcastReceiver?= null

    private val scope = CoroutineScope(Dispatchers.IO)

    init { settingDownload() }

    private fun settingDownload() {
        downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        webView = DreamerWebView(DreamerApp.INSTANCE)
        webView?.webViewClient = object : DreamerClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                run {
                    if (timeout && url != Constants.BLANK_BROWSER) {
                        webView?.loadUrl(Constants.BLANK_BROWSER)
                        DreamerApp.showLongToast(DreamerApp.INSTANCE.getString(R.string.timeout_message))
                    }
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                timeout = false
                try {
                    if (url != Constants.BLANK_BROWSER) {
                        webView?.evaluateJavascript(getServerScript()) {
                            val list : MutableList<String> = mutableListOf()
                            for (string in VideoChecker
                                .getSorterServerList(Tools.stringRawArrayToList(it),true)) {
                                list.add(Tools.embedLink(string))
                            }
                            getData(list)
                        }
                    }
                } catch (e : Exception) { e.printStackTrace() }
            }
        }
        startDownload()
    }

    private fun startDownload() {
        chapter = getChapterFromList()
        if (broadcastReceiver == null) {
            broadcastReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,-1)
                    val query: DownloadManager.Query = DownloadManager.Query()
                    query.setFilterById(id!!)
                    val cursor: Cursor = downloadManager!!.query(query)
                    if (cursor.moveToFirst()) {
                        val columnIndex =
                            cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                        val status = cursor.getInt(columnIndex)
                        val columnReason =
                            cursor.getColumnIndex(DownloadManager.COLUMN_REASON)
                        val reason = cursor.getInt(columnReason)
                        when(status) {
                            DownloadManager.STATUS_SUCCESSFUL -> {
                                DreamerApp.showLongToast("SUCCESSFUL")
                            }
                            DownloadManager.STATUS_PAUSED -> {
                                DreamerApp.showLongToast("PAUSED")
                            }
                            DownloadManager.STATUS_RUNNING -> {
                                DreamerApp.showLongToast("RUNNING")
                            }
                            DownloadManager.STATUS_PENDING -> {
                                DreamerApp.showLongToast("PENDING")
                            }
                            DownloadManager.STATUS_FAILED -> {
                                DreamerApp.showLongToast("FAILED")
                            }
                        }
                    }
                }

            }
            context.registerReceiver(broadcastReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        }

        if (chapter != null) {
            webView?.loadUrl(chapterList.first().reference)
        }
        else onCompleted()
    }

    private fun getChapterFromList() : Chapter? = try { chapterList.first() }
    catch (e : Exception) { null }

    private fun getData(it: List<String>) {
        scope.launch (Dispatchers.IO) {
            prepareDownload(withContext(Dispatchers.Default) { ServerManager.getData(it) })
        }
    }

    private fun prepareDownload(serverList: List<Server>) {
        try {
            var isDownloadFailed = true
            for (server in serverList) {
                if (server.isValidated) {
                    handleDownloads(server.videoList.last().directLink)
                    isDownloadFailed = false
                    break
                }
                else if (server.isConnectionValidated) {
                    handleDownloads(server.videoList.last().directLink)
                    isDownloadFailed = false
                    break
                }
            }
            if (isDownloadFailed) runNextChapter()
        }
        catch ( e : InterruptedException) { e.printStackTrace() }
    }

    private fun handleDownloads(selectedUrl : String) {
        ThreadUtil.runOnUiThread {
            val request = DownloadManager.Request(Uri.parse(selectedUrl))
            val fileDirectory = File(DirectoryManager.getSeriesFolder().absolutePath
                ,chapter?.title?:"errorFolder")
            fileDirectory.manageFolder()
            request.apply {
                setTitle("${chapter?.title} Cap. ${chapter?.chapterNumber}")
                setDescription("Capítulo ${chapter?.chapterNumber} Descargando..")
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    DirectoryManager.mainFolder
                            + "/"  + DirectoryManager.seriesFolder
                            + "/" + chapter?.title
                            + "/" + chapter?.title + " Capítulo ${chapter?.chapterNumber}" +".mp4")
            }
            downloadManager?.let { idAdapter = it.enqueue(request) }
            runNextChapter()
        }
    }

    private fun runNextChapter() {
        if (chapter != null) {
            chapterList.removeFirst()
            startDownload()
        }
        else onCompleted()
    }

    open fun onCompleted() {
        downloadManager = null
        context.unregisterReceiver(broadcastReceiver)
        onDestroy()
    }

    private fun onDestroy() {
        scope.cancel()
        webView = null
    }
}