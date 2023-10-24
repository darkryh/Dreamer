package com.ead.project.dreamer.presentation.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import coil.load
import coil.transform.CircleCropTransformation
import com.ead.commons.lib.lifecycle.activity.showLongToast
import com.ead.commons.lib.lifecycle.observeOnce
import com.ead.commons.lib.views.setResourceImageAndColor
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.AppInfo
import com.ead.project.dreamer.app.data.discord.Discord
import com.ead.project.dreamer.app.data.network.Network
import com.ead.project.dreamer.app.data.network.NetworkType
import com.ead.project.dreamer.app.data.util.DirectoryUtil
import com.ead.project.dreamer.app.data.util.system.launchActivity
import com.ead.project.dreamer.app.data.util.system.launchActivityAndFinish
import com.ead.project.dreamer.app.model.AppBuild
import com.ead.project.dreamer.databinding.ActivityMainBinding
import com.ead.project.dreamer.presentation.directory.DirectoryActivity
import com.ead.project.dreamer.presentation.login.LoginActivity
import com.ead.project.dreamer.presentation.profile.AnimeProfileActivity
import com.ead.project.dreamer.presentation.settings.SettingsActivity
import com.ead.project.dreamer.presentation.update.UpdateActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel : MainActivityViewModel by viewModels()
    private val currentVersion = AppInfo.versionValue

    private var isPostNotificationPermissionGranted = false
    private var isWriteExternalPermissionGranted = false

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Dreamer)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        init()

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_container_view) as NavHostFragment
        val navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_inbox,
                R.id.navigation_news,
                R.id.navigation_directory,
                R.id.navigation_records,
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.bottomNavigationView.setupWithNavController(navController)
    }

    private fun init() {
        initVariables()
        initLayouts()
        handleSplashArt()
        settingPermissions()
        observeNetworkState()
        observeDiscordUserState()
        observeDirectoryState()
        observeApplicationState()
        observeNotificationSubscription()
        handleIfProfileIsRequestedFromPlayer()
    }

    private fun initVariables() {
        viewModel.castManager.initFactory(this,binding.mediaRouteButton)
    }

    private fun initLayouts() {
        binding.apply {
            editTextMainSearch.setOnClickListener{ goToDirectory() }
            imageSearch.setOnClickListener { goToDirectory() }
            imageProfile.setResourceImageAndColor(R.drawable.ic_user,R.color.white)
            imageProfile.setOnClickListener { goToSettings() }
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

    override fun onResume() {
        viewModel.onResume()
        super.onResume()
    }

    override fun onPause() {
        viewModel.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        viewModel.onDestroy()
        super.onDestroy()
    }

    private fun handleSplashArt() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) splashScreen
    }

    private fun observeDirectoryState() {
        lifecycleScope.launch {
            viewModel.getDirectoryState().collect { isSynchronized ->

                if (DirectoryUtil.stateSynchronized) {
                    cancel("No need to collect more")
                    return@collect
                }

                DirectoryUtil.isCompleted = isSynchronized
                if (!isSynchronized) {
                    DirectoryUtil.setupState(this@MainActivity,binding.coordinator)
                }
                else {
                    DirectoryUtil.showCompletedState(this@MainActivity,binding.coordinator)
                }

            }
        }
    }

    private fun observeNetworkState() {
        lifecycleScope.launch {
            Network.networkState.collectLatest { networkType ->
                if (networkType != NetworkType.Wifi) {
                    showLongToast(getString(R.string.wifi_warning))
                }
            }
        }
    }

    private fun observeDiscordUserState() {
        Discord.userLivedata.observeOnce(this@MainActivity) { discordUser ->

            if (discordUser != null) {

                if (discordUser.getAvatarUrl() != null) {
                    binding.imageProfile.load(discordUser.getAvatarUrl()) {
                        transformations(CircleCropTransformation())
                    }
                }

                viewModel.getGuildMember(discordUser.id)
                    .observeOnce(this@MainActivity) { guildMember ->
                        if (guildMember != null) {
                            Discord.login(discordUser.getRoles(guildMember.roles))
                        }
                    }

            }
        }
    }

    private fun observeApplicationState() {
        viewModel.getStatusApp().observeOnce(this) { appBuild ->

            val updateAvailable = currentVersion < appBuild.update.version
            if (updateAvailable) {
                updateVersionBuild(appBuild)
                checkUpdate(appBuild)
            }
            if (currentVersion < appBuild.minVersion && !updateAvailable) {
                goBackToLogin()
            }

        }
    }

    private fun updateVersionBuild(appBuild: AppBuild) {
        viewModel.updateVersion(appBuild.update.version)
    }

    private fun checkUpdate(appBuild: AppBuild) {
        if (!viewModel.updateUseCase.isAlreadyDownloaded()) {
            launchUpdate(appBuild)
        }
    }

    private fun observeNotificationSubscription() {
        lifecycleScope.launch {
            viewModel.getIsAppNotificationActivated().collect { isAppNotificationActivated ->
                viewModel.subscribeToAppTopicIf(isAppNotificationActivated)
            }
        }
    }

    private fun goBackToLogin() { launchActivityAndFinish(LoginActivity::class.java) }

    private fun goToDirectory() { launchActivity(DirectoryActivity::class.java) }

    private fun goToSettings() { launchActivity(SettingsActivity::class.java) }

    private fun launchUpdate(appBuild: AppBuild) {
        launchActivity(
            intent = Intent(this,UpdateActivity::class.java).apply {
                putExtra(UpdateActivity.UPDATE,appBuild)
            }
        )
    }

    private val requestPermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            isPostNotificationPermissionGranted =
                permissions[Manifest.permission.POST_NOTIFICATIONS]?:isPostNotificationPermissionGranted
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            isWriteExternalPermissionGranted =
                permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE]?:isWriteExternalPermissionGranted
        }

    }

    private fun handleIfProfileIsRequestedFromPlayer() {
        lifecycleScope.launch {
            viewModel.playerPreference.collectLatest { playerPreferences ->

                if (playerPreferences.requester.isRequesting) {
                    viewModel.resetRequestingProfile()
                    AnimeProfileActivity.launchActivity(this@MainActivity,playerPreferences.requester)
                }

            }
        }
    }

    companion object {
        const val CHAPTER_HOME_TARGET = "CHAPTER_HOME_TARGET"
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