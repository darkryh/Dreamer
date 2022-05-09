package com.ead.project.dreamer.di

import android.content.Context
import androidx.room.Room
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.WorkManager
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.database.AnimeDatabase
import com.ead.project.dreamer.data.database.dao.AnimeBaseDao
import com.ead.project.dreamer.data.database.dao.AnimeProfileDao
import com.ead.project.dreamer.data.database.dao.ChapterDao
import com.ead.project.dreamer.data.database.dao.ChapterHomeDao
import com.ead.project.dreamer.data.network.WebProvider
import com.ead.project.dreamer.data.retrofit.model.discord.Discord
import com.ead.project.dreamer.data.utils.media.CastManager
import com.ead.project.dreamer.data.utils.receiver.DreamerNotifier
import com.ead.project.dreamer.data.worker.factory.DaggerWorkerFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DreamerModule {

    @Singleton
    @Provides
    fun provideRoomInstance(
        @ApplicationContext context: Context
    ): AnimeDatabase = Room.databaseBuilder(
        context,
        AnimeDatabase::class.java,
        "anime_Database"
    ).build()

    @Singleton
    @Provides
    fun provideRepository(
        animeBaseDao: AnimeBaseDao,
        animeProfileDao: AnimeProfileDao,
        chapterHomeDao: ChapterHomeDao,
        chapterDao: ChapterDao,
        retrofit: Retrofit
    ): AnimeRepository = AnimeRepository(
        animeBaseDao = animeBaseDao,
        animeProfileDao = animeProfileDao,
        chapterHomeDao = chapterHomeDao,
        chapterDao = chapterDao,
        retrofit = retrofit
    )

    @Singleton
    @Provides
    fun provideRetrofit() : Retrofit = Retrofit.Builder()
        .baseUrl(Discord.ENDPOINT)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Singleton
    @Provides
    fun provideAnimeBaseDao(database: AnimeDatabase): AnimeBaseDao = database.animeBaseDao()

    @Singleton
    @Provides
    fun provideAnimeProfileDao(database: AnimeDatabase): AnimeProfileDao =
        database.animeProfileDao()

    @Singleton
    @Provides
    fun provideChapterHomeDao(database: AnimeDatabase): ChapterHomeDao = database.chapterHomeDao()

    @Singleton
    @Provides
    fun provideChapterDao(database: AnimeDatabase): ChapterDao = database.chapterDao()

    @Singleton
    @Provides
    fun provideWebProvider(): WebProvider = WebProvider()

    @Singleton
    @Provides
    fun provideWorkManager(
        @ApplicationContext context: Context
    ): WorkManager = WorkManager.getInstance(context)

    @Singleton
    @Provides
    fun provideConstraintsNetwork(): Constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.UNMETERED)
        .build()

    @Singleton
    @Provides
    fun provideDaggerWorkerFactory(
        repository: AnimeRepository,
        webProvider: WebProvider
    ): DaggerWorkerFactory = DaggerWorkerFactory(
        repository, webProvider,
        provideDreamerNotifier()
    )

    @Singleton
    @Provides
    fun provideDreamerNotifier(): DreamerNotifier = DreamerNotifier()

    @Singleton
    @Provides
    fun provideDreamerCast() : CastManager = CastManager()
    //Casting
}