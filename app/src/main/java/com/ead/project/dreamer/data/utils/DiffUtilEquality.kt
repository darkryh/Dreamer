package com.ead.project.dreamer.data.utils

interface DiffUtilEquality {

    fun equalsHeader(other: Any?): Boolean

    fun equalsContent(other: Any?): Boolean
}