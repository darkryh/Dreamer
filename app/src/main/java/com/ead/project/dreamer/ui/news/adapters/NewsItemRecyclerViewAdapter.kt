package com.ead.project.dreamer.ui.news.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.commons.Tools.Companion.justifyInterWord
import com.ead.project.dreamer.data.database.model.NewsItem
import com.ead.project.dreamer.data.utils.DreamerAsyncDiffUtil
import com.ead.project.dreamer.data.utils.ui.DreamerLayout
import com.ead.project.dreamer.databinding.AdUnifiedNewsItemBinding
import com.ead.project.dreamer.databinding.LayoutNewsItemBinding
import com.ead.project.dreamer.ui.news.NewsActivity
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
            val newsItem = differ.currentList[position] as NewsItem
            val newsItemHolder = holder as ViewHolder
            newsItemHolder.bindTo(newsItem)
        }
        else {
            val nativeAd = differ.currentList[position] as NativeAd
            val nativeHolder = holder as ViewHolderAd
            nativeHolder.bindTo(nativeAd)
        }
    }

    fun submitList (list: List<Any>) {
        differ.submitList(list)
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun getItemViewType(position: Int): Int {
        if (differ.currentList[position] !is NativeAd)
            return NOT_AD

        return IS_AD
    }

    inner class ViewHolder(val binding: LayoutNewsItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindTo (newsItem: NewsItem) {
            DreamerLayout.setClickEffect(binding.root,context)
            binding.txvTitle.text = newsItem.title
            binding.txvDate.text = newsItem.date
            binding.txvType.text = newsItem.type
            binding.imvCover.load(newsItem.cover) {
                transformations(RoundedCornersTransformation(55f))
            }

            binding.root.setOnClickListener {
                it.context.startActivity(
                    Intent(
                        context,
                        NewsActivity::class.java
                    ).apply {
                        putExtra(Constants.REQUESTED_NEWS, newsItem.reference)
                    })
            }
        }

    }

    inner class ViewHolderAd(val binding: AdUnifiedNewsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindTo (nativeAd: NativeAd) {

            if (nativeAd.icon!= null)
                if (nativeAd.icon!!.drawable != null) {
                    binding.adAppIcon.load(nativeAd.icon!!.drawable)
                    binding.nativeView.iconView = binding.adAppIcon
                }

            if (nativeAd.headline != null) {
                binding.adHeadline.text = nativeAd.headline
                binding.nativeView.headlineView = binding.adHeadline
            }
            else {
                binding.adHeadline.visibility = View.GONE
            }

            if (nativeAd.callToAction != null) {
                binding.adCallToAction.text = nativeAd.callToAction
                binding.nativeView.callToActionView = binding.adCallToAction
            }
            else {
                binding.adCallToAction.visibility = View.GONE
            }

            if (nativeAd.store != null) {
                binding.adStore.text = nativeAd.store
            }
            else {
                binding.adStore.visibility = View.GONE
            }

            if (nativeAd.price != null) {
                binding.adPrice.text = nativeAd.price
            }
            else {
                binding.adPrice.visibility = View.GONE
            }

            if (nativeAd.body != null) {
                binding.adBody.text = nativeAd.body
                binding.nativeView.bodyView = binding.adBody
            }
            else {
                binding.adBody.justifyInterWord()
                binding.adBody.visibility = View.GONE
            }

            binding.nativeView.setNativeAd(nativeAd)
        }
    }
}