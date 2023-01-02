package com.ead.project.dreamer.ui.login

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ead.commons.lib.lifecycle.activity.showLongToast
import com.ead.commons.lib.views.setResourceColor
import com.ead.project.dreamer.BuildConfig
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.models.discord.Discord
import com.ead.project.dreamer.data.models.discord.User
import com.ead.project.dreamer.data.utils.DataStore
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

    private var countLaunch = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        init()
    }

    private fun init() {
        screenInit()
        getToken()
        binding.buttonLoginDiscord.setOnClickListener {
            startActivity(
                Intent(this, WebActivity::class.java).apply {
                    putExtra(Constants.WEB_ACTION,WebActivity.ACTION_LOGIN_DISCORD)
                    putExtra(Constants.WEB_ACTION_URL, Discord.ENDPOINT + Discord.LOGIN_PAGE)
                }
            )
        }

        binding.buttonGuest.setOnClickListener { goToMain() }
    }

    private fun screenInit() {
        val nightModeFlags: Int = resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK

        when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES ->
                binding.imvLogoLogin.setResourceColor(R.color.whitePrimary)
            Configuration.UI_MODE_NIGHT_NO ->
                binding.imvLogoLogin.setResourceColor(R.color.blackPrimary)
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
                showLongToast(getString(R.string.discord_error))
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

    private fun goToMain() {
        if (Constants.isVersionNotDeprecated())
            if (++countLaunch == 1) {
                if (Constants.isTermsAndConditionsNotNeeded()) {
                    startActivity(Intent(this, MainActivity::class.java))
                } else
                    startActivity(Intent(this, TermsAndConditionsActivity::class.java))
                finish()
            }
    }

    override fun onResume() {
        super.onResume()
        minVersion = Constants.getMinimumVersion()
        if (minVersion != 0.0) {
            binding.txvVersioning.text = getString(R.string.status_app, currentVersion, minVersion.toString())
            binding.txvVersioning.visibility = View.VISIBLE
            binding.txvVersioning.setOnClickListener {
                val redirectionIntent : Intent = if (Constants.isAppFromGoogle())
                    Intent(Intent.ACTION_VIEW, Uri.parse(Constants.PLAY_STORE_APP))
                else
                    Intent(Intent.ACTION_VIEW, Uri.parse(Constants.WEB_APP))

                startActivity(redirectionIntent)
            }
        }
    }
}