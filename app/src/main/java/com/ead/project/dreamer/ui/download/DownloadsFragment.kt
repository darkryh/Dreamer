package com.ead.project.dreamer.ui.download

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ead.project.dreamer.data.utils.ui.DownloadDesigner
import com.ead.project.dreamer.databinding.FragmentDownloadsBinding
import com.ead.project.dreamer.ui.download.adapter.DownloadRecyclerViewAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class DownloadsFragment : Fragment() {

    private var _binding : FragmentDownloadsBinding?=null
    private val binding get() = _binding!!
   @Inject lateinit var downloadDesigner : DownloadDesigner
   private lateinit var adapter : DownloadRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDownloadsBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.apply {
            layoutManager = LinearLayoutManager(context)
            this@DownloadsFragment.adapter = DownloadRecyclerViewAdapter(activity as Context)
            adapter = this@DownloadsFragment.adapter
            setupData()
        }
    }

    override fun onResume() {
        super.onResume()
        downloadDesigner.onResume()
    }

    override fun onPause() {
        super.onPause()
        downloadDesigner.onPause()
    }

    private fun setupData() {
        downloadDesigner.getChapters().observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}