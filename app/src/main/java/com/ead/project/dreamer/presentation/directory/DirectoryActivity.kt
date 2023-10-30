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
import com.ead.project.dreamer.app.data.util.system.closeTransition
import com.ead.project.dreamer.app.data.util.system.handleNotActionBar
import com.ead.project.dreamer.app.data.util.system.openTransition
import com.ead.project.dreamer.data.database.model.AnimeBase
import com.ead.project.dreamer.databinding.ActivityDirectoryBinding
import com.ead.project.dreamer.presentation.directory.adapter.AnimeBaseRecyclerViewAdapter
import dagger.hilt.android.AndroidEntryPoint
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
        openTransition(R.anim.fade_in,R.anim.fade_out)
        prepareLayout()
        prepareAdapter()
    }

    override fun finish() {
        super.finish()
        closeTransition(R.anim.fade_in,R.anim.fade_out)
    }

    private fun prepareLayout() {
        binding.apply {
            editTextSearch.requestFocus()

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
                        editTextSearch.addTextChangedListener {
                            viewModel.getFullDirectory(editTextSearch.text.toString())
                                .observe(this@DirectoryActivity) { updateData(it) }
                        }
                    }
                    else {
                        editTextSearch.addTextChangedListener {
                            viewModel.getDirectory(editTextSearch.text.toString())
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

}