package com.ead.project.dreamer.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ead.project.dreamer.app.data.home.HomeNotifier
import com.ead.project.dreamer.app.data.home.HomePreferences
import com.ead.project.dreamer.app.data.notifications.NotificationManager
import com.ead.project.dreamer.data.database.model.ChapterHome
import com.ead.project.dreamer.data.models.ChapterNotify
import com.ead.project.dreamer.data.network.WebProvider
import com.ead.project.dreamer.domain.HomeUseCase
import com.ead.project.dreamer.domain.ObjectUseCase
import com.ead.project.dreamer.domain.PreferenceUseCase
import com.ead.project.dreamer.domain.ProfileUseCase
import com.ead.project.dreamer.presentation.settings.options.SettingsNotificationsFragment
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.IOException

@HiltWorker
class HomeWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val workerParameters: WorkerParameters,
    private val homeUseCase: HomeUseCase,
    private val objectUseCase: ObjectUseCase,
    private val profileUseCase: ProfileUseCase,
    private val webProvider: WebProvider,
    private val notifier: HomeNotifier,
    preferenceUseCase: PreferenceUseCase,
) : CoroutineWorker(context,workerParameters) {

    private val notificationList : MutableList<ChapterHome> = ArrayList()
    private val homePreferences = preferenceUseCase.homePreferences
    private val preferences = preferenceUseCase.preferences

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                homeOperator(this)
                notificationOperator(this)
                Result.success()
            }
            catch (ex : IOException) {
                ex.printStackTrace()
                Result.failure()
            }
        }
    }

    //UPDATE HOME

    private suspend fun homeOperator(scope: CoroutineScope) {
        scope.apply {

            val chapterHomeList = homeUseCase.getHomeList()

            val isDataEmpty = chapterHomeList.isEmpty()

            val homeData = async { webProvider.getChaptersHome(context) }
            homeData.await().apply {

                if (isDataEmpty) objectUseCase.insertObject(this)
                else objectUseCase.updateObject(this)

            }
        }
    }

    //CONFIGURATION NOTIFICATIONS

    private suspend fun notificationOperator(scope: CoroutineScope) {
        scope.apply {
            val notificationBaseIndex = homePreferences.getNotificationsIndex()

            val notificationLevel = preferences.getInt(SettingsNotificationsFragment.PREFERENCE_NOTIFICATIONS_OPTION,
                NotificationManager.ALL)

            if (notificationLevel > NotificationManager.NONE) {

                val releaseList = homeUseCase.getHomeList()
                val favoriteList = profileUseCase.getProfilesFavoriteReleases.stringList()
                val chaptersTitleList = homePreferences.getList().map { ChapterNotify(it.title,it.chapterNumber) }

                chaptersTitleList.apply {

                    if (isNotEmpty()) {

                        if (notificationLevel == NotificationManager.FAVORITES) {
                            for (chapter in releaseList) {
                                if (!contains(ChapterNotify(chapter.title,chapter.chapterNumber)) && favoriteList.contains(chapter.title)) {
                                    notificationList.add(chapter)
                                }
                            }
                        }
                        else {
                            for (chapter in releaseList) {
                                if (!contains(ChapterNotify(chapter.title,chapter.chapterNumber))) {
                                    notificationList.add(chapter)
                                }
                            }
                        }

                    }
                    else {

                        homePreferences.addToList(releaseList)
                        return

                    }

                    for (i in notificationList.indices) {

                        val chapter = notificationList[i]
                        val index = notificationBaseIndex + i

                        notifier.onChapterRelease(
                            chapter,
                            HomePreferences.NOTIFICATION_CHANNEL_ID + index
                        )
                    }

                    homePreferences.setNotificationIndex(notificationBaseIndex + notificationList.size)
                }

                if (notificationList.isNotEmpty()) {
                    notifier.createGroupSummaryNotification()
                }
                homePreferences.addToList(notificationList)

            }
        }
    }
}