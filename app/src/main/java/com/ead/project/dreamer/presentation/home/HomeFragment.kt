package com.ead.project.dreamer.presentation.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.utils.ui.ScrollTimer
import com.ead.project.dreamer.databinding.FragmentHomeBinding
import com.ead.project.dreamer.presentation.home.adapters.ChapterHomeRecyclerViewAdapter
import com.ead.project.dreamer.presentation.home.adapters.ProfileBannerRecyclerViewAdapter
import com.ead.project.dreamer.presentation.more_query.QueryActivity
import com.ead.project.dreamer.presentation.news.adapters.NewsItemRecyclerViewAdapter
import dagger.hilt.android.AndroidEntryPoint
import java.util.Timer


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var adapterHome : ChapterHomeRecyclerViewAdapter
    private lateinit var adapterProfile : ProfileBannerRecyclerViewAdapter
    private lateinit var adapterNews : NewsItemRecyclerViewAdapter

    private var objectProfileList : MutableList<Any> = ArrayList()

    private val snapHelper = LinearSnapHelper()
    private val timer = Timer()
    private lateinit var layoutManager : LinearLayoutManager
    private var fTimer = true

    private var _binding : FragmentHomeBinding?=null
    private val binding get() = _binding!!
    private var count = -1


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
                        viewModel.handleChapter
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
                more.setOnClickListener { goToRecentSection() }
            }

            rcvNews.apply {
                layoutManager = LinearLayoutManager(context)
                this@HomeFragment.adapterNews = NewsItemRecyclerViewAdapter(requireContext())
                adapter = this@HomeFragment.adapterNews
                setupNews()
            }
        }
    }

    private fun setupHomeList () {
        viewModel.getChaptersHome().observe(viewLifecycleOwner) {
            adapterHome.submitList(it)
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun setupNews() {
        viewModel.getLimitedNews().observe(viewLifecycleOwner) {
            adapterNews.submitList(it)
        }
    }

    private fun setupRecommendations() {
        viewModel.getRecommendations().observe(viewLifecycleOwner) {
            if (it.isEmpty()) binding.recommendationsSection.layoutParams.height = 0
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

    private fun goToRecentSection() {
        QueryActivity.launchActivity(
            requireActivity(),
            QueryActivity.QUERY_OPTION_CHAPTER_HOME,
            getString(R.string.recent_section_title)
        )
    }

}