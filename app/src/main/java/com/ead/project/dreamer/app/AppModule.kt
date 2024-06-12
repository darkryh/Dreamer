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
        repository: AnimeRepository,
        preferenceUseCase: PreferenceUseCase,
        gson: Gson
    ): WebProvider {
        return WebProvider(
            getDirectoryScrap = GetDirectoryScrap(
               repository = repository,
                preferenceUseCase = preferenceUseCase,
                gson = gson,
            )
        )
    }

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

    @Singleton
    @Provides
    fun provideApplicationUseCase(
        repository: AnimeRepository
    ) : ApplicationUseCase {
        return ApplicationUseCase(
            getApplicationAds = GetApplicationAds(
                repository = repository
            ),
            getAppStatusVersion = GetAppStatusVersion(
                repository = repository
            )
        )
    }

    @Singleton
    @Provides
    fun provideChapterUseCase(
        preferenceUseCase: PreferenceUseCase,
        repository: AnimeRepository,
        gson: Gson,
    ) : ChapterUseCase {
        return ChapterUseCase(
            getChapter = GetChapter(
                repository = repository
            ),
            getChapters = GetChapters(
                repository = repository
            ),
            getChaptersToDownload = GetChaptersToDownload(
                repository = repository
            ),
            getChaptersToFix = GetChaptersToFix(
                repository = repository
            ),
            getChapterScrap = GetChapterScrap(
                repository = repository,
                gson = gson,
                preferenceUseCase = preferenceUseCase
            )
        )
    }

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
    fun provideDirectoryUseCase(
        preferenceUseCase: PreferenceUseCase,
        repository: AnimeRepository,
        gson: Gson
    ) : DirectoryUseCase {
        return DirectoryUseCase(
            getDirectory = GetDirectory(
                repository = repository
            ),
            getDirectoryList = GetDirectoryList(
                repository = repository,
                preferenceUseCase = preferenceUseCase
            ),
            getDirectoryState = GetDirectoryState(
                preferenceUseCase = preferenceUseCase
            ),
            getDirectoryScrap = GetDirectoryScrap(
                repository = repository,
                gson = gson,
                preferenceUseCase = preferenceUseCase
            ),
            setDirectoryState = SetDirectoryState(
                preferenceUseCase = preferenceUseCase
            )
        )
    }

    @Singleton
    @Provides
    fun provideDiscordUseCase(
        repository: AnimeRepository
    ) : DiscordUseCase {
        return DiscordUseCase(
            getDiscordMember = GetDiscordMember(
                getDiscordUserToken = GetDiscordUserToken(
                    repository = repository
                ),
                getDiscordUserRefreshToken = GetDiscordUserRefreshToken(
                    repository = repository
                ),
                getDiscordUserData = GetDiscordUserData(
                    repository = repository
                ),
                getDiscordUserInGuild = GetDiscordUserInGuild(
                    repository = repository
                ),
                getDiscordUserInToGuild = GetDiscordUserInToGuild(
                    repository = repository
                )
            )
        )
    }

    @Singleton
    @Provides
    fun provideDownloadEngine(
        context: Context,
        gson: Gson,
        repository: AnimeRepository,
        downloadStore: DownloadStore,
        downloadManager: DownloadManager,
        appReceiver: AppReceiver,
        preferenceUseCase: PreferenceUseCase
    ) : DownloadEngine {
        return DownloadEngine(
            context = context,
            repository = repository,
            getChapter = GetChapter(
                repository = repository
            ),
            getServerResultToArray = GetServerResultToArray(),
            getSortedServers = GetSortedServers(
                serverIdentifier = ServerIdentifier(),
                preferenceUseCase = preferenceUseCase
            ),
            getServerUntilFindResource = GetServerUntilFindResource(
                context = context
            ),
            enqueueDownload = EnqueueDownload(
                generateDownload = GenerateDownload(
                    downloadManager = downloadManager,
                    downloadStore = downloadStore
                ),
                configureDownload = ConfigureDownload(
                    context = context,
                    gson = gson,
                    preferenceUseCase = preferenceUseCase
                ),
                appReceiver = appReceiver,
                context = context,
            ),
            serverScript = ServerScript(
                repository = repository,
                preferenceUseCase = preferenceUseCase
            ),
            downloadStore = downloadStore,
            appReceiver = appReceiver
        )
    }

    @Singleton
    @Provides
    fun provideDownloadUseCase(
        context: Context,
        gson: Gson,
        appReceiver: AppReceiver,
        downloadStore: DownloadStore,
        downloadManager: DownloadManager,
        preferenceUseCase: PreferenceUseCase,
        launchPeriodicTimeRequest: LaunchPeriodicTimeRequest
    ) : DownloadUseCase {

        val launchDownload = LaunchDownload(
            launchPeriodicTimeRequest = launchPeriodicTimeRequest
        )

        val enqueueDownload = EnqueueDownload(
            generateDownload = GenerateDownload(
                downloadManager = downloadManager,
                downloadStore = downloadStore
            ),
            configureDownload = ConfigureDownload(
                context = context,
                gson = gson,
                preferenceUseCase = preferenceUseCase
            ),
            appReceiver = appReceiver,
            context = context
        )

        val removeDownload = RemoveDownload(
            downloadManager = downloadManager,
            downloadStore = downloadStore
        )

        return DownloadUseCase(
            add = AddDownload(
                streamingState = StreamingState(
                    downloadStore = downloadStore,
                    launchDownload = launchDownload,
                    enqueueDownload = enqueueDownload
                ),
                runningState = RunningState(),
                pendingState = PendingState(),
                pausedState = PausedState(),
                failedState = FailedState(
                    removeDownload = removeDownload,
                    launchDownload = launchDownload,
                    enqueueDownload = enqueueDownload
                ),
                downloadedState = DownloadedState(
                    filesPreferences = preferenceUseCase.filesPreferences,
                    launchDownload = launchDownload,
                    enqueueDownload = enqueueDownload

                )
            ),
            remove = removeDownload,
            isInParallelLimit = IsInParallelDownloadLimit(
                downloadStore = downloadStore
            )
        )
    }

    @Singleton
    @Provides
    fun provideFirebaseClient() : FirebaseClient
    = FirebaseClient()

    @Singleton
    @Provides
    fun provideHomeNotifier(context: Context) : HomeNotifier
    = HomeNotifier(context)

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
        repository: AnimeRepository,
        preferenceUseCase: PreferenceUseCase,
        gson: Gson
    ) : HomeUseCase {
        return HomeUseCase(
            getHomeList = GetHomeList(
                repository = repository,
                preferenceUseCase = preferenceUseCase
            ),
            getHomeRecommendations = GetHomeRecommendations(
                repository = repository,
                preferenceUseCase = preferenceUseCase
            ),
            getHomeReleaseList = GetHomeReleaseList(
                repository = repository
            ),
            getHomeScrap = GetHomeScrap(
                repository = repository,
                preferenceUseCase = preferenceUseCase,
                gson = gson
            )
        )
    }

    @Singleton
    @Provides
    fun provideInstallWorkers(workManager: WorkManager,constraints: Constraints) : InstallWorkers
    = InstallWorkers(workManager,constraints)

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
    fun provideLaunchOneTimeRequest(workManager: WorkManager,constraints: Constraints) : LaunchOneTimeRequest
    = LaunchOneTimeRequest(workManager,constraints)

    @Singleton
    @Provides
    fun provideLaunchPeriodicOneTimeRequest(workManager: WorkManager,constraints: Constraints) : LaunchPeriodicTimeRequest
    = LaunchPeriodicTimeRequest(workManager,constraints)

    @Singleton
    @Provides
    fun provideMonosChinosUseCase(
        repository: AnimeRepository
    ) : MonosChinosUseCase {
        return MonosChinosUseCase(
            login = Login(
                repository = repository
            )
        )
    }

    @Singleton
    @Provides
    fun provideNewsUseCase(
        repository: AnimeRepository,
        preferenceUseCase: PreferenceUseCase,
        gson: Gson
    ) : NewsUseCase {
        return NewsUseCase(
            getNews = GetNews(
                repository = repository,
                preferenceUseCase = preferenceUseCase
            ),
            getNewsItemScrap = GetNewsItemScrap(
                repository = repository,
                preferenceUseCase = preferenceUseCase,
                gson = gson
            ),
            getNewsItemWebScrap = GetNewsItemWebScrap(
                repository = repository,
                preferenceUseCase = preferenceUseCase,
                gson = gson
            )
        )
    }

    @Singleton
    @Provides
    fun provideObjectUseCase(
        repository: AnimeRepository
    ) : ObjectUseCase {
        return ObjectUseCase(
            insertObject = InsertObject(
                repository = repository
            ),
            updateObject = UpdateObject(
                repository = repository
            ),
            deleteObject = DeleteObject(
                repository = repository
            )
        )
    }

    @Singleton
    @Provides
    fun providePreferenceUseCase(
        context: Context,
        appBuildStore : DataStore<AppBuild>,
        adPreferenceStore : DataStore<AdPreference>,
        homePreferencesStore :DataStore<HomePreference>,
        filesPreferencesStore : DataStore<FilePreference>,
        playerPreferencesStore : DataStore<PlayerPreference>,
        userPreferenceStore : DataStore<EadAccount?>
    ) : PreferenceUseCase {
        return PreferenceUseCase(
            appBuildPreferences = AppBuildPreferences(
                store = appBuildStore
            ),
            actionStore = ActionStore(
                context = context
            ),
            preferences = Preferences(
                context = context
            ),
            adPreferences = AdPreferences(
                store = adPreferenceStore
            ),
            homePreferences = HomePreferences(
                store = homePreferencesStore
            ),
            filesPreferences = FilesPreferences(
                store = filesPreferencesStore
            ),
            playerPreferences = PlayerPreferences(
                store = playerPreferencesStore
            ),
            userPreferences = EadPreferences(
                store = userPreferenceStore
            )
        )
    }

    @Singleton
    @Provides
    fun provideProfileUseCase(
        context: Context,
        repository: AnimeRepository,
        preferenceUseCase: PreferenceUseCase,
        gson: Gson
    ) : ProfileUseCase {
        return ProfileUseCase(
            getProfile = GetProfile(
                repository = repository
            ),
            getProfileScrap = GetProfileScrap(
                repository = repository,
                preferenceUseCase = preferenceUseCase,
                gson = gson
            ),
            getLikedProfiles = GetLikedProfiles(
                repository = repository
            ),
            getMostViewedProfiles = GetMostViewedProfiles(
                repository = repository
            ),
            getProfilePlayerRecommendations = GetProfilePlayerRecommendations(
                repository = repository,
                preferenceUseCase = preferenceUseCase,
                context = context
            ),
            getProfileList = GetProfileList(
                repository = repository
            ),
            getProfileInboxRecommendations = GetProfileInboxRecommendations(
                repository = repository
            ),
            getProfilesFavoriteReleases = GetProfilesFavoriteReleases(
                repository = repository
            ),
            getProfilesReleases = GetProfilesReleases(
                repository = repository
            ),
            getProfilesToFix = GetProfilesToFix(
                repository = repository
            )
        )
    }

    @Singleton
    @Provides
    fun provideRecordsUseCase(
        repository: AnimeRepository
    ) : RecordsUseCase {
        return RecordsUseCase(
            getRecords = GetRecords(
                repository = repository
            ),
            configureRecords = ConfigureRecords(
                repository = repository
            )
        )
    }

    @Singleton
    @Provides
    fun provideServerUseCase(
        context: Context,
        preferenceUseCase: PreferenceUseCase,
    ) : ServerUseCase {
        return ServerUseCase(
            getServer = GetServer(
                context = context
            ),
            getServerUntilFindResource = GetServerUntilFindResource(
                context = context
            ),
            getSortedServers = GetSortedServers(
                serverIdentifier = ServerIdentifier(),
                preferenceUseCase = preferenceUseCase
            ),
            getEmbedServers = GetEmbedServers()
        )
    }

    @Singleton
    @Provides
    fun provideUpdateUseCase(
        downloadStore: DownloadStore,
        preferenceUseCase: PreferenceUseCase
    ): UpdateUseCase {
        return UpdateUseCase(
            getUpdate = GetUpdate(
                downloadStore = downloadStore
            ),
            isAlreadyDownloaded = IsAlreadyDownloaded(
                preferenceUseCase = preferenceUseCase
            )
        )
    }
}