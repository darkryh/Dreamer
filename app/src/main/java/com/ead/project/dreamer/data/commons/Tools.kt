package com.ead.project.dreamer.data.commons

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.database.Cursor
import android.graphics.Insets
import android.graphics.Rect
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.text.Layout
import android.text.format.Formatter.formatIpAddress
import android.util.Size
import android.util.TypedValue
import android.view.*
import android.webkit.CookieManager
import android.webkit.URLUtil
import android.webkit.WebView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.ead.project.dreamer.app.DreamerApp
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.models.ChapterDownload
import com.ead.project.dreamer.data.models.VideoModel
import com.ead.project.dreamer.data.utils.DataStore
import com.ead.project.dreamer.data.utils.DirectoryManager
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

        fun getAutomaticSizeReference(reference_measure : Int): Int {
            val dpWidth = getDeviceWidth()
            return (dpWidth / reference_measure).toInt()
        }

        fun stringRawArrayToList(string: String) : List<String> {
            return string
                .removePrefix("[\"")
                .removeSuffix("\"]")
                .split("${Constants.QUOTATION},${Constants.QUOTATION}")
        }

        fun embedLink(string: String) : String {
            if (string.contains("url="))  return string.substringAfter("url=")
            return string.trim()
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

        private fun getDeviceWidth() : Float {
            val metrics = DreamerApp.INSTANCE.resources.displayMetrics
            return metrics.widthPixels / metrics.density
        }

        fun launchIntent(activity: Activity, chapter : Chapter, typeClass: Class<*>?, playList: List<VideoModel>, isDirect: Boolean=true) {
            activity.startActivity(Intent(activity,typeClass).apply {
                putExtra(Constants.REQUESTED_CHAPTER, chapter)
                putExtra(Constants.REQUESTED_IS_DIRECT,isDirect)
                putParcelableArrayListExtra(Constants.PLAY_VIDEO_LIST, playList as java.util.ArrayList<out Parcelable>)
            })
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

            }catch (e : Exception) {
                DataStore.writeBoolean(Constants.PROFILE_SENDER_VIDEO_PLAYER,false)
                e.printStackTrace()
            }
        }

        @SuppressLint("DiscouragedApi")
        fun getNavigationBarHeight(context: Context, orientation: Int): Int {
            val resources: Resources = context.resources
            val id: Int = resources.getIdentifier(
                if (orientation == Configuration.ORIENTATION_PORTRAIT) "navigation_bar_height" else "navigation_bar_height_landscape",
                "dimen", "android"
            )
            return if (id > 0) {
                resources.getDimensionPixelSize(id)
            } else 0
        }

        @Suppress("DEPRECATION")
        fun getScreenSize (activity : Activity) : Size {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                val metrics: WindowMetrics = activity.windowManager.currentWindowMetrics
                val windowInsets = metrics.windowInsets
                val insets: Insets = windowInsets.getInsetsIgnoringVisibility(
                    WindowInsets.Type.navigationBars()
                            or WindowInsets.Type.displayCutout()
                )
                val insetsWidth: Int = insets.right + insets.left
                val insetsHeight: Int = insets.top + insets.bottom
                val bounds: Rect = metrics.bounds
                return Size(
                    bounds.width() - insetsWidth,
                    bounds.height() - insetsHeight
                )
            }
            val display: Display = activity.windowManager.defaultDisplay
            val width: Int = display.width
            val height: Int = display.height
            return Size(width,height)
        }

        fun checkGooglePolicies(genres: MutableList<String>) : String {
            var value = genres.random()
            if (Constants.isGooglePolicyActivate())
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

        fun downloadRequest(chapter: Chapter,url : String) : DownloadManager.Request {
            val request = DownloadManager.Request(Uri.parse(url))
            val fileDirectory = File(DirectoryManager.getSeriesFolder().absolutePath, chapter.title)
            fileDirectory.manageFolder()
            request.configureChapter(chapter)
            return request
        }

        @SuppressLint("Range")
        fun Cursor.getChapterDownload() : ChapterDownload = getObject(getDescription())

        @SuppressLint("Range")
        fun Cursor.getDescription(): String = getString(getColumnIndex(DownloadManager.COLUMN_DESCRIPTION))

        private fun DownloadManager.isChapterInProgress(chapter: Chapter) : Boolean {
            val cursor : Cursor = this.query(DownloadManager.Query().setFilterByStatus(
                DownloadManager.STATUS_SUCCESSFUL or DownloadManager.STATUS_RUNNING
            or DownloadManager.STATUS_PENDING or DownloadManager.STATUS_PAUSED
            ))
            while (cursor.moveToNext()) if (chapter.id == cursor.getChapterDownload().idChapter) return true

            return false
        }

        fun DownloadManager.isChapterNotInProgress(chapter: Chapter) = !isChapterInProgress(chapter)

        private fun DownloadManager.Request.configureChapter(chapter: Chapter) {
            apply {
                setTitle("${chapter.title} Cap.${chapter.chapterNumber}")
                setDescription(
                    ChapterDownload(
                    chapter.id,
                    0,
                    chapter.idProfile,
                    chapter.title,
                    chapter.chapterCover,
                    chapter.chapterNumber,
                    chapter.downloadState,
                    0,
                    0).toJson())
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    DirectoryManager.mainFolder
                            + "/"  + DirectoryManager.seriesFolder
                            + "/" + chapter.title + "/" + chapter.title +
                            " Capítulo ${chapter.chapterNumber}" + ".mp4"
                )
            }
        }

        private inline fun <reified T> getObject(string: String) : T = Gson().fromJson(string, T::class.java)

        fun Any.toJson() : String = try { Gson().toJson(this) } catch (e : Exception) { "null" }

        inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
            SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
            else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
        }

        inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
            SDK_INT >= 33 -> getParcelable(key, T::class.java)
            else -> @Suppress("DEPRECATION") getParcelable(key) as? T
        }

        inline fun <reified T : Parcelable> Bundle.parcelableArrayList(key: String): ArrayList<T>? = when {
            SDK_INT >= 33 -> getParcelableArrayList(key, T::class.java)
            else -> @Suppress("DEPRECATION") getParcelableArrayList(key)
        }

        inline fun <reified T : Parcelable> Intent.parcelableArrayList(key: String): ArrayList<T>? = when {
            SDK_INT >= 33 -> getParcelableArrayListExtra(key, T::class.java)
            else -> @Suppress("DEPRECATION") getParcelableArrayListExtra(key)
        }

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
                window.setDecorFitsSystemWindows(false)
                window.insetsController?.show(WindowInsets.Type.systemBars())
            } else {
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility = (
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
            }
        }

        @SuppressLint("WrongConstant")
        fun TextView.justifyInterWord() {
            if (SDK_INT >= Build.VERSION_CODES.O) {
                this.justificationMode =
                    Layout.JUSTIFICATION_MODE_INTER_WORD
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

        fun <T> Collection<T>.notContains(e:T) = !this.contains(e)

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

        fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
            observe(lifecycleOwner, object : Observer<T> {
                override fun onChanged(t: T?) {
                    observer.onChanged(t)
                    removeObserver(this)
                }
            })
        }

        fun Float.round(decimals: Int): Double {
            var multiplier = 1.0
            repeat(decimals) { multiplier *= 10 }
            return round(this * multiplier) / multiplier
        }

        fun View.setVisibility(visible : Boolean) {
            if (visible) this.visibility = View.VISIBLE
            else this.visibility = View.GONE
        }

        fun View.setVisibilityReverse(visible : Boolean) {
            if (visible) this.visibility = View.VISIBLE
            else this.visibility = View.INVISIBLE
        }

        fun View.margin(left: Float? = null, top: Float? = null, right: Float? = null, bottom: Float? = null) {
            layoutParams<ViewGroup.MarginLayoutParams> {
                left?.run { leftMargin = dpToPx(this) }
                top?.run { topMargin = dpToPx(this) }
                right?.run { rightMargin = dpToPx(this) }
                bottom?.run { bottomMargin = dpToPx(this) }
            }
        }

        inline fun <reified T : ViewGroup.LayoutParams> View.layoutParams(block: T.() -> Unit) {
            if (layoutParams is T) block(layoutParams as T)
        }

        fun File.manageFolder() { if (!exists()) mkdirs() }

        fun urlIsValid(reference: String) = URLUtil.isValidUrl(reference)

        private fun View.dpToPx(dp: Float): Int = context.dpToPx(dp)
        private fun Context.dpToPx(dp: Float): Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).toInt()

        fun AppCompatActivity.onBack() {
            if(!onBackPressedDispatcher.hasEnabledCallbacks()){
                onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() { finish() } })
            }
            onBackPressedDispatcher.onBackPressed()
        }

        fun AppCompatActivity.onBackHandle(task: () -> Unit) {
            if(!onBackPressedDispatcher.hasEnabledCallbacks()){
                onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() { task() } })
            }
        }

        fun AppCompatActivity.onBackHandlePressed() {
            if (onBackPressedDispatcher.hasEnabledCallbacks()) onBackPressedDispatcher.onBackPressed()
        }

        fun WebView.clearCookies() {
            CookieManager.getInstance().removeAllCookies(null)
            CookieManager.getInstance().flush()
        }

        fun WebView.load(url : String) {
            clearData()
            loadUrl(url)
        }

        fun WebView.clearData() {
            this.clearHistory()
            this.clearCache(true)
        }

        fun WebView.onDestroy() {
            this.loadUrl("about:blank")
            this.onPause()
            this.removeAllViews()
            this.destroy()
        }
    }
}