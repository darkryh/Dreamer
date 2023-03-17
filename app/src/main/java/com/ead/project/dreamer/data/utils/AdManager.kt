package com.ead.project.dreamer.data.utils

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.ead.project.dreamer.app.DreamerApp
import com.ead.project.dreamer.ui.home.adapters.ChapterHomeRecyclerViewAdapter
import com.ead.project.dreamer.ui.news.adapters.NewsItemRecyclerViewAdapter
import com.ead.project.dreamer.ui.player.content.adapters.ProfileRecyclerViewAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd

class AdManager(
    private val context : Context,
    private val adId : String,
    private var anyList : MutableList<Any>?=null,
    private val adapter : RecyclerView.Adapter<RecyclerView.ViewHolder>?=null,
    private val quantityAds : Int = 1
)  {

    private var adListLivedata : MutableLiveData<MutableList<Any>>? = null
    private var adLiveData: MutableLiveData<NativeAd>? = null

    private var adLoader: AdLoader?= null
    private var ads : MutableList<NativeAd>?=null
    private var ad : NativeAd?=null

    fun setUp(returnIfNot : Boolean = false) {
        if (!returnIfNot) return

        if (quantityAds > 1) {
            ads = mutableListOf()
            if (adListLivedata == null) adListLivedata = MutableLiveData()
        }
        else if (adLiveData == null) adLiveData = MutableLiveData()

        try {
            DreamerApp.InitializationStatus.apply {
                adLoader = AdLoader.Builder(context,adId)
                    .forNativeAd { workingAd(it) }
                    .withAdListener(object : AdListener() {
                        override fun onAdFailedToLoad(p0: LoadAdError) { workingOnFailedAd(p0)}
                    })
                    .build()

                if (quantityAds > 1) adLoader?.loadAds(AdRequest.Builder().build(), quantityAds)
                else adLoader?.loadAd(AdRequest.Builder().build())
            }
        } catch (e : Exception) { e.printStackTrace() }
    }

    private fun workingAd(it : NativeAd) {
        if (ads != null) {
            ads?.add(it)
            if (adLoader?.isLoading == false) implementAds()
        }
        else {
            ad = it
            adLiveData?.postValue(ad!!)
        }
    }

    private fun workingOnFailedAd(it: LoadAdError) { print(it.message) }

    fun onViewStateRestored() {
        ads?.clear()
    }

    fun onDestroy() {
        if (ads != null) {
            for (adItem in ads!!)
                adItem.destroy()
        }
        ad?.destroy()
        adLoader = null
    }

    private fun implementAds() {
        if (ads != null && adapter != null && anyList != null && anyList?.isNotEmpty() == true) {
            val offset: Int = adapter.itemCount / ads!!.size + 1
            var index = 0
            if (anyList!!.first() !is NativeAd) {
                for (ad in ads!!) {
                    anyList!!.add(index, ad)
                    index += offset
                }
                adListLivedata?.postValue(anyList!!)
            }
        }
    }

    fun setAnyList (list: List<Any>) {
        anyList = list.toMutableList()
        if (ads?.isNotEmpty() == true) implementAds()
    }

    fun getAds() : MutableLiveData<MutableList<Any>> {
        if (adListLivedata == null) adListLivedata = MutableLiveData()
        return adListLivedata!!
    }

    fun getAd() : MutableLiveData<NativeAd> {
        if (adLiveData == null) adLiveData = MutableLiveData()
        return adLiveData!!
    }

    fun submitList(list: List<Any>) {
        when(adapter) {
            is ChapterHomeRecyclerViewAdapter -> adapter.submitList(list)
            is NewsItemRecyclerViewAdapter -> adapter.submitList(list)
            is ProfileRecyclerViewAdapter -> adapter.submitList(list)
        }
    }
}