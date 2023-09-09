package com.ead.project.dreamer.presentation.home.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.CachePolicy
import coil.transform.RoundedCornersTransformation
import com.ead.commons.lib.views.addSelectableItemEffect
import com.ead.project.dreamer.app.data.util.system.toPixels
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.database.model.ChapterHome
import com.ead.project.dreamer.data.utils.ui.mechanism.DreamerAsyncDiffUtil
import com.ead.project.dreamer.databinding.LayoutChapterHomeLinearBinding
import com.ead.project.dreamer.domain.downloads.LaunchDownload
import com.ead.project.dreamer.domain.servers.HandleChapter


class ChapterLinearRecyclerViewAdapter(
    private val context: Context,
    private val handleChapter: HandleChapter,
    private val launchDownload: LaunchDownload,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dreamerAsyncDiffUtil = object : DreamerAsyncDiffUtil<Any>(){}
    private val differ = AsyncListDiffer(this,dreamerAsyncDiffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutChapterHomeLinearBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chapter = differ.currentList[position] as ChapterHome
        val chapterHolder = holder as ViewHolder
        chapterHolder.bindTo(chapter)
    }

    fun submitList (list: List<Any>) = differ.submitList(list)

    override fun getItemCount(): Int = differ.currentList.size

    inner class ViewHolder(val binding: LayoutChapterHomeLinearBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindTo (chapter: ChapterHome) {
            settingsLayouts(chapter)
            settingImages(chapter)
            settingFunctionality(chapter)
        }

        private fun settingsLayouts(chapter: ChapterHome) {
            binding.txvTitle.text = chapter.title
            binding.txvType.text = chapter.type
            binding.txvChapterNumber.text = chapter.chapterNumber.toString()
            binding.root.addSelectableItemEffect()
        }

        private fun settingImages(chapter: ChapterHome) {
            binding.imvCover.load(chapter.chapterCover){
                crossfade(true)
                crossfade(500)
                transformations(RoundedCornersTransformation(15f.toPixels()))
                memoryCachePolicy(CachePolicy.ENABLED)
                diskCachePolicy(CachePolicy.ENABLED)
            }
        }

        private fun settingFunctionality(chapter: ChapterHome) {
            val chapterSender = Chapter(0, 0, chapter.title,
                chapter.chapterCover, chapter.chapterNumber,chapter.reference)

            binding.root.setOnClickListener { handleChapter(context,chapterSender) }
            binding.root.setOnLongClickListener {
                launchDownload(chapterSender)
                return@setOnLongClickListener true
            }
        }
    }
}