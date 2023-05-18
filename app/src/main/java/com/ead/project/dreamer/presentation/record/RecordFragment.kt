package com.ead.project.dreamer.presentation.record

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ead.commons.lib.metrics.getAvailableWidthReference
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.models.discord.DiscordUser
import com.ead.project.dreamer.databinding.FragmentRecordsBinding
import com.ead.project.dreamer.presentation.record.adapters.BannerViewHolderAd
import com.ead.project.dreamer.presentation.record.adapters.ChapterRecordRecyclerViewAdapter
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RecordFragment : Fragment() {

    private lateinit var viewModel: RecordViewModel

    private lateinit var adapter : ChapterRecordRecyclerViewAdapter
    private var isSmallDevice = false

    private var _binding : FragmentRecordsBinding?=null
    private val binding get() = _binding!!
    private lateinit var bannerViewHolderAd: BannerViewHolderAd

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
        initAdSettings()
        initLayouts()
        setupAds()
    }

    private fun initLayouts() {
        bannerViewHolderAd = BannerViewHolderAd(binding.banner)
        binding.banner.root.visibility = View.GONE
        prepareRecyclerView()
    }

    private fun initAdSettings() {
        viewModel.adManager.setUp(
            returnCase = DiscordUser.isVip(),
            adId = getString(R.string.ad_unit_id_native_records)
        )
    }

    private fun prepareRecyclerView() {
        binding.list.apply {

            layoutManager = getLayoutManagerMode()
            this@RecordFragment.adapter = ChapterRecordRecyclerViewAdapter(activity as Context, isSmallDevice, viewModel.handleChapter, viewModel.launchDownload)
            adapter = this@RecordFragment.adapter
            setupRecords()

        }
    }

    private fun getLayoutManagerMode() : RecyclerView.LayoutManager =
        when(getAvailableWidthReference(380)) {
            0 -> {
                isSmallDevice = true
                binding.list.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                LinearLayoutManager(requireContext())
            }
            else -> GridLayoutManager(requireContext(),getAvailableWidthReference(180))
        }

    private fun setupRecords() {
        viewModel.getLiveDataRecords().observe(viewLifecycleOwner) {

            if (it.isNotEmpty()) {
                adapter.submitList(it)
            }
            else {
                binding.txvIsEmpty.visibility = View.VISIBLE
                binding.list.visibility = View.GONE
            }
            if (viewModel.checkIfUpgradeExist(it)) {
                viewModel.updateContinuation(it)
            }

        }
    }

    private fun setupAds() {
        viewModel.adManager.getItem().observe(viewLifecycleOwner) {

            bannerViewHolderAd.bindTo(it?:return@observe)
            binding.banner.root.visibility = View.VISIBLE

        }
    }

    override fun onDestroy() {
        viewModel.adManager.onDestroy()
        super.onDestroy()
    }
}