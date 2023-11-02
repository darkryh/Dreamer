package com.ead.project.dreamer.presentation.player.cast

import android.content.Intent
import android.os.Bundle
import android.text.format.DateUtils
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import coil.load
import coil.transform.CircleCropTransformation
import com.ead.commons.lib.lifecycle.activity.onBack
import com.ead.commons.lib.resource.getDrawableFromIdNotNull
import com.ead.commons.lib.views.addSelectableItemEffect
import com.ead.commons.lib.views.setResourceImageAndColor
import com.ead.commons.lib.views.setVisibility
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.data.util.system.hide
import com.ead.project.dreamer.app.data.util.system.show
import com.ead.project.dreamer.data.database.model.AnimeProfile
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.databinding.ActivityExpandedControllerCastBinding
import com.ead.project.dreamer.presentation.player.PlayerViewModel
import com.ead.project.dreamer.presentation.player.cast.adapters.CastingViewPagerAdapter
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.cast.framework.media.widget.ExpandedControllerActivity
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ExpandedControlsActivity  : ExpandedControllerActivity() {

    private val viewModel : PlayerViewModel by viewModels()

    private var chapter : Chapter?= null
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
        observeUser()
    }

    private fun settingVariables() {
        chapter = viewModel.castManager.getChapter()?:return
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
            textTitle.text = chapter?.title
            textChapterNumber.text = getString(R.string.chapter_number,chapter?.number)
        }
    }

    private fun setFunctionality() {
        binding.apply {

            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayShowTitleEnabled(false)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            shimmerBanner.show()
            toolbar.navigationIcon = getDrawableFromIdNotNull(R.drawable.ic_down_back_24)
            toolbar.setNavigationOnClickListener { onBack() }

            buttonForward.addSelectableItemEffect()
            buttonRewind.addSelectableItemEffect()
            buttonPlay.addSelectableItemEffect()

            castViewHolderAd = CastViewHolderAd(banner)

        }
    }

    private fun setCastingFunctionality() {
        binding.apply {
            uiMediaController.bindSeekBar(seekBar)
            uiMediaController.bindTextViewToStreamPosition(textCurrentProgress,true)
            uiMediaController.bindTextViewToStreamDuration(textTotalProgress)
            uiMediaController.bindImageViewToPlayPauseToggle(
                buttonPlay,
                getDrawableFromIdNotNull(R.drawable.ic_play),
                getDrawableFromIdNotNull(R.drawable.ic_pause),
                getDrawableFromIdNotNull(com.google.android.gms.cast.framework.R.drawable.cast_ic_expanded_controller_stop),
                progressBar,
                true)
            uiMediaController.bindViewToRewind(buttonRewind,30 * DateUtils.SECOND_IN_MILLIS)
            uiMediaController.bindViewToForward(buttonForward,30 * DateUtils.SECOND_IN_MILLIS)
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

    private fun observeUser() {
        lifecycleScope.launch {
            viewModel.getAccount().collectLatest { eadAccount ->
                if (eadAccount != null) {
                    if (eadAccount.profileImage != null) {
                        binding.imageProfile.load(eadAccount.profileImage) {
                            transformations(CircleCropTransformation())
                        }
                    }
                    if (eadAccount.isVip) {
                        stateAd(false)
                        return@collectLatest
                    }
                }
                setupAd()
            }
        }
    }

    private fun setupAd() {
        val context = this@ExpandedControlsActivity

        val adLoader = AdLoader.Builder(context, context.getString(R.string.ad_unit_id_native_casting))
            .forNativeAd { ad: NativeAd ->
                castViewHolderAd.bindTo(ad)
                stateAd(true)
            }.build()

        val adRequest = AdRequest.Builder().build()
        adLoader.loadAd(adRequest)
    }

    private fun stateAd(showAd : Boolean) {
        binding.banner.root.layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
        binding.shimmerBanner.hide()
        binding.banner.root.setVisibility(showAd)
    }

    private fun likeProfile(animeProfile: AnimeProfile) {
        binding.imageLikeProfile.setOnClickListener {
            viewModel.updateAnimeProfile(animeProfile.copy(
                isFavorite = !animeProfile.isFavorite
            ))
        }
    }

    private fun updateLike(animeProfile: AnimeProfile) {
        if (animeProfile.isFavorite)
            binding.imageLikeProfile.setResourceImageAndColor(R.drawable.ic_favorite_24, R.color.pink)
         else
             binding.imageLikeProfile.setResourceImageAndColor(R.drawable.ic_favorite_border_24, R.color.white)
    }

}