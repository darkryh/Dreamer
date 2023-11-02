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
import com.ead.commons.lib.lifecycle.observeOnce
import com.ead.commons.lib.views.setResourceImageAndColor
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.AppInfo
import com.ead.project.dreamer.app.data.network.Network
import com.ead.project.dreamer.app.data.network.NetworkType
import com.ead.project.dreamer.app.data.util.DirectoryUtil
import com.ead.project.dreamer.app.data.util.system.launchActivity
import com.ead.project.dreamer.app.data.util.system.launchActivityAndFinish
import com.ead.project.dreamer.app.data.util.system.showSnackBar
import com.ead.project.dreamer.app.model.AppBuild
import com.ead.project.dreamer.data.system.extensions.toast
import com.ead.project.dreamer.databinding.ActivityMainBinding
import com.ead.project.dreamer.presentation.directory.DirectoryActivity
import com.ead.project.dreamer.presentation.login.LoginActivity
import com.ead.project.dreamer.presentation.main.termsandconditions.TermsAndConditionsActivity
import com.ead.project.dreamer.presentation.settings.SettingsActivity
import com.ead.project.dreamer.presentation.update.UpdateActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel : MainActivityViewModel by viewModels()
    private val isGoogleVersion = AppInfo.isGoogleAppVersion
    private val currentVersion = AppInfo.versionValue
    private var isConnectionUnavailable = true

    private var isPostNotificationPermissionGranted = false
    private var isWriteExternalPermissionGranted = false

    private var appUpdateManager: AppUpdateManager?= null

    private val isUpdateImmediate = true
    private val updateType = if (isUpdateImmediate) AppUpdateType.IMMEDIATE else AppUpdateType.FLEXIBLE

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
                R.id.navigation_records,
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.bottomNavigationView.setupWithNavController(navController)
    }

    private fun init() {
        initVariables()
        initLayouts()
        observeNetworkState()
        observeDiscordUserState()
        observeDirectoryState()
        observeApplicationState()
        observeNotificationSubscription()
        handleContractInGoogleVersion()
    }

    private fun initVariables() {
        viewModel.castManager.initFactory(this,binding.mediaRouteButton)
    }

    private fun initLayouts() {
        binding.apply {
            editTextMainSearch.setOnClickListener { goToDirectory() }
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

    private val installStateUpdateListener = InstallStateUpdatedListener { installState ->  
        if (installState.installStatus() == InstallStatus.DOWNLOADED) {
            showSnackBar(
                rootView = binding.coordinator,
                text = "Descarga completa, reiniciando app.",
                color = R.color.orange_peel_dark,
                duration = Snackbar.LENGTH_SHORT
            )

            lifecycleScope.launch {
                delay(3.seconds)
                appUpdateManager?.completeUpdate()
            }
        }
    }

    override fun onResume() {
        viewModel.onResume()
        super.onResume()
        if (updateType == AppUpdateType.IMMEDIATE)
            appUpdateManager?.appUpdateInfo?.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                sendUpdateFlowResult(appUpdateInfo)
            }
        }
    }

    override fun onPause() {
        viewModel.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        viewModel.onDestroy()
        super.onDestroy()
        if (updateType == AppUpdateType.FLEXIBLE) {
            appUpdateManager?.unregisterListener(installStateUpdateListener)
        }
    }

    private fun observeDirectoryState() {
        lifecycleScope.launch {
            viewModel.getDirectoryState().collect { isSynchronized ->

                if (DirectoryUtil.stateSynchronized || isConnectionUnavailable) {
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
                isConnectionUnavailable = networkType != NetworkType.Wifi
                if (isConnectionUnavailable) {
                    showSnackBar(
                        rootView = binding.coordinator,
                        text = getString(R.string.wifi_warning),
                        color = R.color.orange_peel_dark,
                        duration = Snackbar.LENGTH_SHORT
                    )
                }
            }
        }
    }


    private fun observeDiscordUserState() {
        lifecycleScope.launch {
            viewModel.getAccount().collectLatest { eadAccount ->
                if (eadAccount == null) return@collectLatest

                binding.imageProfile.load(eadAccount.profileImage?:return@collectLatest){
                    transformations(CircleCropTransformation())
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

    private fun checkGoogleUpdates() {
        appUpdateManager?.appUpdateInfo?.addOnSuccessListener { appUpdateInfo ->
            val updateIsAvailable = appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            val updateIsAllowed = when(updateType) {
                AppUpdateType.FLEXIBLE -> appUpdateInfo.isFlexibleUpdateAllowed
                AppUpdateType.IMMEDIATE -> appUpdateInfo.isImmediateUpdateAllowed
                else -> false
            }

            if (updateIsAvailable && updateIsAllowed) {
                sendUpdateFlowResult(appUpdateInfo)
            }
        }
    }

    private fun sendUpdateFlowResult(appUpdateInfo : AppUpdateInfo) {
        appUpdateManager?.startUpdateFlowForResult(
            appUpdateInfo,
            updateType,
            this@MainActivity,
            GOOGLE_UPDATE_CODE
        )
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

    private fun handleContractInGoogleVersion() {
        lifecycleScope.launch {
            viewModel.getContractIfIsGoogleBuild().collectLatest { googleBuild ->
                if (!isGoogleVersion) {
                    settingPermissions()
                    return@collectLatest
                }


                if (googleBuild == null || !googleBuild.isTermsAndConditionsAccepted) {
                    launchActivity(TermsAndConditionsActivity::class.java)
                }
                else {
                    appUpdateManager = AppUpdateManagerFactory.create(applicationContext)
                    if (updateType == AppUpdateType.FLEXIBLE) {
                        appUpdateManager?.registerListener(installStateUpdateListener)
                    }
                    checkGoogleUpdates()
                    settingPermissions()
                }
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

    @Deprecated("Deprecated in Java", ReplaceWith(
        "super.onActivityResult(requestCode, resultCode, data)",
        "androidx.appcompat.app.AppCompatActivity"
    ))
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_UPDATE_CODE) {
            if (resultCode != RESULT_OK) {
                toast("error google update")
            }
        }
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

    companion object {
        const val CHAPTER_HOME_TARGET = "CHAPTER_HOME_TARGET"
        const val GOOGLE_UPDATE_CODE = 240
    }

}