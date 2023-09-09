package com.ead.project.dreamer.presentation.directory

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ead.project.dreamer.R
import com.ead.project.dreamer.databinding.FragmentDirectoryBinding
import com.ead.project.dreamer.presentation.home.adapters.ChapterHomeRecyclerViewAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DirectoryFragment : Fragment() {

    private lateinit var viewModel : DirectoryViewModel
    private lateinit var adapterHome : ChapterHomeRecyclerViewAdapter

    private var _binding : FragmentDirectoryBinding?=null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[DirectoryViewModel::class.java]
        viewModel.adManager.restore()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDirectoryBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            binding.popularSection.apply {
                title.text = requireContext().getText(R.string.popular_section_title)
                recyclerView.apply {
                    layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)
                    this@DirectoryFragment.adapterHome = ChapterHomeRecyclerViewAdapter(
                        activity as Context,
                        viewModel.handleChapter
                    )
                    adapter = this@DirectoryFragment.adapterHome
                }
            }
            binding.lastSeriesSection.apply {
                title.text = requireContext().getText(R.string.last_series_section_title)
                recyclerView.apply {
                    layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)
                    adapter = this@DirectoryFragment.adapterHome
                    setupHomeList()
                }
            }
        }
    }

    private fun setupHomeList () {
        viewModel.getChaptersHome().observe(viewLifecycleOwner) {
            adapterHome.submitList(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}