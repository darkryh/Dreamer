package com.ead.project.dreamer.ui.player.content

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.transform.CircleCropTransformation
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.DreamerApp
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.database.model.AnimeProfile
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.retrofit.model.discord.User
import com.ead.project.dreamer.databinding.FragmentPlayerContentBinding
import com.ead.project.dreamer.ui.player.content.adapters.ProfileRecyclerViewAdapter
import com.ead.project.dreamer.ui.player.PlayerViewModel
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.material.color.MaterialColors
import dagger.hilt.android.AndroidEntryPoint


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@AndroidEntryPoint
class PlayerContentFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private val playerViewModel : PlayerViewModel by viewModels()
    private lateinit var chapter : Chapter
    private lateinit var adapter : ProfileRecyclerViewAdapter
    private var objetList : MutableList<Any> = ArrayList()
    private var adList : MutableList<NativeAd> = ArrayList()
    private lateinit var adLoader : AdLoader
    private var suggestionsIsLoaded : Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            chapter = it.getParcelable(Constants.REQUESTED_CHAPTER)!!
        }
    }


    private var _binding: FragmentPlayerContentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerContentBinding.inflate(layoutInflater,container,false)
        settingLayouts()
        settingRcvViews()
        return binding.root
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        adList.clear()
    }

    private fun settingLayouts() {

        binding.ChapterProfile.load(chapter.chapterCover){
            transformations(CircleCropTransformation())
        }
        binding.txvTitle.text = getString(R.string.title_seeing,chapter.title)
        binding.txvCurrentChapter.text = getString(R.string.chapter_number,chapter.chapterNumber.toString())
    }

    private fun settingRcvViews() {
        val surfaceColor = MaterialColors.getColor(requireContext(), R.attr.colorSurface, Color.GRAY)
        binding.appBarLayout.setBackgroundColor(surfaceColor)
        if(!User.isVip())
            setupAd()
        binding.rcvSuggestions.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            this@PlayerContentFragment.adapter =
                ProfileRecyclerViewAdapter(activity as Context,true)
            adapter = this@PlayerContentFragment.adapter
            settingProfile()
        }
    }


    private fun settingProfile() {
        playerViewModel.getProfile(chapter.idProfile).observe(viewLifecycleOwner) {
            if (it != null) {
                settingSuggestions(it)
            }
        }
    }

    private var countSuggestion = 0
    private fun settingSuggestions(animeProfile: AnimeProfile) {
        playerViewModel
            .getProfilesListFrom(animeProfile.genres as MutableList<String>,animeProfile.rating,animeProfile.id)
            .observe(viewLifecycleOwner) {
                if (++countSuggestion == 1) {
                    Constants.setQuantityAdsPlayer(it.size)
                    if(!suggestionsIsLoaded)
                        objetList = it.toMutableList()

                    if (adList.isNotEmpty()) {
                        implementAds()
                    }
                    else {
                        adapter.submitList(it)
                    }
                }
            }
    }

    private fun implementAds() {
        if (adList.isEmpty() || objetList.isEmpty()) return

        val offset: Int = adapter.itemCount / adList.size + 1
        var index = 0
        if (objetList.first() !is NativeAd) {
            for (ad in adList) {
                objetList.add(index, ad)
                index += offset
            }
        }
        adapter.submitList(objetList)
        if (_binding != null) {
            binding.rcvSuggestions.smoothScrollToPosition(0)
        }
    }

    private fun setupAd() {
        try {
            DreamerApp.MOBILE_AD_INSTANCE.apply {
                adLoader = AdLoader.Builder(requireContext(),
                    getString(R.string.ad_unit_id_native_player))
                    .forNativeAd {
                        adList.add(it)
                        if (!adLoader.isLoading) {
                            commitAdvertisements()
                        }
                    }
                    .withAdListener(object : AdListener() {
                        override fun onAdFailedToLoad(p0: LoadAdError) {
                            if (!adLoader.isLoading) {
                                commitAdvertisements()
                            }
                        }
                    })
                    .build()

                adLoader.loadAd(AdRequest.Builder().build())
            }
        } catch (e : Exception) {
           e.printStackTrace()
        }
    }

    private  fun commitAdvertisements() {
        countSuggestion = 0
        suggestionsIsLoaded = true
        implementAds()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        for (ad in adList) {
            ad.destroy()
        }
        super.onDestroy()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PlayerContentFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PlayerContentFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}