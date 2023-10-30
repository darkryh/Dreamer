package com.ead.project.dreamer.presentation.main.termsandconditions

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ead.commons.lib.lifecycle.activity.onBackHandle
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.AppInfo
import com.ead.project.dreamer.databinding.ActivityTermsAndConditionsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TermsAndConditionsActivity : AppCompatActivity() {

    private var emailProtected = false

    private val binding : ActivityTermsAndConditionsBinding by lazy {
        ActivityTermsAndConditionsBinding.inflate(layoutInflater)
    }

    private val viewModel : TermsAndConditionsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackHandle { finishAffinity() }
        setContentView(binding.root)
        supportActionBar?.hide()
        bindingContract()
    }

    private fun bindingContract() {
        binding.apply {
            buttonAccept.setOnClickListener {
                viewModel.acceptContract()
                finish()
            }

            buttonReject.setOnClickListener {
                finishAffinity()
            }

            textEmailProtected.setOnClickListener {
                emailProtected = !emailProtected
                if (!emailProtected)
                    textEmailProtected.text = AppInfo.CONTACT_DEVELOPER_EMAIL
                else
                    textEmailProtected.text = getString(R.string.email_protected)
            }
        }
    }
}