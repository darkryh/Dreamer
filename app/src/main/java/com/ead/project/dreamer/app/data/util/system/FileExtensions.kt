package com.ead.project.dreamer.app.data.util.system

import java.io.File

fun File.configureFolder() {
    if (!exists()) mkdirs()
}