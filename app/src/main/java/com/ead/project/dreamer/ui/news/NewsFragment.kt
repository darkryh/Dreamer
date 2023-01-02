package com.ead.project.dreamer.ui.news

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.models.discord.User
import com.ead.project.dreamer.data.utils.AdManager
import com.ead.project.dreamer.databinding.FragmentNewsBinding
import com.ead.project.dreamer.ui.news.adapters.NewsItemRecyclerViewAdapter
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList


@AndroidEntryPoint
class NewsFragment : Fragment() {

    private lateinit var newsViewModel: NewsViewModel
    private lateinit var adapter: NewsItemRecyclerViewAdapter
    private var _binding : FragmentNewsBinding?= null
    private val binding get() = _binding!!
    private var objetList : MutableList<Any> = ArrayList()

    private var adManager : AdManager?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        newsViewModel = ViewModelProvider(this)[NewsViewModel::class.java]
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        adManager?.onViewStateRestored()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newsViewModel.synchronizeNews()
        setupLayouts()
        binding.rcvNews.apply {
            layoutManager = LinearLayoutManager(context)
            this@NewsFragment.adapter = NewsItemRecyclerViewAdapter(requireContext())
            adManager = AdManager(
                context =  requireContext(),
                adId =  getString(R.string.ad_unit_id_native_news),
                anyList = objetList,
                adapter = this@NewsFragment.adapter,
                quantityAds = 3)
            adapter = this@NewsFragment.adapter
            adManager?.setUp(User.isNotVip())
            setupNews()
        }
    }

    private fun setupLayouts() {
        binding.swipeRefresh.apply {
            setColorSchemeColors(resources.getColor(R.color.blackPrimary,requireContext().theme))
            setOnRefreshListener {
                isRefreshing = true
                newsViewModel.synchronizeNews()
            }
        }
    }

    private fun setupNews() {
        newsViewModel.getNewsItems().observe(viewLifecycleOwner) {
            adManager?.setAnyList(it)
            adManager?.submitList(it)
            binding.swipeRefresh.isRefreshing = false
        }
        adManager?.getAds()?.observe(viewLifecycleOwner) {
            adManager?.submitList(it)
            binding.swipeRefresh.isRefreshing = false
        }
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