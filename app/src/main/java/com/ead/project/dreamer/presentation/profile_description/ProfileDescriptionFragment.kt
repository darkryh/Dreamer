package com.ead.project.dreamer.presentation.profile_description

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ead.commons.lib.views.addSelectableItemEffect
import com.ead.commons.lib.views.justifyInterWord
import com.ead.commons.lib.views.margin
import com.ead.commons.lib.views.setVisibility
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.data.util.system.hide
import com.ead.project.dreamer.data.database.model.AnimeProfile
import com.ead.project.dreamer.databinding.FragmentProfileDescriptionBinding
import com.ead.project.dreamer.presentation.profile.AnimeProfileViewModel
import com.ead.project.dreamer.presentation.profile.MiniBannerViewHolderAd
import com.ead.project.dreamer.presentation.profile.adapters.GenreRecyclerViewAdapter
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.NativeAd
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileDescriptionFragment : Fragment() {

    private val viewModel : AnimeProfileViewModel by viewModels()
    private lateinit var miniBannerViewHolderAd: MiniBannerViewHolderAd

    private val minLettersCharacter = 240
    private var descriptionOverloaded = false
    private var wrappedDescription = false

    var profileId : Int = -1

    private var _binding : FragmentProfileDescriptionBinding?=null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileDescriptionBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    private var count = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        miniBannerViewHolderAd = MiniBannerViewHolderAd(binding.banner)

        binding.apply {

            recyclerViewGenres.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false)
            }
            textDescriptionContent.justifyInterWord()
            textDescriptionContent.addSelectableItemEffect()

        }

        viewModel.getAnimeProfile(profileId).observe(viewLifecycleOwner){ animeProfile ->

            if (animeProfile != null) {
                if (++count == 1) {
                    loadAnimeProfileDetails(animeProfile)
                }
            }

        }

        observeUser()
    }

    private fun loadAnimeProfileDetails(animeProfile: AnimeProfile) {
        binding.apply {

            recyclerViewGenres.adapter = GenreRecyclerViewAdapter(animeProfile.genres,requireContext())
            textState.text = animeProfile.state
            textDate.text = animeProfile.date
            textRating.text = animeProfile.rating.toString()
            ratingBar.rating = animeProfile.rating
            containerRating.setVisibility(animeProfile.rating != -1f)

            if (animeProfile.description.length > minLettersCharacter) {
                descriptionOverloaded = true
                wrappedDescription(animeProfile,false)
            } else {
                textDescriptionContent.text = animeProfile.description
            }

            textDescriptionContent.setOnClickListener{
                if (descriptionOverloaded) {
                    val params = textDescriptionContent.layoutParams as ViewGroup.LayoutParams
                    if (wrappedDescription) {
                        wrappedDescription(animeProfile,false)
                        params.height = resources.getDimensionPixelSize(R.dimen.dimen_115dp)
                    }
                    else {
                        wrappedDescription(animeProfile,true)
                        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    }
                    textDescriptionContent.layoutParams = params
                    wrappedDescription = !wrappedDescription
                }
            }

        }
    }

    private fun observeUser() {
        lifecycleScope.launch {
            viewModel.getAccount().collectLatest { eadAccount ->
                if (eadAccount?.isVip == true) {
                    stateAd(false,null)
                    return@collectLatest
                }
                setupAd()
            }
        }
    }

    private fun setupAd() {
        val context = requireContext()

        val adLoader = AdLoader.Builder(context, context.getString(R.string.ad_unit_id_native_profile))
            .forNativeAd { ad: NativeAd ->
                miniBannerViewHolderAd.bindTo(ad)
                stateAd(true,ad)
            }.build()

        val adRequest = AdRequest.Builder().build()
        adLoader.loadAd(adRequest)
    }

    private fun stateAd(showAd : Boolean,nativeAd: NativeAd?) {
        if (_binding == null) {
            nativeAd?.destroy()
            return
        }
        binding.banner.root.layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
        binding.shimmerBanner.hide()
        binding.banner.root.setVisibility(showAd)
        binding.containerRating.margin(dpInTop =  if (!showAd) 0f else return)
    }

    private fun wrappedDescription(animeProfile: AnimeProfile, wrapContent : Boolean) {
        val text : String = animeProfile.description.substring(0,minLettersCharacter)
        binding.apply {

            if(!wrapContent) {
                textDescriptionContent
                    .text = HtmlCompat.fromHtml(
                    "$text<font color='Cyan'> <u>Mostrar más...</u></font>",
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
            }
            else {
                textDescriptionContent
                    .text = HtmlCompat.fromHtml(
                    "${animeProfile.description}<font color='Red'> <u>Mostrar menos...</u></font>",
                    HtmlCompat.FROM_HTML_MODE_LEGACY)
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}