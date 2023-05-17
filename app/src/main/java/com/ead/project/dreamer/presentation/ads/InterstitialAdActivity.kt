package com.ead.project.dreamer.presentation.ads

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ead.commons.lib.lifecycle.parcelable
import com.ead.commons.lib.lifecycle.parcelableArrayList
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.data.util.system.launchActivityAndFinish
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.models.VideoModel
import com.ead.project.dreamer.databinding.ActivityInterstitialAdBinding
import com.ead.project.dreamer.presentation.player.PlayerExternalActivity
import com.ead.project.dreamer.presentation.player.PlayerWebActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList

@AndroidEntryPoint
class InterstitialAdActivity : AppCompatActivity() {

    private val viewModel : InterstitialAdViewModel by viewModels()

    private var interstitialAd : InterstitialAd?= null

    private lateinit var chapter: Chapter
    private lateinit var videoPlayList : List<VideoModel>
    private var videoUrlIsDirect = true

    private val binding: ActivityInterstitialAdBinding by lazy {
        ActivityInterstitialAdBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.hide()
        initVariables()
        initAds()
    }

    private fun initVariables() {
        intent.extras?.let {
            chapter = it.parcelable(Chapter.REQUESTED)?:return@let
            videoPlayList = it.parcelableArrayList(Chapter.PLAY_VIDEO_LIST)?:return@let
            videoUrlIsDirect = it.getBoolean(Chapter.CONTENT_IS_DIRECT)
        }
    }

    private fun initAds() {
        try {
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(
                this,
                getString(R.string.ad_unit_id_interstitial_player),
                adRequest,object : InterstitialAdLoadCallback() {

                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        this@InterstitialAdActivity.interstitialAd = interstitialAd
                        initListener()
                        showAds()
                    }

                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        this@InterstitialAdActivity.interstitialAd = null
                    }
                })
        } catch (e : Exception) {
            e.printStackTrace()
        }
    }

    private fun showAds() { interstitialAd?.show(this) }

    private fun initListener() {
        interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {

            override fun onAdShowedFullScreenContent() {
                viewModel.resetViews()
                interstitialAd = null
            }

            override fun onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent()
                launchPlayer()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                super.onAdFailedToShowFullScreenContent(adError)
                launchPlayer()
            }
        }
    }

    private fun launchPlayer() {
        if (videoUrlIsDirect) {
            launchToPlayer(PlayerExternalActivity::class.java)
        }
        else {
            launchToPlayer(PlayerWebActivity::class.java)
        }
    }

    private fun launchToPlayer(typeClass: Class<*>?) {
        launchActivityAndFinish(
            intent = Intent(this@InterstitialAdActivity,typeClass).apply {
                putExtra(Chapter.REQUESTED, chapter)
                putParcelableArrayListExtra(
                    Chapter.PLAY_VIDEO_LIST,
                    videoPlayList as ArrayList<out Parcelable>
                )
            },
        )
    }
}