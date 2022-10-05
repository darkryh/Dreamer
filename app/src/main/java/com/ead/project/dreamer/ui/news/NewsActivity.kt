package com.ead.project.dreamer.ui.news

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.MediaController
import android.widget.TextView
import android.widget.VideoView
import androidx.activity.viewModels
import coil.load
import coil.transform.CircleCropTransformation
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.DreamerApp
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.commons.Tools.Companion.justifyInterWord
import com.ead.project.dreamer.data.commons.Tools.Companion.margin
import com.ead.project.dreamer.data.commons.Tools.Companion.onBack
import com.ead.project.dreamer.data.database.model.Image
import com.ead.project.dreamer.data.database.model.NewsItemWeb
import com.ead.project.dreamer.data.database.model.Title
import com.ead.project.dreamer.data.database.model.Video
import com.ead.project.dreamer.databinding.ActivityNewsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewsActivity : AppCompatActivity() {


    private lateinit var binding : ActivityNewsBinding
    private var reference : String = "null"
    private val newsViewModel : NewsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initVariables()
        settingLayouts()
        if (reference != "null") setupWebPage()
        else {
            DreamerApp.showLongToast("error página null")
            finish()
        }
    }

    private fun initVariables() {
        reference = intent!!.extras!!.getString(Constants.REQUESTED_NEWS,"null")
    }

    private fun settingLayouts() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBack() }
    }

    private fun setupWebPage() {
        newsViewModel.getWebPageData(reference).observe(this) {
            if (it !=null) {
                bindHeader(it)
                bindBody(it)
                bindFooter(it)
            }
        }
    }

    private fun bindHeader(newsItemWeb: NewsItemWeb) {
        binding.imvCover.load(newsItemWeb.cover)
        binding.txvTitle.text = newsItemWeb.title
        binding.txvType.text = getString(R.string.topic_news,newsItemWeb.type)
        binding.txvAuthor.text = newsItemWeb.author
        binding.txvDate.text = newsItemWeb.date
    }
    @SuppressLint("SetJavaScriptEnabled")
    private fun bindBody(newsItemWeb: NewsItemWeb) {
        for (item in newsItemWeb.bodyList) {
            var view = View(this)
            when(item) {
                is String -> {
                    view = TextView(this).apply {
                        text = item.toString()
                        justifyInterWord()
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT, 1f
                        )
                    }
                }
                is Title -> {
                    view = TextView(this).apply {
                        text = item.text
                        setTypeface(null, Typeface.BOLD)
                        val size = when (item.type) {
                            "h2" -> 18f
                            "h3" -> 16f
                            "h4" -> 15f
                            else -> 14f
                        }
                        textSize = size
                        justifyInterWord()
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT, 1f
                        )
                    }
                }
                is Image -> {
                    view = ImageView(this).apply {
                        load(item.source)
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT, 1f
                        )
                        layoutParams.height = resources.getDimensionPixelSize(R.dimen.dimen_300dp)
                    }
                }
                is Video -> {
                    if (item.isEmbedded) {
                            view = WebView(this).apply {
                                layoutParams = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT, 1f
                                )
                                layoutParams.height =
                                    resources.getDimensionPixelSize(R.dimen.dimen_300dp)
                                settings.javaScriptEnabled = true
                                loadUrl(item.source)
                            }
                        }
                    else {
                        view = VideoView(this).apply {
                            layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT, 1f
                            )
                            layoutParams.height =
                                resources.getDimensionPixelSize(R.dimen.dimen_300dp)
                            setVideoURI(Uri.parse(item.source))
                            val mediaController = MediaController(this@NewsActivity)
                            this.setMediaController(mediaController)
                            this.setOnPreparedListener {
                                mediaController.setAnchorView(this)
                            }
                        }
                    }
                }
                is List<*> -> {
                    view = LinearLayout(this).apply {
                        orientation = LinearLayout.VERTICAL
                        setPadding(16,0,16,0)
                        val textList = item.map { it.toString() }
                        for (textContent in textList) {
                            addView( TextView(this@NewsActivity).apply {
                                text = textContent
                                justifyInterWord()
                                layoutParams = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT, 1f
                                )
                            })
                        }
                    }
                }
            }
            view.margin(top = 4f, bottom = 4f, right = 16f, left = 16f)
            binding.linearBody.addView(view)
        }
    }
    private fun bindFooter(newsItemWeb: NewsItemWeb) {
        binding.imvCoverAuthor.load(newsItemWeb.photoAuthor){
            transformations(CircleCropTransformation())
        }
        binding.txvAuthorFooter.text = newsItemWeb.author
        binding.txvAuthorWords.text = newsItemWeb.authorWords
    }
}