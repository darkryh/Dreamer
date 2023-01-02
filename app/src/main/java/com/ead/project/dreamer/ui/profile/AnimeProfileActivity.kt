package com.ead.project.dreamer.ui.profile

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.transform.RoundedCornersTransformation
import com.ead.commons.lib.lifecycle.activity.onBack
import com.ead.commons.lib.lifecycle.activity.onBackHandle
import com.ead.commons.lib.lifecycle.activity.showLongToast
import com.ead.commons.lib.lifecycle.activity.showShortToast
import com.ead.commons.lib.views.*
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.commons.Tools.Companion.hide
import com.ead.project.dreamer.data.commons.Tools.Companion.show
import com.ead.project.dreamer.data.database.model.AnimeProfile
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.models.discord.User
import com.ead.project.dreamer.data.utils.AdManager
import com.ead.project.dreamer.data.utils.DataStore
import com.ead.project.dreamer.data.utils.media.CastManager
import com.ead.project.dreamer.databinding.ActivityAnimeProfileBinding
import com.ead.project.dreamer.ui.profile.adapters.ChapterRecyclerViewAdapter
import com.ead.project.dreamer.ui.profile.adapters.GenreRecyclerViewAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AnimeProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnimeProfileBinding
    private val animeProfileViewModel : AnimeProfileViewModel by viewModels()

    var id = -1
    var reference = "reference"
    private var animeProfileSender : AnimeProfile ?= null
    private lateinit var adapterChapters : ChapterRecyclerViewAdapter
    private var countTargeting = 0

    private val minLettersCharacter = 230
    private var descriptionOverloaded = false
    private var wrappedDescription = false

    private lateinit var miniBannerViewHolderAd: MiniBannerViewHolderAd
    private var castManager: CastManager = CastManager()
    private var adManager : AdManager?=null

    private var isProfileNotWorking = false
    private var isChaptersNotWorking = false

    var count = 0
    private var countSections = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnimeProfileBinding.inflate(layoutInflater)
        onBackHandle { onBackPressedMode() }
        setContentView(binding.root)
        prepareVariables()
        prepareLayout()
        configureProfile()
        gettingProfile()
        setupAds()
    }

    private fun prepareVariables() {
        intent.let {
            id =  it.getIntExtra(Constants.PREFERENCE_ID_BASE,0)
            reference = it.getStringExtra(Constants.PREFERENCE_LINK)!!
        }
        castManager.setViewModel(animeProfileViewModel)
        castManager.initButtonFactory(this,binding.mediaRouteButton)
        adManager = AdManager(
            context = this,
            adId = getString(R.string.ad_unit_id_native_profile))
        adManager?.setUp(User.isNotVip())
    }

    override fun onStart() {
        super.onStart()
        if (DataStore.readBoolean(Constants.PROFILE_SENDER_VIDEO_PLAYER)) { finish() }
    }

    override fun onResume() {
        castManager.onResume()
        super.onResume()
    }

    override fun onPause() {
        castManager.onPause()
        super.onPause()
    }

    private fun prepareLayout() {
        binding.bannerShimmer.show()
        miniBannerViewHolderAd = MiniBannerViewHolderAd(binding.banner)
        if (User.isVip()) {
            binding.lnBanner.visibility = View.GONE
            binding.bannerShimmer.hide()
        }
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.navigationIcon =
            binding.toolbar.navigationIcon?.getMutated(this,R.color.white)
        binding.toolbar.setNavigationOnClickListener { onBack() }
        binding.imvFixer.setOnClickListener {
            it.isEnabled = false
           showLongToast(getString(R.string.warning_executing_manual_fixer))
            if (isProfileNotWorking) animeProfileViewModel.repairingProfiles()
            if (isChaptersNotWorking) animeProfileViewModel.repairingChapters()
            if (!isProfileNotWorking && !isChaptersNotWorking) showShortToast(getString(R.string.all_is_working))
        }
        binding.imvDownloads.addSelectableItemEffect()
        binding.imvDownloadSelected.addSelectableItemEffect()
        binding.imvDownloads.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.to_download))
                .setMessage(getString(R.string.message_to_download_all_series,animeProfileSender?.title.toString()))
                .setPositiveButton(getString(R.string.confirm)) { _: DialogInterface?, _: Int -> animeProfileViewModel.downloadAllChapters(id) }
                .setNegativeButton(R.string.cancel,null)
                .show()
        }
        binding.imvDownloadSelected.setOnClickListener { launchDownloads(adapterChapters.getSelectedList())}
        binding.imvDownloadManualSelected.setOnClickListener {
            showShortToast(getString(R.string.in_develop))
            adapterChapters.removeEditMode()
        }
        binding.imvChapterModeDownloaded.setOnClickListener {
            animeProfileViewModel.updateChapters(adapterChapters.getSelectedList().onEach {
                it.downloadState = Chapter.DOWNLOAD_STATUS_COMPLETED
            })
            adapterChapters.removeEditMode()
        }
        binding.imvChapterModeNotDownloaded.setOnClickListener {
            animeProfileViewModel.updateChapters(adapterChapters.getSelectedList().onEach {
                it.downloadState = Chapter.DOWNLOAD_STATUS_INITIALIZED
            })
            adapterChapters.removeEditMode()
        }
        adapterChapters = ChapterRecyclerViewAdapter(this,binding.editModeView)
        binding.rcvChapters.layoutManager = LinearLayoutManager(this)
        binding.rcvChapters.adapter = adapterChapters
    }

    private fun configureProfile() = animeProfileViewModel.configureProfileData(id,reference)

    private fun configureChapters() = animeProfileViewModel.configureChaptersData(id,reference)

    private fun gettingProfile() {
        animeProfileViewModel.getAnimeProfile(id).observe(this) { animeProfile ->
            if (animeProfile != null) {
                if (animeProfile.checkPolicies()) {
                    animeProfileSender = animeProfile
                    if (++count == 1) {
                        isProfileNotWorking = animeProfile.isNotWorking()
                        showFixer(isProfileNotWorking)
                        likeProfile(animeProfile)
                        loadProfile(animeProfile)
                        configureChapters()
                    }
                    updateLike(animeProfile)
                    if (animeProfile.size != 0) {
                        if (++countSections == 1) {
                            if (!needsSections(animeProfile.size)) gettingChapters()
                            else adaptingChaptersInSections(animeProfile.size)
                            binding.imvDownloads.setVisibility(true)
                        }
                    }
                }
                else {
                    showLongToast(getString(R.string.google_policies_message))
                    finish()
                }
            }
        }
    }

    private fun fixerUiUpdate() {
        Constants.isProfileFixerLaunched().observe(this@AnimeProfileActivity) {
            if (it) {
                animeProfileSender?.let { animeProfile->
                    loadProfile(animeProfile)
                    Constants.setProfileFixer(false)
                }
            }
        }
    }

    private fun needsSections(size: Int) = size >= 200

    private fun adaptingChaptersInSections(size: Int) {
        adapterChapters = ChapterRecyclerViewAdapter(this,binding.editModeView)
        binding.rcvChapters.adapter = adapterChapters

        var pairNumberList : MutableList<Pair<Int,Int>> = ArrayList()
        var layoutRanges : MutableList<String> = ArrayList()
        binding.txvChapterTitle.visibility = View.GONE
        binding.txInputChapters.visibility = View.VISIBLE
        val quantitySection = when {
            size <= 250 -> 4
            size <= 300 -> 6
            size <= 400 -> 8
            size <= 500 -> 10
            size <= 700 -> 12
            size <= 1000 -> 14
            else -> 16
        }
        for (i in 0 until quantitySection ) {
            var start = (size/quantitySection) * i
            var end = (size/quantitySection) * (i + 1)
            if (i > 0)
                start += 1
            if (i == quantitySection -1 )
                end = size

            pairNumberList.add(Pair(start,end))
            layoutRanges.add("${pairNumberList[i].second} - ${pairNumberList[i].first}")
        }
        pairNumberList = pairNumberList.asReversed()
        layoutRanges = layoutRanges.asReversed()
        val arrayAdapter = ArrayAdapter(this,R.layout.drop_down_item,layoutRanges)
        binding.actChapters.setAdapter(arrayAdapter)
        binding.actChapters.setOnItemClickListener { _, _, i, _ ->
            gettingChaptersBySections(pairNumberList[i].first,pairNumberList[i].second)
        }
    }

    private fun gettingChapters() {
        animeProfileViewModel.getChaptersFromProfile(id).observe(this) {
            preventChaptersWorking(it)
            adapterChapters.submitList(it)
        }
    }

    private fun gettingChaptersBySections(start: Int, end: Int) {
        animeProfileViewModel.getChaptersFromProfile(id,start,end).observe(this) {
            preventChaptersWorking(it)
            adapterChapters.submitList(it)
        }
    }

    private fun loadProfile(animeProfile: AnimeProfile) {
        binding.collapsingToolbar.title = animeProfile.title
        binding.txvStateProfile.text = getString(R.string.wordDecorate,animeProfile.state)
        binding.txvDescriptionContent.addSelectableItemEffect()

        if (animeProfile.description.length > minLettersCharacter) {
            descriptionOverloaded = true
            wrappedDescription(animeProfile,false)
        } else {
            binding.txvDescriptionContent.text = animeProfile.description
        }
        binding.txvYear.text = getString(R.string.wordDecorate,animeProfile.date)
        binding.txvRating.text = getString(R.string.ratingLayout,animeProfile.rating.toString())
        binding.imvProfileBase.load(animeProfile.profilePhoto){
            transformations(RoundedCornersTransformation(30f))
        }
        binding.imvCoverProfile.load(animeProfile.coverPhoto)
        settingGenres(animeProfile)

        binding.txvDescriptionContent.setOnClickListener {
            if (descriptionOverloaded) {
                val params = binding.txvDescriptionContent.layoutParams as ViewGroup.LayoutParams
                if (wrappedDescription) {
                    wrappedDescription(animeProfile,false)
                    params.height = resources.getDimensionPixelSize(R.dimen.dimen_80dp)
                }
                else {
                    wrappedDescription(animeProfile,true)
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT
                }
                binding.txvDescriptionContent.layoutParams = params
                wrappedDescription = !wrappedDescription
            }
        }
    }

    private fun wrappedDescription(animeProfile: AnimeProfile, wrapContent : Boolean) {
        val text : String = animeProfile.description.substring(0,minLettersCharacter)
        binding.txvDescriptionContent.justifyInterWord()
        if(!wrapContent) {
            binding.txvDescriptionContent
                .text = HtmlCompat.fromHtml(
                "$text<font color='Cyan'> <u>Mostrar más...</u></font>",
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        }
        else {
            binding.txvDescriptionContent
                .text = HtmlCompat.fromHtml(
                "${animeProfile.description}<font color='Red'> <u>Mostrar menos...</u></font>",
                HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
    }

    private fun settingGenres (it : AnimeProfile) {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
        binding.rcvGenres.layoutManager = layoutManager
        binding.rcvGenres.adapter = GenreRecyclerViewAdapter(it.genres,this)
    }

    private fun likeProfile (animeProfile: AnimeProfile) {
        binding.imvLikeProfile.setOnClickListener {
            animeProfile.isFavorite = !animeProfile.isFavorite
            animeProfileViewModel.updateAnimeProfile(animeProfile)
        }
    }

    private fun updateLike (animeProfile: AnimeProfile) {
        if (animeProfile.isFavorite)
            binding.imvLikeProfile.setResourceImageAndColor(R.drawable.ic_favorite_24,R.color.pink)
        else
            binding.imvLikeProfile.setResourceImageAndColor(R.drawable.ic_favorite_border_24,R.color.white)
    }

    private fun launchDownloads(downloadList : MutableList<Chapter>) {
        if (downloadList.isNotEmpty()) {
            if (downloadList.size == 1) animeProfileViewModel.downloadFromChapter(downloadList.first())
            else animeProfileViewModel.downloadFromChapters(downloadList)
            adapterChapters.removeEditMode()
        }
        else showLongToast(getString(R.string.error_download))
    }

    private fun preventChaptersWorking(list: List<Chapter>) {
        if (list.isEmpty()) return
        isChaptersNotWorking = ++countTargeting == 1 && list.first().isNotWorking()
        showFixer(isChaptersNotWorking)
    }

    private fun showFixer(isNeeded: Boolean) {
        if (isNeeded) {
            binding.imvFixer.visibility = View.VISIBLE
            binding.imvFixer.margin(dpInRight = 8f)
            fixerUiUpdate()
        }
    }

    private fun setupAds() {
        adManager?.getAd()?.observe(this) {
            binding.bannerShimmer.hide()
            binding.lnBanner.background = ContextCompat.getDrawable(this@AnimeProfileActivity
                , R.drawable.background_horizontal_border)
            binding.banner.root.layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
            miniBannerViewHolderAd.bindTo(it)
            binding.banner.root.visibility = View.VISIBLE
        }
    }

    private fun onBackPressedMode() {
        if (adapterChapters.isEditMode) adapterChapters.removeEditMode()
        else finish()
    }

    override fun onStop() {
        DataStore.writeBooleanAsync(Constants.WORK_PREFERENCE_CLICKED_PROFILE,false)
        DataStore.writeBooleanAsync(Constants.WORK_PREFERENCE_CLICKED_PROFILE_SUGGESTION,false)
        super.onStop()
    }

    override fun onDestroy() {
        adManager?.onDestroy()
        castManager.onDestroy()
        super.onDestroy()
    }
}