package com.ead.project.dreamer.presentation.directory

import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ead.commons.lib.metrics.getAvailableWidthReference
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.data.util.system.handleNotActionBar
import com.ead.project.dreamer.data.database.model.AnimeBase
import com.ead.project.dreamer.databinding.ActivityDirectoryBinding
import com.ead.project.dreamer.presentation.directory.adapter.AnimeBaseRecyclerViewAdapter
import com.ead.project.dreamer.presentation.profile.AnimeProfileActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class DirectoryActivity : AppCompatActivity() {

    private val viewModel : DirectoryViewModel by viewModels()
    private lateinit var adapter: AnimeBaseRecyclerViewAdapter

    private var isSmallDevice = false

    private val binding : ActivityDirectoryBinding by lazy {
        ActivityDirectoryBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        overridePendingTransition(
            R.anim.fade_in, R.anim.fade_out)
        prepareLayout()
        prepareAdapter()
        handleIfProfileIsRequestedFromPlayer()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(
            R.anim.fade_in, R.anim.fade_out)
    }

    private fun prepareLayout() {
        binding.apply {
            editextSearch.requestFocus()

            handleNotActionBar(toolbar,viewModel.appBuildPreferences.isDarkTheme())

            recyclerView.apply {
                adapter?.stateRestorationPolicy =
                    RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
                foregroundGravity = Gravity.CENTER_HORIZONTAL
                layoutManager = layoutManagerAvailable()
            }
        }
    }

    private fun layoutManagerAvailable() : RecyclerView.LayoutManager =
        when(getAvailableWidthReference(380)) {
            0 -> {
                isSmallDevice = true
                binding.recyclerView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                LinearLayoutManager(this)
            }
            else -> GridLayoutManager(this,getAvailableWidthReference(110))
        }

    private fun prepareAdapter() {
        binding.apply {
            lifecycleScope.launch {
                viewModel.getDirectoryState().collect { isSynchronized ->

                    if (isSynchronized) {
                        editextSearch.addTextChangedListener {
                            viewModel.getFullDirectory(editextSearch.text.toString())
                                .observe(this@DirectoryActivity) { updateData(it) }
                        }
                    }
                    else {
                        editextSearch.addTextChangedListener {
                            viewModel.getDirectory(editextSearch.text.toString())
                                .observe(this@DirectoryActivity) { updateData(it) }
                        }
                    }

                }
            }
        }
    }

    private fun updateData(animeBaseList: List<AnimeBase>) {
        this.adapter = AnimeBaseRecyclerViewAdapter(animeBaseList, this,isSmallDevice)
        binding.recyclerView.adapter = adapter
    }

    private fun handleIfProfileIsRequestedFromPlayer() {
        lifecycleScope.launch {
            viewModel.playerPreference.collectLatest { playerPreferences ->

                if (playerPreferences.requester.isRequesting) {
                    viewModel.resetRequestingProfile()
                    AnimeProfileActivity.launchActivity(this@DirectoryActivity,playerPreferences.requester)
                }

            }
        }
    }

}