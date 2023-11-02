package com.ead.project.dreamer.app

import android.app.DownloadManager
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import androidx.room.Room
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.WorkManager
import com.ead.project.dreamer.app.data.action.ActionStore
import com.ead.project.dreamer.app.data.ads.AD_PREFERENCE
import com.ead.project.dreamer.app.data.ads.AdPreferences
import com.ead.project.dreamer.app.data.ads.AdPreferencesSerializer
import com.ead.project.dreamer.app.data.discord.Discord
import com.ead.project.dreamer.app.data.downloads.DOWNLOADS_ENQUEUE
import com.ead.project.dreamer.app.data.downloads.DownloadSerializer
import com.ead.project.dreamer.app.data.downloads.DownloadStore
import com.ead.project.dreamer.app.data.files.FILES_PREFERENCE
import com.ead.project.dreamer.app.data.files.FilesPreferences
import com.ead.project.dreamer.app.data.files.FilesSerializer
import com.ead.project.dreamer.app.data.home.HOME_PREFERENCES
import com.ead.project.dreamer.app.data.home.HomeNotifier
import com.ead.project.dreamer.app.data.home.HomePreferences
import com.ead.project.dreamer.app.data.home.HomeSerializer
import com.ead.project.dreamer.app.data.notifications.NotificationManager
import com.ead.project.dreamer.app.data.player.PLAYER_PREFERENCE
import com.ead.project.dreamer.app.data.player.PlayerPreferenceSerializer
import com.ead.project.dreamer.app.data.player.PlayerPreferences
import com.ead.project.dreamer.app.data.player.casting.CastManager
import com.ead.project.dreamer.app.data.preference.APP_BUILD
import com.ead.project.dreamer.app.data.preference.AppBuildPreferences
import com.ead.project.dreamer.app.data.preference.AppBuildSerializer
import com.ead.project.dreamer.app.data.preference.EAD_ACCOUNT
import com.ead.project.dreamer.app.data.preference.EadAccountSerializer
import com.ead.project.dreamer.app.data.preference.EadPreferences
import com.ead.project.dreamer.app.data.preference.Preferences
import com.ead.project.dreamer.app.model.AdPreference
import com.ead.project.dreamer.app.model.AppBuild
import com.ead.project.dreamer.app.model.EadAccount
import com.ead.project.dreamer.app.model.FilePreference
import com.ead.project.dreamer.app.model.HomePreference
import com.ead.project.dreamer.app.model.PlayerPreference
import com.ead.project.dreamer.app.repository.FirebaseClient
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.database.AnimeDatabase
import com.ead.project.dreamer.data.database.dao.*
import com.ead.project.dreamer.data.models.DownloadList
import com.ead.project.dreamer.data.network.WebProvider
import com.ead.project.dreamer.domain.*
import com.ead.project.dreamer.domain.apis.app.*
import com.ead.project.dreamer.domain.apis.discord.*
import com.ead.project.dreamer.domain.apis.monos_chinos.Login
import com.ead.project.dreamer.domain.configurations.*
import com.ead.project.dreamer.domain.databasequeries.*
import com.ead.project.dreamer.domain.directory.GetDirectoryState
import com.ead.project.dreamer.domain.directory.SetDirectoryState
import com.ead.project.dreamer.domain.downloads.*
import com.ead.project.dreamer.domain.downloads.states.DownloadedState
import com.ead.project.dreamer.domain.downloads.states.FailedState
import com.ead.project.dreamer.domain.downloads.states.PausedState
import com.ead.project.dreamer.domain.downloads.states.PendingState
import com.ead.project.dreamer.domain.downloads.states.RunningState
import com.ead.project.dreamer.domain.downloads.states.StreamingState
import com.ead.project.dreamer.domain.operations.DeleteObject
import com.ead.project.dreamer.domain.operations.InsertObject
import com.ead.project.dreamer.domain.operations.UpdateObject
import com.ead.project.dreamer.domain.servers.*
import com.ead.project.dreamer.domain.update.IsAlreadyDownloaded
import com.ead.project.dreamer.domain.update.GetUpdate
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

     @Singleton
     @Provides
     fun provideContext(@ApplicationContext context: Context) : Context
     = context

    @Singleton
    @Provides
    fun provideCastContext(context: Context) : CastContext {
        val executor = Executors.newSingleThreadExecutor()
        val castContextTask: Task<CastContext> = CastContext.getSharedInstance(context, executor)

        return try {
            runBlocking {
                castContextTask.await()
            }
        } catch (e : Exception) {
            throw IllegalStateException("error couldn't get CastContext")
        }
    }

    @Singleton
    @Provides
    fun provideRoomInstance(
        context: Context
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
    fun provideAppReceiver() : AppReceiver = AppReceiver()

    @Singleton
    @Provides
    fun provideActionStore(context: Context) : ActionStore
    = ActionStore(context = context)

    @Singleton
    @Provides
    fun provideAdPreferences(
        store : DataStore<AdPreference>
    ) : AdPreferences
    = AdPreferences(store)

    @Singleton
    @Provides
    fun provideAppBuildPreferences(
        store: DataStore<AppBuild>
    ) : AppBuildPreferences
    = AppBuildPreferences(store)

    @Singleton
    @Provides
    fun provideEadPreferences(
        store : DataStore<EadAccount?>
    ) : EadPreferences
    = EadPreferences(store)

    @Singleton
    @Provides
    fun provideFilesPreferences(
        store : DataStore<FilePreference>,
    ) : FilesPreferences
    = FilesPreferences(store)

    @Singleton
    @Provides
    fun provideHomePreferences(
        store: DataStore<HomePreference>
    ) : HomePreferences
    = HomePreferences(store)

    @Singleton
    @Provides
    fun provideAnimeBaseDao(database: AnimeDatabase): AnimeBaseDao = database.animeBaseDao()

    @Singleton
    @Provides
    fun provideAnimeProfileDao(database: AnimeDatabase): AnimeProfileDao =
        database.animeProfileDao()

    @Singleton
    @Provides
    fun provideCastManager(
        context: Context,
        castContext: CastContext,
        objectUseCase: ObjectUseCase,
        preferenceUseCase: PreferenceUseCase
    ) : CastManager = CastManager(null, context ,objectUseCase , castContext, preferenceUseCase)

    @Singleton
    @Provides
    fun provideChapterHomeDao(database: AnimeDatabase): ChapterHomeDao = database.chapterHomeDao()

    @Singleton
    @Provides
    fun provideChapterDao(database: AnimeDatabase): ChapterDao = database.chapterDao()

    @Singleton
    @Provides
    fun provideCoroutineScope() : CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @Singleton
    @Provides
    fun provideDownloadStore(
        store: DataStore<DownloadList>,
        downloadManager: DownloadManager,
        getChapter: GetChapter
    ) : DownloadStore
    = DownloadStore(store,downloadManager,getChapter)

    @Singleton
    @Provides
    fun provideDataStoreAdPreferences(context: Context) : DataStore<AdPreference> {
        return DataStoreFactory.create(
            serializer = AdPreferencesSerializer,
            produceFile = { context.dataStoreFile(AD_PREFERENCE) },
            corruptionHandler = null
        )
    }

    @Singleton
    @Provides
    fun provideDataStoreAppBuildPreferences(context: Context) : DataStore<AppBuild> {
        return DataStoreFactory.create(
            serializer = AppBuildSerializer,
            produceFile = { context.dataStoreFile(APP_BUILD) },
            corruptionHandler = null
        )
    }

    @Singleton
    @Provides
    fun provideDataStoreEadPreferences(context: Context) : DataStore<EadAccount?> {
        return DataStoreFactory.create(
            serializer = EadAccountSerializer,
            produceFile = { context.dataStoreFile(EAD_ACCOUNT) },
            corruptionHandler = null
        )
    }

    @Singleton
    @Provides
    fun provideDataStoreDownloadsPreferences(context: Context) : DataStore<DownloadList> {
        return DataStoreFactory.create(
            serializer = DownloadSerializer,
            produceFile = { context.dataStoreFile(DOWNLOADS_ENQUEUE) },
            corruptionHandler = null
        )
    }

    @Singleton
    @Provides
    fun provideDataStoreFilePreference(context: Context) : DataStore<FilePreference> {
        return DataStoreFactory.create(
            serializer = FilesSerializer,
            produceFile = { context.dataStoreFile(FILES_PREFERENCE) },
            corruptionHandler = null
        )
    }

    @Singleton
    @Provides
    fun provideDataStoreHomePreferences(context: Context) : DataStore<HomePreference> {
        return DataStoreFactory.create(
            serializer = HomeSerializer,
            produceFile = { context.dataStoreFile(HOME_PREFERENCES) },
            corruptionHandler = null
        )
    }

    @Singleton
    @Provides
    fun provideDataStorePlayerPreferences(context: Context) : DataStore<PlayerPreference> {
        return DataStoreFactory.create(
            serializer = PlayerPreferenceSerializer,
            produceFile = { context.dataStoreFile(PLAYER_PREFERENCE) },
            corruptionHandler = null
        )
    }

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
        context: Context
    ): WorkManager = WorkManager.getInstance(context)

    @Singleton
    @Provides
    fun provideConstraintsNetwork(): Constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.UNMETERED)
        .build()

    @Singleton
    @Provides
    fun provideNotificationManagerGoogle(context: Context) : android.app.NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager

    @Singleton
    @Provides
    fun provideNotificationManager(context: Context): NotificationManager =
        NotificationManager(context)

    @Singleton
    @Provides
    fun provideDownloadManagerGoogle(context: Context) : DownloadManager =
        context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager


    //DOMAIN

    @Singleton
    @Provides
    fun provideAddDownload(
        streamingState: StreamingState,
        runningState: RunningState,
        pendingState: PendingState,
        pausedState: PausedState,
        failedState: FailedState,
        downloadedState: DownloadedState
    ) : AddDownload
    = AddDownload(streamingState, runningState, pendingState, pausedState, failedState, downloadedState)

    @Singleton
    @Provides
    fun provideApplicationUseCase(
        getApplicationAds: GetApplicationAds,
        getAppStatusVersion: GetAppStatusVersion
    ) : ApplicationUseCase = ApplicationUseCase(getApplicationAds, getAppStatusVersion)

    @Singleton
    @Provides
    fun provideChapterUseCase(
        getChapter: GetChapter,
        getChapters: GetChapters,
        getChaptersToDownload: GetChaptersToDownload,
        getChaptersToFix: GetChaptersToFix,
        getChapterScrap: GetChapterScrap
    ) : ChapterUseCase = ChapterUseCase(getChapter,getChapters, getChaptersToDownload,getChaptersToFix,getChapterScrap)

    @Singleton
    @Provides
    fun provideCheckIfUpdateIsAlreadyDownloaded(
        preferenceUseCase: PreferenceUseCase
    ) : IsAlreadyDownloaded
    = IsAlreadyDownloaded(preferenceUseCase)

    @Singleton
    @Provides
    fun provideConfigureChapters(
        repository: AnimeRepository,
        launchOneTimeRequest: LaunchOneTimeRequest
    ) : ConfigureChapters = ConfigureChapters(repository,launchOneTimeRequest)

    @Singleton
    @Provides
    fun provideConfigureDownload(
        context: Context,
        gson: Gson,
        preferenceUseCase: PreferenceUseCase
    ) : ConfigureDownload
    = ConfigureDownload(context, gson, preferenceUseCase)

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
    fun provideGenerateDownload(
        downloadManager: DownloadManager,
        downloadStore: DownloadStore
    ) : GenerateDownload
    = GenerateDownload(downloadManager,downloadStore)

    @Singleton
    @Provides
    fun provideDeleteObject(
        repository: AnimeRepository
    ) : DeleteObject = DeleteObject(repository)

    @Singleton
    @Provides
    fun provideDirectoryUseCase(
        getDirectoryList: GetDirectoryList,
        getDirectory: GetDirectory,
        getDirectoryScrap: GetDirectoryScrap,
        getDirectoryState: GetDirectoryState,
        setDirectoryState: SetDirectoryState
    ) : DirectoryUseCase = DirectoryUseCase(getDirectoryList, getDirectory,getDirectoryScrap,getDirectoryState, setDirectoryState)

    @Singleton
    @Provides
    fun provideDiscordUseCase(
        getDiscordMember: GetDiscordMember
    ) : DiscordUseCase =
        DiscordUseCase(getDiscordMember)


    @Singleton
    @Provides
    fun provideDownloadEngine(
        context: Context,
        repository: AnimeRepository,
        getChapter: GetChapter,
        getServerResultToArray: GetServerResultToArray,
        getSortedServers: GetSortedServers,
        getServers: GetServers,
        enqueueDownload: EnqueueDownload,
        downloadStore: DownloadStore,
        serverScript: ServerScript,
        //downloadWebView: DownloadWebView,
        appReceiver: AppReceiver
    ) : DownloadEngine
    = DownloadEngine(context, repository, getChapter ,getServerResultToArray, getSortedServers, getServers, enqueueDownload, serverScript, downloadStore,/*downloadWebView,*/ appReceiver)

