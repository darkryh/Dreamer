package com.ead.project.dreamer.ui.player.cast

import android.content.Intent
import android.os.Bundle
import android.text.format.DateUtils
import android.view.View
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.viewpager2.widget.ViewPager2
import coil.load
import coil.transform.CircleCropTransformation
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.commons.Tools.Companion.hide
import com.ead.project.dreamer.data.commons.Tools.Companion.onBack
import com.ead.project.dreamer.data.commons.Tools.Companion.show
import com.ead.project.dreamer.data.database.model.AnimeProfile
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.models.discord.Discord
import com.ead.project.dreamer.data.models.discord.User
import com.ead.project.dreamer.data.utils.AdManager
import com.ead.project.dreamer.data.utils.media.CastManager
import com.ead.project.dreamer.data.utils.ui.DreamerLayout
import com.ead.project.dreamer.databinding.ActivityExpandedControllerCastBinding
import com.ead.project.dreamer.ui.player.PlayerViewModel
import com.ead.project.dreamer.ui.player.cast.adapters.CastingViewPagerAdapter
import com.google.android.gms.cast.framework.media.widget.ExpandedControllerActivity
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ExpandedControlsActivity  : ExpandedControllerActivity() {

    private lateinit var binding : ActivityExpandedControllerCastBinding
    private var castManager : CastManager = CastManager(true)
    private val playerViewModel : PlayerViewModel by viewModels()
    private lateinit var chapter : Chapter
    private val user : User? = User.get()
    private lateinit var viewPager: CastingViewPagerAdapter
    private var count = 0

    private lateinit var castViewHolderAd: CastViewHolderAd
    private var adManager : AdManager?= null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpandedControllerCastBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init(false)
        castManager.setViewModel(playerViewModel)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        init(true)
    }

    private fun init(isNewIntent : Boolean) {
        settingVariables()
        setFunctionality()
        setCastingFunctionality()
        settingLayouts()
        settingProfile()
        if (!isNewIntent) castManager.setViewModel(playerViewModel)
    }

    private fun settingVariables() {
        chapter = Chapter.getCasting()!!
        adManager = AdManager(
            context = this,
            adId = getString(R.string.ad_unit_id_native_casting)
        )
        adManager?.setUp(User.isNotVip())
        castManager.initButtonFactory(this,binding.mediaRouteButton)
    }

    private fun settingProfile() {
        playerViewModel.getProfile(chapter.idProfile).observe(this) { animeProfile ->
            if (animeProfile != null) {
                if (++count == 1) likeProfile(animeProfile)
                updateLike(animeProfile)
            }
        }
    }

    private fun settingLayouts() {
        viewPager = CastingViewPagerAdapter(this,chapter)
        binding.viewPager2.adapter = viewPager
        binding.viewPager2.registerOnPageChangeCallback(object  : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.tabLayout.getTabAt(position)?.select()
            }
        })
        setupTabLayout()
        if (user != null)
            if (user.avatar != null) {
                binding.imvProfile.load(Discord.PROFILE_IMAGE) {
                    transformations(CircleCropTransformation())
                }
            }
        binding.imvShare.setOnClickListener {
            try {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Dreamer App")
                var shareMessage = "\nMe encanto ver este capítulo de ${chapter.title}\n\n"
                shareMessage =
                    """
                    $shareMessage ${chapter.reference}
                    """.trimIndent()
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                startActivity(Intent.createChooser(shareIntent, "Escoge uno"))
            } catch (e: Exception) { e.toString() }
        }
        binding.txvTitle.text = chapter.title
        binding.txvChapterNumber.text = getString(R.string.chapter_number,chapter.chapterNumber.toString())
    }

    private fun setFunctionality() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.bannerShimmer.show()
        binding.toolbar.navigationIcon = DreamerLayout.getDrawable(R.drawable.ic_expand_38)
        binding.toolbar.setNavigationOnClickListener { onBack() }
        DreamerLayout.setClickEffect(binding.btnForward,this)
        DreamerLayout.setClickEffect(binding.btnRewind,this)
        DreamerLayout.setClickEffect(binding.btnPlay,this)
        castViewHolderAd = CastViewHolderAd(binding.banner)
        if (User.isVip()) {
            binding.lnBanner.visibility = View.GONE
            binding.bannerShimmer.hide()
        }
        setupAd()
    }

    private fun setCastingFunctionality() {
        uiMediaController.bindSeekBar(binding.seekBar)
        uiMediaController.bindTextViewToStreamPosition(binding.txvCurrentProgress,true)
        uiMediaController.bindTextViewToStreamDuration(binding.txvTotalProgress)
        uiMediaController.bindImageViewToPlayPauseToggle(
            binding.btnPlay,
            DreamerLayout.getDrawable(R.drawable.ic_play),
            DreamerLayout.getDrawable(R.drawable.ic_pause),
            DreamerLayout.getDrawable(R.drawable.cast_ic_expanded_controller_stop),
            binding.progressBar,
            true)
        uiMediaController.bindViewToRewind(binding.btnRewind,30 * DateUtils.SECOND_IN_MILLIS)
        uiMediaController.bindViewToForward(binding.btnForward,30 * DateUtils.SECOND_IN_MILLIS)
    }

    private fun setupTabLayout() {
        binding.tabLayout.addOnTabSelectedListener(object  : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    binding.viewPager2.currentItem = tab.position
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupAd() {
        adManager?.getAd()?.observe(this) {
            binding.banner.root.layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
            castViewHolderAd.bindTo(it)
            binding.bannerShimmer.hide()
            binding.banner.root.visibility = View.VISIBLE
        }
    }

    private fun likeProfile (animeProfile: AnimeProfile) {
        binding.imvLikeProfile.setOnClickListener {
            animeProfile.isFavorite = !animeProfile.isFavorite
            playerViewModel.updateAnimeProfile(animeProfile)
        }
    }

    private fun updateLike (animeProfile: AnimeProfile) {
        if (animeProfile.isFavorite) {
            binding.imvLikeProfile.setImageResource(R.drawable.ic_favorite_24)
            binding.imvLikeProfile.setImageDrawable(
                DreamerLayout.getBackgroundColor(
                    binding.imvLikeProfile.drawable,
                    R.color.pink
                )
            )
        } else {
            binding.imvLikeProfile.setImageResource(R.drawable.ic_favorite_border_24)
            binding.imvLikeProfile.setImageDrawable(
                DreamerLayout.getBackgroundColor(
                    binding.imvLikeProfile.drawable,
                    R.color.white
                )
            )
        }
    }

    override fun onPause() {
        castManager.updateChapterMetaData()
        super.onPause()
    }

    override fun onDestroy() {
        castManager.onDestroy()
        adManager?.onDestroy()
        adManager = null
        super.onDestroy()
    }
}