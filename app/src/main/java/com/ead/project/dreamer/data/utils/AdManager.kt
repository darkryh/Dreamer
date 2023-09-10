package com.ead.project.dreamer.data.utils

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.RecyclerView
import com.ead.project.dreamer.app.App
import com.ead.project.dreamer.presentation.home.adapters.ChapterHomeRecyclerViewAdapter
import com.ead.project.dreamer.presentation.news.adapters.NewsItemRecyclerViewAdapter
import com.ead.project.dreamer.presentation.player.content.adapters.ProfileRecyclerViewAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class AdManager @Inject constructor(
    private val context : Context
)  {

    private var adId : String = ""
    private var quantityAds : Int = 1

    private val mutableAdsFlow : MutableSharedFlow<List<Any>> = MutableSharedFlow()
    private val mutableAdFlow : MutableSharedFlow<NativeAd?> = MutableSharedFlow()


    private var adLoader: AdLoader?= null
    private var adapter : RecyclerView.Adapter<RecyclerView.ViewHolder>?=null
    private val nativeAdClass = NativeAd::class.java

    private var items : MutableList<Any> = mutableListOf()
    private var ads : MutableList<NativeAd> = mutableListOf()

    private var scope = CoroutineScope(Dispatchers.IO)
    fun setUp(
        returnCase : Boolean,
        adId : String,
        adapter : RecyclerView.Adapter<RecyclerView.ViewHolder>? = null,
        quantityAds : Int = 1
    ) {
        restore()
        this@AdManager.adapter = adapter
        if (returnCase || adId.isBlank()) return

        this@AdManager.adId = adId
        this@AdManager.quantityAds = quantityAds

        App.InitializationStatus.apply {
            adLoader = AdLoader.Builder(context,adId)
                .forNativeAd { scope.launch { workingAd(it) } }
                .withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        workingOnFailedAd(loadAdError)
                    }
                }).build()

            loadAds()
        }
    }


    private fun loadAds() {
        when(this@AdManager.quantityAds) {
            in 2..5 -> adLoader?.loadAds(AdRequest.Builder().build(), quantityAds)
            else -> adLoader?.loadAd(AdRequest.Builder().build())
        }
    }

    fun restore() {
        restoreItems()
        scope.launch {
            restoreAds()
        }
    }

    private fun restoreItems() {
        items.clear()
        ads.clear()
    }

    private suspend fun restoreAds() {
        mutableAdsFlow.emit(emptyList())
        mutableAdFlow.emit(null)
    }

    fun setItems(items : List<Any>) {
        this.items = items.toMutableList()
        if (ads.isNotEmpty()) {
            implementAds()
        }
    }

    private suspend fun workingAd(it : NativeAd) {
        ads.add(it)
        if (adLoader?.isLoading == false) {
            if (ads.size > 1) {
                implementAds()
            }
            else {
                mutableAdFlow.emit(ads.first())
            }
        }
    }

    private fun workingOnFailedAd(it: LoadAdError) { print(it.message) }

    fun onDestroy() {
        for (adItem in ads) {
            adItem.destroy()
        }
    }

    private fun implementAds() {
        Run.catching {
            scope.launch {
                if (isItemsPrepared()) {
                    val offset: Int = (adapter?.itemCount?.div(ads.size) ?: -1) + 1
                    var index = 0

                    if (!items.any { nativeAdClass.isInstance(it) }) {
                        for (ad in ads) {
                            items.add(index, ad)
                            index += offset
                        }
                        mutableAdsFlow.emit(items)
                    }
                }
            }
        }
    }

    private fun isItemsPrepared() : Boolean {
        return adapter != null && items.isNotEmpty()
    }

    fun getItems() : LiveData<List<Any>> {
        return mutableAdsFlow.asLiveData()
    }

    fun getItem() : LiveData<NativeAd?> {
        return mutableAdFlow.asLiveData()
    }

    fun submitList(list: List<Any>) {
        when(adapter) {
            is ChapterHomeRecyclerViewAdapter ->
                (adapter as ChapterHomeRecyclerViewAdapter).submitList(list)
            is NewsItemRecyclerViewAdapter ->
                (adapter as NewsItemRecyclerViewAdapter).submitList(list)
            is ProfileRecyclerViewAdapter ->
                (adapter as ProfileRecyclerViewAdapter).submitList(list)
        }
    }
}