package com.ead.project.dreamer.ui.home

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.DreamerApp
import com.ead.project.dreamer.app.model.Publicity
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.retrofit.model.discord.User
import com.ead.project.dreamer.data.utils.ui.DreamerLayout
import com.ead.project.dreamer.data.utils.ui.ScrollTimer
import com.ead.project.dreamer.databinding.FragmentHomeBinding
import com.ead.project.dreamer.ui.home.adapters.ChapterHomeRecyclerViewAdapter
import com.ead.project.dreamer.ui.home.adapters.ProfileBannerRecyclerViewAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var columnCount = 1
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var adapterHome : ChapterHomeRecyclerViewAdapter
    private lateinit var adapterProfile : ProfileBannerRecyclerViewAdapter

    private var objetList : MutableList<Any> = ArrayList()
    private var adList : MutableList<NativeAd> = ArrayList()
    private var objectProfileList : MutableList<Any> = ArrayList()
    private var publicity : Publicity?= null
    private var directoryChecked = false

    private lateinit var adLoader : AdLoader
    private val snapHelper = LinearSnapHelper()
    private val timer = Timer()
    private var timerAdv : Timer ?= null
    private lateinit var layoutManager : LinearLayoutManager
    private var fTimer = true

    private var _binding : FragmentHomeBinding?=null
    private val binding get() = _binding!!
    private var count = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        fTimer = false
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        fTimer = true
        adList.clear()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater,container,false)
        count = 0

        binding.rcvLiveRecommendations.apply {
            this@HomeFragment.adapterProfile =
                ProfileBannerRecyclerViewAdapter(activity as Context)
            this@HomeFragment.layoutManager =
                LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
            layoutManager = this@HomeFragment.layoutManager
            adapter = this@HomeFragment.adapterProfile
            setHasFixedSize(true)
            snapHelper.attachToRecyclerView(this)
            setupRecommendations()
        }

        binding.swipeRefresh.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                setColorSchemeColors(resources
                    .getColor(R.color.blackPrimary,requireContext().theme))

            setOnRefreshListener {
                isRefreshing = true
                refreshData()
            }
        }
        prepareSettings()
        return binding.root
    }

    private fun prepareSettings() {
        prepareHomeContent()
        if (!User.isVip()) {
            setupAds()
        }
        if (Constants.isCustomizedCommunicator()) {
            setupCustomizedAdsApp()
        }
    }

    private fun prepareHomeContent() {
        binding.shimmerClassic.startShimmer()
        setupDefaultHome()
        if (!Constants.isDirectorySynchronized()) syncState()
    }

    private fun setupDefaultHome() {
        binding.rcvReleases.apply {
            layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }
            this@HomeFragment.adapterHome = ChapterHomeRecyclerViewAdapter(activity as Context)
            adapter = this@HomeFragment.adapterHome
            setupHomeList()
        }
    }

    private fun syncState() {
        lifecycleScope.launch (Dispatchers.Main) {
            homeViewModel.directoryState().collect {
                activity?.runOnUiThread {
                    if (!directoryChecked)
                        if (it) {
                            DreamerLayout.showSnackbar(
                                view = binding.root,
                                text = getString(R.string.successfully_sync),
                                color = R.color.green
                            )
                            directoryChecked = true
                        }
                        else {
                            if (timerAdv == null) {
                                DreamerLayout.showSnackbar(
                                    view = binding.root,
                                    text = getString(R.string.requesting_data),
                                    color = R.color.red,
                                    length = Snackbar.LENGTH_INDEFINITE
                                )
                                timerAdv = Timer()
                                timerAdv?.schedule(object : TimerTask() {
                                    override fun run() {
                                        showAdvices()
                                    }
                                }, 5000, 13000)
                            }
                        }
                }
            }
        }
    }
    private var countAdv = 0
    private fun showAdvices() {
        if (!directoryChecked && _binding != null)
            when(++countAdv ) {
                1 -> DreamerLayout.showSnackbar(view = binding.root, text = getString(R.string.requesting_data_adv1),
                        color = R.color.red, length = Snackbar.LENGTH_INDEFINITE)
                2 -> DreamerLayout.showSnackbar(view = binding.root, text = getString(R.string.requesting_data_adv2),
                        color = R.color.red, length = Snackbar.LENGTH_INDEFINITE)
                3 -> {
                    DreamerLayout.showSnackbar(view = binding.root, text = getString(R.string.requesting_data_adv3),
                        color = R.color.red, length = Snackbar.LENGTH_INDEFINITE)
                    countAdv = 0
                }
            }
    }

    private fun setupHomeList () {
        homeViewModel.getChaptersHome().observe(viewLifecycleOwner) {
            binding.shimmerClassic.visibility = View.GONE
            binding.shimmerClassic.stopShimmer()
            objetList = it.toMutableList()
            if (adList.isNotEmpty()) {
                implementAds()
            }
            else {
                adapterHome.submitList(it)
            }
            binding.swipeRefresh.isRefreshing = false
        }
    }


    private fun setupCustomizedAdsApp(){
        homeViewModel.getPublicity().observe(viewLifecycleOwner) {
            publicity = it
            implementCustomizeAd()
        }
    }

    private fun setupAds() {
        try {
            DreamerApp.MOBILE_AD_INSTANCE.apply {
                adLoader = AdLoader.Builder(requireContext(),
                    getString(R.string.ad_unit_id_native_home))
                    .forNativeAd {
                        adList.add(it)
                        if (!adLoader.isLoading) {
                            implementAds()
                        }
                    }
                    .withAdListener(object : AdListener() {
                        override fun onAdFailedToLoad(p0: LoadAdError) {
                            if (!adLoader.isLoading) {
                                implementAds()
                            }
                        }
                    })
                    .build()

                adLoader.loadAds(AdRequest.Builder().build(), NUM_ADS)
            }
        } catch (e : Exception) {
            e.printStackTrace()
        }
    }

    private fun implementCustomizeAd() {
        try {
            if (objectProfileList.isNotEmpty())
                if (publicity != null && Constants.isCustomizedCommunicator()
                    && objectProfileList[0] !is Publicity
                ) {
                    objectProfileList[0] = publicity!!
                }
        } catch (e : Exception){
            e.printStackTrace()
        }
    }

    private fun implementAds() {
        if (adList.isEmpty() || objetList.isEmpty()) return

        val offset: Int = adapterHome.itemCount / adList.size + 1
        var index = 0
        if (objetList.first() !is NativeAd)
            for (ad in adList) {
                objetList.add(index, ad)
                index += offset
            }
        adapterHome.submitList(objetList)
    }

    private fun setupRecommendations() {
        homeViewModel.getRecommendations().observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.appBarLayout.layoutParams.height = 0
            }
            else {
                if (++count == 1) {
                    objectProfileList = it.toSet().toList().toMutableList()
                    implementCustomizeAd()
                    this.adapterProfile.submitList(objectProfileList)
                    if (fTimer)
                        timer.schedule(object : ScrollTimer(
                            layoutManager,
                            adapterProfile,
                            binding.rcvLiveRecommendations,
                            fTimer
                        ) {}, 0, 10000)
                }
            }
        }
    }

    private fun refreshData() {
        homeViewModel.synchronizeHome()
        homeViewModel.synchronizeNewContent()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        for (ad in adList) {
            ad.destroy()
        }
        super.onDestroy()
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"
        const val NUM_ADS = 3

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}