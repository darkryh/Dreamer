package com.ead.project.dreamer.data.models

import android.content.Context

open class EmbedServer(context: Context,url : String): Server(context, url) {
    override val isDirect: Boolean
        get() = false

    override suspend fun onExtract() {
        if (isAvailable()) {
            addDefault()
        }
    }

    protected open fun isAvailable(): Boolean {
        return false
    }
}
