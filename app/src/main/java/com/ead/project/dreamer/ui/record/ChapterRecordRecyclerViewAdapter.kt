package com.ead.project.dreamer.ui.record

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.BlurTransformation
import coil.transform.RoundedCornersTransformation
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.utils.DataStore
import com.ead.project.dreamer.data.utils.DreamerAsyncDiffUtil
import com.ead.project.dreamer.data.utils.ui.DreamerLayout
import com.ead.project.dreamer.databinding.LayoutChapterRecordBinding
import com.ead.project.dreamer.ui.menuplayer.MenuPlayerFragment

class ChapterRecordRecyclerViewAdapter (
    private val context: Context
) :
    RecyclerView.Adapter<ChapterRecordRecyclerViewAdapter.ViewHolder>() {

    private val dreamerAsyncDiffUtil = object : DreamerAsyncDiffUtil<Chapter>(){}

    private val differ = AsyncListDiffer(this,dreamerAsyncDiffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterRecordRecyclerViewAdapter.ViewHolder {
        return ViewHolder(
            LayoutChapterRecordBinding.inflate(
                LayoutInflater.from(parent.context), parent,false))
    }

    override fun onBindViewHolder(holder: ChapterRecordRecyclerViewAdapter.ViewHolder, position: Int) {
        val chapter = differ.currentList[position]
        holder.bindTo(chapter)
    }

    fun submitList (list: List<Chapter>) {
        differ.submitList(list)
    }

    override fun getItemCount(): Int = differ.currentList.size

    inner class ViewHolder(val binding: LayoutChapterRecordBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo (chapter: Chapter) {
            settingsLayouts(chapter)
            settingImages(chapter)
            settingProgress(chapter)
            settingFunctionality(chapter)
        }

        private fun settingsLayouts(chapter: Chapter) {
            binding.txvTitle.text = chapter.title
            binding.txvChapterRecord.text = chapter.chapterNumber.toString()
            binding.txvTitle.gravity = Gravity.CENTER_VERTICAL
            binding.imvChapterProfile.alpha = 0.93f
            DreamerLayout
                .setClickEffect(binding.root,context)
        }

        private fun settingImages(chapter: Chapter) {
            binding.imvChapterProfile.load(chapter.chapterCover){
                crossfade(true)
                crossfade(500)
                transformations(
                    BlurTransformation(context,1f)
                    , RoundedCornersTransformation(30f)
                )
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