package com.ead.project.dreamer.ui.main


import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import coil.load
import coil.transform.CircleCropTransformation
import com.ead.project.dreamer.BuildConfig
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.AppManager
import com.ead.project.dreamer.app.DreamerApp
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.commons.Tools
import com.ead.project.dreamer.data.retrofit.model.discord.Discord
import com.ead.project.dreamer.data.retrofit.model.discord.User
import com.ead.project.dreamer.data.utils.DataStore
import com.ead.project.dreamer.data.utils.media.CastManager
import com.ead.project.dreamer.databinding.ActivityMainBinding
import com.ead.project.dreamer.ui.directory.DirectoryActivity
import com.ead.project.dreamer.ui.login.LoginActivity
import com.ead.project.dreamer.ui.settings.SettingsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainActivityViewModel : MainActivityViewModel by viewModels()
    private val user : User? = User.get()
    private val currentVersion = BuildConfig.VERSION_NAME.toDouble()
    private var castManager: CastManager = CastManager()
    private val appManager = AppManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Dreamer)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        supportActionBar?.hide()
        init()
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
                R.id.navigation_favorites,
                R.id.navigation_records,
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        binding.edtMainSearch.setOnClickListener{
            goToDirectory()
        }
        binding.imvSearch.setOnClickListener {
            goToDirectory()
        }

    }

    private fun init(){
        DreamerApp.initAdsPreferences()
        initSettings()
        userSettings()
        appSettings()
        checkStatusApp()
        checkSubscribedTopic()
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
    }

    private fun appSettings() {
        if (DataStore.readBoolean(Constants.IS_THE_APP_FROM_GOOGLE)) DataStore
            .writeBooleanAsync(Constants.PREFERENCE_GOOGLE_POLICY, true)

        binding.imvProfile.setOnClickListener {
            if (!DataStore.readBoolean(Constants.PREFERENCE_SETTINGS_CLICKED)) {
                DataStore.writeBooleanAsync(Constants.PREFERENCE_SETTINGS_CLICKED,true)
                startActivity(Intent(this, SettingsActivity::class.java))
            }
        }
        castManager.setViewModel(mainActivityViewModel)
        castManager.initButtonFactory(this,binding.mediaRouteButton)
    }


    private fun userSettings() {
        if (user == null) {
            DataStore.writeBooleanAsync(Constants.PREFERENCE_RANK_AUTOMATIC_PLAYER,false)
        }
        else {
            mainActivityViewModel.getGuildMember(user.id).observe(this) {
                try {
                    if (it != null && it.roles.isNotEmpty()) {
                        user.rankLevel = User.getRankValue(it.roles)
                        user.rank = User.getRank(user.rankLevel)
                        User.set(user)
                    }
                }catch (e : Exception) {
                    e.printStackTrace()
                }
            }

            if (user.avatar != null) {
                binding.imvProfile.load(Discord.PROFILE_IMAGE) {
                    transformations(CircleCropTransformation())
                }
            }
        }
    }

    private fun checkStatusApp() {
        mainActivityViewModel.getStatusApp().observe(this) {
            if (currentVersion < it.version ) {
                startActivity(Intent(this, LoginActivity::class.java))
                DataStore.writeDouble(Constants.MINIMUM_VERSION_REQUIRED,it.version)
                DataStore.writeBooleanAsync(Constants.VERSION_DEPRECATED,true)
                finish()
            }
        }
    }

    private fun checkSubscribedTopic() {
        if (DataStore.readBoolean(Constants.DREAMER_TOPIC,true))
            FirebaseMessaging.getInstance().subscribeToTopic(Constants.DREAMER_TOPIC)
        else
            FirebaseMessaging.getInstance().unsubscribeFromTopic(Constants.DREAMER_TOPIC)
    }

    private fun goToDirectory() {
        if (!DataStore.readBoolean(Constants.PREFERENCE_DIRECTORY_CLICKED,true)) {
            DataStore.writeBooleanAsync(Constants.PREFERENCE_DIRECTORY_CLICKED,true)
            startActivity(Intent(this, DirectoryActivity::class.java))
        }
    }
}