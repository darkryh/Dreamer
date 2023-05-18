package com.ead.project.dreamer.presentation.player.content

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
import com.ead.project.dreamer.data.database.model.AnimeProfile
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.models.discord.DiscordUser
import com.ead.project.dreamer.databinding.FragmentPlayerContentBinding
import com.ead.project.dreamer.presentation.player.content.adapters.ProfileRecyclerViewAdapter
import com.ead.project.dreamer.presentation.player.PlayerViewModel
import com.google.android.material.color.MaterialColors
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayerContentFragment : Fragment() {

    private val viewModel : PlayerViewModel by viewModels()
    private lateinit var chapter : Chapter
    private lateinit var adapter : ProfileRecyclerViewAdapter

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
        return binding.root
    }

    private fun initLayouts() {
        binding.ChapterProfile.load(chapter.cover){
            transformations(CircleCropTransformation())
        }
        binding.txvTitle.text = getString(R.string.title_seeing,chapter.title)
        binding.txvCurrentChapter.text = getString(R.string.chapter_number,chapter.number.toString())
    }

    private fun initRecyclerViews() {
        val surfaceColor = MaterialColors.getColor(requireContext(), R.attr.colorSurface, Color.GRAY)
        binding.appBarLayout.setBackgroundColor(surfaceColor)
        binding.rcvSuggestions.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            this@PlayerContentFragment.adapter =
                ProfileRecyclerViewAdapter(activity as Context,
                    isFromContent = true,
                    isFavoriteSegment = false,
                    preferenceUseCase = viewModel.preferenceUseCase
                )
            adapter = this@PlayerContentFragment.adapter
            viewModel.adManager.setUp(
                returnCase = DiscordUser.isVip(),
                adId = getString(R.string.ad_unit_id_native_player),
                adapter,
                quantityAds = 3
            )
            settingProfile()
        }
    }

    private fun settingProfile() {
        viewModel.getProfileData(chapter.idProfile).observe(viewLifecycleOwner) {
            if (it != null) settingSuggestions(it)
        }
    }

    private fun settingSuggestions(animeProfile: AnimeProfile) {
        viewModel.getProfilesListFrom(animeProfile)
            .observe(viewLifecycleOwner) {
                if (++countSuggestion == 1) {
                    viewModel.adManager.setItems(it)
                    viewModel.adManager.submitList(it)
                }
            }
        viewModel.adManager.getItems().observe(viewLifecycleOwner) { viewModel.adManager.submitList(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        viewModel.adManager.onDestroy()
        super.onDestroy()
    }
}