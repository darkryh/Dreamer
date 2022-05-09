package com.ead.project.dreamer.ui.record

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.DreamerApp
import com.ead.project.dreamer.data.commons.Tools
import com.ead.project.dreamer.data.retrofit.model.discord.User
import com.ead.project.dreamer.databinding.FragmentRecordsBinding
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import dagger.hilt.android.AndroidEntryPoint

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RecordFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class RecordFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var recordViewModel: RecordViewModel
    private lateinit var adapter : ChapterRecordRecyclerViewAdapter
    private lateinit var adLoader : AdLoader
    private var nativeAd: NativeAd?= null

    private var _binding : FragmentRecordsBinding?=null
    private val binding get() = _binding!!
    private lateinit var bannerViewHolderAd: BannerViewHolderAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recordViewModel = ViewModelProvider(this)[RecordViewModel::class.java]
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
        _binding = FragmentRecordsBinding.inflate(layoutInflater,container,false)
        val columnCount = Tools.getAutomaticSizeReference(180)
        prepareLayout()
        binding.list.apply{
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 1f
            )
            layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }
            this@RecordFragment.adapter = ChapterRecordRecyclerViewAdapter(activity as Context)
            adapter = this@RecordFragment.adapter
            setupRecords()
        }
        return binding.root
    }

    private fun prepareLayout() {
        bannerViewHolderAd = BannerViewHolderAd(binding.banner)
        if (!User.isVip())
            setupAd()
        binding.banner.root.layoutParams.height = 0
    }

    private fun setupRecords() {
        recordViewModel.getLiveDataRecords().observe(viewLifecycleOwner) {
            adapter.submitList(it)

            if (recordViewModel.checkIfUpgradeExist(it))
                recordViewModel.updateContinuation(it)
        }
    }

    private fun setupAd() {
        try {
            DreamerApp.MOBILE_AD_INSTANCE.apply {
                adLoader = AdLoader.Builder(requireContext(),
                    getString(R.string.ad_unit_id_native_records))
                    .forNativeAd {
                        if (!adLoader.isLoading) {
                            binding.banner.root.layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
                            nativeAd = it
                            bannerViewHolderAd.bindTo(nativeAd!!)
                        }
                    }
                    .withAdListener(object : AdListener() {
                        override fun onAdFailedToLoad(p0: LoadAdError) {

                        }
                    })
                    .build()

                adLoader.loadAd(AdRequest.Builder().build())
            }
        } catch (e : Exception) {
            e.printStackTrace()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        nativeAd?.destroy()
    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RecordFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}