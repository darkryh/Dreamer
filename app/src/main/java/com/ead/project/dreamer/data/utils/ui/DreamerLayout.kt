package com.ead.project.dreamer.data.utils.ui


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.DreamerApp
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.utils.DataStore
import com.google.android.material.snackbar.Snackbar


class DreamerLayout {

    companion object {

        fun setClickEffect (view: View,context: Context) {
            view.foreground  = with(TypedValue()) {
                context.theme.resolveAttribute(
                    R.attr.selectableItemBackground, this, true)
                ContextCompat.getDrawable(context, resourceId)
            }
        }


        fun getBackgroundColor(drawable: Drawable, color :Int) : Drawable {
            val colorDrawable = getDrawable(drawable)
            colorDrawable.mutate().colorFilter = BlendModeColorFilterCompat
                .createBlendModeColorFilterCompat(
                    DreamerApp.INSTANCE.getColor(color),
                    BlendModeCompat.SRC_ATOP)
            return colorDrawable
        }

        fun setColorFilter(drawable: Drawable, @ColorInt color: Int) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                drawable.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
            } else {
                drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
            }
        }

        private fun getDrawable(drawable: Drawable) : Drawable = drawable
            .constantState?.newDrawable()!!

        @SuppressLint("UseCompatLoadingForDrawables")
        fun getDrawable(resourcesId : Int) : Drawable {
            return DreamerApp.INSTANCE.resources.getDrawable(resourcesId,DreamerApp.INSTANCE.theme)
        }

        fun getColor(colorId: Int) = ContextCompat.getColor(DreamerApp.INSTANCE, colorId)

        fun isDarkTheme() = DataStore.readBoolean(Constants.PREFERENCE_THEME_MODE)

        @SuppressLint("CutPasteId")
        fun showSnackbar(view: View, text : String, color: Int = R.color.blackPrimary,size : Int = R.dimen.snackbar_text_size,length : Int = Snackbar.LENGTH_SHORT) {
            val snackbar: Snackbar = Snackbar.make(view, text, length)
            snackbar.setBackgroundTint(ContextCompat.getColor(DreamerApp.INSTANCE, color))
            val viewGroup = snackbar.view
                .findViewById<View>(com.google.android.material.R.id.snackbar_text).parent as ViewGroup
            viewGroup.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER
            }
            viewGroup.setPadding(64,0,64,0)
            val textView = snackbar.view
                .findViewById<View>(com.google.android.material.R.id.snackbar_text) as TextView
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DreamerApp.INSTANCE.resources.getDimension(size))

            if (length == Snackbar.LENGTH_INDEFINITE) {
                val progressBar = ProgressBar(DreamerApp.INSTANCE)

                setColorFilter(progressBar.indeterminateDrawable, getColor(R.color.blue_light))
                viewGroup.addView(progressBar)
                progressBar.layoutParams.height = 80
                (progressBar.layoutParams as LinearLayout.LayoutParams).gravity = Gravity.CENTER
            }
            snackbar.show()
        }
    }
}