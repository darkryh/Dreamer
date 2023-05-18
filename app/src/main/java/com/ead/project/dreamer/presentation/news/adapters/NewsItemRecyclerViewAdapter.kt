package com.ead.project.dreamer.presentation.news.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.CachePolicy
import coil.transform.RoundedCornersTransformation
import com.ead.commons.lib.views.addSelectableItemEffect
import com.ead.commons.lib.views.justifyInterWord
import com.ead.commons.lib.views.setVisibility
import com.ead.project.dreamer.app.data.util.system.isNotNullOrNotEmpty
import com.ead.project.dreamer.data.database.model.NewsItem
import com.ead.project.dreamer.data.utils.ui.mechanism.DreamerAsyncDiffUtil
import com.ead.project.dreamer.databinding.AdUnifiedNewsItemBinding
import com.ead.project.dreamer.databinding.LayoutNewsItemBinding
import com.ead.project.dreamer.presentation.news.NewsActivity
import com.google.android.gms.ads.nativead.NativeAd

class NewsItemRecyclerViewAdapter(private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val IS_AD = 1
        const val NOT_AD = 0
    }

    private val dreamerAsyncDiffUtil = object : DreamerAsyncDiffUtil<Any>(){}

    private val differ = AsyncListDiffer(this,dreamerAsyncDiffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == IS_AD) {
            return ViewHolderAd(
                AdUnifiedNewsItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
        return ViewHolder(
            LayoutNewsItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == NOT_AD) {
            val newsItem = differ.currentList[position] as NewsItem?
            val newsItemHolder = holder as ViewHolder
            newsItemHolder.bindTo(newsItem?:return)
        }
        else {
            val nativeAd = differ.currentList[position] as NativeAd?
            val nativeHolder = holder as ViewHolderAd
            nativeHolder.bindTo(nativeAd?:return)
        }
    }

    fun submitList (list: List<Any>) = differ.submitList(list)

    override fun getItemCount(): Int = differ.currentList.size

    override fun getItemViewType(position: Int): Int {
        if (differ.currentList[position] !is NativeAd)
            return NOT_AD

        return IS_AD
    }

    inner class ViewHolder(val binding: LayoutNewsItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindTo (newsItem: NewsItem) {
            binding.apply {
                root.addSelectableItemEffect()
                txvTitle.text = newsItem.title
                txvDate.text = newsItem.date
                txvType.text = newsItem.type
                imvCover.load(newsItem.cover) {
                    transformations(RoundedCornersTransformation(55f))
                    memoryCachePolicy(CachePolicy.ENABLED)
                    diskCachePolicy(CachePolicy.ENABLED)
                }
                root.setOnClickListener {
                    it.context.startActivity(
                        Intent(context, NewsActivity::class.java
                        ).apply {
                            putExtra(NewsItem.REQUESTED_NEWS, newsItem.reference)
                        }
                    )
                }
            }
        }

    }

    inner class ViewHolderAd(val binding: AdUnifiedNewsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindTo (nativeAd: NativeAd) {
            binding.apply {
                if (nativeAd.icon != null)
                    if (nativeAd.icon!!.drawable != null) {
                        adAppIcon.load(nativeAd.icon!!.drawable)
                        nativeView.iconView = binding.adAppIcon
                    }

                if (nativeAd.headline != null) {
                    adHeadline.text = nativeAd.headline
                    nativeView.headlineView = binding.adHeadline
                }
                else {
                    binding.adHeadline.visibility = View.GONE
                }

                if (nativeAd.callToAction != null) {
                    adCallToAction.text = nativeAd.callToAction
                    nativeView.callToActionView = binding.adCallToAction
                }
                else {
                    adCallToAction.visibility = View.GONE
                }

                if (nativeAd.store != null) {
                    adStore.text = nativeAd.store
                }
                adStore.setVisibility(nativeAd.store.isNotNullOrNotEmpty())

                if (nativeAd.price != null) {
                    adPrice.text = nativeAd.price
                }
                else {
                    adPrice.visibility = View.GONE
                }

                if (nativeAd.body != null) {
                    adBody.text = nativeAd.body
                    nativeView.bodyView = binding.adBody
                }
                else {
                    adBody.justifyInterWord()
                    adBody.visibility = View.GONE
                }

                nativeView.setNativeAd(nativeAd)
            }
        }
    }
}