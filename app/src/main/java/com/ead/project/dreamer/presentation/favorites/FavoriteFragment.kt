package com.ead.project.dreamer.presentation.favorites

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.ead.project.dreamer.databinding.FragmentFavoritesBinding
import com.ead.project.dreamer.presentation.directory.filter.FilterFragment
import com.ead.project.dreamer.presentation.player.content.adapters.ProfileRecyclerViewAdapter
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FavoriteFragment : Fragment() {

    private var columnCount = 1

    private lateinit var viewModel: FavoriteViewModel

    private lateinit var adapter: ProfileRecyclerViewAdapter

    private var _binding : FragmentFavoritesBinding?=null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[FavoriteViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareLayout()
        binding.rcvFavorites.apply {
            layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }
            this@FavoriteFragment.adapter = ProfileRecyclerViewAdapter(
                context = activity as Context,
                isFavoriteSegment = true,
                isFromContent = false,
                preferenceUseCase = viewModel.preferenceUseCase
            )
            adapter = this@FavoriteFragment.adapter
            countProfile = 0
            setupDirectoryFavoriteList()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        countProfile = 0
        setupDirectoryFavoriteList()
    }

    private fun prepareLayout(){
        binding.favSorter.setOnClickListener {
            launchFilter()
        }
    }

    var countProfile = 0
    private fun setupDirectoryFavoriteList() {
        viewModel.getLikedDirectory().observe(viewLifecycleOwner) {

            if (++countProfile == 1) {
                if (it.isNotEmpty()) {
                    binding.txvIsEmpty.visibility = View.GONE
                    adapter.submitList(it)
                }
                else {
                    binding.txvIsEmpty.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun launchFilter() {
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val filterFragment = FilterFragment()
        filterFragment.adapter = adapter
        filterFragment.viewModel = viewModel
        filterFragment.show(fragmentManager, null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}