/*    @Singleton
    @Provides
    @MainThread
    fun provideDownloadWebView(context: Context) : DownloadWebView = DownloadWebView(context)*/


    @Singleton
    @Provides
    fun provideDownloadUseCase(
        addDownload: AddDownload,
        removeDownload: RemoveDownload,
        isInParallelLimit: IsInParallelDownloadLimit
    ) : DownloadUseCase
    = DownloadUseCase(addDownload,removeDownload,isInParallelLimit)

    @Singleton
    @Provides
    fun provideStreamingState(
        downloadStore: DownloadStore,
        launchDownload: LaunchDownload,
        enqueueDownload: EnqueueDownload
    ) : StreamingState = StreamingState(downloadStore, launchDownload, enqueueDownload)

    @Singleton
    @Provides
    fun provideRunningState() : RunningState = RunningState()

    @Singleton
    @Provides
    fun providePendingState() : PendingState = PendingState()

    @Singleton
    @Provides
    fun providePausedState() : PausedState = PausedState()

    @Singleton
    @Provides
    fun provideFailedState(removeDownload: RemoveDownload, launchDownload: LaunchDownload, enqueueDownload: EnqueueDownload) : FailedState = FailedState(removeDownload, launchDownload, enqueueDownload)

    @Singleton
    @Provides
    fun provideDownloadedState(filesPreferences: FilesPreferences, launchDownload: LaunchDownload, enqueueDownload: EnqueueDownload) : DownloadedState = DownloadedState(filesPreferences, launchDownload, enqueueDownload)

    @Singleton
    @Provides
    fun provideFirebaseClient() : FirebaseClient
    = FirebaseClient()

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
    fun provideGetChapterScrap(repository: AnimeRepository, gson: Gson,preferenceUseCase: PreferenceUseCase) : GetChapterScrap
    = GetChapterScrap(repository,gson, preferenceUseCase)

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
    fun provideGetDirectory(repository: AnimeRepository) : GetDirectory
    = GetDirectory(repository)

    @Singleton
    @Provides
    fun provideGetDirectoryList(
        repository: AnimeRepository,
        preferenceUseCase: PreferenceUseCase
    ) : GetDirectoryList
    = GetDirectoryList(repository,preferenceUseCase)

    @Singleton
    @Provides
    fun provideGetDirectoryScrap(
        repository: AnimeRepository,
        gson: Gson,
        preferenceUseCase: PreferenceUseCase
    ) : GetDirectoryScrap
    = GetDirectoryScrap(repository, gson, preferenceUseCase)

    @Singleton
    @Provides
    fun provideGetDirectoryState(
        preferenceUseCase: PreferenceUseCase
    ) : GetDirectoryState
    = GetDirectoryState(preferenceUseCase)

    @Singleton
    @Provides
    fun provideGetDiscordMember(
        getDiscordUserData: GetDiscordUserData,
        getDiscordUserInGuild: GetDiscordUserInGuild,
        getDiscordUserInToGuild: GetDiscordUserInToGuild,
        getDiscordUserRefreshToken: GetDiscordUserRefreshToken,
        getDiscordUserToken: GetDiscordUserToken
    ) : GetDiscordMember
    = GetDiscordMember(
        getDiscordUserToken,
        getDiscordUserRefreshToken,
        getDiscordUserData,
        getDiscordUserInGuild,
        getDiscordUserInToGuild
    )

    @Singleton
    @Provides
    fun provideGetDiscordUserData(repository: AnimeRepository) : GetDiscordUserData
    = GetDiscordUserData(repository)


    @Singleton
    @Provides
    fun provideGetDiscordUserInGuild(
        repository: AnimeRepository
    ) : GetDiscordUserInGuild
    = GetDiscordUserInGuild(repository)

    @Singleton
    @Provides
    fun provideGetDiscordUserInToGuild(
        repository: AnimeRepository
    ) : GetDiscordUserInToGuild
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
    fun provideGetEmbedServer(context: Context,getServerResultToArray: GetServerResultToArray,serverScript: ServerScript) : GetEmbedServers
    = GetEmbedServers(context,getServerResultToArray,serverScript)

    @Singleton
    @Provides
    fun provideGetEmbedServerMutable(context: Context,getServerResultToArray: GetServerResultToArray,serverScript: ServerScript) : GetEmbedServersMutable
    = GetEmbedServersMutable(context,getServerResultToArray,serverScript)

    @Singleton
    @Provides
    fun provideGetHomeList(
        repository: AnimeRepository,
        preferenceUseCase: PreferenceUseCase
    ) : GetHomeList
    = GetHomeList(repository,preferenceUseCase)

    @Singleton
    @Provides
    fun provideHomeNotifier(context: Context) : HomeNotifier
    = HomeNotifier(context)

    @Singleton
    @Provides
    fun provideGetHomeRecommendations(
        repository: AnimeRepository,
        preferenceUseCase: PreferenceUseCase
    ) : GetHomeRecommendations
    = GetHomeRecommendations(repository,preferenceUseCase)

    @Singleton
    @Provides
    fun provideGetHomeReleaseList(repository: AnimeRepository) : GetHomeReleaseList
    = GetHomeReleaseList(repository)

    @Singleton
    @Provides
    fun provideGetHomeScrap(
        repository: AnimeRepository,
        gson: Gson,
        preferenceUseCase: PreferenceUseCase
    ) : GetHomeScrap
    = GetHomeScrap(repository, gson , preferenceUseCase)

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
    fun provideGetNews(
        repository: AnimeRepository,
        preferenceUseCase: PreferenceUseCase
    ) : GetNews
    = GetNews(repository,preferenceUseCase)

    @Singleton
    @Provides
    fun provideGetNewsItemScrap(repository: AnimeRepository, gson: Gson, preferenceUseCase: PreferenceUseCase) : GetNewsItemScrap
    = GetNewsItemScrap(repository,gson, preferenceUseCase)

    @Singleton
    @Provides
    fun provideGetNewsItemWebScrap(repository: AnimeRepository, gson: Gson, preferenceUseCase: PreferenceUseCase) : GetNewsItemWebScrap
    = GetNewsItemWebScrap(repository, gson, preferenceUseCase)

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
    fun provideGetProfilePlayerRecommendations(
        repository: AnimeRepository,
        context: Context,
        preferenceUseCase: PreferenceUseCase
    ): GetProfilePlayerRecommendations
    = GetProfilePlayerRecommendations(repository,context, preferenceUseCase)

    @Singleton
    @Provides
    fun provideGetProfileScrap(repository: AnimeRepository, gson: Gson, preferenceUseCase: PreferenceUseCase) : GetProfileScrap
    = GetProfileScrap(repository, gson, preferenceUseCase)

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
    fun provideGetServerScript(repository: AnimeRepository,preferenceUseCase: PreferenceUseCase) : ServerScript
    = ServerScript(repository,preferenceUseCase)

    @Singleton
    @Provides
    fun provideGetSortedServer(
        serverIdentifier: ServerIdentifier,
        preferenceUseCase: PreferenceUseCase
    ) : GetSortedServers
    = GetSortedServers(serverIdentifier,preferenceUseCase)

    @Singleton
    @Provides
    fun provideGetPlayerType(
        preferenceUseCase: PreferenceUseCase
    ) : GetPlayerType
    = GetPlayerType(preferenceUseCase)

    @Singleton
    @Provides
    fun provideGson() : Gson = Gson()

    @Singleton
    @Provides
    fun provideHandleChapter(
        launchVideo: LaunchVideo
    ) : HandleChapter
    = HandleChapter(launchVideo)

    @Singleton
    @Provides
    fun provideHomeUseCase(
        getHomeList: GetHomeList,
        getHomeRecommendations: GetHomeRecommendations,
        getHomeReleaseList: GetHomeReleaseList,
        getHomeScrap: GetHomeScrap
    ) : HomeUseCase = HomeUseCase(getHomeList, getHomeRecommendations,getHomeReleaseList,getHomeScrap)

    @Singleton
    @Provides
    fun provideInsertObject(
        repository: AnimeRepository
    ) : InsertObject = InsertObject(repository)

    @Singleton
    @Provides
    fun provideInstallWorkers(workManager: WorkManager,constraints: Constraints) : InstallWorkers
    = InstallWorkers(workManager,constraints)

    @Singleton
    @Provides
    fun provideIsInParallelDownloadLimit(downloadStore: DownloadStore) : IsInParallelDownloadLimit
    = IsInParallelDownloadLimit(downloadStore)

    @Singleton
    @Provides
    fun provideLaunchToPlayerActivity(
        preferenceUseCase: PreferenceUseCase
    ) : LaunchToPlayerActivity
    = LaunchToPlayerActivity(preferenceUseCase)

    @Singleton
    @Provides
    fun provideLaunchVideo(
        getPlayerType: GetPlayerType,
        launchToPlayerActivity: LaunchToPlayerActivity
    ) : LaunchVideo
    = LaunchVideo(
        getPlayerType,
        launchToPlayerActivity
    )

    @Singleton
    @Provides
    fun provideEnqueueDownload(
        generateDownload: GenerateDownload,
        configureDownload: ConfigureDownload,
        appReceiver: AppReceiver,
        context: Context
    ) : EnqueueDownload
    = EnqueueDownload(generateDownload, configureDownload, appReceiver, context)

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
    fun provideGetUpdate(downloadStore: DownloadStore) : GetUpdate
    = GetUpdate(downloadStore)

    @Singleton
    @Provides
    fun provideMonosChinosLogin(repository: AnimeRepository) : Login
    = Login(repository)

    @Singleton
    @Provides
    fun provideMonosChinosUseCase(
        login: Login
    ) : MonosChinosUseCase
    = MonosChinosUseCase(login)

    @Singleton
    @Provides
    fun provideNewsUseCase(
        getNews: GetNews,
        getNewsItemScrap: GetNewsItemScrap,
        getNewsItemWebScrap: GetNewsItemWebScrap
    ) : NewsUseCase = NewsUseCase(getNews, getNewsItemScrap ,getNewsItemWebScrap)

    @Singleton
    @Provides
    fun provideObjectUseCase(
        insertObject: InsertObject,
        updateObject: UpdateObject,
        deleteObject: DeleteObject
    ) : ObjectUseCase = ObjectUseCase(insertObject, updateObject, deleteObject)

    @Singleton
    @Provides
    fun providePreferenceUseCase(
        appBuildPreferences: AppBuildPreferences,
        actionStore: ActionStore,
        preferencesSettings: Preferences,
        adPreferences: AdPreferences,
        homePreferences: HomePreferences,
        filesPreferences: FilesPreferences,
        playerPreferences: PlayerPreferences,
        eadPreferences: EadPreferences
    ) : PreferenceUseCase =
        PreferenceUseCase(
            appBuildPreferences = appBuildPreferences,
            actionStore = actionStore,
            preferences = preferencesSettings,
            adPreferences = adPreferences,
            homePreferences = homePreferences,
            filesPreferences = filesPreferences,
            playerPreferences = playerPreferences,
            userPreferences = eadPreferences
        )

    @Singleton
    @Provides
    fun providePlayerPreferences(
        store : DataStore<PlayerPreference>
    ) : PlayerPreferences
    = PlayerPreferences(store)

    @Singleton
    @Provides
    fun providePreferences(context: Context) : Preferences =
        Preferences(context = context)

    @Singleton
    @Provides
    fun provideProfileUseCase(
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
    ) : ProfileUseCase = ProfileUseCase(getProfile, getProfileList , getProfilesToFix ,getLikedProfiles, getMostViewedProfiles, getProfileInboxRecommendations, getProfilePlayerRecommendations,getProfilesReleases,getProfilesFavoriteReleases,getProfileScrap)

    @Singleton
    @Provides
    fun provideRecordsUseCase(
        getRecords: GetRecords,
        configureRecords: ConfigureRecords
    ) : RecordsUseCase = RecordsUseCase(getRecords,configureRecords)

    @Singleton
    @Provides
    fun provideRemoveDownload(
        downloadManager: DownloadManager,
        downloadStore: DownloadStore ) : RemoveDownload
    = RemoveDownload(downloadManager,downloadStore)

    @Singleton
    @Provides
    fun provideSetDirectoryState(
        preferenceUseCase: PreferenceUseCase
    ) : SetDirectoryState
    = SetDirectoryState(preferenceUseCase)

    @Singleton
    @Provides
    fun provideServerEngine(context : Context,getServerResultToArray: GetServerResultToArray,serverScript: ServerScript) : ServerEngine
    = ServerEngine(context,getServerResultToArray,serverScript)

    @Singleton
    @Provides
    fun provideServerIdentifier() : ServerIdentifier
    = ServerIdentifier()

    @Singleton
    @Provides
    fun provideServerUseCase(getServer: GetServer, getServers: GetServers, getEmbedServers: GetEmbedServers, getEmbedServersMutable: GetEmbedServersMutable, getSortedServers: GetSortedServers, serverScript: ServerScript) : ServerUseCase
    = ServerUseCase(getServer, getServers, getEmbedServers,getEmbedServersMutable, getSortedServers,serverScript)

    @Singleton
    @Provides
    fun provideUpdateObject(repository: AnimeRepository) : UpdateObject
    = UpdateObject(repository)

    @Singleton
    @Provides
    fun provideUpdateUseCase(getUpdate: GetUpdate, isAlreadyDownloaded: IsAlreadyDownloaded) : UpdateUseCase
    = UpdateUseCase(getUpdate, isAlreadyDownloaded)
}