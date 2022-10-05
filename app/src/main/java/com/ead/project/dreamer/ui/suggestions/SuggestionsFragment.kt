package com.ead.project.dreamer.ui.suggestions

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ead.project.dreamer.R
import com.ead.project.dreamer.databinding.FragmentSuggestionsBinding
import com.ead.project.dreamer.ui.player.content.adapters.ProfileRecyclerViewAdapter
import com.ead.project.dreamer.ui.suggestions.adapters.ProfileMiniRecyclerViewAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SuggestionsFragment : Fragment() {

    private var _binding : FragmentSuggestionsBinding?=null
    private lateinit var mostViewedAdapter : ProfileMiniRecyclerViewAdapter
    private lateinit var suggestionsAdapter: ProfileRecyclerViewAdapter
    private lateinit var suggestionsViewModel: SuggestionsViewModel
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        suggestionsViewModel = ViewModelProvider(this)[SuggestionsViewModel::class.java]
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
        binding.rcvMostViewedSeries.apply {
            layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
            this@SuggestionsFragment.mostViewedAdapter = ProfileMiniRecyclerViewAdapter(activity as Context)
            adapter = this@SuggestionsFragment.mostViewedAdapter
            setupMostViewedSeries()
        }
        binding.rcvSuggestions.apply {
            layoutManager =  LinearLayoutManager(context)
            this@SuggestionsFragment.suggestionsAdapter = ProfileRecyclerViewAdapter(activity as Context)
            adapter = this@SuggestionsFragment.suggestionsAdapter
            setupRecommendations()
        }
    }

    private fun setupMostViewedSeries() {
        suggestionsViewModel.getMostViewedSeries().observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                mostViewedAdapter.submitList(it)
                binding.txvMostViewed.visibility = View.VISIBLE
                binding.rcvMostViewedSeries.visibility = View.VISIBLE
                binding.txvIsEmpty.visibility = View.GONE
            }
            else {
                binding.txvMostViewed.visibility = View.GONE
                binding.rcvMostViewedSeries.visibility = View.GONE
                binding.txvIsEmpty.visibility = View.VISIBLE
                binding.txvSuggestions.text = getString(R.string.recommendations)
            }
        }
    }

    private fun setupRecommendations() {
        suggestionsViewModel.getRecommendations().observe(viewLifecycleOwner) {
            suggestionsAdapter.submitList(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}