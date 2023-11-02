package com.ead.project.dreamer.presentation.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ead.project.dreamer.R
import com.ead.project.dreamer.databinding.FragmentNewsBinding
import com.ead.project.dreamer.presentation.news.adapters.NewsItemRecyclerViewAdapter
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.NativeAd
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class NewsFragment : Fragment() {

    private lateinit var viewModel: NewsViewModel
    private lateinit var adLoader: AdLoader

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
        binding.recyclerViewNews.apply {
            layoutManager = LinearLayoutManager(context)
            this@NewsFragment.adapter = NewsItemRecyclerViewAdapter(requireContext())
            adapter = this@NewsFragment.adapter
            setupNews()
        }
        observeUser()
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
            viewModel.setNews(it)
            binding.swipeRefresh.isRefreshing = false
        }

        viewModel.news.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    private fun observeUser() {
        lifecycleScope.launch {
            viewModel.getAccount().collectLatest { eadAccount ->
                setupAds(eadAccount?.isVip?:false)
            }
        }
    }

    private fun setupAds(returnCase : Boolean) {
        if (returnCase) return
        val context = requireContext()
        val adList = mutableListOf<NativeAd>()

        adLoader = AdLoader.Builder(context, context.getString(R.string.ad_unit_id_native_news))
            .forNativeAd { ad: NativeAd ->
                adList.add(ad)
                if (adLoader.isLoading) return@forNativeAd
                viewModel.setNews(adList)
            }.build()

        val adRequest = AdRequest.Builder().build()
        adLoader.loadAds(adRequest,2)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}