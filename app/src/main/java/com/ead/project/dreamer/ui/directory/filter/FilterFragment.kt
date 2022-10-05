package com.ead.project.dreamer.ui.directory.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.ead.project.dreamer.R
import com.ead.project.dreamer.databinding.BottomModalFilterBinding
import com.ead.project.dreamer.ui.favorites.FavoriteViewModel
import com.ead.project.dreamer.ui.player.content.adapters.ProfileRecyclerViewAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FilterFragment : BottomSheetDialogFragment() {

    lateinit var adapter: ProfileRecyclerViewAdapter
    lateinit var favoriteViewModel: FavoriteViewModel
    private var state : String?=null
    private var type : String?=null
    private var genre : String?=null

    private var _binding : BottomModalFilterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomModalFilterBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareLayout()
        selecting()
    }

    private fun prepareLayout() {
        val states =  resources.getStringArray(R.array.state_list)
        val types = resources.getStringArray(R.array.types_list).sorted()
        val genres =  resources.getStringArray(R.array.genres_list).sorted()
        val statesAdapter  = ArrayAdapter(requireContext(),R.layout.drop_down_item,states)
        val typesAdapter  = ArrayAdapter(requireContext(),R.layout.drop_down_item,types)
        val genresAdapter  = ArrayAdapter(requireContext(),R.layout.drop_down_item,genres)
        binding.actState.setAdapter(statesAdapter)
        binding.actType.setAdapter(typesAdapter)
        binding.actGenre.setAdapter(genresAdapter)
        binding.btnFilter.setOnClickListener {
            filtering()
            dismiss()
        }
    }

    private fun selecting() {
        binding.actState.setOnItemClickListener { _, view, _, _ -> state = (view as TextView).text as String? }
        binding.actType.setOnItemClickListener { _, view, _, _ -> type = (view as TextView).text as String? }
        binding.actGenre.setOnItemClickListener { _, view, _, _ -> genre = (view as TextView).text as String? }
    }

    private fun filtering() {
        favoriteViewModel.getFilterDirectory(state,genre).observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }
}