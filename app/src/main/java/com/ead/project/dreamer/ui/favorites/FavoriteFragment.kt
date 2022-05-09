package com.ead.project.dreamer.ui.favorites

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
import com.ead.project.dreamer.ui.directory.filter.FilterFragment
import com.ead.project.dreamer.ui.player.content.adapters.ProfileRecyclerViewAdapter
import dagger.hilt.android.AndroidEntryPoint

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@AndroidEntryPoint
class FavoriteFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var columnCount = 1
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var adapter: ProfileRecyclerViewAdapter
    private var _binding : FragmentFavoritesBinding?=null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        favoriteViewModel = ViewModelProvider(this)[FavoriteViewModel::class.java]
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentFavoritesBinding.inflate(layoutInflater, container, false)
        prepareLayout()
        binding.rcvFavorites.apply {
            layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }
            this@FavoriteFragment.adapter = ProfileRecyclerViewAdapter(activity as Context)
            adapter = this@FavoriteFragment.adapter
            setupDirectoryFavoriteList()
        }
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        setupDirectoryFavoriteList()
    }



    private fun prepareLayout(){
        binding.imvSorter.setOnClickListener {
            launchFilter()
        }
    }

    var countProfile = 0
    private fun setupDirectoryFavoriteList() {
        countProfile = 0
        favoriteViewModel.getLikedDirectory().observe(viewLifecycleOwner) {
            if (++countProfile == 1)
                adapter.submitList(it)
        }
    }

    private fun launchFilter() {
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val filterFragment = FilterFragment()
        filterFragment.adapter = adapter
        filterFragment.favoriteViewModel = favoriteViewModel
        filterFragment.show(fragmentManager, null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FavoriteFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FavoriteFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}