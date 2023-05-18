package com.ead.project.dreamer.presentation.main

import android.Manifest
import android.content.DialogInterface
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
import com.ead.project.dreamer.app.data.server.Server
import com.ead.project.dreamer.app.data.util.system.launchActivity
import com.ead.project.dreamer.app.data.util.system.launchActivityAndFinish
import com.ead.project.dreamer.app.model.AppBuild
import com.ead.project.dreamer.databinding.ActivityMainBinding
import com.ead.project.dreamer.presentation.directory.DirectoryActivity
import com.ead.project.dreamer.presentation.login.LoginActivity
import com.ead.project.dreamer.presentation.profile.AnimeProfileActivity
import com.ead.project.dreamer.presentation.settings.SettingsActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Timer

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel : MainActivityViewModel by viewModels()
    private val currentVersion = AppInfo.versionValue

    private var directoryChecked = false
    private var timerAdv : Timer ?= null
    private var countAdv = 0

    private var isPostNotificationPermissionGranted = false
    private var isWriteExternalPermissionGranted = false

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Dreamer)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        supportActionBar?.hide()
        init()

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView) as NavHostFragment
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
        binding.navView.setupWithNavController(navController)
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
            edtMainSearch.setOnClickListener{ goToDirectory() }
            imvSearch.setOnClickListener { goToDirectory() }
            imvProfile.setResourceImageAndColor(R.drawable.ic_user,R.color.white)
            imvProfile.setOnClickListener { goToSettings() }
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

                if (!isSynchronized) {
                    syncState()
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
                    binding.imvProfile.load(discordUser.getAvatarUrl()) {
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
            else {

                Server.setAutomaticResolver(false)

            }

        }
    }

    private fun observeApplicationState() {
        viewModel.getStatusApp().observeOnce(this) { appBuild ->

            val updateAvailable = currentVersion < appBuild.lastVersion
            if (updateAvailable) {
                checkUpdate(appBuild)
            }
            if (currentVersion < appBuild.minVersion && !updateAvailable) {
                goBackToLogin()
            }

        }
    }

    private fun checkUpdate(appBuild: AppBuild) {
        val updateApk = getString(R.string.apk_title_new_version_download, appBuild.lastVersion.toString())
        //viewModel.setPreference(Constants.VERSION_UPDATE,updateApk)
        if (!viewModel.downloadUseCase.checkIfUpdateIsAlreadyDownloaded()) {
            showUpdateMessage(appBuild,updateApk)
        }
    }

    private fun showUpdateMessage(appBuild: AppBuild, title : String) {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.title_new_version_download,appBuild.lastVersion.toString()))
            .setMessage(appBuild.resumedVersionNotes?:getString(R.string.content_new_version_download))
            .setPositiveButton(getString(R.string.to_download)) { _: DialogInterface?, _: Int ->
                viewModel.downloadUseCase.launchUpdate(title, appBuild.downloadReference)
            }
            .setNegativeButton(R.string.cancel,null)
            .show()
    }

    private fun syncState() {
        /*viewModel.directoryState().observe(this) {
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

        }*/
    }

    private fun showAdvices() {
        /*if (!directoryChecked)
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
            }*/
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


    /* para mostrar notificación de configuración

    * isGranted -> {
                if (Constants.isFirstTimeShowingNotification())
                    NotificationManager.showSettingNotification(notifier,this)
                "Granted permission"
            }
            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> "Rational permission"
            else -> "Denied permission"*/
}