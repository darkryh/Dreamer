package com.ead.project.dreamer.data.commons

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Insets
import android.graphics.Rect
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import android.text.Layout
import android.util.Size
import android.util.TypedValue
import android.view.*
import android.webkit.URLUtil
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.ead.project.dreamer.app.DreamerApp
import com.ead.project.dreamer.data.utils.DataStore
import com.ead.project.dreamer.ui.profile.AnimeProfileActivity
import com.facebook.shimmer.ShimmerFrameLayout
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.io.File
import kotlin.math.round


class Tools {

    companion object {

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


        fun longToSeconds(long: Long): Int = long.toInt() / 1000

        fun secondsToLong(int: Int): Long = (int * 1000).toLong()

        private fun getDeviceWidth() : Float {
            val metrics = DreamerApp.INSTANCE.resources.displayMetrics
            return metrics.widthPixels / metrics.density
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

        fun String.contains(stringList: List<String>) : Boolean {
            for (data in stringList) if (data in this) return true
            return false
        }

        fun List<String>.getCatch(index: Int) : String = try { this[index] } catch (e : Exception) {""}

        fun String.toIntCatch() : Int = try { this.toInt() } catch (e : Exception) { -1 }

        fun String.toFloatCatch() : Float = try { this.toFloat() } catch (e : Exception) { -1f }

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
    }
}