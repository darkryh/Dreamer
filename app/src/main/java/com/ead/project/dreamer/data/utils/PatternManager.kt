package com.ead.project.dreamer.data.utils

import java.util.regex.Pattern

class PatternManager {

    companion object {

        fun sliceReference(url : String): String? {

            val regex = "([vf])([/=])(.+)([/&])?"
            val pattern = Pattern.compile(regex)
            val matcher = pattern.matcher(url)
            return if (matcher.find()) {
                matcher.group(3)?.replace("[&/]", "")
            }
            else null
        }


        fun variableReference(url: String,regex : String): String? {
            val pattern = Pattern.compile(regex, Pattern.MULTILINE)
            val matcher = pattern.matcher(url)
            return if (matcher.find()) {
                matcher.group(1)
            } else null
        }
    }
}