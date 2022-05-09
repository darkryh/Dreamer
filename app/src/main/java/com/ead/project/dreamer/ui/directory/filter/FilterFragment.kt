package com.ead.project.dreamer.ui.directory.filter

import android.os.Bundle
import androidx.fragment.app.Fragment
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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FilterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class FilterFragment : BottomSheetDialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var adapter: ProfileRecyclerViewAdapter
    lateinit var favoriteViewModel: FavoriteViewModel
    private var state : String?=null
    private var type : String?=null
    private var genre : String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private var _binding : BottomModalFilterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = BottomModalFilterBinding.inflate(inflater,container,false)
        prepareLayout()
        selecting()
        return binding.root
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
        binding.actState.setOnItemClickListener { _, view, _, _ ->
            state = (view as TextView).text as String?
        }

        binding.actType.setOnItemClickListener { _, view, _, _ ->
            type = (view as TextView).text as String?
        }

        binding.actGenre.setOnItemClickListener { _, view, _, _ ->
            genre = (view as TextView).text as String?
        }
    }

    private fun filtering() {
        favoriteViewModel.getFilterDirectory(state,genre).observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FilterFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FilterFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}