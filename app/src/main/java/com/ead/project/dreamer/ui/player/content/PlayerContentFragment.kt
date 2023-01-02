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
import com.ead.commons.lib.lifecycle.parcelable
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.database.model.AnimeProfile
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.models.discord.User
import com.ead.project.dreamer.data.utils.AdManager
import com.ead.project.dreamer.databinding.FragmentPlayerContentBinding
import com.ead.project.dreamer.ui.player.content.adapters.ProfileRecyclerViewAdapter
import com.ead.project.dreamer.ui.player.PlayerViewModel
import com.google.android.material.color.MaterialColors
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayerContentFragment : Fragment() {

    private val playerViewModel : PlayerViewModel by viewModels()
    private lateinit var chapter : Chapter
    private lateinit var adapter : ProfileRecyclerViewAdapter
    private var objetList : MutableList<Any> = ArrayList()
    private var countSuggestion = 0

    private var adManager : AdManager?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            chapter = it.parcelable(Constants.REQUESTED_CHAPTER)!!
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
        adManager?.onViewStateRestored()
    }

    private fun settingLayouts() {
        binding.ChapterProfile.load(chapter.cover){
            transformations(CircleCropTransformation())
        }
        binding.txvTitle.text = getString(R.string.title_seeing,chapter.title)
        binding.txvCurrentChapter.text = getString(R.string.chapter_number,chapter.number.toString())
    }

    private fun settingRcvViews() {
        val surfaceColor = MaterialColors.getColor(requireContext(), R.attr.colorSurface, Color.GRAY)
        binding.appBarLayout.setBackgroundColor(surfaceColor)
        binding.rcvSuggestions.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            this@PlayerContentFragment.adapter =
                ProfileRecyclerViewAdapter(activity as Context,true)
            adManager =  AdManager(
                context =  requireContext(),
                adId =  getString(R.string.ad_unit_id_native_player),
                anyList = objetList,
                adapter = this@PlayerContentFragment.adapter,
                quantityAds = 3)
            adapter = this@PlayerContentFragment.adapter
            adManager?.setUp(User.isNotVip())
            settingProfile()
        }
    }

    private fun settingProfile() {
        playerViewModel.getProfileData(chapter.idProfile).observe(viewLifecycleOwner) {
            if (it != null) settingSuggestions(it)
        }
    }

    private fun settingSuggestions(animeProfile: AnimeProfile) {
        playerViewModel.getProfilesListFrom(animeProfile)
            .observe(viewLifecycleOwner) {
                if (++countSuggestion == 1) {
                    Constants.setQuantityAdsPlayer(it.size)
                        adManager?.setAnyList(it)

                    adManager?.submitList(it)
                }
            }
        adManager?.getAds()?.observe(viewLifecycleOwner) { adManager?.submitList(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        adManager?.onDestroy()
        adManager = null
        super.onDestroy()
    }
}