package com.ead.project.dreamer.presentation.profile

import android.content.ContentValues.TAG
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import coil.load
import coil.transform.RoundedCornersTransformation
import com.ead.commons.lib.lifecycle.activity.onBack
import com.ead.commons.lib.lifecycle.activity.onBackHandle
import com.ead.commons.lib.views.addSelectableItemEffect
import com.ead.commons.lib.views.getMutated
import com.ead.commons.lib.views.setVisibility
import com.ead.commons.lib.views.setVisibilityReverse
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.data.util.system.launchActivity
import com.ead.project.dreamer.app.data.util.system.toPixels
import com.ead.project.dreamer.app.model.Requester
import com.ead.project.dreamer.data.database.model.AnimeProfile
import com.ead.project.dreamer.data.models.discord.DiscordUser
import com.ead.project.dreamer.data.utils.ui.AppBarStateChangeListener
import com.ead.project.dreamer.databinding.ActivityAnimeProfileBinding
import com.ead.project.dreamer.presentation.profile.adapters.ProfileViewPagerAdapter
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AnimeProfileActivity : AppCompatActivity() {

    private val viewModel : AnimeProfileViewModel by viewModels()

    private var id = -1
    private var reference = "reference"

    private var animeProfile : AnimeProfile ?= null

    private lateinit var viewPager : ProfileViewPagerAdapter

    private val binding: ActivityAnimeProfileBinding by lazy {
        ActivityAnimeProfileBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackHandle { onBackPressedMode() }
        setContentView(binding.root)
        init(false)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        count = 0
        init(true)
    }

    private fun init(isNewIntent : Boolean) {
        if(!isNewIntent) {
            initVariables()
        }
        initLayouts()
        configureProfile()
        observeProfile()
        if (isNewIntent) return
        handleIfProfileIsRequestedFromPlayer()
    }

    private var count = 0
    private fun observeProfile() {
        viewModel.getAnimeProfile(id).observe(this) { animeProfile ->
            if (animeProfile != null) {
                if (++count == 1) {
                    configureChapters()
                    loadAnimeProfileHeader(animeProfile)
                }
            }
        }
    }

    private fun initVariables() {
        intent.extras?.apply {
            id =  getInt(PREFERENCE_ID_BASE,0)
            reference = getString(PREFERENCE_LINK)?:return@apply
        }

        viewModel.castManager.initFactory(this,binding.mediaRouteButton)
        viewModel.adManager.setUp(
            returnCase = DiscordUser.isVip(),
            adId = getString(R.string.ad_unit_id_native_profile)
        )
    }

    override fun onResume() {
        viewModel.castManager.onResume()
        super.onResume()
    }

    override fun onPause() {
        viewModel.castManager.onPause()
        super.onPause()
    }

    private fun configureProfile() = viewModel.configureProfileData(id,reference)
    private fun configureChapters() = viewModel.configureChaptersData(id,reference)

    private fun loadAnimeProfileHeader(animeProfile: AnimeProfile) {
        binding.apply {
            binding.txvTitle.text = animeProfile.title
            binding.txvAnimeState.text = animeProfile.state
            binding.imvProfile.load(animeProfile.profilePhoto) {
                transformations(RoundedCornersTransformation(12f.toPixels()))
            }
            binding.imvCover.load(animeProfile.coverPhoto)
            binding.txvSecondTitle.text = animeProfile.titleAlternate
            binding.txvSecondTitle.setVisibility(animeProfile.titleAlternate != "null" && animeProfile.titleAlternate.isNotBlank())

        }
    }

    private fun initLayouts() {
        binding.apply {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayShowTitleEnabled(false)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            toolbar.navigationIcon =
                toolbar.navigationIcon?.getMutated(this@AnimeProfileActivity,R.color.white)
            toolbar.setNavigationOnClickListener { onBack() }
            imvDownloads.addSelectableItemEffect()
            imvDownloads.setOnClickListener {
                MaterialAlertDialogBuilder(this@AnimeProfileActivity)
                    .setTitle(getString(R.string.to_download))
                    .setMessage(getString(R.string.message_to_download_all_series,animeProfile?.title.toString()))
                    .setPositiveButton(getString(R.string.confirm)) { _: DialogInterface?, _: Int -> viewModel.downloadAllChapters(id) }
                    .setNegativeButton(R.string.cancel,null)
                    .show()
            }
            viewPager = ProfileViewPagerAdapter(this@AnimeProfileActivity,id)
            viewPager2.adapter = viewPager
            viewPager2.registerOnPageChangeCallback(object  : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    tabLayout.getTabAt(position)?.select()
                    if (position == 1) {
                        appBarLayout.setExpanded(false)
                    }
                    else {
                        appBarLayout.setExpanded(true)
                    }
                }
            })
            appBarLayout.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
                override fun onStateChanged(appBarLayout: AppBarLayout, state: State) {
                    when(state) {
                        State.EXPANDED -> {
                            constraintProfile.setVisibilityReverse(true)
                        }
                        State.COLLAPSED -> {
                            constraintProfile.setVisibilityReverse(false)
                        }
                        State.IDLE -> {
                            Log.d(TAG, "Profile App Bar Idle")
                        }
                    }
                }
            })
        }
        setupTabLayout()
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

    private fun handleIfProfileIsRequestedFromPlayer() {
        lifecycleScope.launch {
            viewModel.playerPreference.collectLatest { playerPreferences ->

                if (playerPreferences.requester.isRequesting) {
                    viewModel.resetRequestingProfile()
                    launchActivity(this@AnimeProfileActivity,playerPreferences.requester)
                }

            }
        }
    }


    private fun onBackPressedMode() { finish() }

    companion object {
        const val PREFERENCE_LINK = "PREFERENCE_LINK"
        const val PREFERENCE_ID_BASE = "ID_BASE"

        fun launchActivity(context : Context,requester: Requester) {
            context.launchActivity(
                intent = Intent(context,AnimeProfileActivity::class.java).apply {
                    Log.d("testing", " before launchActivity: $requester")
                    addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    putExtra(PREFERENCE_ID_BASE,requester.profileId)
                    putExtra(PREFERENCE_LINK,requester.profileReference)
                }
            )
        }
    }
}