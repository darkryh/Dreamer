package com.ead.project.dreamer.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.model.Publicity
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.commons.Tools.Companion.hide
import com.ead.project.dreamer.data.commons.Tools.Companion.show
import com.ead.project.dreamer.data.database.model.ChapterHome
import com.ead.project.dreamer.data.models.discord.User
import com.ead.project.dreamer.data.utils.AdManager
import com.ead.project.dreamer.data.utils.ui.AppBarStateChangeListener
import com.ead.project.dreamer.data.utils.ui.DreamerLayout
import com.ead.project.dreamer.data.utils.ui.ScrollTimer
import com.ead.project.dreamer.databinding.FragmentHomeBinding
import com.ead.project.dreamer.ui.home.adapters.ChapterHomeRecyclerViewAdapter
import com.ead.project.dreamer.ui.home.adapters.ProfileBannerRecyclerViewAdapter
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var columnCount = 1
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var adapterHome : ChapterHomeRecyclerViewAdapter
    private lateinit var adapterProfile : ProfileBannerRecyclerViewAdapter

    private var objetList : MutableList<Any> = ArrayList()
    private var objectProfileList : MutableList<Any> = ArrayList()
    private var publicityList : MutableList<Publicity> = ArrayList()

    private val snapHelper = LinearSnapHelper()
    private val timer = Timer()
    private lateinit var layoutManager : LinearLayoutManager
    private var fTimer = true

    private var _binding : FragmentHomeBinding?=null
    private val binding get() = _binding!!
    private var count = -1
    private var countHome = 0

    private var adManager : AdManager?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        fTimer = false
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        fTimer = true
        adManager?.onViewStateRestored()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingVariables()
        prepareLayouts()
        prepareSettings()
    }

    private fun settingVariables() {
        count = 0
    }

    private fun prepareSettings() {
        binding.shimmerClassic.show()
        if (Constants.isCustomizedAdvertiserActivated()) setupCustomizedAdsApp()
    }

    private fun prepareLayouts() {
        preparePrincipalLayouts()
        prepareRecyclerViews()
    }

    private fun preparePrincipalLayouts() {
        binding.swipeRefresh.apply {
            setColorSchemeColors(resources.getColor(R.color.blackPrimary,requireContext().theme))
            setOnRefreshListener {
                isRefreshing = true
                refreshData()
            }
        }
        binding.appBarLayout.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout?, state: State?) {
                when(state) {
                    State.EXPANDED -> binding.  rcvLiveRecommendations.visibility = View.VISIBLE
                    State.COLLAPSED -> binding.rcvLiveRecommendations.visibility = View.GONE
                    else -> { }
                }
            }
        })
    }

    private fun prepareRecyclerViews() {
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
        binding.rcvReleases.apply {
            layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }
            this@HomeFragment.adapterHome = ChapterHomeRecyclerViewAdapter(activity as Context)
            adManager = AdManager(
                context =  requireContext(),
                adId =  getString(R.string.ad_unit_id_native_home),
                anyList = objetList,
                adapter = this@HomeFragment.adapterHome,
                quantityAds = 3)
            adapter = this@HomeFragment.adapterHome
            adManager?.setUp(User.isNotVip())
            setupHomeList()
        }
    }

    private fun setupHomeList () {
        homeViewModel.getChaptersHome().observe(viewLifecycleOwner) {
            binding.shimmerClassic.hide()
            setupMessageError(it)
            adManager?.setAnyList(it)
            adManager?.submitList(it)
            binding.swipeRefresh.isRefreshing = false
        }
        adManager?.getAds()?.observe(viewLifecycleOwner) { adManager?.submitList(it) }
    }

    private fun setupMessageError(chapterHomeList : List<ChapterHome>) {
        if (chapterHomeList.isNotEmpty()) if (++countHome == 1 && chapterHomeList.first().isNotWorking())
            DreamerLayout.showSnackbar(
                view = binding.root,
                text = getString(R.string.warning_manual_fixer),
                color = R.color.red, length = Snackbar.ANIMATION_MODE_SLIDE,
                size = R.dimen.snackbar_text_size_mini
            )
    }

    private fun setupCustomizedAdsApp(){
        homeViewModel.getPublicity().observe(viewLifecycleOwner) {
            publicityList = it.toMutableList()
            implementCustomizeAd()
        }
    }

    private fun implementCustomizeAd() {
        try {
            val adapterCount = adapterProfile.itemCount
            if (objectProfileList.isEmpty() || adapterCount == 0 || publicityList.isEmpty()) return
            if (Constants.isCustomizedAdvertiserActivated() && objectProfileList.first() !is Publicity) {
                val offset: Int = adapterCount / publicityList.size + 1
                var index = 0
                for (publicity in publicityList) {
                    objectProfileList.add(index, publicity)
                    index += offset
                }
            }
        } catch (e : Exception) { e.printStackTrace() }
    }

    private fun setupRecommendations() {
        homeViewModel.getRecommendations().observe(viewLifecycleOwner) {
            if (it.isEmpty()) binding.appBarLayout.layoutParams.height = 0
            else {
                if (++count == 1) {
                    objectProfileList = it.toMutableList()
                    this.adapterProfile.submitList(objectProfileList)
                }
            }
        }
        setupScrollRecommendations()
    }

    private fun setupScrollRecommendations() {
        if (fTimer)
            timer.schedule(object : ScrollTimer(layoutManager, adapterProfile,
                binding.rcvLiveRecommendations, fTimer) {}, 10000, 10000)
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
        adManager?.onDestroy()
        adManager = null
        super.onDestroy()
    }
}