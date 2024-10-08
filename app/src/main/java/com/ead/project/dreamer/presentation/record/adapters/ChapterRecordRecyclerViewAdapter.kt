package com.ead.project.dreamer.presentation.record.adapters

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.CachePolicy
import coil.transform.RoundedCornersTransformation
import com.ead.commons.lib.views.addSelectableItemEffect
import com.ead.commons.lib.views.setVisibility
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.data.util.system.round
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.utils.ui.mechanism.DreamerAsyncDiffUtil
import com.ead.project.dreamer.databinding.AdUnifiedBannerBinding
import com.ead.project.dreamer.databinding.LayoutChapterRecordGridBinding
import com.ead.project.dreamer.databinding.LayoutChapterRecordLinearBinding
import com.ead.project.dreamer.domain.databasequeries.GetProfile
import com.ead.project.dreamer.domain.servers.HandleChapter
import com.ead.project.dreamer.presentation.chapter.settings.ChapterSettingsFragment
import com.google.android.gms.ads.nativead.NativeAd
import kotlinx.coroutines.runBlocking

class ChapterRecordRecyclerViewAdapter (
    private val context: Context,
    private val isLinear : Boolean = false,
    private val handleChapter: HandleChapter,
    private val getProfile : GetProfile,
) : RecyclerView.Adapter<ChapterRecordRecyclerViewAdapter.ViewHolder>() {

    companion object {
        const val IS_AD = 1
        const val NOT_AD = 0
    }

    private val dreamerAsyncDiffUtil = object : DreamerAsyncDiffUtil<Any>(){}
    private val differ = AsyncListDiffer(this,dreamerAsyncDiffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == IS_AD) {
            return ViewHolder(
                AdUnifiedBannerBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
        if (isLinear)
            return ViewHolder(
                LayoutChapterRecordLinearBinding.inflate(
                    LayoutInflater.from(parent.context), parent,false))

        return ViewHolder(
            LayoutChapterRecordGridBinding.inflate(
                LayoutInflater.from(parent.context), parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when(val any = differ.currentList[position]) {
            is Chapter -> holder.bindTo(any)
            is NativeAd -> holder.bindTo(any)
        }

    }

    override fun getItemViewType(position: Int): Int {
        if (differ.currentList[position] !is NativeAd)
            return NOT_AD

        return IS_AD
    }

    fun submitList (list: List<Any>) {
        differ.submitList(list)
    }

    override fun getItemCount(): Int = differ.currentList.size

    inner class ViewHolder(val binding: androidx.viewbinding.ViewBinding) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var bannerViewHolderAd: BannerViewHolderAd

        fun bindTo(chapter: Chapter) {
            when (binding) {
                is LayoutChapterRecordLinearBinding -> bindToLinear(binding, chapter)
                is LayoutChapterRecordGridBinding -> bindToGrid(binding, chapter)
            }
        }

        fun bindTo(nativeAd: NativeAd) {
            when (binding) {
                is AdUnifiedBannerBinding -> {
                    bannerViewHolderAd = BannerViewHolderAd(binding)
                    bannerViewHolderAd.bindTo(nativeAd)
                }
            }
        }

        private fun bindToLinear(binding: LayoutChapterRecordLinearBinding, chapter: Chapter) {
            binding.textTitle.text = chapter.title
            binding.textChapterRecord.text = chapter.number.toString()
            binding.textTitle.gravity = Gravity.CENTER_VERTICAL
            binding.imageChapterProfile.alpha = 0.93f
            binding.root.addSelectableItemEffect()

            binding.imageChapterProfile.load(chapter.cover.ifBlank { runBlocking { getProfile(chapter.idProfile) }?.profilePhoto }) {
                crossfade(true)
                crossfade(500)
                transformations(
                    RoundedCornersTransformation(30f, 0f, 30f, 0f)
                )
                memoryCachePolicy(CachePolicy.ENABLED)
                diskCachePolicy(CachePolicy.ENABLED)
            }
            val percent =
                ((chapter.currentProgress * 100f) / chapter.totalProgress).round(2).toString()
            binding.textCurrentProgress.text = context.getString(R.string.current_progress, percent)

            if (chapter.totalProgress > 0) {
                binding.progressBarSeen.max = chapter.totalProgress
            }
            binding.progressBarSeen.progress = chapter.currentProgress
            binding.imageDownload.setVisibility(chapter.isDownloaded())

            binding.root.setOnClickListener { handleChapter(context, chapter) }
            binding.root.setOnLongClickListener {
                ChapterSettingsFragment.launch(context, chapter, true)
                return@setOnLongClickListener true
            }
        }

        private fun bindToGrid(binding: LayoutChapterRecordGridBinding, chapter: Chapter) {
            binding.textTitle.text = chapter.title
            binding.textChapterRecord.text = chapter.number.toString()
            binding.textTitle.gravity = Gravity.CENTER_VERTICAL
            binding.imageChapterProfile.alpha = 0.93f
            binding.root.addSelectableItemEffect()

            binding.imageChapterProfile.load(chapter.cover.ifBlank { runBlocking { getProfile(chapter.idProfile) }?.profilePhoto }) {
                crossfade(true)
                crossfade(500)
                transformations(
                    RoundedCornersTransformation(30f)
                )
                memoryCachePolicy(CachePolicy.ENABLED)
                diskCachePolicy(CachePolicy.ENABLED)
            }

            if (chapter.totalProgress > 0) {
                binding.progressBarSeen.max = chapter.totalProgress
            }
            binding.progressBarSeen.progress = chapter.currentProgress
            binding.imageDownload.setVisibility(chapter.isDownloaded())

            binding.root.setOnClickListener { handleChapter(context, chapter) }
            binding.root.setOnLongClickListener {
                ChapterSettingsFragment.launch(context, chapter, true)
                return@setOnLongClickListener true
            }
        }
    }
}