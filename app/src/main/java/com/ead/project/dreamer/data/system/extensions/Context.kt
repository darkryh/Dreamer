package com.ead.project.dreamer.data.system.extensions

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment

fun Context.toast(text : String,duration : Int = Toast.LENGTH_LONG) {
    Toast.makeText(this,text,duration).show()
}

fun Context.debug(text: String) {
    Log.d(DEBUG, text)
}

fun Context.error(text: String) {
    Log.e(ERROR, text)
}

fun Fragment.toast(text : String,duration : Int = Toast.LENGTH_LONG) {
    requireActivity().toast(text, duration)
}

const val DEBUG = "debug"
const val ERROR = "error"