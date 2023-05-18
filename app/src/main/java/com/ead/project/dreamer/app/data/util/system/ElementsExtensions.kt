package com.ead.project.dreamer.app.data.util.system

import org.jsoup.nodes.Element
import org.jsoup.select.Elements

fun Elements.getCatch(index : Int) : Element {
    return try {
        this[index]
    } catch (e : IndexOutOfBoundsException) {
        Element("null")
    }
}