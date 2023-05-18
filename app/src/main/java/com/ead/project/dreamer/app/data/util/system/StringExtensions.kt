package com.ead.project.dreamer.app.data.util.system

fun List<String>.getCatch(index: Int) : String {
    return try { this[index] }
    catch (e : IndexOutOfBoundsException) { "" }
}

fun String.delete(string: String) = replace(string,"")

fun String.contains(stringList: List<String>) : Boolean {
    for (data in stringList) if (data in this) return true
    return false
}

fun String?.isNotNullOrNotEmpty() = !isNullOrEmpty()

fun String.toIntCatch() : Int {
    return try {
        toInt()
    } catch (e : Exception) {
        -1
    }
}

fun String.toFloatCatch() : Float {
    return try {
        toFloat()
    } catch (e : Exception) {
        -1f
    }
}