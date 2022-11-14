package com.ead.project.dreamer.ui.profile.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.commons.Tools.Companion.setVisibility
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.utils.DreamerAsyncDiffUtil
import com.ead.project.dreamer.databinding.LayoutChapterBinding

class ChapterRecyclerViewAdapter (
    private val context: Context,
    private val editModeView : View? = null
) :
    RecyclerView.Adapter<ChapterRecyclerViewAdapter.ViewHolder>() {

    private val dreamerAsyncDiffUtil = object : DreamerAsyncDiffUtil<Chapter>(){}
    private val differ = AsyncListDiffer(this,dreamerAsyncDiffUtil)
    var isEditMode = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutChapterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chapter = differ.currentList[position]
        holder.bindTo(chapter)
    }

    fun submitList (list: List<Chapter>) { differ.submitList(list) }

    fun removeEditMode() {
        isEditMode = false
        hideAnimation(editModeView)
        val temporalList = differ.currentList.onEach { item -> item.selected = false }
        differ.submitList(null)
        differ.submitList(temporalList)
    }

    private fun hideAnimation(view: View?) {
        view?.let {
            val bottomDown: Animation = AnimationUtils.loadAnimation(
                context,
                R.anim.bottom_down
            )
            it.startAnimation(bottomDown)
            it.visibility = View.INVISIBLE
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    fun getDownloadList() = try {
        differ.currentList.filter { filter -> filter.selected }
        .sortedBy { it.chapterNumber }.toMutableList()
    } catch (e : Exception) { emptyList<Chapter?>().toMutableList() }

    inner class ViewHolder(val binding: LayoutChapterBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo (chapter: Chapter) {
            settingsLayouts(chapter)
            settingImages(chapter)
            settingProgress(chapter)
            settingFunctionality(chapter)
        }

        private fun settingsLayouts(chapter: Chapter) {
            binding.txvTitle.text = context
                .getString(R.string.chapter_number,chapter.chapterNumber.toString())
        }

        private fun settingImages(chapter: Chapter) {
            binding.imvChapterProfile.load(chapter.chapterCover){
                crossfade(true)
                crossfade(500)
                transformations(RoundedCornersTransformation(0f,16f,0f,16f))
            }
        }

        private fun settingProgress(chapter: Chapter) {
            if (chapter.totalToSeen > 0) binding.progressBarSeen.max = chapter.totalToSeen
            binding.progressBarSeen.progress = chapter.currentSeen
            binding.imvDownload.setVisibility(chapter.isDownloaded())
        }

        private fun settingFunctionality(chapter: Chapter) {
            binding.root.setOnClickListener {
                if (!isEditMode) Chapter.manageVideo(context, chapter)
                else configureEditMode(chapter)
            }
            binding.root.setOnLongClickListener {
                isEditMode = true
                showAnimation(editModeView)
                configureEditMode(chapter)
                return@setOnLongClickListener true
            }
        }

        private fun showAnimation(view: View?) {
            view?.let {
                val bottomUp: Animation = AnimationUtils.loadAnimation(
                    context,
                    R.anim.bottom_up
                )
                it.startAnimation(bottomUp)
                it.visibility = View.VISIBLE
            }
        }

        private fun configureEditMode(chapter: Chapter) {
            chapter.selected = !chapter.selected
            if (chapter.selected) binding.root.strokeWidth = 4
            else binding.root.strokeWidth = 0
        }
    }
}