package com.ead.project.dreamer.data.commons


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Insets
import android.graphics.Rect
import android.os.Build
import android.util.Size
import android.view.Display
import android.view.View
import android.view.WindowInsets
import android.view.WindowMetrics
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.ead.project.dreamer.app.DreamerApp
import com.ead.project.dreamer.data.utils.DataStore
import com.ead.project.dreamer.ui.profile.AnimeProfileActivity


class Tools {

    companion object {

        fun getAutomaticSizeReference(reference_measure : Int): Int {
            val dpWidth = getDeviceWidth()
            return (dpWidth / reference_measure).toInt()
        }

        fun stringRawArrayToList(string: String) : List<String> {
            return string
                .removePrefix("[${Constants.QUOTATION}")
                .removeSuffix("]${Constants.QUOTATION}")
                .split("${Constants.QUOTATION},${Constants.QUOTATION}")
        }

        fun embedLink(string: String) : String = string.substringAfter("url=")
            .replace(Constants.QUOTATION.toString(),"")
            .replace("[","")
            .replace("]","")

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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
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

        fun hideSystemUI(activity: Activity, view : View) {
            WindowCompat.setDecorFitsSystemWindows(activity.window, false)
            WindowInsetsControllerCompat(activity.window, view).let { controller ->
                controller.hide(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }
}