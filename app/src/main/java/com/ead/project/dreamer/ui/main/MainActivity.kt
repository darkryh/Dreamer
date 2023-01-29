package com.ead.project.dreamer.ui.main

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.mediarouter.app.MediaRouteButton
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import coil.load
import coil.transform.CircleCropTransformation
import com.ead.commons.lib.lifecycle.activity.showLongToast
import com.ead.commons.lib.lifecycle.observeOnce
import com.ead.commons.lib.views.setResourceImageAndColor
import com.ead.project.dreamer.BuildConfig
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.AppManager
import com.ead.project.dreamer.app.DreamerApp
import com.ead.project.dreamer.app.model.AppStatus
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.commons.Tools
import com.ead.project.dreamer.data.models.discord.Discord
import com.ead.project.dreamer.data.models.discord.User
import com.ead.project.dreamer.data.utils.DataStore
import com.ead.project.dreamer.data.utils.DirectoryManager
import com.ead.project.dreamer.data.utils.NotificationManager
import com.ead.project.dreamer.data.utils.media.CastManager
import com.ead.project.dreamer.data.utils.ui.DownloadDesigner
import com.ead.project.dreamer.data.utils.ui.DreamerLayout
import com.ead.project.dreamer.databinding.ActivityMainBinding
import com.ead.project.dreamer.domain.DownloadManager
import com.ead.project.dreamer.ui.directory.DirectoryActivity
import com.ead.project.dreamer.ui.login.LoginActivity
import com.ead.project.dreamer.ui.settings.SettingsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject lateinit var downloadDesigner : DownloadDesigner
    @Inject lateinit var notifier: NotificationManager
    @Inject lateinit var downloadManager: DownloadManager

    private lateinit var binding: ActivityMainBinding
    private val mainActivityViewModel : MainActivityViewModel by viewModels()
    private val user : User? = User.get()
    private val currentVersion = BuildConfig.VERSION_NAME.toDouble()
    var castManager: CastManager = CastManager()
    private val appManager = AppManager()
    lateinit var mediaRouteButton : MediaRouteButton
    private var directoryChecked = false
    private var timerAdv : Timer ?= null
    private var countAdv = 0

    private var isPostNotificationPermissionGranted = false
    private var isWriteExternalPermissionGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Dreamer)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        mediaRouteButton = binding.mediaRouteButton
        supportActionBar?.hide()
        init()
        mainActivityViewModel.synchronizeScrapper()
        mainActivityViewModel.synchronizeHome()
        mainActivityViewModel.synchronizeNewContent()
        mainActivityViewModel.synchronizeDirectory()
        mainActivityViewModel.synchronizeReleases()

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_inbox,
                R.id.navigation_news,
                R.id.navigation_records,
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        DirectoryManager.initDirectories()
    }

    private fun init() {
        DreamerApp.initAdsPreferences()
        settingPermissions()
        prepareLayouts()
        initSettings()
        connectionSettings()
        userSettings()
        appSettings()
        checkStatusApp()
        checkSubscribedTopic()
    }

    private fun prepareLayouts() {
        binding.edtMainSearch.setOnClickListener{ goToDirectory() }
        binding.imvSearch.setOnClickListener { goToDirectory() }
        binding.imvProfile.setResourceImageAndColor(R.drawable.ic_person_outline_24,R.color.white)
        binding.imvProfile.setOnClickListener {
            if (!Constants.isConfigurationActivityClicked()) {
                Constants.setConfigurationActivityClicked(true)
                startActivity(Intent(this, SettingsActivity::class.java))
            }
        }

        if (user?.avatar != null) {
            binding.imvProfile.load(Discord.PROFILE_IMAGE) {
                transformations(CircleCropTransformation())
            }
        }
    }

    private fun settingPermissions() {
        val permissionRequest : MutableList<String> = ArrayList()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            isPostNotificationPermissionGranted =
                ContextCompat.checkSelfPermission(this,Manifest.permission.POST_NOTIFICATIONS) ==
                        PackageManager.PERMISSION_GRANTED
            if (!isPostNotificationPermissionGranted) permissionRequest.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            isWriteExternalPermissionGranted =
                ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED
            if (!isWriteExternalPermissionGranted) permissionRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (permissionRequest.isNotEmpty()) requestPermission.launch(permissionRequest.toTypedArray())
    }

    override fun onStart() {
        super.onStart()
        Tools.launchRequestedProfile(this)
    }

    override fun onResume() {
        castManager.onResume()
        super.onResume()
    }

    override fun onPause() {
        castManager.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        castManager.onDestroy()
        appManager.onDestroy()
        super.onDestroy()
    }

    private fun initSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) splashScreen
        Constants.setAppFromGoogle(false)
        downloadDesigner.firstTimeReset()
    }

    private fun appSettings() {
        if (Constants.isAppFromGoogle()) Constants.setGooglePolicyTo(true)
        castManager.initButtonFactory(this,binding.mediaRouteButton)
        castManager.setViewModel(mainActivityViewModel)
        if (!Constants.isDirectorySynchronized()) syncState()
    }

    private fun connectionSettings() {
        if (Tools.isConnectionIncompatible())
            showLongToast(getString(R.string.wifi_warning))
    }

    private fun userSettings() {
        if (user == null) Constants.setAutomaticPlayerMode(false)
        else
            mainActivityViewModel.getGuildMember(user.id).observe(this) {
                try {
                    if (it != null && it.roles.isNotEmpty()) {
                        user.rankLevel = User.getRankValue(it.roles)
                        user.rank = User.getRank(user.rankLevel)
                        User.set(user)
                    }
                } catch (e : Exception) { e.printStackTrace() }
            }
    }

    private fun checkStatusApp() {
        mainActivityViewModel.getStatusApp().observeOnce(this) { appStatus ->
            val updateAvailable = currentVersion < appStatus.lastVersion
            if (updateAvailable) checkUpdate(appStatus)
            if (currentVersion < appStatus.minVersion && !updateAvailable) goToLogin(appStatus.minVersion)
        }
    }

    private fun checkUpdate(appStatus: AppStatus) {
        val updateApk = getString(R.string.apk_title_new_version_download, appStatus.lastVersion.toString())
        Constants.setVersionUpdateRoute(updateApk)
        if (!downloadManager.checkIfUpdateIsAlreadyDownloaded())
            showUpdateMessage(appStatus,updateApk)
    }

    private fun showUpdateMessage(appStatus: AppStatus,title : String) {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.title_new_version_download,appStatus.lastVersion.toString()))
            .setMessage(appStatus.resumedVersionNotes?:getString(R.string.content_new_version_download))
            .setPositiveButton(getString(R.string.to_download)) { _: DialogInterface?, _: Int ->
                downloadManager.launchUpdate(title, appStatus.downloadReference)
            }
            .setNegativeButton(R.string.cancel,null)
            .show()
    }

    private fun goToLogin(version : Double) {
        startActivity(Intent(this, LoginActivity::class.java))
        DataStore.writeDouble(Constants.MINIMUM_VERSION_REQUIRED,version)
        DataStore.writeBooleanAsync(Constants.VERSION_DEPRECATED,true)
        finish()
    }

    private fun syncState() {
        mainActivityViewModel.directoryState().observe(this) {
            if (!directoryChecked)
                if (it) {
                    DreamerLayout.showSnackbar(
                        view = binding.coordinator,
                        text = getString(R.string.successfully_sync),
                        color = R.color.green
                    )
                    directoryChecked = true
                }
                else {
                    if (timerAdv == null) {
                        DreamerLayout.showSnackbar(
                            view = binding.coordinator,
                            text = getString(R.string.requesting_data),
                            color = R.color.red,
                            length = Snackbar.LENGTH_INDEFINITE
                        )
                        timerAdv = Timer()
                        timerAdv?.schedule(object : TimerTask() {
                            override fun run() {
                                showAdvices()
                            }
                        }, 5000, 13000)
                    }
                }

        }
    }

    private fun showAdvices() {
        if (!directoryChecked)
            when(++countAdv ) {
                1 -> DreamerLayout.showSnackbar(view = binding.coordinator, text = getString(R.string.requesting_data_adv1),
                    color = R.color.red, length = Snackbar.LENGTH_INDEFINITE)
                2 -> DreamerLayout.showSnackbar(view = binding.coordinator, text = getString(R.string.requesting_data_adv2),
                    color = R.color.red, length = Snackbar.LENGTH_INDEFINITE)
                3 -> {
                    DreamerLayout.showSnackbar(view = binding.coordinator, text = getString(R.string.requesting_data_adv3),
                        color = R.color.red, length = Snackbar.LENGTH_INDEFINITE)
                    countAdv = 0
                }
            }
    }

    private fun checkSubscribedTopic() {
        if (Constants.isActiveFirebaseNotifications())
            FirebaseMessaging.getInstance().subscribeToTopic(Constants.DREAMER_TOPIC)
        else
            FirebaseMessaging.getInstance().unsubscribeFromTopic(Constants.DREAMER_TOPIC)
    }

    private fun goToDirectory() {
        if (!Constants.isDirectoryActivityClicked()) {
            Constants.setDirectoryActivityClicked(true)
            startActivity(Intent(this, DirectoryActivity::class.java))
        }
    }

    private val requestPermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            isPostNotificationPermissionGranted =
                permissions[Manifest.permission.POST_NOTIFICATIONS]?:isPostNotificationPermissionGranted

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R)
            isWriteExternalPermissionGranted =
                permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE]?:isWriteExternalPermissionGranted
    }

    /* para mostrar notificación de configuración

    * isGranted -> {
                if (Constants.isFirstTimeShowingNotification())
                    NotificationManager.showSettingNotification(notifier,this)
                "Granted permission"
            }
            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> "Rational permission"
            else -> "Denied permission"*/
}