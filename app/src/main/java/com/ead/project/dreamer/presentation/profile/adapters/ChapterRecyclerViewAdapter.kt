package com.ead.project.dreamer.presentation.profile.adapters

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
import com.ead.commons.lib.views.setVisibility
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.data.util.system.toPixels
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.utils.ui.mechanism.DreamerAsyncDiffUtil
import com.ead.project.dreamer.databinding.LayoutChapterBinding
import com.ead.project.dreamer.domain.servers.HandleChapter
import com.ead.project.dreamer.presentation.chapter.settings.ChapterSettingsFragment

class ChapterRecyclerViewAdapter(
    private val context: Context,
    private val editModeView : View? = null,
    private val handleChapter: HandleChapter
) : RecyclerView.Adapter<ChapterRecyclerViewAdapter.ViewHolder>() {

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

    fun submitList (list: List<Chapter>) = differ.submitList(list)

    fun removeEditMode() {
        /*isEditMode = false
        hideAnimation(editModeView)
        val temporalList = differ.currentList.map { item -> item.copy(isSelected = false) }
        differ.submitList(null)
        differ.submitList(temporalList)*/
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

    /*fun getSelectedList() = try {
        differ.currentList.filter { filter -> filter.isSelected }
        .sortedBy { it.number }.toMutableList()
    } catch (e : Exception) { emptyList<Chapter?>().toMutableList() }*/

    inner class ViewHolder(val binding: LayoutChapterBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo (chapter: Chapter) {
            settingsLayouts(chapter)
            settingImages(chapter)
            settingProgress(chapter)
            settingFunctionality(chapter)
        }

        private fun settingsLayouts(chapter: Chapter) {
            binding.txvTitle.text = context
                .getString(R.string.chapter_number,chapter.number.toString())
        }

        private fun settingImages(chapter: Chapter) {
            binding.imvChapterProfile.load(chapter.cover){
                crossfade(true)
                crossfade(500)
                transformations(RoundedCornersTransformation(8f.toPixels()))
            }
        }

        private fun settingProgress(chapter: Chapter) {
            if (chapter.totalProgress > 0) binding.progressBarSeen.max = chapter.totalProgress
            binding.progressBarSeen.progress = chapter.currentProgress
            binding.imvDownload.setVisibility(chapter.isDownloaded())
        }

        private fun settingFunctionality(chapter: Chapter) {
            binding.root.setOnClickListener {
                if (!isEditMode) handleChapter(context, chapter)
                else configureEditMode(chapter)
            }
            binding.root.setOnLongClickListener {
                //isEditMode = true
                //showAnimation(editModeView)
                //configureEditMode(chapter)
                ChapterSettingsFragment.launch(context,chapter,true)
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
            //error val
            /*chapter.isSelected = !chapter.isSelected
            if (chapter.isSelected) binding.root.strokeWidth = 4
            else binding.root.strokeWidth = 0*/
        }
    }
}