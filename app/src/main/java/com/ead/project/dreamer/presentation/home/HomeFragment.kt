package com.ead.project.dreamer.presentation.home

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.database.model.ChapterHome
import com.ead.project.dreamer.data.utils.ui.AppBarStateChangeListener
import com.ead.project.dreamer.data.utils.ui.ScrollTimer
import com.ead.project.dreamer.databinding.FragmentHomeBinding
import com.ead.project.dreamer.presentation.home.adapters.ChapterHomeRecyclerViewAdapter
import com.ead.project.dreamer.presentation.home.adapters.ProfileBannerRecyclerViewAdapter
import com.google.android.material.appbar.AppBarLayout
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var adapterHome : ChapterHomeRecyclerViewAdapter
    private lateinit var adapterProfile : ProfileBannerRecyclerViewAdapter

    private var objectProfileList : MutableList<Any> = ArrayList()

    private val snapHelper = LinearSnapHelper()
    private val timer = Timer()
    private lateinit var layoutManager : LinearLayoutManager
    private var fTimer = true

    private var _binding : FragmentHomeBinding?=null
    private val binding get() = _binding!!
    private var count = -1

    private var countHome = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        viewModel.adManager.restore()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        fTimer = false
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        fTimer = true
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
    }

    private fun settingVariables() {
        count = 0
    }

    private fun prepareLayouts() {
        preparePrincipalLayouts()
        prepareRecyclerViews()
    }

    private fun preparePrincipalLayouts() {
        binding.apply {
            swipeRefresh.apply {
                setColorSchemeColors(resources.getColor(R.color.blackPrimary,requireContext().theme))
                setOnRefreshListener {
                    isRefreshing = true
                    refreshData()
                }
            }

            appBarLayout.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
                override fun onStateChanged(appBarLayout: AppBarLayout, state: State) {
                    when(state) {
                        State.EXPANDED -> recyclerViewRecommendations.visibility = View.VISIBLE
                        State.COLLAPSED -> recyclerViewRecommendations.visibility = View.GONE
                        State.IDLE -> Log.d(TAG, "Home App Bar Idle")
                    }
                }
            })
        }
    }

    private fun prepareRecyclerViews() {
        binding.apply {

            recyclerViewRecommendations.apply {
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

            popularSection.apply {
                title.text = requireContext().getText(R.string.popular_section_title)
                recyclerView.apply {
                    layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
                    this@HomeFragment.adapterHome = ChapterHomeRecyclerViewAdapter(
                        activity as Context,
                        viewModel.handleChapter,
                        viewModel.launchDownload
                    )
                    adapter = this@HomeFragment.adapterHome
                }
            }

            recentSection.apply {
                title.text = requireContext().getText(R.string.recent_section_title)
                recyclerView.apply {
                    layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
                    adapter = this@HomeFragment.adapterHome
                    setupHomeList()
                }
            }
        }
    }

    private fun setupHomeList () {
        viewModel.getChaptersHome().observe(viewLifecycleOwner) {
            setupMessageError(it)
            adapterHome.submitList(it)
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun setupMessageError(chapterHomeList : List<ChapterHome>) {
        /*if (chapterHomeList.isNotEmpty()) if (++countHome == 1 && chapterHomeList.first().isNotWorking())
            DreamerLayout.showSnackbar(
                view = binding.root,
                text = getString(R.string.warning_manual_fixer),
                color = R.color.red, length = Snackbar.ANIMATION_MODE_SLIDE,
                size = R.dimen.snackbar_text_size_mini
            )*/
    }

    private fun setupRecommendations() {
        viewModel.getRecommendations().observe(viewLifecycleOwner) {
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
                binding.recyclerViewRecommendations, fTimer) {}, 8000, 8000)
    }

    private fun refreshData() {
        viewModel.synchronizeHome()
        viewModel.synchronizeNewContent()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}