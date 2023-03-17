package com.ead.project.dreamer.data.system.extensions

import android.content.Context
import android.widget.Toast

fun Context.toast(text : String,duration : Int = Toast.LENGTH_LONG) {
    Toast.makeText(this,text,duration).show()
}