package com.ead.project.dreamer.presentation.news

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.webkit.WebView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.MediaController
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import coil.load
import coil.transform.CircleCropTransformation
import com.ead.commons.lib.views.justifyInterWord
import com.ead.commons.lib.views.margin
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.data.util.system.handleNotActionBar
import com.ead.project.dreamer.data.database.model.NewsItem
import com.ead.project.dreamer.data.models.Image
import com.ead.project.dreamer.data.models.NewsItemWeb
import com.ead.project.dreamer.data.models.Title
import com.ead.project.dreamer.data.models.Video
import com.ead.project.dreamer.data.system.extensions.toast
import com.ead.project.dreamer.databinding.ActivityNewsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewsActivity : AppCompatActivity() {


    private var reference : String = "null"
    private val viewModel : NewsViewModel by viewModels()

    private val binding : ActivityNewsBinding by lazy {
        ActivityNewsBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initVariables()
        settingLayouts()
        if (reference != "null") {
            setupWebPage()
        }
        else {
            toast(getString(R.string.error_web_page_null),Toast.LENGTH_SHORT)
            finish()
        }
    }

    private fun initVariables() {
        intent.extras?.let {
            reference = it.getString(NewsItem.REQUESTED_NEWS)?:return@let
        }
    }

    private fun settingLayouts() {
        binding.apply {
            handleNotActionBar(toolbar)
        }
    }

    private fun setupWebPage() {
        viewModel.getWebPageData(reference).observe(this) {

            if (it !=null) {
                bindHeader(it)
                bindBody(it)
                bindFooter(it)
                binding.imvCover.requestFocus()
            }

        }
    }

    private fun bindHeader(newsItemWeb: NewsItemWeb) {
        binding.apply {

            imvCover.load(newsItemWeb.cover)
            txvTitle.text = newsItemWeb.title
            txvType.text = getString(R.string.topic_news,newsItemWeb.type)
            txvAuthor.text = newsItemWeb.author
            txvDate.text = newsItemWeb.date

        }
    }

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
                        setLineSpacing(0f,1.5f)
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
                    }
                }
                is Video -> {
                    if (item.isEmbedded) {
                        @SuppressLint("SetJavaScriptEnabled")
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
                            ).apply { gravity = Gravity.CENTER_HORIZONTAL }
                            layoutParams.height = resources.getDimensionPixelSize(R.dimen.dimen_300dp)
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
                                setLineSpacing(0f,1.5f)
                            })
                        }
                    }
                }
            }
            view.margin(dpInTop = 8f, dpInBottom = 8f, dpInRight = 16f, dpInLeft = 16f)
            binding.linearBody.addView(view)
        }
    }
    private fun bindFooter(newsItemWeb: NewsItemWeb) {
        binding.apply {

            imvCoverAuthor.load(newsItemWeb.photoAuthor){
                transformations(CircleCropTransformation())
            }
            txvAuthorFooter.text = newsItemWeb.author
            txvAuthorWords.text = newsItemWeb.authorWords

        }
    }
}