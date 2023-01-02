package com.ead.project.dreamer.ui.record

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ead.commons.lib.metrics.getAvailableWidthReference
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.models.discord.User
import com.ead.project.dreamer.data.utils.AdManager
import com.ead.project.dreamer.databinding.FragmentRecordsBinding
import com.ead.project.dreamer.ui.record.adapters.BannerViewHolderAd
import com.ead.project.dreamer.ui.record.adapters.ChapterRecordRecyclerViewAdapter
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RecordFragment : Fragment() {

    private lateinit var recordViewModel: RecordViewModel
    private lateinit var adapter : ChapterRecordRecyclerViewAdapter
    private var isLinear = false

    private var _binding : FragmentRecordsBinding?=null
    private val binding get() = _binding!!
    private lateinit var bannerViewHolderAd: BannerViewHolderAd
    private var adManager : AdManager?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recordViewModel = ViewModelProvider(this)[RecordViewModel::class.java]
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        adManager?.onViewStateRestored()
        super.onViewStateRestored(savedInstanceState)
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
        prepareAdSettings()
        prepareLayout()
        setupAds()
    }

    private fun prepareLayout() {
        bannerViewHolderAd = BannerViewHolderAd(binding.banner)
        binding.banner.root.visibility = View.GONE
        prepareRecyclerView()
    }

    private fun prepareAdSettings() {
        adManager = AdManager(
            context = requireContext(),
            adId = getString(R.string.ad_unit_id_native_records))
        adManager?.setUp(User.isNotVip())
    }

    private fun prepareRecyclerView() {
        binding.list.apply {
            layoutManager = getLayoutManagerMode()
            this@RecordFragment.adapter = ChapterRecordRecyclerViewAdapter(activity as Context,isLinear)
            adapter = this@RecordFragment.adapter
            setupRecords()
        }
    }

    private fun getLayoutManagerMode() : RecyclerView.LayoutManager =
        when(getAvailableWidthReference(380)) {
            0 -> {
                isLinear = true
                binding.list.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                LinearLayoutManager(requireContext())
            }
            else -> GridLayoutManager(requireContext(),getAvailableWidthReference(180))
        }

    private fun setupRecords() {
        recordViewModel.getLiveDataRecords().observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) adapter.submitList(it)
            else {
                binding.txvIsEmpty.visibility = View.VISIBLE
                binding.list.visibility = View.GONE
            }
            if (recordViewModel.checkIfUpgradeExist(it)) recordViewModel.updateContinuation(it)
        }
    }

    private fun setupAds() {
        adManager?.getAd()?.observe(viewLifecycleOwner) {
            binding.banner.root.visibility = View.VISIBLE
            bannerViewHolderAd.bindTo(it)
        }
    }

    override fun onDestroy() {
        adManager?.onDestroy()
        adManager = null
        super.onDestroy()
    }
}