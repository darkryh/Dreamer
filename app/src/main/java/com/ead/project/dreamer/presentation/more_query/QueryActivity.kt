package com.ead.project.dreamer.presentation.more_query

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ead.project.dreamer.app.data.util.system.handleNotActionBar
import com.ead.project.dreamer.app.data.util.system.launchActivity
import com.ead.project.dreamer.data.database.model.AnimeProfile
import com.ead.project.dreamer.data.database.model.ChapterHome
import com.ead.project.dreamer.databinding.ActivityQueryBinding
import com.ead.project.dreamer.presentation.home.adapters.ChapterLinearRecyclerViewAdapter
import com.ead.project.dreamer.presentation.player.content.adapters.ProfileRecyclerViewAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QueryActivity : AppCompatActivity() {

    private val viewModel : QueryViewModel by viewModels()
    private lateinit var adapter : RecyclerView.Adapter<RecyclerView.ViewHolder>

    private var queryOption = -1
    private var queryTitle = "title"

    private val binding : ActivityQueryBinding by lazy {
        ActivityQueryBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initVariables()
        setupLayout()
        setupRecyclerView()
        setupQuery()
    }

    private fun initVariables() {
        intent.extras?.apply {
            queryOption =  getInt(QUERY_OPTION_KEY,0)
            queryTitle = getString(QUERY_OPTION_TITLE)?:return@apply
        }
    }

    private fun setupLayout() {
        binding.apply {
            textTitle.text = queryTitle
            handleNotActionBar(toolbar,viewModel.appBuildPreferences.isDarkTheme())
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerViewQuery.apply {
            this@QueryActivity.adapter = getRequestedAdapter()
            layoutManager = LinearLayoutManager(context)
            adapter = this@QueryActivity.adapter
        }
    }

    private fun setupQuery() {
        viewModel.getQuery(queryOption).observe(this) { items ->
            submitList(items)
        }
    }

    private fun getRequestedAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder> {
        return when(queryOption) {
            QUERY_OPTION_CHAPTER_HOME -> ChapterLinearRecyclerViewAdapter(this,viewModel.handleChapter,viewModel.launchDownload)
            QUERY_OPTION_PROFILE -> ProfileRecyclerViewAdapter(this, preferenceUseCase = viewModel.preferenceUseCase)
            else -> ChapterLinearRecyclerViewAdapter(this,viewModel.handleChapter,viewModel.launchDownload)
        }
    }

    private fun submitList(items: List<Any>) {
        if (items.isEmpty()) return

        when(items.component1()) {
            is ChapterHome -> (adapter as? ChapterLinearRecyclerViewAdapter)?.submitList(items)
            is AnimeProfile -> (adapter as? ProfileRecyclerViewAdapter)?.submitList(items)
        }
    }

    companion object {

        private const val QUERY_OPTION_KEY = "QUERY_OPTION_KEY"
        private const val QUERY_OPTION_TITLE = "QUERY_OPTION_TITLE"

        const val QUERY_OPTION_CHAPTER_HOME = -700
        const val QUERY_OPTION_PROFILE = -701

        fun launchActivity(context : Context,queryOption : Int, title : String) {
            context.launchActivity(
                intent = Intent(context,QueryActivity::class.java).apply {
                    putExtra(QUERY_OPTION_KEY,queryOption)
                    putExtra(QUERY_OPTION_TITLE,title)
                }
            )
        }
    }
}