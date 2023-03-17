package com.ead.project.dreamer.ui.record.adapters

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
import com.ead.project.dreamer.data.commons.Tools.Companion.round
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.utils.DreamerAsyncDiffUtil
import com.ead.project.dreamer.databinding.LayoutChapterRecordGridBinding
import com.ead.project.dreamer.databinding.LayoutChapterRecordLinearBinding

class ChapterRecordRecyclerViewAdapter (
    private val context: Context,
    private val isLinear : Boolean = false
) : RecyclerView.Adapter<ChapterRecordRecyclerViewAdapter.ViewHolder>() {

    private val dreamerAsyncDiffUtil = object : DreamerAsyncDiffUtil<Chapter>(){}

    private val differ = AsyncListDiffer(this,dreamerAsyncDiffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (isLinear)
            return ViewHolder(
                LayoutChapterRecordLinearBinding.inflate(
                    LayoutInflater.from(parent.context), parent,false))

        return ViewHolder(
            LayoutChapterRecordGridBinding.inflate(
                LayoutInflater.from(parent.context), parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chapter = differ.currentList[position]
        holder.bindTo(chapter)
    }

    fun submitList (list: List<Chapter>) {
        differ.submitList(list)
    }

    override fun getItemCount(): Int = differ.currentList.size

    inner class ViewHolder(val binding: androidx.viewbinding.ViewBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo (chapter: Chapter) {
            when(binding) {
                is LayoutChapterRecordLinearBinding -> bindToLinear(binding,chapter)
                is LayoutChapterRecordGridBinding -> bindToGrid(binding,chapter)
            }
        }

        private fun bindToLinear(binding: LayoutChapterRecordLinearBinding, chapter: Chapter) {
            binding.txvTitle.text = chapter.title
            binding.txvChapterRecord.text = chapter.number.toString()
            binding.txvTitle.gravity = Gravity.CENTER_VERTICAL
            binding.imvChapterProfile.alpha = 0.93f
            binding.root.addSelectableItemEffect()

            binding.imvChapterProfile.load(chapter.cover){
                crossfade(true)
                crossfade(500)
                transformations(
                    RoundedCornersTransformation(30f,0f,30f,0f)
                )
                memoryCachePolicy(CachePolicy.ENABLED)
                diskCachePolicy(CachePolicy.ENABLED)
            }
            val percent = ((chapter.currentSeen * 100f) / chapter.totalToSeen).round(2).toString()
            binding.txvCurrentProgress.text = context.getString(R.string.current_progress,percent)

            if (chapter.totalToSeen > 0) {
                binding.progressBarSeen.max = chapter.totalToSeen
            }
            binding.progressBarSeen.progress = chapter.currentSeen
            binding.imvDownload.setVisibility(chapter.isDownloaded())

            binding.root.setOnClickListener { Chapter.manageVideo(context, chapter) }
            binding.root.setOnLongClickListener {
                Chapter.callInAdapterSettings(context, chapter, isRecord = true)
                return@setOnLongClickListener true
            }
        }

        private fun bindToGrid(binding: LayoutChapterRecordGridBinding, chapter: Chapter) {
            binding.txvTitle.text = chapter.title
            binding.txvChapterRecord.text = chapter.number.toString()
            binding.txvTitle.gravity = Gravity.CENTER_VERTICAL
            binding.imvChapterProfile.alpha = 0.93f
            binding.root.addSelectableItemEffect()

            binding.imvChapterProfile.load(chapter.cover){
                crossfade(true)
                crossfade(500)
                transformations(
                    RoundedCornersTransformation(30f)
                )
                memoryCachePolicy(CachePolicy.ENABLED)
                diskCachePolicy(CachePolicy.ENABLED)
            }

            if (chapter.totalToSeen > 0) {
                binding.progressBarSeen.max = chapter.totalToSeen
            }
            binding.progressBarSeen.progress = chapter.currentSeen
            binding.imvDownload.setVisibility(chapter.isDownloaded())

            binding.root.setOnClickListener { Chapter.manageVideo(context,chapter) }
            binding.root.setOnLongClickListener {
                Chapter.callInAdapterSettings(context, chapter, isRecord = true)
                return@setOnLongClickListener true
            }
        }
    }
}