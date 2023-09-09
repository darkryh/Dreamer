package com.ead.project.dreamer.app.data.util.system

import android.Manifest
import android.app.Activity
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationChannelGroupCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.ead.project.dreamer.R
import com.google.android.material.snackbar.Snackbar


fun Context.launchActivity(typeClass: Class<*>?=null,intent: Intent? = null) {
    startActivity(intent?: Intent(this,typeClass))
}

fun Context.launchActivityAndFinish(typeClass: Class<*>?=null,intent: Intent? = null){
    launchActivity(typeClass, intent)
    if (this is Activity) { this.finish() }
}


val Context.notificationManager: NotificationManager
    get() = getSystemService(NotificationManager::class.java)

val Context.inputMethodManager : InputMethodManager
    get() = getSystemService(InputMethodManager::class.java)

fun Context.notify(id: Int, notification: Notification) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && PermissionChecker.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PermissionChecker.PERMISSION_GRANTED) {
        return
    }
    NotificationManagerCompat.from(this).notify(id, notification)
}

fun Context.cancelNotification(id: Int) {
    NotificationManagerCompat.from(this).cancel(id)
}

fun Context.notificationBuilder(channelId : String, block : (NotificationCompat.Builder.() -> Unit)? =null) : NotificationCompat.Builder {
    val builder = NotificationCompat.Builder(this,channelId)
    if (block != null) {
        builder.block()
    }
    return builder
}

fun Context.buildNotificationChannelGroup(
    channelId: String,
    block: (NotificationChannelGroupCompat.Builder.() -> Unit),
): NotificationChannelGroupCompat {
    val builder = NotificationChannelGroupCompat.Builder(channelId)
    builder.block()
    return builder.build()
}

fun buildNotificationChannel(
    channelId: String,
    channelImportance: Int,
    block: (NotificationChannelCompat.Builder.() -> Unit),
): NotificationChannelCompat {
    val builder = NotificationChannelCompat.Builder(channelId, channelImportance)
    builder.block()
    return builder.build()
}

fun Context.showSnackBar(
    rootView : View,text : String,duration : Int = Snackbar.LENGTH_SHORT,
    color : Int = R.color.orange_peel_dark,textSize : Int = R.dimen.snackbar_text_size,
    isLoading : Boolean = false) {
    Snackbar.make(rootView,text,duration).apply {

        setBackgroundTint(getColorCompact(color))
        val parentViewGroup = getParentViewGroup()

        parentViewGroup.apply {
            setPadding(64,0,64,0)
            val textViewContent = getTextViewContent()
            textViewContent.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                this@showSnackBar.resources.getDimension(textSize)
            )

            if (isLoading) {
                val progressBar = ProgressBar(this@showSnackBar).let {
                    val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 80)
                    layoutParams.gravity = Gravity.CENTER_VERTICAL
                    it.layoutParams = layoutParams
                    it
                }
                addView(progressBar)
            }

        }

        show()
    }
}

fun Context.getColorCompact(@ColorRes colorRes: Int) : Int {
    return ContextCompat.getColor(this,colorRes)
}

fun setDrawableColor(drawable: Drawable, @ColorInt color: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        drawable.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
    } else {
        @Suppress("DEPRECATION") drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    }
}
