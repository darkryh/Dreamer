package com.ead.project.dreamer.presentation.login.termsandconditions

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.AppInfo
import com.ead.project.dreamer.databinding.ActivityTermsAndConditionsBinding
import com.ead.project.dreamer.presentation.main.MainActivity


class TermsAndConditionsActivity : AppCompatActivity() {

    private lateinit var binding : ActivityTermsAndConditionsBinding
    private var emailProtected = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTermsAndConditionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        binding.buttonAccept.setOnClickListener {
            //DataStore.writeBooleanAsync(Constants.PREFERENCE_TERMS_AND_CONDITIONS,true)
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
        binding.buttonReject.setOnClickListener {
            finish()
        }
        binding.textEmailProtected.setOnClickListener {
            emailProtected = !emailProtected
            if (!emailProtected)
                binding.textEmailProtected.text = AppInfo.CONTACT_DEVELOPER_EMAIL
            else
                binding.textEmailProtected.text = getString(R.string.email_protected)
        }
    }
}