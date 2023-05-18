package com.ead.project.dreamer.presentation.suggestions

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.ead.project.dreamer.R
import com.ead.project.dreamer.databinding.FragmentSuggestionsBinding
import com.ead.project.dreamer.presentation.player.content.adapters.ProfileRecyclerViewAdapter
import com.ead.project.dreamer.presentation.suggestions.adapters.ProfileMiniRecyclerViewAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SuggestionsFragment : Fragment() {

    private lateinit var viewModel: SuggestionsViewModel
    lateinit var viewPager2: ViewPager2

    private lateinit var mostViewedAdapter : ProfileMiniRecyclerViewAdapter
    private lateinit var suggestionsAdapter: ProfileRecyclerViewAdapter

    private var _binding : FragmentSuggestionsBinding?=null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[SuggestionsViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSuggestionsBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            recyclerViewMostViewedSeries.apply {
                layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
                this@SuggestionsFragment.mostViewedAdapter = ProfileMiniRecyclerViewAdapter(activity as Context)
                adapter = this@SuggestionsFragment.mostViewedAdapter
                setupMostViewedSeries()
            }

            recyclerViewSuggestions.apply {
                layoutManager =  LinearLayoutManager(context)
                this@SuggestionsFragment.suggestionsAdapter = ProfileRecyclerViewAdapter(activity as Context,
                    isFromContent = false,
                    isFavoriteSegment = false,
                    preferenceUseCase = viewModel.preferenceUseCase
                )
                adapter = this@SuggestionsFragment.suggestionsAdapter
                setupRecommendations()
            }

        }
    }

    private fun setupMostViewedSeries() {
        viewModel.getMostViewedSeries().observe(viewLifecycleOwner) {
            binding.apply {

                if (it.isNotEmpty()) {
                    mostViewedAdapter.submitList(it)
                    txvMostViewed.visibility = View.VISIBLE
                    recyclerViewMostViewedSeries.visibility = View.VISIBLE
                    txvIsEmpty.visibility = View.GONE
                }
                else {
                    txvMostViewed.visibility = View.GONE
                    recyclerViewMostViewedSeries.visibility = View.GONE
                    txvIsEmpty.visibility = View.VISIBLE
                    txvSuggestions.text = getString(R.string.recommendations)
                }

            }
        }
    }

    private fun setupRecommendations() {
        viewModel.getRecommendations().observe(viewLifecycleOwner) {
            suggestionsAdapter.submitList(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}