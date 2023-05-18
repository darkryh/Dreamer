package com.ead.project.dreamer.app.data.files

import androidx.datastore.core.DataStore
import com.ead.project.dreamer.app.model.FilePreference
import com.ead.project.dreamer.data.database.model.Chapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import javax.inject.Inject

class FilesPreferences @Inject constructor(
    private val store : DataStore<FilePreference>
) {

    private val scope : CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    val preference get() = store.data
    val series : File = Files.seriesFile

    private fun getFilePreference() : FilePreference = runBlocking { store.data.first() }


    fun fetchingPreferences() {
        val preference = getFilePreference()


        if (preference.isFirstTimeChecking) {

            disableTimeChecker()

            if (Files.mainFile.exists()) {
                Files.mainFile.deleteRecursively()
            }

        }
        else {

            if(!preference.isMainFolderCreated){
                setMainFolderToCreate(!Files.mainFile.exists())
            }

            if (!preference.isSeriesFolderCreated){
                setSeriesFolderToCreate(!Files.seriesFile.exists())
            }
        }
    }

    fun getChapterFile(chapter: Chapter) : File {
        return File(getChapterRoute(chapter))
    }

    fun getChapterRoute(chapter: Chapter) : String {
        return series.absolutePath + "/" + chapter.title + "/" + chapter.title +
                " Capítulo ${chapter.number}" +".mp4"
    }

    private fun disableTimeChecker() {
        scope.launch {
            store.updateData { filePreference: FilePreference ->
                filePreference.copy(
                    isFirstTimeChecking = false
                )
            }
        }
    }

    private fun setMainFolderToCreate(needsToCreate : Boolean) {
        scope.launch {
            store.updateData { filePreference: FilePreference ->
                filePreference.copy(
                    isMainFolderCreated = if (needsToCreate) { Files.mainFile.mkdirs() }
                    else { false }
                )
            }
        }
    }

    private fun setSeriesFolderToCreate(needsToCreate: Boolean) {
        scope.launch {
            store.updateData { filePreference: FilePreference ->
                filePreference.copy(
                    isSeriesFolderCreated = if (needsToCreate) { Files.seriesFile.mkdirs() }
                    else { false }
                )
            }
        }
    }
}