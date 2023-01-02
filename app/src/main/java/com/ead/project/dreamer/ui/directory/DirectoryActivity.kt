package com.ead.project.dreamer.ui.directory

import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ead.commons.lib.lifecycle.activity.onBack
import com.ead.commons.lib.metrics.getAvailableWidthReference
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.commons.Tools
import com.ead.project.dreamer.data.database.model.AnimeBase
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DirectoryActivity : AppCompatActivity() {

    private val directoryViewModel : DirectoryViewModel by viewModels()
    private lateinit var recyclerView : RecyclerView
    private lateinit var adapter: AnimeBaseRecyclerViewAdapter
    private lateinit var edtSearch : EditText
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private var isLinear = false
    private val isDirectoryProfileStateCompleted = Constants.isDirectorySynchronized()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_directory)
        overridePendingTransition(
            R.anim.fade_in, R.anim.fade_out)
        prepareLayout()
        recyclerView.layoutManager = getLayoutManager()
        prepareAdapter()
    }

    override fun onStart() {
        super.onStart()
        Tools.launchRequestedProfile(this)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(
            R.anim.fade_in, R.anim.fade_out)
    }

    private fun prepareLayout() {
        supportActionBar?.hide()
        edtSearch = findViewById(R.id.edtSearch)
        edtSearch.requestFocus()
        toolbar = findViewById(R.id.toolbarSearching)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBack() }
        recyclerView = findViewById(R.id.rcvFinder)
        recyclerView.adapter?.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        recyclerView.foregroundGravity = Gravity.CENTER_HORIZONTAL
    }

    private fun getLayoutManager() : RecyclerView.LayoutManager =
        when(getAvailableWidthReference(380)) {
            0 -> {
                isLinear = true
                recyclerView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                LinearLayoutManager(this)
            }
            else -> GridLayoutManager(this,getAvailableWidthReference(110))
        }

    private fun prepareAdapter() {
        if (isDirectoryProfileStateCompleted)
            edtSearch.addTextChangedListener { directoryViewModel.getFullDirectory(edtSearch.text.toString()).observe(this) {updateData(it) } }
        else
            edtSearch.addTextChangedListener { directoryViewModel.getDirectory(edtSearch.text.toString()).observe(this) { updateData(it)} }
    }

    private fun updateData(animeBaseList: List<AnimeBase>) {
        this.adapter = AnimeBaseRecyclerViewAdapter(animeBaseList, this,isLinear)
        recyclerView.adapter = adapter
    }

    override fun onStop() {
        super.onStop()
        Constants.setDirectoryActivityClicked(false)
    }
}