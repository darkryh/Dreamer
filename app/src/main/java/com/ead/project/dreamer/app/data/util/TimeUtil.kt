package com.ead.project.dreamer.app.data.util

import java.util.Calendar
import java.util.Date

object TimeUtil {

    fun getNow() : Date = Calendar.getInstance().time

}