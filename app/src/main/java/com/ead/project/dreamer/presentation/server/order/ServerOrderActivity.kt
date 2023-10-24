package com.ead.project.dreamer.presentation.server.order

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.ead.commons.lib.lifecycle.activity.onBack
import com.ead.commons.lib.views.getMutated
import com.ead.project.dreamer.R
import com.ead.project.dreamer.databinding.ActivityServerOrderBinding
import com.ead.project.dreamer.presentation.server.order.adapter.ServerOrderViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ServerOrderActivity : AppCompatActivity() {

    private val viewModel : ServerOrderViewModel by viewModels()
    private lateinit var viewPager : ServerOrderViewPagerAdapter

    var isInfoExpanded = false

    private val binding : ActivityServerOrderBinding by lazy {
        ActivityServerOrderBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupLayouts()
        setupViewPager()
        setupTabLayout()
    }

    private fun setupLayouts() {
        binding.apply {

            textTitle.text = getString(R.string.servers_orders)

            supportActionBar?.hide()

            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayShowTitleEnabled(false)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            toolbar.setNavigationOnClickListener { onBack() }

            if (viewModel.appBuildPreferences.isDarkTheme()) {
                toolbar.navigationIcon =
                    toolbar.navigationIcon?.getMutated(this@ServerOrderActivity, R.color.white)
            }
            else {
                toolbar.navigationIcon =
                    toolbar.navigationIcon?.getMutated(this@ServerOrderActivity, R.color.blackPrimary)
            }
        }
    }

    private fun setupViewPager() {
        binding.apply {
            viewPager = ServerOrderViewPagerAdapter(this@ServerOrderActivity)
            viewPager2.adapter = viewPager
            viewPager2.registerOnPageChangeCallback(object  : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    binding.tabLayout.getTabAt(position)?.select()
                }
            })
        }
    }

    private fun setupTabLayout() {
        binding.apply {
            tabLayout.addOnTabSelectedListener(object  : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    if (tab != null) {
                        viewPager2.currentItem = tab.position
                    }
                }
                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })
        }
    }
}