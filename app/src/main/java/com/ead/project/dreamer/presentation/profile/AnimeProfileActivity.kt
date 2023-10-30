package com.ead.project.dreamer.presentation.profile

import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import coil.load
import coil.transform.RoundedCornersTransformation
import com.ead.commons.lib.lifecycle.activity.onBackHandle
import com.ead.commons.lib.views.addSelectableItemEffect
import com.ead.commons.lib.views.setResourceImageAndColor
import com.ead.commons.lib.views.setVisibility
import com.ead.commons.lib.views.setVisibilityReverse
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.data.util.system.handleNotActionBar
import com.ead.project.dreamer.app.data.util.system.toPixels
import com.ead.project.dreamer.data.database.model.AnimeProfile
import com.ead.project.dreamer.data.system.extensions.toast
import com.ead.project.dreamer.data.utils.ui.AppBarStateChangeListener
import com.ead.project.dreamer.databinding.ActivityAnimeProfileBinding
import com.ead.project.dreamer.presentation.profile.adapters.ProfileViewPagerAdapter
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AnimeProfileActivity : AppCompatActivity() {

    private val viewModel : AnimeProfileViewModel by viewModels()
    private lateinit var viewPager : ProfileViewPagerAdapter

    private var id = 1
    private var reference = "reference"
    private var animeProfile : AnimeProfile ?= null
    private var isFavorite : Boolean? = null

    private val binding: ActivityAnimeProfileBinding by lazy {
        ActivityAnimeProfileBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackHandle { onBackPressedMode() }
        setContentView(binding.root)
        initVariables()
        initLayouts()
        configureProfile()
        observeProfile()
        observeLikedProfile()
    }

    private fun observeProfile() {
        viewModel.getAnimeProfile(id).observe(this) { animeProfile ->
            if (animeProfile == null || this.animeProfile?.id == animeProfile.id) return@observe

            this.animeProfile = animeProfile

            if (viewModel.appBuildPreferences.isUnlockedVersion() || animeProfile.isAuthorizedData()) {
                configureChapters()
                loadAnimeProfileHeader(animeProfile)
            }
            else {
                toast(getString(R.string.google_policies_message),Toast.LENGTH_SHORT)
                finish()
            }
        }
    }

    private fun observeLikedProfile() {
        viewModel.getIsLikedProfile(id).observe(this) { isFavorite ->
            if (isFavorite == null || this.isFavorite == isFavorite) return@observe
            this@AnimeProfileActivity.isFavorite = isFavorite

            bindingLikedProfile(isFavorite)
        }
    }

    private fun initVariables() {
        intent.extras?.apply {
            id = getInt(PREFERENCE_ID)
            reference = getString(PREFERENCE_LINK)?:return@apply
        }
        viewModel.castManager.initFactory(this,binding.mediaRouteButton)
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
            textTitle.text = animeProfile.title
            textAnimeState.text = animeProfile.state
            imageProfile.load(animeProfile.profilePhoto) {
                transformations(RoundedCornersTransformation(12f.toPixels()))
            }
            imageCover.load(animeProfile.coverPhoto)
            textSecondTitle.text = animeProfile.titleAlternate
            textSecondTitle.setVisibility(animeProfile.titleAlternate != "null" && animeProfile.titleAlternate.isNotBlank())
        }
    }

    private fun initLayouts() {
        binding.apply {
            handleNotActionBar(toolbar)
            imageDownloads.addSelectableItemEffect()
            imageDownloads.setOnClickListener {
                MaterialAlertDialogBuilder(this@AnimeProfileActivity)
                    .setTitle(getString(R.string.to_download))
                    .setMessage(getString(R.string.message_to_download_all_series,animeProfile?.title.toString()))
                    .setPositiveButton(getString(R.string.confirm)) { _: DialogInterface?, _: Int -> viewModel.downloadAllChapters(id) }
                    .setNegativeButton(R.string.cancel,null)
                    .show()
            }

            imageLikeProfile.setOnClickListener {

                val animeProfile = this@AnimeProfileActivity.animeProfile
                val isFavorite = this@AnimeProfileActivity.isFavorite
                if (animeProfile == null || isFavorite == null) return@setOnClickListener

                viewModel.updateAnimeProfile(animeProfile.copy(isFavorite = !isFavorite))
            }
        }
        setupViewPager2()
        setupAppBarStateListener()
        setupTabLayout()
    }

    private fun setupViewPager2() {
        binding.apply {
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
        }
    }

    private fun setupAppBarStateListener() {
        binding.apply {
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

    private fun bindingLikedProfile(isFavorite : Boolean) {
        binding.apply {
            if (isFavorite) {
                imageLikeProfile.setResourceImageAndColor(
                    R.drawable.ic_favorite_24, R.color.pink)
            }
            else {
                imageLikeProfile.setResourceImageAndColor(
                    R.drawable.ic_favorite_border_24, R.color.white)
            }
        }
    }

    private fun onBackPressedMode() { finish() }

    companion object {
        const val PREFERENCE_LINK = "PROFILE_LINK"
        const val PREFERENCE_ID = "PROFILE_ID"
    }
}