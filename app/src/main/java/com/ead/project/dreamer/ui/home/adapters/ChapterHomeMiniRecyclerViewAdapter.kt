package com.ead.project.dreamer.ui.home.adapters

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
import com.ead.project.dreamer.data.database.model.ChapterHome
import com.ead.project.dreamer.data.utils.DataStore
import com.ead.project.dreamer.data.utils.DreamerAsyncDiffUtil
import com.ead.project.dreamer.databinding.LayoutChapterHomeMiniBinding
import com.ead.project.dreamer.ui.menuplayer.MenuPlayerFragment

class ChapterHomeMiniRecyclerViewAdapter(private val context: Context) :
    RecyclerView.Adapter<ChapterHomeMiniRecyclerViewAdapter.ViewHolder>() {

    private val dreamerAsyncDiffUtil = object : DreamerAsyncDiffUtil<ChapterHome>(){}

    private val differ = AsyncListDiffer(this,dreamerAsyncDiffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutChapterHomeMiniBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chapter = differ.currentList[position]
        holder.bindTo(chapter)
    }

    fun submitList (list: List<ChapterHome>) {
        differ.submitList(list)
    }

    override fun getItemCount(): Int = differ.currentList.size

    inner class ViewHolder(val binding: LayoutChapterHomeMiniBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo (chapter: ChapterHome) {
            settingsLayouts(chapter)
            settingImages(chapter)
            settingFunctionality(chapter)
        }

        private fun settingsLayouts(chapter: ChapterHome) {
            binding.txvTitle.text = chapter.title
            binding.txvType.text = chapter.type
            binding.txvChapterNumber.text = context
                .getString(R.string.chapter_number_min,chapter.chapterNumber.toString())
        }

        private fun settingImages(chapter: ChapterHome) {
            binding.imvCoverBase.load(chapter.chapterCover){
                crossfade(true)
                crossfade(500)
                transformations(RoundedCornersTransformation(11f,11f,0f,0f))
            }
        }

        private fun settingFunctionality(chapter: ChapterHome) {
            val chapterSender = Chapter(0, 0, chapter.title,
                chapter.chapterCover, chapter.chapterNumber,chapter.reference)

            binding.root.setOnClickListener {
                if (!DataStore.readBoolean(Constants.WORK_PREFERENCE_CLICKED_CHAPTER)) {
                    DataStore.writeBooleanAsync(Constants.WORK_PREFERENCE_CLICKED_CHAPTER,true)

                    val fragmentManager: FragmentManager = (context as FragmentActivity).supportFragmentManager
                    val data = Bundle().apply {
                        putParcelable(Constants.REQUESTED_CHAPTER, chapterSender)
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