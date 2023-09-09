package com.ead.project.dreamer.presentation.home.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.CachePolicy
import coil.transform.RoundedCornersTransformation
import com.ead.commons.lib.views.addSelectableItemEffect
import com.ead.commons.lib.views.setVisibility
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.data.util.system.isNotNullOrNotEmpty
import com.ead.project.dreamer.app.data.util.system.toPixels
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.database.model.ChapterHome
import com.ead.project.dreamer.data.utils.ui.mechanism.DreamerAsyncDiffUtil
import com.ead.project.dreamer.databinding.AdUnifiedChapterHomeBinding
import com.ead.project.dreamer.databinding.LayoutChapterHomeGridBinding
import com.ead.project.dreamer.domain.servers.HandleChapter
import com.ead.project.dreamer.presentation.chapter.settings.ChapterSettingsFragment
import com.google.android.gms.ads.nativead.NativeAd


class ChapterHomeRecyclerViewAdapter(
    private val context: Context,
    private val handleChapter: HandleChapter,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val IS_AD = 1
        const val NOT_AD = 0
    }

    private val dreamerAsyncDiffUtil = object : DreamerAsyncDiffUtil<Any>(){}
    private val differ = AsyncListDiffer(this,dreamerAsyncDiffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == IS_AD) {
            return ViewHolderAd(
                AdUnifiedChapterHomeBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
        return ViewHolder(
            LayoutChapterHomeGridBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == NOT_AD) {
            val chapter = differ.currentList[position] as ChapterHome
            val chapterHolder = holder as ViewHolder
            chapterHolder.bindTo(chapter)
        }
        else {
            val nativeAd = differ.currentList[position] as NativeAd
            val nativeHolder = holder as ViewHolderAd
            nativeHolder.bindTo(nativeAd)
        }
    }

    fun submitList (list: List<Any>) = differ.submitList(list)

    override fun getItemCount(): Int = differ.currentList.size

    override fun getItemViewType(position: Int): Int {
        if (differ.currentList[position] !is NativeAd)
            return NOT_AD

        return IS_AD
    }

    inner class ViewHolder(val binding: LayoutChapterHomeGridBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindTo (chapter: ChapterHome) {
            settingsLayouts(chapter)
            settingImages(chapter)
            settingFunctionality(chapter)
        }

        private fun settingsLayouts(chapter: ChapterHome) {
            binding.txvTitle.text = context.getString(R.string.title_home_chapter,chapter.title,chapter.chapterNumber)
            binding.txvType.text = chapter.type
            binding.root.addSelectableItemEffect()
        }

        private fun settingImages(chapter: ChapterHome) {
            binding.imvCover.load(chapter.chapterCover){
                crossfade(true)
                crossfade(500)
                transformations(RoundedCornersTransformation(12f.toPixels()))
                memoryCachePolicy(CachePolicy.ENABLED)
                diskCachePolicy(CachePolicy.ENABLED)
            }
        }

        private fun settingFunctionality(chapter: ChapterHome) {
            val chapterSender = Chapter(0, 0, chapter.title,
                chapter.chapterCover, chapter.chapterNumber,chapter.reference)

            binding.root.setOnClickListener { handleChapter(context,chapterSender) }
            binding.root.setOnLongClickListener {
                ChapterSettingsFragment.launch(context,chapterSender,true)
                return@setOnLongClickListener true
            }
        }
    }

    inner class ViewHolderAd(val binding: AdUnifiedChapterHomeBinding) :
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
            binding.adStore.setVisibility(nativeAd.store.isNotNullOrNotEmpty())

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
                binding.adBody.visibility = View.GONE
            }

            binding.nativeView.setNativeAd(nativeAd)
        }
    }
}