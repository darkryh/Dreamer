package com.ead.project.dreamer.data.commons

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.content.Intent
import android.database.Cursor
import android.graphics.*
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Parcelable
import android.text.format.Formatter.formatIpAddress
import android.view.*
import android.webkit.URLUtil
import android.webkit.WebView
import androidx.core.content.FileProvider
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.DreamerApp
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.models.DownloadItem
import com.ead.project.dreamer.data.models.VideoModel
import com.ead.project.dreamer.data.utils.DataStore
import com.ead.project.dreamer.data.utils.WebServer
import com.ead.project.dreamer.ui.profile.AnimeProfileActivity
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.gson.Gson
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.round


class Tools {

    companion object {

        private fun getWifiIpAddress(context: Context): String {
            return if (SDK_INT >= Build.VERSION_CODES.R) {
                val connectivityManager =
                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val properties = connectivityManager.getLinkProperties(connectivityManager.activeNetwork)
                properties?.let {
                    it.linkAddresses[1].address?.hostAddress
                }?:"localhost"
            }
            else {
                val manager = context.applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
                @Suppress("DEPRECATION") val dhcp = manager.dhcpInfo
                @Suppress("DEPRECATION") formatIpAddress(dhcp.ipAddress)
            }
        }

        fun getWebServerAddress() : String = "http://" + getWifiIpAddress(DreamerApp.INSTANCE) + ":${WebServer.PORT}"

        fun isConnectionAvailable(url: String): Boolean {
            return try {
                val urlObject = URL(url)
                val connection: HttpURLConnection = urlObject.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connect()
                val code = connection.responseCode
                connection.disconnect()
                when (code) {
                    200 -> true
                    else -> false
                }
            } catch (e : Exception) { false }
        }

        fun isConnectionAvailableInt(url: String): Int {
            return try {
                val urlObject = URL(url)
                val connection: HttpURLConnection = urlObject.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connect()
                val code = connection.responseCode
                connection.disconnect()
                when (code) {
                    200 -> 1
                    else -> 0
                }
            } catch (e : Exception) { -1 }
        }

        private fun connectionType(context: Context): Int {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    return NetworkCapabilities.TRANSPORT_CELLULAR
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    return NetworkCapabilities.TRANSPORT_WIFI
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    return NetworkCapabilities.TRANSPORT_WIFI
                }
            }
            return -1
        }

        fun isConnectionIncompatible() = connectionType(DreamerApp.INSTANCE) != NetworkCapabilities.TRANSPORT_WIFI

        fun longToSeconds(long: Long): Int = long.toInt() / 1000

        fun secondsToLong(int: Int): Long = (int * 1000).toLong()

        fun launchIntent(activity: Activity, chapter : Chapter, typeClass: Class<*>?, playList: List<VideoModel>, isDirect: Boolean=true) {
            activity.startActivity(Intent(activity,typeClass).apply {
                putExtra(Constants.REQUESTED_CHAPTER, chapter)
                putExtra(Constants.REQUESTED_IS_DIRECT,isDirect)
                putParcelableArrayListExtra(Constants.PLAY_VIDEO_LIST, playList as java.util.ArrayList<out Parcelable>)
            })
        }

        fun installApk(context: Context, apkFile: File) {
            try {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(
                    FileProvider
                        .getUriForFile(
                            context,
                            context.applicationContext.packageName + Constants.FILES_PROVIDER_PATH,
                            apkFile),
                    Constants.INSTALL_MIME_TYPE
                )
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                context.startActivity(intent)
            } catch (e : Exception) {
                e.printStackTrace()
                DreamerApp.showLongToast(context.getString(R.string.warning_error_installing))
            }
        }
        fun launchRequestedProfile(context: Context) {
            try {
                if (DataStore.readBoolean(Constants.PROFILE_SENDER_VIDEO_PLAYER)) {
                    DataStore.writeBooleanAsync(Constants.PROFILE_SENDER_VIDEO_PLAYER,false)
                    context.startActivity(
                        Intent(context, AnimeProfileActivity::class.java)
                            .putExtra(Constants.PREFERENCE_ID_BASE,
                                DataStore.readInt(Constants.VALUE_VIDEO_PLAYER_ID_PROFILE))
                            .putExtra(Constants.PREFERENCE_LINK,
                                DataStore.readString(Constants.VALUE_VIDEO_PLAYER_LINK)))
                }

            } catch (e : Exception) {
                DataStore.writeBoolean(Constants.PROFILE_SENDER_VIDEO_PLAYER,false)
                e.printStackTrace()
            }
        }

        fun filterGenreByGooglePolicies(genres: List<String>) : String {
            var value = genres.random()
            if (value == Constants.TYPE_ECCHI) {
                for (genre in genres) {
                    if (genre != Constants.TYPE_ECCHI){
                        value = genre
                        break
                    }
                }
            }
            return value
        }

        @SuppressLint("Range")
        fun Cursor.downloadItem() : DownloadItem = getObject(getDescription())

        @SuppressLint("Range")
        fun Cursor.getDescription(): String = getString(getColumnIndex(DownloadManager.COLUMN_DESCRIPTION))

        private inline fun <reified T> getObject(string: String) : T = Gson().fromJson(string, T::class.java)


        fun Any.toJson() : String = try { Gson().toJson(this) } catch (e : Exception) { "null" }


        fun Activity.hideSystemUI() {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                window.insetsController?.let {
                    it.systemBarsBehavior =
                        WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                    it.hide(WindowInsets.Type.systemBars())
                }
            } else {
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility = (
                        View.SYSTEM_UI_FLAG_IMMERSIVE
                                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                or View.SYSTEM_UI_FLAG_FULLSCREEN
                                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
                @Suppress("DEPRECATION")
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            }
        }

        fun Activity.showSystemUI() {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                window.let {
                    it.setDecorFitsSystemWindows(false)
                    it.insetsController?.show(WindowInsets.Type.systemBars())
                }
            } else {
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility = (
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
            }
        }

        fun ShimmerFrameLayout.show() {
            startShimmer()
            visibility = View.VISIBLE
        }

        fun ShimmerFrameLayout.hide() {
            stopShimmer()
            visibility = View.GONE
        }

        fun <T>Collection<T>.notContains(e:T) = !this.contains(e)

        fun String.delete(string: String) = replace(string,"")

        fun String.contains(stringList: List<String>) : Boolean {
            for (data in stringList) if (data in this) return true
            return false
        }

        fun String?.isNotNullOrNotEmpty() = !isNullOrEmpty()

        fun List<String>.getCatch(index: Int) : String = try { this[index] } catch (e : Exception) {""}

        fun String.toIntCatch() : Int = try { toInt() } catch (e : Exception) { -1 }

        fun String.toFloatCatch() : Float = try { toFloat() } catch (e : Exception) { -1f }

        fun Elements.getCatch(index : Int) : Element = try{this[index]} catch (e : Exception) { Element("null") }

        fun Float.round(decimals: Int): Double {
            var multiplier = 1.0
            repeat(decimals) { multiplier *= 10 }
            return round(this * multiplier) / multiplier
        }

        fun File.manageFirstTimeFolder()  {
            if (Constants.isFirstDirectoryInstall()) {
                if (exists()) deleteRecursively()
                else mkdirs()
                Constants.disableDirectoryInstall()
            }
            else manageFolder()
        }

        fun File.manageFolder() { if (!exists()) mkdirs() }

        fun urlIsValid(reference: String) = URLUtil.isValidUrl(reference)

        fun WebView.load(url : String) {
            clearData()
            loadUrl(url)
        }

        fun WebView.clearData() {
            this.clearHistory()
            this.clearCache(true)
        }

        fun WebView.onDestroy() {
            this.loadUrl(Constants.BLANK_BROWSER)
            this.onPause()
            this.removeAllViews()
            this.destroy()
        }
    }
}