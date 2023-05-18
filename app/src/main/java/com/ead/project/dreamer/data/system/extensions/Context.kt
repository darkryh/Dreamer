package com.ead.project.dreamer.data.system.extensions

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment

fun Context.toast(text : String,duration : Int = Toast.LENGTH_LONG) {
    Toast.makeText(this,text,duration).show()
}

fun Fragment.toast(text : String,duration : Int = Toast.LENGTH_LONG) {
    requireActivity().toast(text, duration)
}