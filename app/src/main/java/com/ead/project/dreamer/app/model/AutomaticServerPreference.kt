package com.ead.project.dreamer.app.model

data class AutomaticServerPreference(
    val internalServerList : List<String>,
    val externalServerList : List<String>,
    val downloadServerList : List<String>
) {

    fun sameQuantityServers(other: AutomaticServerPreference) : Boolean {
        return internalServerList.size == externalServerList.size &&
                internalServerList.size == downloadServerList.size &&
                internalServerList.size == other.internalServerList.size &&
                externalServerList.size == other.externalServerList.size &&
                downloadServerList.size == other.downloadServerList.size
    }
}