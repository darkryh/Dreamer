package com.ead.project.dreamer.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ead.project.dreamer.data.database.dao.AnimeBaseDao
import com.ead.project.dreamer.data.database.dao.AnimeProfileDao
import com.ead.project.dreamer.data.database.dao.ChapterDao
import com.ead.project.dreamer.data.database.dao.ChapterHomeDao
import com.ead.project.dreamer.data.database.model.AnimeBase
import com.ead.project.dreamer.data.database.model.AnimeProfile
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.database.model.ChapterHome
import com.ead.project.dreamer.data.utils.Converters

@Database(
    entities = [
        AnimeBase::class,
        ChapterHome::class,
        AnimeProfile::class,
        Chapter::class
    ],
    version = 1,
    exportSchema = false)
@TypeConverters(value = [Converters::class])
abstract class AnimeDatabase : RoomDatabase() {

    abstract fun animeBaseDao() : AnimeBaseDao
    abstract fun chapterHomeDao() : ChapterHomeDao
    abstract fun animeProfileDao() : AnimeProfileDao
    abstract fun chapterDao() : ChapterDao
}