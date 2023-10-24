package com.ead.project.dreamer.presentation.player.cast.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.ead.commons.lib.views.setVisibility
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.utils.ui.mechanism.DreamerAsyncDiffUtil
import com.ead.project.dreamer.databinding.LayoutChapterCastingBinding
import com.ead.project.dreamer.domain.servers.HandleChapter

class ChapterCastingRecyclerViewAdapter (
    private val context: Context,
    private val handleChapter: HandleChapter
) : RecyclerView.Adapter<ChapterCastingRecyclerViewAdapter.ViewHolder>() {

    private val dreamerAsyncDiffUtil = object : DreamerAsyncDiffUtil<Chapter>(){}

    private val differ = AsyncListDiffer(this,dreamerAsyncDiffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutChapterCastingBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chapter = differ.currentList[position]
        holder.bindTo(chapter)
    }

    fun submitList (list: List<Chapter>) = differ.submitList(list)

    override fun getItemCount(): Int = differ.currentList.size

    inner class ViewHolder(val binding: LayoutChapterCastingBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo (chapter: Chapter) {
            settingsLayouts(chapter)
            settingImages(chapter)
            settingProgress(chapter)
            settingFunctionality(chapter)
        }

        private fun settingsLayouts(chapter: Chapter) {
            binding.textTitle.text = context
                .getString(R.string.chapter_number,chapter.number.toString())
        }

        private fun settingImages(chapter: Chapter) {
            binding.imageChapterProfile.load(chapter.cover){
                crossfade(true)
                crossfade(500)
                transformations(RoundedCornersTransformation(0f,16f,0f,16f))
            }
        }

        private fun settingProgress(chapter: Chapter) {
            if (chapter.totalProgress > 0) {
                binding.progressBarSeen.max = chapter.totalProgress
            }
            binding.progressBarSeen.progress = chapter.currentProgress
            binding.imageDownload.setVisibility(chapter.isDownloaded())
        }

        private fun settingFunctionality(chapter: Chapter) {
            binding.root.setOnClickListener { handleChapter(context,chapter) }
        }
    }
}