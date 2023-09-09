package com.ead.project.dreamer.presentation.player.cast

import android.content.Intent
import android.os.Bundle
import android.text.format.DateUtils
import android.view.View
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.viewpager2.widget.ViewPager2
import coil.load
import coil.transform.CircleCropTransformation
import com.ead.commons.lib.lifecycle.activity.onBack
import com.ead.commons.lib.resource.getDrawableFromIdNotNull
import com.ead.commons.lib.views.addSelectableItemEffect
import com.ead.commons.lib.views.setResourceImageAndColor
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.data.discord.Discord
import com.ead.project.dreamer.app.data.util.system.hide
import com.ead.project.dreamer.app.data.util.system.show
import com.ead.project.dreamer.data.database.model.AnimeProfile
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.models.discord.DiscordUser
import com.ead.project.dreamer.databinding.ActivityExpandedControllerCastBinding
import com.ead.project.dreamer.presentation.player.PlayerViewModel
import com.ead.project.dreamer.presentation.player.cast.adapters.CastingViewPagerAdapter
import com.google.android.gms.cast.framework.media.widget.ExpandedControllerActivity
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ExpandedControlsActivity  : ExpandedControllerActivity() {

    private val viewModel : PlayerViewModel by viewModels()

    private var chapter : Chapter?= null
    private val discordUser : DiscordUser? = Discord.getUser()
    private lateinit var viewPager: CastingViewPagerAdapter
    private var count = 0

    private lateinit var castViewHolderAd: CastViewHolderAd
    private val shareIntent = Intent(Intent.ACTION_SEND)

    private val binding : ActivityExpandedControllerCastBinding by lazy {
        ActivityExpandedControllerCastBinding.inflate(layoutInflater)
    }
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        init()
    }

    private fun init() {
        settingVariables()
        setFunctionality()
        setCastingFunctionality()
        settingLayouts()
        settingProfile()
    }

    private fun settingVariables() {
        chapter = viewModel.castManager.getChapter()?:return
        viewModel.adManager.setUp(
            returnCase = DiscordUser.isVip(),
            adId = getString(R.string.ad_unit_id_native_casting)
        )
        viewModel.castManager.initFactory(this,binding.mediaRouteButton)
    }

    private fun settingProfile() {
        viewModel.getProfileData(chapter?.idProfile?:return).observe(this) { animeProfile ->
            if (animeProfile != null) {
                if (++count == 1) likeProfile(animeProfile)
                updateLike(animeProfile)
            }
        }
    }

    private fun settingLayouts() {
        binding.apply {
            viewPager = CastingViewPagerAdapter(this@ExpandedControlsActivity,chapter?:return)
            viewPager2.adapter = viewPager
            viewPager2.registerOnPageChangeCallback(object  : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    tabLayout.getTabAt(position)?.select()
                }
            })
            setupTabLayout()
            if (discordUser?.getAvatarUrl()!= null)
                imvProfile.load(discordUser.getAvatarUrl()) { transformations(CircleCropTransformation()) }

            imvShare.setOnClickListener {
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Dreamer App")
                var shareMessage = "\nMe encanto ver este capítulo de ${chapter?.title}\n\n"
                shareMessage =
                    """
                    $shareMessage ${chapter?.reference}
                    """.trimIndent()
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                startActivity(Intent.createChooser(shareIntent, "Escoge uno"))
            }
            txvTitle.text = chapter?.title
            txvChapterNumber.text = getString(R.string.chapter_number,chapter?.number.toString())
        }
    }

    private fun setFunctionality() {
        binding.apply {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayShowTitleEnabled(false)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            bannerShimmer.show()
            toolbar.navigationIcon = getDrawableFromIdNotNull(R.drawable.ic_down_back_24)
            toolbar.setNavigationOnClickListener { onBack() }
            btnForward.addSelectableItemEffect()
            btnRewind.addSelectableItemEffect()
            btnPlay.addSelectableItemEffect()
            castViewHolderAd = CastViewHolderAd(banner)
            if (DiscordUser.isVip()) {
                lnBanner.visibility = View.GONE
                bannerShimmer.hide()
            }
            setupAd()
        }
    }

    private fun setCastingFunctionality() {
        binding.apply {
            uiMediaController.bindSeekBar(seekBar)
            uiMediaController.bindTextViewToStreamPosition(txvCurrentProgress,true)
            uiMediaController.bindTextViewToStreamDuration(txvTotalProgress)
            uiMediaController.bindImageViewToPlayPauseToggle(
                btnPlay,
                getDrawableFromIdNotNull(R.drawable.ic_play),
                getDrawableFromIdNotNull(R.drawable.ic_pause),
                getDrawableFromIdNotNull(R.drawable.cast_ic_expanded_controller_stop),
                progressBar,
                true)
            uiMediaController.bindViewToRewind(btnRewind,30 * DateUtils.SECOND_IN_MILLIS)
            uiMediaController.bindViewToForward(btnForward,30 * DateUtils.SECOND_IN_MILLIS)
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

    private fun setupAd() {
        viewModel.adManager.getItem().observe(this) {
            castViewHolderAd.bindTo(it?:return@observe)
            binding.banner.root.layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
            binding.bannerShimmer.hide()
            binding.banner.root.visibility = View.VISIBLE
        }
    }

    private fun likeProfile(animeProfile: AnimeProfile) {
        binding.imvLikeProfile.setOnClickListener {
            viewModel.updateAnimeProfile(animeProfile.copy(
                isFavorite = !animeProfile.isFavorite
            ))
        }
    }

    private fun updateLike(animeProfile: AnimeProfile) {
        if (animeProfile.isFavorite)
            binding.imvLikeProfile.setResourceImageAndColor(R.drawable.ic_favorite_24, R.color.pink)
         else
             binding.imvLikeProfile.setResourceImageAndColor(R.drawable.ic_favorite_border_24, R.color.white)
    }

    override fun onPause() {
        viewModel.castManager.onMediaStatus()
        super.onPause()
    }

    override fun onDestroy() {
        viewModel.castManager.onDestroy()
        viewModel.adManager.restore()
        viewModel.adManager.onDestroy()
        super.onDestroy()
    }
}