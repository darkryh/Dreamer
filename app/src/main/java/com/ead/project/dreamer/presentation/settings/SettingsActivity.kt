package com.ead.project.dreamer.presentation.settings

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ead.commons.lib.lifecycle.activity.onBack
import com.ead.commons.lib.views.getMutated
import com.ead.project.dreamer.R
import com.ead.project.dreamer.databinding.ActivitySettingsBinding
import com.ead.project.dreamer.presentation.settings.viewmodels.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    private val viewModel : SettingsViewModel by viewModels()

    private val binding : ActivitySettingsBinding by lazy {
        ActivitySettingsBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { onBack() }
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        init()
        settingThemeLayouts()
    }

    private fun init() {
        val fragment = SettingsDashboardFragment()
        val transaction = supportFragmentManager
            .beginTransaction()
        transaction.replace(R.id.frame_content_settings,fragment).commit()
    }

    private fun settingThemeLayouts() {
        val data = viewModel.isDarkThemeMode()

        if (data)
            binding.toolbar.navigationIcon =
                binding.toolbar.navigationIcon?.getMutated(this,R.color.whitePrimary)
        else
            binding.toolbar.navigationIcon =
                binding.toolbar.navigationIcon?.getMutated(this,R.color.blackPrimary)

    }

}