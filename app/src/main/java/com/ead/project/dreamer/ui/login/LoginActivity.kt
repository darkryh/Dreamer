package com.ead.project.dreamer.ui.login

import android.content.Intent
import android.content.res.Configuration

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ead.project.dreamer.BuildConfig
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.DreamerApp
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.retrofit.model.discord.Discord
import com.ead.project.dreamer.data.retrofit.model.discord.User
import com.ead.project.dreamer.data.utils.DataStore
import com.ead.project.dreamer.data.utils.ui.DreamerLayout
import com.ead.project.dreamer.databinding.ActivityLoginBinding
import com.ead.project.dreamer.ui.login.termsandconditions.TermsAndConditionsActivity
import com.ead.project.dreamer.ui.main.MainActivity
import com.ead.project.dreamer.ui.web.WebActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel : LoginViewModel by viewModels()

    private var currentVersion = BuildConfig.VERSION_NAME
    private var minVersion : Double ?= null
    private var user : User?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Dreamer)
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        init()
    }

    private fun init() {
        DataStore.writeBooleanAsync(Constants.IS_THE_APP_FROM_GOOGLE,false)
        screenInit()
        userSettings()
        getToken()
        binding.buttonLoginDiscord.setOnClickListener {
            startActivity(
                Intent(this, WebActivity::class.java).apply {
                    putExtra(Constants.WEB_ACTION,WebActivity.ACTION_LOGIN_DISCORD)
                    putExtra(Constants.WEB_ACTION_URL,Discord.ENDPOINT + Discord.LOGIN_PAGE)
                }
            )
        }

        binding.buttonGuest.setOnClickListener {
            goToMain()
        }
    }

    private fun screenInit() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) splashScreen

        val nightModeFlags: Int = resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK

        when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES ->
                binding.imvLogoLogin.setImageDrawable(
                DreamerLayout.getBackgroundColor(
                    binding.imvLogoLogin.drawable,
                    R.color.whitePrimary
                )
            )
            Configuration.UI_MODE_NIGHT_NO ->
                binding.imvLogoLogin.setImageDrawable(
                DreamerLayout.getBackgroundColor(
                    binding.imvLogoLogin.drawable,
                    R.color.blackPrimary
                ))
        }

    }

    private fun userSettings() {
        try {
            user = User.get()
            if (user != null) {
                DataStore.writeBooleanAsync(Constants.PREFERENCE_SKIP_LOGIN,false)
                goToMain()
            }
            else {
                if (DataStore.readBoolean(Constants.PREFERENCE_SKIP_LOGIN)) {
                    goToMain()
                }
            }
        } catch (e : Exception) {
            DreamerApp.showLongToast(e.cause!!.message.toString())
        }
    }


    private fun getToken() {
        loginViewModel.getToken().observe(this) {
            if (it != null && !it.isTokenUsed()) {
                DataStore.apply {
                    writeStringAsync(Constants.USED_ACCESS_TOKEN,it.access_token)
                    writeString(Discord.ACCESS_TOKEN, it.access_token)
                    writeString(Discord.REFRESH_TOKEN, it.refresh_token)
                }
                getUserData()
            }
        }
    }

    private fun getUserData() {
        loginViewModel.getUserData().observe(this) {
            try {
                if (it != null) {
                    binding.buttonGuest.text = getString(R.string.accepted_user,it.username)
                    User.set(it)
                    getRefreshToken(it)
                }
            }
            catch (e : Exception) {
                e.printStackTrace()
                DreamerApp.showLongToast(getString(R.string.discord_error))
            }
        }
    }

    private fun getRefreshToken(user: User) {
        loginViewModel.getRefreshToken().observe(this) {
            if (it != null) {
                DataStore.writeString(Discord.ACCESS_TOKEN, it.access_token)
                getUserIntoGuild(user)
            }
        }
    }

    private fun getUserIntoGuild(user: User) {
        loginViewModel.getUserInToGuild(user.id).observe(this) {
            goToMain()
        }
    }

    private var countLaunch = 0
    private fun goToMain() {
        if (!DataStore.readBoolean(Constants.VERSION_DEPRECATED))
            if (++countLaunch == 1) {
                if (DataStore.readBoolean(Constants.PREFERENCE_TERMS_AND_CONDITIONS)) {
                    startActivity(Intent(this, MainActivity::class.java))
                } else
                    startActivity(Intent(this, TermsAndConditionsActivity::class.java))
                finish()
            }
    }

    override fun onResume() {
        super.onResume()
        minVersion = DataStore.readDouble(Constants.MINIMUM_VERSION_REQUIRED)
        if (minVersion != 0.0) {
            binding.txvVersioning.text = getString(R.string.status_app, currentVersion, minVersion.toString())
            binding.txvVersioning.visibility = View.VISIBLE
            binding.txvVersioning.setOnClickListener {
                if (DataStore.readBoolean(Constants.IS_THE_APP_FROM_GOOGLE)) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.PLAY_STORE_APP))
                    startActivity(intent)
                }
                else {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.BLOG_APP))
                    startActivity(intent)
                }
            }
        }
    }
}