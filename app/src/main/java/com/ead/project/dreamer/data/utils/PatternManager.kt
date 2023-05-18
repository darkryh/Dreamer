package com.ead.project.dreamer.data.utils

import java.util.regex.Pattern

object PatternManager {

    fun singleMatch(string: String, regex : String, groupIndex : Int = 1): String? {
        val pattern = Pattern.compile(regex, Pattern.MULTILINE)
        val matcher = pattern.matcher(string)
        return if (matcher.find()) {
            matcher.group(groupIndex)
        } else null
    }

    fun multipleMatches(string: String, regex: String, groupIndex: Int=1): List<String> {
        val pattern = Pattern.compile(regex, Pattern.MULTILINE)
        val matcher = pattern.matcher(string)
        val stringArrayList = ArrayList<String>()
        while (matcher.find()) {
            matcher.group(groupIndex)?.let { stringArrayList.add(it) }
        }
        if (stringArrayList.isEmpty()) throw RuntimeException("null")
        return stringArrayList
    }
}