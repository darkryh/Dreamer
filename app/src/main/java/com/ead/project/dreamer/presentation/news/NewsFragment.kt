package com.ead.project.dreamer.presentation.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.models.discord.DiscordUser
import com.ead.project.dreamer.databinding.FragmentNewsBinding
import com.ead.project.dreamer.presentation.news.adapters.NewsItemRecyclerViewAdapter
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NewsFragment : Fragment() {

    private lateinit var viewModel: NewsViewModel

    private lateinit var adapter: NewsItemRecyclerViewAdapter
    private var _binding : FragmentNewsBinding?= null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[NewsViewModel::class.java]
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
        setupLayouts()
        binding.rcvNews.apply {
            layoutManager = LinearLayoutManager(context)
            this@NewsFragment.adapter = NewsItemRecyclerViewAdapter(requireContext())
            adapter = this@NewsFragment.adapter
            viewModel.adManager.setUp(
                returnCase = DiscordUser.isVip(),
                adId = getString(R.string.ad_unit_id_native_news),
                adapter = adapter,
                3
            )
            setupNews()
        }
    }

    private fun setupLayouts() {
        binding.swipeRefresh.apply {
            setColorSchemeColors(resources.getColor(R.color.blackPrimary,requireContext().theme))
            setOnRefreshListener {
                isRefreshing = true
                viewModel.synchronizeNews()
            }
        }
    }

    private fun setupNews() {
        viewModel.getNewsItems().observe(viewLifecycleOwner) {
            viewModel.adManager.setItems(it)
            viewModel.adManager.submitList(it)
            binding.swipeRefresh.isRefreshing = false
        }
        viewModel.adManager.getItems().observe(viewLifecycleOwner) {
            viewModel.adManager.submitList(it)
            binding.swipeRefresh.isRefreshing = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        viewModel.adManager.onDestroy()
        super.onDestroy()
    }
}