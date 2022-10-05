package com.ead.project.dreamer.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ead.project.dreamer.data.database.dao.*
import com.ead.project.dreamer.data.database.model.*
import com.ead.project.dreamer.data.utils.Converters

@Database(
    entities = [
        AnimeBase::class,
        ChapterHome::class,
        AnimeProfile::class,
        Chapter::class,
        NewsItem::class
    ],
    version = 1,
    exportSchema = false)
@TypeConverters(value = [Converters::class])
abstract class AnimeDatabase : RoomDatabase() {

    abstract fun animeBaseDao() : AnimeBaseDao
    abstract fun chapterHomeDao() : ChapterHomeDao
    abstract fun animeProfileDao() : AnimeProfileDao
    abstract fun chapterDao() : ChapterDao
    abstract fun newsItemDao() : NewsItemDao
}