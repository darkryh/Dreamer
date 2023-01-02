package com.ead.project.dreamer.ui.settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ead.commons.lib.lifecycle.activity.onBack
import com.ead.commons.lib.views.getMutated
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.utils.DataStore
import com.ead.project.dreamer.databinding.ActivitySettingsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
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
        transaction.replace(R.id.Frame_Content_Settings,fragment).commit()
    }

    private fun settingThemeLayouts() {
        val data = DataStore
            .readBoolean(Constants.PREFERENCE_THEME_MODE)

        if (data)
            binding.toolbar.navigationIcon =
                binding.toolbar.navigationIcon?.getMutated(this,R.color.whitePrimary)
        else
            binding.toolbar.navigationIcon =
                binding.toolbar.navigationIcon?.getMutated(this,R.color.blackPrimary)

    }

    override fun onStop() {
        super.onStop()
        Constants.setConfigurationActivityClicked(false)
    }
}