package com.ead.project.dreamer.ui.profile.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.utils.DataStore
import com.ead.project.dreamer.data.utils.DreamerAsyncDiffUtil
import com.ead.project.dreamer.databinding.LayoutChapterBinding
import com.ead.project.dreamer.ui.menuplayer.MenuPlayerFragment

class ChapterRecyclerViewAdapter (private val context: Context) :
    RecyclerView.Adapter<ChapterRecyclerViewAdapter.ViewHolder>() {

    private val dreamerAsyncDiffUtil = object : DreamerAsyncDiffUtil<Chapter>(){}

    private val differ = AsyncListDiffer(this,dreamerAsyncDiffUtil)

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

    fun submitList (list: List<Chapter>) {
        differ.submitList(list)
    }

    override fun getItemCount(): Int = differ.currentList.size

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
            if (chapter.totalToSeen > 0) {
                binding.progressBarSeen.max = chapter.totalToSeen
            }
            binding.progressBarSeen.progress = chapter.currentSeen
        }

        private fun settingFunctionality(chapter: Chapter) {
            binding.chapterLayout.setOnClickListener {
                if (!DataStore.readBoolean(Constants.WORK_PREFERENCE_CLICKED_CHAPTER)) {
                    DataStore.writeBooleanAsync(Constants.WORK_PREFERENCE_CLICKED_CHAPTER,true)

                    val fragmentManager: FragmentManager = (context as FragmentActivity).supportFragmentManager
                    val data = Bundle()
                    data.apply {
                        putParcelable(Constants.REQUESTED_CHAPTER, chapter)
                    }
                    val chapterMenu = MenuPlayerFragment()
                    chapterMenu.apply {
                        arguments = data
                        show(fragmentManager, Constants.MENU_PLAYER_FRAGMENT)
                    }
                }
            }
        }
    }
}