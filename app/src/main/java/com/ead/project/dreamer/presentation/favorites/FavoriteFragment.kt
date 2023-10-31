package com.ead.project.dreamer.presentation.favorites

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.ead.commons.lib.views.setVisibility
import com.ead.project.dreamer.data.database.model.AnimeProfile
import com.ead.project.dreamer.databinding.FragmentFavoritesBinding
import com.ead.project.dreamer.presentation.directory.filter.FilterFragment
import com.ead.project.dreamer.presentation.player.content.adapters.ProfileRecyclerViewAdapter
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FavoriteFragment : Fragment() {

    private var columnCount = 1

    private lateinit var viewModel: FavoriteViewModel
    private lateinit var adapter: ProfileRecyclerViewAdapter

    private var likedAnimes : List<AnimeProfile> = listOf()
    var filteredAnimes : List<AnimeProfile> = listOf()

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
        binding.recyclerViewFavorites.apply {
            layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }
            this@FavoriteFragment.adapter = ProfileRecyclerViewAdapter(
                context = activity as Context,
                isFromContent = false,
                isFavoriteSegment = true
            )
            adapter = this@FavoriteFragment.adapter
            setupDirectoryFavoriteList()
        }
    }

    private fun prepareLayout(){
        binding.floatButtonSorter.setOnClickListener {

            FilterFragment.launch(
                context = activity as Context,
                adapter = adapter,
                favoriteFragment = this@FavoriteFragment,
                favoriteViewModel = viewModel
            )

        }
    }

    private fun setupDirectoryFavoriteList() {
        viewModel.getLikedDirectory().observe(viewLifecycleOwner) { likedAnimes ->
            binding.textIsEmpty.setVisibility(likedAnimes.isEmpty())
            if (likedAnimes.isEmpty() || this.likedAnimes == likedAnimes) return@observe

            this.likedAnimes
            
            adapter.submitList(likedAnimes)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}