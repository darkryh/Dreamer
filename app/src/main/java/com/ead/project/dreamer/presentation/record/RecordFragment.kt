package com.ead.project.dreamer.presentation.record

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ead.commons.lib.views.setVisibility
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.utils.AdOrder
import com.ead.project.dreamer.databinding.FragmentRecordsBinding
import com.ead.project.dreamer.presentation.record.adapters.ChapterRecordRecyclerViewAdapter
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.NativeAd
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class RecordFragment : Fragment() {

    private lateinit var viewModel: RecordViewModel

    private lateinit var adapter : ChapterRecordRecyclerViewAdapter
    private var isSmallDevice = false

    private var _binding : FragmentRecordsBinding?=null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[RecordViewModel::class.java]
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecordsBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLayouts()
        observeUser()
    }

    private fun initLayouts() {
        prepareRecyclerView()
    }

    private fun prepareRecyclerView() {
        binding.list.apply {

            layoutManager = getLayoutManagerMode()
            this@RecordFragment.adapter = ChapterRecordRecyclerViewAdapter(activity as Context, isSmallDevice, viewModel.handleChapter, viewModel.getAnimeProfile)
            adapter = this@RecordFragment.adapter
            setupRecords()

        }
    }

    private fun getLayoutManagerMode() : RecyclerView.LayoutManager {
        isSmallDevice = true
        binding.list.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        return LinearLayoutManager(requireContext())
    }

    private fun setupRecords() {
        viewModel.getLiveDataRecords().observe(viewLifecycleOwner) {

            val isDataEmpty = it.isEmpty()

            binding.textIsEmpty.setVisibility(isDataEmpty)
            binding.list.setVisibility(!isDataEmpty)

            if (isDataEmpty) return@observe

            viewModel.setRecords(it)
            viewModel.configureRecords(it)
        }

        viewModel.records.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            if (AdOrder.isFirstItemAd(it)) {
                binding.list.smoothScrollToPosition(0)
            }
        }
    }

    private fun observeUser() {
        lifecycleScope.launch {
            viewModel.getAccount().collectLatest { eadAccount ->
                setupAd(eadAccount?.isVip?:false)
            }
        }
    }

    private fun setupAd(returnCase : Boolean) {
        if (returnCase) return
        val context = requireContext()

        val adLoader = AdLoader.Builder(context, context.getString(R.string.ad_unit_id_native_records))
            .forNativeAd { ad: NativeAd ->
                viewModel.setRecords(listOf(ad))
            }.build()

        val adRequest = AdRequest.Builder().build()
        adLoader.loadAd(adRequest)
    }
}