package com.ead.project.dreamer.ui.login.termsandconditions

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.utils.DataStore
import com.ead.project.dreamer.databinding.ActivityTermsAndConditionsBinding
import com.ead.project.dreamer.ui.main.MainActivity


class TermsAndConditionsActivity : AppCompatActivity() {

    private lateinit var binding : ActivityTermsAndConditionsBinding
    private var emailProtected = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTermsAndConditionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        binding.buttonAccept.setOnClickListener {
            DataStore.writeBooleanAsync(Constants.PREFERENCE_TERMS_AND_CONDITIONS,true)
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
        binding.buttonReject.setOnClickListener {
            finish()
        }
        binding.txvEmailProtected.setOnClickListener {
            emailProtected = !emailProtected
            if (!emailProtected)
                binding.txvEmailProtected.text = Constants.CONTACT_EMAIL
            else
                binding.txvEmailProtected.text = getString(R.string.email_protected)
        }
    }
}