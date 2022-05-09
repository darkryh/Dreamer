package com.ead.project.dreamer.ui.directory

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.EditText
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.commons.Tools
import com.ead.project.dreamer.data.utils.DataStore
import com.ead.project.dreamer.data.utils.ui.DreamerLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DirectoryActivity : AppCompatActivity() {

    private val directoryActivityViewModel : DirectoryActivityViewModel by viewModels()
    private lateinit var recyclerView : RecyclerView
    private lateinit var adapter: AnimeBaseRecyclerViewAdapter
    private lateinit var edtSearch : EditText
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_directory)
        overridePendingTransition(
            R.anim.fade_in, R.anim.fade_out)
        prepareLayout()
        recyclerView.layoutManager =
            GridLayoutManager(this, Tools.getAutomaticSizeReference(130))
        prepareAdapter()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(
            R.anim.fade_in, R.anim.fade_out)
    }

    override fun onStart() {
        super.onStart()
        Tools.launchRequestedProfile(this)
    }

    private fun prepareLayout() {
        supportActionBar?.hide()
        edtSearch = findViewById(R.id.edtSearch)
        edtSearch.requestFocus()
        toolbar = findViewById(R.id.toolbarSearching)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        settingThemeLayouts()
        toolbar.setNavigationOnClickListener { onBackPressed()}
        recyclerView = findViewById(R.id.rcvFinder)
        recyclerView.adapter?.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            recyclerView.foregroundGravity = Gravity.CENTER_HORIZONTAL
        }
    }

    private fun prepareAdapter() {
        edtSearch.addTextChangedListener {
            directoryActivityViewModel.getDirectory(edtSearch.text.toString()).observe(this) {
                this.adapter = AnimeBaseRecyclerViewAdapter(it, this)
                recyclerView.adapter = adapter
            }
        }
    }

    private fun settingThemeLayouts() {
        val data = DataStore.readBoolean(Constants.PREFERENCE_THEME_MODE)

        if (data) {
            toolbar.navigationIcon =
                DreamerLayout.getBackgroundColor(
                    toolbar.navigationIcon!!,
                    R.color.whitePrimary
                )
        }
        else {
            toolbar.navigationIcon =
                DreamerLayout.getBackgroundColor(
                    toolbar.navigationIcon!!,
                    R.color.blackPrimary
                )
        }
    }

    override fun onStop() {
        super.onStop()
        DataStore.writeBooleanAsync(Constants.PREFERENCE_DIRECTORY_CLICKED,false)
    }
}