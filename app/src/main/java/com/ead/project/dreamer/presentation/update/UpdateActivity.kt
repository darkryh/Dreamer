package com.ead.project.dreamer.presentation.update

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ead.commons.lib.lifecycle.activity.onBack
import com.ead.commons.lib.lifecycle.parcelable
import com.ead.commons.lib.views.justifyInterWord
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.data.util.Apk
import com.ead.project.dreamer.app.data.util.system.round
import com.ead.project.dreamer.app.model.AppBuild
import com.ead.project.dreamer.data.system.extensions.toast
import com.ead.project.dreamer.databinding.ActivityUpdateBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

@AndroidEntryPoint
class UpdateActivity : AppCompatActivity() {

    private val viewModel : UpdateViewModel by viewModels()
    private lateinit var appBuild : AppBuild
    private var installingCount = 0

    private val binding : ActivityUpdateBinding by lazy {
        ActivityUpdateBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initVariables()
        setupLayouts()
        bindingUpdate()
    }

    private fun initVariables() {
        intent?.extras?.apply {
            appBuild = parcelable(UPDATE)?:return
        }
    }

    private fun setupLayouts() {
        binding.apply {
            imageClose.setOnClickListener { onBack() }
            buttonSkip.setOnClickListener { onBack() }
            textResumedVersion.justifyInterWord()
        }
    }

    private fun bindingUpdate() {
        binding.apply {
            textTitle.text = getString(R.string.new_version, appBuild.update.version)
            textResumedVersion.text = appBuild.resumedVersionNotes ?:getString(R.string.content_new_version_download)

            buttonDownloadAndInstall.setOnClickListener {
                if (!canRequestUpdate()) {

                    requestInstallPermission()

                    return@setOnClickListener
                }

                if (viewModel.updateUseCase.isAlreadyDownloaded()) {

                    installUpdate()

                }
                else {

                    viewModel.updateUseCase.getUpdate(viewModel.downloadUpdate(appBuild)).observe(this@UpdateActivity) { download ->
                        if (download == null) return@observe

                        buttonDownloadAndInstall.isEnabled = false

                        val percentProgress = ((download.current * 100f) / download.total).round(2)
                        val currentProgress = percentProgress.roundToInt()
                        linearProgressDownload.progress = currentProgress
                        textProgress.text = getString(R.string.current_percent,percentProgress)

                        if (currentProgress < 100) return@observe
                        if (++installingCount > 1) return@observe

                        installUpdate()
                    }

                }
            }
        }

    }

    private fun requestInstallPermission() {
        toast(getString(R.string.requesting_installer_permission))

        val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            toast(getString(R.string.couldnt_open_package_installer))
        }
    }

    private fun canRequestUpdate() : Boolean {
        return packageManager.canRequestPackageInstalls()
    }

    private fun installUpdate() {
        Apk.install(this@UpdateActivity,viewModel.appBuildPreferences.getLastVersionFile())
    }

    companion object {
        const val UPDATE = "UPDATE_APP"
    }
}