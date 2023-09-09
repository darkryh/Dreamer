package com.ead.project.dreamer.presentation.download

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ead.commons.lib.views.setVisibility
import com.ead.project.dreamer.data.models.Download
import com.ead.project.dreamer.databinding.FragmentDownloadsBinding
import com.ead.project.dreamer.presentation.download.adapter.DownloadRecyclerViewAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class DownloadsFragment : Fragment() {

    private lateinit var viewModel: DownloadsViewModel

    private lateinit var adapter : DownloadRecyclerViewAdapter

    private var _binding : FragmentDownloadsBinding?=null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[DownloadsViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDownloadsBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rcvDownloads.apply {
            layoutManager = LinearLayoutManager(context)
            this@DownloadsFragment.adapter = DownloadRecyclerViewAdapter(
                activity as Context,
                viewModel.chapterUseCase,
                viewModel.handleChapter
            )
            adapter = this@DownloadsFragment.adapter
            getDownloads()
        }
    }

    private fun getDownloads() {
        lifecycleScope.launch {
            viewModel.downloadStore.downloads.collectLatest {
                binding.txvIsEmpty.setVisibility(it.isEmpty())
                if (it.isEmpty()) return@collectLatest

                adapter.submitList(
                    it.sortedBy {
                            download: Download -> download.title
                    }
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}