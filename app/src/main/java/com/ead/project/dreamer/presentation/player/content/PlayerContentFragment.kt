package com.ead.project.dreamer.presentation.player.content

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.transform.CircleCropTransformation
import com.ead.commons.lib.lifecycle.parcelable
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.data.discord.Discord
import com.ead.project.dreamer.data.database.model.AnimeProfile
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.databinding.FragmentPlayerContentBinding
import com.ead.project.dreamer.presentation.player.PlayerViewModel
import com.ead.project.dreamer.presentation.player.content.adapters.ProfileRecyclerViewAdapter
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.material.color.MaterialColors
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlayerContentFragment : Fragment() {

    private val viewModel : PlayerViewModel by viewModels()
    private lateinit var chapter : Chapter
    private lateinit var adapter : ProfileRecyclerViewAdapter

    private lateinit var adLoader: AdLoader

    private var countSuggestion = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            chapter = it.parcelable(Chapter.REQUESTED)?:return@let
        }
    }

    private var _binding: FragmentPlayerContentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerContentBinding.inflate(layoutInflater,container,false)
        initLayouts()
        initRecyclerViews()
        observeUser()
        return binding.root
    }

    private fun initLayouts() {
        binding.imageChapterProfile.load(chapter.cover){
            transformations(CircleCropTransformation())
        }

        binding.textTitle.text = getString(R.string.title_seeing,chapter.title)
        binding.textCurrentChapterNumber.text = getString(R.string.chapter_number,chapter.number)
    }

    private fun initRecyclerViews() {
        val surfaceColor = MaterialColors.getColor(requireContext(), com.google.android.material.R.attr.colorSurface, Color.GRAY)
        binding.appBarLayout.setBackgroundColor(surfaceColor)

        binding.recyclerViewSuggestions.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            this@PlayerContentFragment.adapter =
                ProfileRecyclerViewAdapter(
                    activity as Context,
                    isFromContent = true,
                    isFavoriteSegment = false
                )
            adapter = this@PlayerContentFragment.adapter
            settingProfile()
        }
    }

    private fun settingProfile() {
        viewModel.getProfileData(chapter.idProfile).observe(viewLifecycleOwner) {
            if (it != null) settingSuggestions(it)
        }
    }

    private fun settingSuggestions(animeProfile: AnimeProfile) {
        viewModel.getProfilesListFrom(animeProfile).observe(viewLifecycleOwner) {
            if (++countSuggestion == 1) {
                viewModel.setRecommendedProfiles(it)
            }
        }

        viewModel.recommendedProfiles.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    private fun observeUser() {
        lifecycleScope.launch {
            Discord.user.collectLatest { user ->
                setupAds(user?.isVip?:false)
            }
        }
    }
    private fun setupAds(returnCase : Boolean) {
        if (returnCase) return
        val context = requireContext()
        val adList = mutableListOf<NativeAd>()

        adLoader = AdLoader.Builder(context, context.getString(R.string.ad_unit_id_native_player))
            .forNativeAd { ad: NativeAd ->
                adList.add(ad)
                if (adLoader.isLoading) return@forNativeAd
                viewModel.setRecommendedProfiles(adList)
            }.build()

        val adRequest = AdRequest.Builder().build()
        adLoader.loadAds(adRequest,3)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        fun launch(
            context: Context,
            contentPlayer: FrameLayout,
            chapter: Chapter?
        ) {
            contentPlayer.removeAllViews()

            val fragmentManager = (context as FragmentActivity).supportFragmentManager
            val transaction = fragmentManager.beginTransaction()

            val playerContentFragment = PlayerContentFragment()
            playerContentFragment.apply {
                arguments = Bundle().apply {
                    putParcelable(Chapter.REQUESTED,chapter)
                }

                transaction.replace(
                    R.id.frame_content,
                    playerContentFragment
                ).commit()
            }
        }
    }
}