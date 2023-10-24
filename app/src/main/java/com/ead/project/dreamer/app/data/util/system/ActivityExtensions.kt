package com.ead.project.dreamer.app.data.util.system

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.ead.commons.lib.lifecycle.activity.onBack
import com.ead.commons.lib.views.getMutated
import com.ead.project.dreamer.R

@Suppress("DEPRECATION")
fun Activity.hideSystemUI() {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

        window.insetsController?.let {
            it.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            it.hide(WindowInsets.Type.systemBars())
        }

    } else {

        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)

        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
    }
}

@Suppress("DEPRECATION")
fun Activity.showSystemUI() {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

        window.let {
            it.setDecorFitsSystemWindows(false)
            it.insetsController?.show(WindowInsets.Type.systemBars())
        }

    } else {

        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)

    }
}

fun AppCompatActivity.handleNotActionBar(toolbar: Toolbar, isDarkTheme : Boolean) {
    supportActionBar(toolbar)
    if (isDarkTheme) {
        toolbar.navigationIcon =
            toolbar.navigationIcon?.getMutated(this, R.color.white)
    }
    else {
        toolbar.navigationIcon =
            toolbar.navigationIcon?.getMutated(this, R.color.blackPrimary)
    }
    toolbar.setNavigationOnClickListener { onBack() }
}

fun AppCompatActivity.handleNotActionBar(toolbar: Toolbar) {
    supportActionBar(toolbar)
    toolbar.navigationIcon =
        toolbar.navigationIcon?.getMutated(this, R.color.white)
    toolbar.setNavigationOnClickListener { onBack() }
}

private fun AppCompatActivity.supportActionBar(toolbar: Toolbar) {
    supportActionBar?.hide()
    setSupportActionBar(toolbar)
    supportActionBar?.setDisplayShowTitleEnabled(false)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
}

@Suppress("DEPRECATION")
fun AppCompatActivity.openTransition(enterAnim : Int, exitAnim : Int) {
    if (Build.VERSION.SDK_INT >= 34) {
        overrideActivityTransition(AppCompatActivity.OVERRIDE_TRANSITION_OPEN, enterAnim, exitAnim)
    }
    else {
        overridePendingTransition(
            R.anim.fade_in, R.anim.fade_out)
    }
}

@Suppress("DEPRECATION")
fun AppCompatActivity.closeTransition(enterAnim : Int, exitAnim : Int)  {
    if (Build.VERSION.SDK_INT >= 34) {
        overrideActivityTransition(AppCompatActivity.OVERRIDE_TRANSITION_CLOSE, enterAnim, exitAnim)
    }
    else {
        overridePendingTransition(
            R.anim.fade_in, R.anim.fade_out)
    }
}