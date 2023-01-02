package com.ead.project.dreamer.di

import android.content.Context
import androidx.room.Room
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.WorkManager
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.database.AnimeDatabase
import com.ead.project.dreamer.data.database.dao.*
import com.ead.project.dreamer.data.network.WebProvider
import com.ead.project.dreamer.data.models.discord.Discord
import com.ead.project.dreamer.data.utils.NotificationManager
import com.ead.project.dreamer.data.utils.media.CastManager
import com.ead.project.dreamer.data.utils.ui.DownloadDesigner
import com.ead.project.dreamer.domain.*
import com.ead.project.dreamer.domain.apis.app.*
import com.ead.project.dreamer.domain.apis.discord.*
import com.ead.project.dreamer.domain.configurations.*
import com.ead.project.dreamer.domain.databasequeries.*
import com.ead.project.dreamer.domain.downloads.*
import com.ead.project.dreamer.domain.operations.DeleteObject
import com.ead.project.dreamer.domain.operations.InsertObject
import com.ead.project.dreamer.domain.operations.UpdateObject
import com.ead.project.dreamer.domain.servers.*
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
        AnimeDatabase.DATABASE
    ).build()

    @Singleton
    @Provides
    fun provideRepository(
        animeBaseDao: AnimeBaseDao,
        animeProfileDao: AnimeProfileDao,
        chapterHomeDao: ChapterHomeDao,
        chapterDao: ChapterDao,
        newsItemDao: NewsItemDao,
        retrofit: Retrofit
    ): AnimeRepository = AnimeRepository(
        animeBaseDao = animeBaseDao,
        animeProfileDao = animeProfileDao,
        chapterHomeDao = chapterHomeDao,
        chapterDao = chapterDao,
        newsItemDao = newsItemDao,
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
    fun provideNewsItemDao(database: AnimeDatabase): NewsItemDao = database.newsItemDao()

    @Singleton
    @Provides
    fun provideWebProvider(
        getHomeScrap: GetHomeScrap,
        getDirectoryScrap: GetDirectoryScrap,
        getProfileScrap: GetProfileScrap,
        getChapterScrap: GetChapterScrap,
        getNewsItemScrap: GetNewsItemScrap,
        getNewsItemWebScrap: GetNewsItemWebScrap
    ): WebProvider = WebProvider(getHomeScrap, getDirectoryScrap, getProfileScrap, getChapterScrap, getNewsItemScrap, getNewsItemWebScrap)

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
    fun provideNotificationManagerGoogle(@ApplicationContext context: Context) : android.app.NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager

    @Singleton
    @Provides
    fun provideNotificationManager(@ApplicationContext context: Context): NotificationManager =
        NotificationManager(context)

    @Singleton
    @Provides
    fun provideDreamerCast() : CastManager = CastManager()

    @Singleton
    @Provides
    fun provideDownloadManagerGoogle(@ApplicationContext context: Context) : android.app.DownloadManager =
        context.getSystemService(Context.DOWNLOAD_SERVICE) as android.app.DownloadManager

    @Singleton
    @Provides
    fun provideDownloadDesigner(downloadManager: android.app.DownloadManager) : DownloadDesigner =
        DownloadDesigner(downloadManager)


    //DOMAIN

    @Singleton
    @Provides
    fun provideAddDownload(getTempDownloads: GetTempDownloads, downloadEngine: DownloadEngine) : AddDownload
    = AddDownload(getTempDownloads,downloadEngine)

    @Singleton
    @Provides
    fun provideApplicationManager(
        getApplicationAds: GetApplicationAds,
        getAppStatusVersion: GetAppStatusVersion
    ) : ApplicationManager = ApplicationManager(getApplicationAds, getAppStatusVersion)

    @Singleton
    @Provides
    fun provideChapterManager(
        getChapter: GetChapter,
        getChapters: GetChapters,
        getChaptersToDownload: GetChaptersToDownload,
        getChaptersToFix: GetChaptersToFix,
        getChapterScrap: GetChapterScrap
    ) : ChapterManager = ChapterManager(getChapter,getChapters, getChaptersToDownload,getChaptersToFix,getChapterScrap)

    @Singleton
    @Provides
    fun provideCheckIfUpdateIsAlreadyDownloaded() : CheckIfUpdateIsAlreadyDownloaded
    = CheckIfUpdateIsAlreadyDownloaded()

    @Singleton
    @Provides
    fun provideConfigureChapters(
        repository: AnimeRepository,
        launchOneTimeRequest: LaunchOneTimeRequest
    ) : ConfigureChapters = ConfigureChapters(repository,launchOneTimeRequest)

    @Singleton
    @Provides
    fun provideConfigureDownloadRequest() : ConfigureDownloadRequest
    = ConfigureDownloadRequest()

    @Singleton
    @Provides
    fun provideConfigureProfile(
        repository: AnimeRepository,
        launchOneTimeRequest: LaunchOneTimeRequest
    ) : ConfigureProfile
    = ConfigureProfile(repository, launchOneTimeRequest)

    @Singleton
    @Provides
    fun provideConfigureRecords(
        repository: AnimeRepository
    ) : ConfigureRecords
    = ConfigureRecords(repository)

    @Singleton
    @Provides
    fun provideCreateDownloadRequest(
        downloadManager: android.app.DownloadManager
    ) : CreateDownloadRequest
    = CreateDownloadRequest(downloadManager)

    @Singleton
    @Provides
    fun provideDeleteObject(
        repository: AnimeRepository
    ) : DeleteObject = DeleteObject(repository)

    @Singleton
    @Provides
    fun provideDirectoryManager(
        getDirectoryList: GetDirectoryList,
        getDirectory: GetDirectory,
        getDirectoryScrap: GetDirectoryScrap
    ) : DirectoryManager = DirectoryManager(getDirectoryList, getDirectory,getDirectoryScrap)

    @Singleton
    @Provides
    fun provideDiscordManager(
        getDiscordMember: GetDiscordMember,
        getDiscordUserData: GetDiscordUserData,
        getDiscordUserInToGuild: GetDiscordUserInToGuild,
        getDiscordUserRefreshToken: GetDiscordUserRefreshToken,
        getDiscordUserToken: GetDiscordUserToken
    ) : DiscordManager = DiscordManager(getDiscordMember, getDiscordUserData, getDiscordUserInToGuild, getDiscordUserRefreshToken, getDiscordUserToken)

    @Singleton
    @Provides
    fun provideDownloadEngine(
        @ApplicationContext context: Context,
        repository: AnimeRepository,
        tempDownloads: GetTempDownloads,
        getServerResultToArray: GetServerResultToArray,
        getSortedServers: GetSortedServers,
        getServers: GetServers,
        launchDownload: LaunchDownload
    ) : DownloadEngine
    = DownloadEngine(context, repository, tempDownloads, getServerResultToArray, getSortedServers, getServers, launchDownload)

    @Singleton
    @Provides
    fun provideDownloadManager(
        startDownload: StartDownload,
        startManualDownload: StartManualDownload,
        launchManualDownload: LaunchManualDownload,
        launchUpdate: LaunchUpdate,
        filterDownloads: FilterDownloads,
        checkIfUpdateIsAlreadyDownloaded: CheckIfUpdateIsAlreadyDownloaded,
        removeDownload: RemoveDownload
    ) : DownloadManager
    = DownloadManager(startDownload,startManualDownload, launchManualDownload, launchUpdate , filterDownloads, checkIfUpdateIsAlreadyDownloaded, removeDownload)

    @Singleton
    @Provides
    fun provideFilterDownloads(
        getDownloads: GetDownloads,
        getTempDownloads: GetTempDownloads,
        isInDownloadManagerProgress: IsInDownloadManagerProgress
    ) : FilterDownloads
    = FilterDownloads(getDownloads, getTempDownloads, isInDownloadManagerProgress)

    @Singleton
    @Provides
    fun provideGetApplicationAds(repository: AnimeRepository) : GetApplicationAds
    = GetApplicationAds(repository)

    @Singleton
    @Provides
    fun provideGetAppStatusVersion(repository: AnimeRepository) : GetAppStatusVersion
    = GetAppStatusVersion(repository)

    @Singleton
    @Provides
    fun provideGetChapter(repository: AnimeRepository) : GetChapter
    = GetChapter(repository)

    @Singleton
    @Provides
    fun provideGetChapters(repository: AnimeRepository) : GetChapters
    = GetChapters(repository)

    @Singleton
    @Provides
    fun provideGetChapterScrap(repository: AnimeRepository) : GetChapterScrap
    = GetChapterScrap(repository)

    @Singleton
    @Provides
    fun provideGetChaptersToDownload(repository: AnimeRepository) : GetChaptersToDownload
    = GetChaptersToDownload(repository)

    @Singleton
    @Provides
    fun provideGetChaptersToFix(repository: AnimeRepository) : GetChaptersToFix
    = GetChaptersToFix(repository)

    @Singleton
    @Provides
    fun provideGetCursorFromDownloads(downloadManager: android.app.DownloadManager) : GetCursorFromDownloads
    = GetCursorFromDownloads(downloadManager)

    @Singleton
    @Provides
    fun provideGetDirectory(repository: AnimeRepository) : GetDirectory
    = GetDirectory(repository)

    @Singleton
    @Provides
    fun provideGetDirectoryList(repository: AnimeRepository) : GetDirectoryList
    = GetDirectoryList(repository)

    @Singleton
    @Provides
    fun provideGetDirectoryScrap(repository: AnimeRepository) : GetDirectoryScrap
    = GetDirectoryScrap(repository)

    @Singleton
    @Provides
    fun provideGetDiscordMember(repository: AnimeRepository,retrofit: Retrofit) : GetDiscordMember
    = GetDiscordMember(repository,retrofit)

    @Singleton
    @Provides
    fun provideGetDiscordUserData(repository: AnimeRepository) : GetDiscordUserData
    = GetDiscordUserData(repository)

    @Singleton
    @Provides
    fun provideGetDiscordUserInToGuild(repository: AnimeRepository) : GetDiscordUserInToGuild
    = GetDiscordUserInToGuild(repository)

    @Singleton
    @Provides
    fun provideGetDiscordUserRefreshToken(repository: AnimeRepository) : GetDiscordUserRefreshToken
    = GetDiscordUserRefreshToken(repository)

    @Singleton
    @Provides
    fun provideGetDiscordUserToken(repository: AnimeRepository) : GetDiscordUserToken
    = GetDiscordUserToken(repository)

    @Singleton
    @Provides
    fun provideGetDownload() : GetDownloads = GetDownloads()

    @Singleton
    @Provides
    fun provideGetEmbedServer(@ApplicationContext context: Context,getServerResultToArray: GetServerResultToArray) : GetEmbedServers
    = GetEmbedServers(context, getServerResultToArray)

    @Singleton
    @Provides
    fun provideGetEmbedServerMutable(@ApplicationContext context: Context,getServerResultToArray: GetServerResultToArray) : GetEmbedServersMutable
    = GetEmbedServersMutable(context, getServerResultToArray)

    @Singleton
    @Provides
    fun provideGetHomeList(repository: AnimeRepository) : GetHomeList
    = GetHomeList(repository)

    @Singleton
    @Provides
    fun provideGetHomeRecommendations(repository: AnimeRepository) : GetHomeRecommendations
    = GetHomeRecommendations(repository)

    @Singleton
    @Provides
    fun provideGetHomeReleaseList(repository: AnimeRepository) : GetHomeReleaseList
    = GetHomeReleaseList(repository)

    @Singleton
    @Provides
    fun provideGetHomeScrap(repository: AnimeRepository) : GetHomeScrap
    = GetHomeScrap(repository)

    @Singleton
    @Provides
    fun provideGetLikedProfiles(repository: AnimeRepository) : GetLikedProfiles
    = GetLikedProfiles(repository)

    @Singleton
    @Provides
    fun provideMostViewedProfiles(repository: AnimeRepository) : GetMostViewedProfiles
    = GetMostViewedProfiles(repository)

    @Singleton
    @Provides
    fun provideGetNews(repository: AnimeRepository) : GetNews
    = GetNews(repository)

    @Singleton
    @Provides
    fun provideGetNewsItemScrap(repository: AnimeRepository) : GetNewsItemScrap
    = GetNewsItemScrap(repository)

    @Singleton
    @Provides
    fun provideGetNewsItemWebScrap(repository: AnimeRepository) : GetNewsItemWebScrap
    = GetNewsItemWebScrap(repository)

    @Singleton
    @Provides
    fun provideGetProfile(repository: AnimeRepository) : GetProfile
    = GetProfile(repository)

    @Singleton
    @Provides
    fun provideGetProfileInboxRecommendations(repository: AnimeRepository) : GetProfileInboxRecommendations
    = GetProfileInboxRecommendations(repository)

    @Singleton
    @Provides
    fun provideGetProfileList(repository: AnimeRepository) : GetProfileList
    = GetProfileList(repository)

    @Singleton
    @Provides
    fun provideGetProfilePlayerRecommendations(repository: AnimeRepository) : GetProfilePlayerRecommendations
    = GetProfilePlayerRecommendations(repository)

    @Singleton
    @Provides
    fun provideGetProfileScrap(repository: AnimeRepository) : GetProfileScrap
    = GetProfileScrap(repository)

    @Singleton
    @Provides
    fun provideGetProfilesFavoriteReleases(repository: AnimeRepository) : GetProfilesFavoriteReleases
    = GetProfilesFavoriteReleases(repository)

    @Singleton
    @Provides
    fun provideGetProfilesReleases(repository: AnimeRepository) : GetProfilesReleases
    = GetProfilesReleases(repository)

    @Singleton
    @Provides
    fun provideGetProfileToFix(repository: AnimeRepository) : GetProfilesToFix
    = GetProfilesToFix(repository)

    @Singleton
    @Provides
    fun provideGetRecords(repository: AnimeRepository) : GetRecords
    = GetRecords(repository)

    @Singleton
    @Provides
    fun provideGetServer(serverIdentifier: ServerIdentifier) : GetServer
    = GetServer(serverIdentifier)

    @Singleton
    @Provides
    fun provideGetServerResultToArray() : GetServerResultToArray
    = GetServerResultToArray()

    @Singleton
    @Provides
    fun provideGetServers(getServer: GetServer) : GetServers
    = GetServers(getServer)

    @Singleton
    @Provides
    fun provideGetServerScript(repository: AnimeRepository) : GetServerScript
    = GetServerScript(repository)

    @Singleton
    @Provides
    fun provideGetSortedServer(serverIdentifier: ServerIdentifier) : GetSortedServers
    = GetSortedServers(serverIdentifier)

    @Singleton
    @Provides
    fun provideGetTempDownloads() : GetTempDownloads = GetTempDownloads()

    @Singleton
    @Provides
    fun provideHomeManager(
        getHomeList: GetHomeList,
        getHomeRecommendations: GetHomeRecommendations,
        getHomeReleaseList: GetHomeReleaseList,
        getHomeScrap: GetHomeScrap
    ) : HomeManager = HomeManager(getHomeList, getHomeRecommendations,getHomeReleaseList,getHomeScrap)

    @Singleton
    @Provides
    fun provideInsertObject(
        repository: AnimeRepository
    ) : InsertObject = InsertObject(repository)

    @Singleton
    @Provides
    fun provideInstallUpdate(@ApplicationContext context: Context) : InstallUpdate
    = InstallUpdate(context)

    @Singleton
    @Provides
    fun provideInstallWorkers(workManager: WorkManager,constraints: Constraints) : InstallWorkers
    = InstallWorkers(workManager,constraints)

    @Singleton
    @Provides
    fun provideIsInDownloadManagerProgress(getCursorFromDownloads: GetCursorFromDownloads) : IsInDownloadManagerProgress
    = IsInDownloadManagerProgress(getCursorFromDownloads)

    @Singleton
    @Provides
    fun provideLaunchDownload(
        createDownloadRequest: CreateDownloadRequest,
        configureDownloadRequest: ConfigureDownloadRequest
    ) : LaunchDownload
    = LaunchDownload(createDownloadRequest,configureDownloadRequest)

    @Singleton
    @Provides
    fun provideLaunchManualDownload(
        downloadEngine: DownloadEngine,
        launchDownload: LaunchDownload
    ) : LaunchManualDownload
    = LaunchManualDownload(downloadEngine, launchDownload)

    @Singleton
    @Provides
    fun provideLaunchOneTimeRequest(workManager: WorkManager,constraints: Constraints) : LaunchOneTimeRequest
    = LaunchOneTimeRequest(workManager,constraints)

    @Singleton
    @Provides
    fun provideLaunchPeriodicOneTimeRequest(workManager: WorkManager,constraints: Constraints) : LaunchPeriodicTimeRequest
    = LaunchPeriodicTimeRequest(workManager,constraints)

    @Singleton
    @Provides
    fun provideLaunchUpdate(launchDownload: LaunchDownload,installUpdate: InstallUpdate) : LaunchUpdate
    = LaunchUpdate(launchDownload, installUpdate)

    @Singleton
    @Provides
    fun provideNewsManager(
        getNews: GetNews,
        getNewsItemScrap: GetNewsItemScrap,
        getNewsItemWebScrap: GetNewsItemWebScrap
    ) : NewsManager = NewsManager(getNews, getNewsItemScrap ,getNewsItemWebScrap)

    @Singleton
    @Provides
    fun provideObjectManager(
        insertObject: InsertObject,
        updateObject: UpdateObject,
        deleteObject: DeleteObject
    ) : ObjectManager = ObjectManager(insertObject, updateObject, deleteObject)

    @Singleton
    @Provides
    fun provideProfileManager(
        getProfile: GetProfile,
        getProfileList: GetProfileList,
        getProfilesToFix: GetProfilesToFix,
        getLikedProfiles: GetLikedProfiles,
        getMostViewedProfiles: GetMostViewedProfiles,
        getProfileInboxRecommendations: GetProfileInboxRecommendations,
        getProfilePlayerRecommendations: GetProfilePlayerRecommendations,
        getProfilesReleases: GetProfilesReleases,
        getProfilesFavoriteReleases: GetProfilesFavoriteReleases,
        getProfileScrap: GetProfileScrap
    ) : ProfileManager = ProfileManager(getProfile, getProfileList , getProfilesToFix ,getLikedProfiles, getMostViewedProfiles, getProfileInboxRecommendations, getProfilePlayerRecommendations,getProfilesReleases,getProfilesFavoriteReleases,getProfileScrap)

    @Singleton
    @Provides
    fun provideRecordsManager(
        getRecords: GetRecords,
        configureRecords: ConfigureRecords
    ) : RecordsManager = RecordsManager(getRecords, configureRecords)

    @Singleton
    @Provides
    fun provideRemoveDownload(downloadManager: android.app.DownloadManager) : RemoveDownload
    = RemoveDownload(downloadManager)

    @Singleton
    @Provides
    fun provideServerEngine(@ApplicationContext context: Context,getServerResultToArray: GetServerResultToArray) : ServerEngine
    = ServerEngine(context,getServerResultToArray)

    @Singleton
    @Provides
    fun provideServerIdentifier() : ServerIdentifier
    = ServerIdentifier()

    @Singleton
    @Provides
    fun provideServerManager(getServer: GetServer, getServers: GetServers, getEmbedServers: GetEmbedServers ,getEmbedServersMutable: GetEmbedServersMutable, getSortedServers: GetSortedServers, getServerScript: GetServerScript) : ServerManager
    = ServerManager(getServer, getServers, getEmbedServers,getEmbedServersMutable, getSortedServers,getServerScript)

    @Singleton
    @Provides
    fun provideStartDownload(
        @ApplicationContext context: Context,
        filterDownloads: FilterDownloads,
        addDownload: AddDownload,
        removeDownload: RemoveDownload,
        getDownloads: GetDownloads
    ) : StartDownload
    = StartDownload(context, filterDownloads, addDownload, removeDownload, getDownloads)

    @Singleton
    @Provides
    fun provideStartManualDownload(
        @ApplicationContext context: Context,
        getDownloads: GetDownloads,
        filterDownloads: FilterDownloads,
        removeDownload: RemoveDownload
    ) : StartManualDownload
    = StartManualDownload(context, getDownloads, filterDownloads, removeDownload)

    @Singleton
    @Provides
    fun provideUpdateObject(repository: AnimeRepository) : UpdateObject
    = UpdateObject(repository)
}