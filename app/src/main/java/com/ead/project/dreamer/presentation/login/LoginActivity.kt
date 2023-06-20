package com.ead.project.dreamer.presentation.login

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ead.commons.lib.views.setResourceColor
import com.ead.commons.lib.views.setVisibility
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.AppInfo
import com.ead.project.dreamer.app.data.discord.Discord
import com.ead.project.dreamer.app.data.monos_chinos.MonosChinos
import com.ead.project.dreamer.app.data.util.system.launchActivity
import com.ead.project.dreamer.app.data.util.system.launchActivityAndFinish
import com.ead.project.dreamer.data.models.discord.DiscordUser
import com.ead.project.dreamer.data.system.extensions.toast
import com.ead.project.dreamer.databinding.ActivityLoginBinding
import com.ead.project.dreamer.presentation.main.MainActivity
import com.ead.project.dreamer.presentation.web.WebActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val viewModel : LoginViewModel by viewModels()

    private var isVersionDeprecated = false

    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.hide()

        initLayouts()
        observeApplicationState()
        observerAuthentication()
        observeDiscordState()
    }

    private fun initLayouts() {
        binding.apply {

            val nightModeFlags: Int = resources.configuration.uiMode and
                    Configuration.UI_MODE_NIGHT_MASK

            when (nightModeFlags) {
                Configuration.UI_MODE_NIGHT_YES ->
                    imvLogoLogin.setResourceColor(R.color.whitePrimary)
                Configuration.UI_MODE_NIGHT_NO ->
                    imvLogoLogin.setResourceColor(R.color.blackPrimary)
            }

            buttonLoginDiscord.setOnClickListener {
                launchActivity(
                    intent = Intent(this@LoginActivity, WebActivity::class.java).apply {
                        putExtra(WebActivity.WEB_ACTION,WebActivity.ACTION_LOGIN_DISCORD)
                        putExtra(WebActivity.WEB_ACTION_URL, Discord.ENDPOINT + Discord.LOGIN_PAGE)
                    }
                )
            }

            buttonGuest.setOnClickListener { goToMain() }
        }
    }

    private fun observeApplicationState() {
        lifecycleScope.launch {
            viewModel.getApplicationState().collect { appBuild ->
                isVersionDeprecated = appBuild.currentVersionDeprecated

                if (isVersionDeprecated) {
                    binding.apply {
                        txvVersioning.text = getString(R.string.status_app, AppInfo.version,appBuild.minVersion.toString())
                        txvVersioning.setVisibility(false)
                        txvVersioning.setOnClickListener {
                            val redirectionIntent : Intent = if (AppInfo.isGoogleAppVersion)
                                Intent(Intent.ACTION_VIEW, Uri.parse(AppInfo.PLAY_STORE_APP))
                            else
                                Intent(Intent.ACTION_VIEW, Uri.parse(AppInfo.WEB_APP))

                            launchActivity(intent = redirectionIntent)
                        }
                    }
                }

            }
        }
    }

    private fun observerAuthentication() {
        binding.apply {

            buttonLogin.setOnClickListener {

                if (editTextEmail.text.isBlank() || editTextPassword.text.isBlank()) {

                    this@LoginActivity.toast("Completar los datos.")
                    return@setOnClickListener

                }

                val username = editTextEmail.text.toString()
                val password = editTextPassword.text.toString()

                Log.d("testing", "observerAuthentication: $username - $password")

                viewModel.getAuthMe(username, password).observe(this@LoginActivity) { loginResponse ->

                    if (loginResponse != null) {
                        toast("login response")
                        Log.d("testing", "observerAuthentication: $loginResponse")
                    }
                    else {
                        toast("response null")
                    }
                }

            }

            buttonRegister.setOnClickListener {

                launchActivity(intent =  Intent(Intent.ACTION_VIEW, Uri.parse(MonosChinos.REGISTER)))

            }
        }
    }

    private fun getDiscordUser() {
        viewModel.getDiscordUser().observe(this) { discordUser ->

            if (discordUser != null) {
                binding.buttonGuest.text = getString(R.string.accepted_user,discordUser.username)
                Discord.login(discordUser)
                observeDiscordRefreshToken(discordUser)
            }

        }
    }

    private fun observeDiscordState() {
        viewModel.getDiscordToken().observe(this) { discordToken ->

            if (discordToken != null && (!discordToken.isAccessTokenUsed() || discordToken.notInitialized())) {

                Discord.setAccessUsedToken(discordToken.access_token)
                Discord.setDiscordToken(discordToken)
                getDiscordUser()

            }

        }
    }

    private fun observeDiscordRefreshToken(discordUser: DiscordUser) {
        viewModel.getRefreshToken().observe(this) { accessToken ->

            if (accessToken != null) {
                Discord.setDiscordToken(accessToken)
                getDiscordUserIntoGuild(discordUser)
            }

        }
    }

    private fun getDiscordUserIntoGuild(discordUser: DiscordUser) {
        viewModel.getDiscordUserInToGuild(discordUser.id).observe(this) {
            goToMain()
        }
    }

    private fun goToMain() {
        if (!isVersionDeprecated) {
            launchActivityAndFinish(MainActivity::class.java)
        }
    }
}