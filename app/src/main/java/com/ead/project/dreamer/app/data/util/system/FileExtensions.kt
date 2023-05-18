package com.ead.project.dreamer.app.data.util.system

import java.io.File

fun File.manageFolder() {
    if (!exists()) mkdirs()
}