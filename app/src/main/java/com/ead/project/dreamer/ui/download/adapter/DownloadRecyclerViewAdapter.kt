package com.ead.project.dreamer.ui.download.adapter

import android.app.DownloadManager
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.commons.Tools.Companion.round
import com.ead.project.dreamer.data.commons.Tools.Companion.setVisibility
import com.ead.project.dreamer.data.commons.Tools.Companion.setVisibilityReverse
import com.ead.project.dreamer.data.models.ChapterDownload
import com.ead.project.dreamer.data.utils.DreamerAsyncDiffUtil
import com.ead.project.dreamer.data.utils.ui.DreamerLayout
import com.ead.project.dreamer.databinding.LayoutDownloadBinding

class DownloadRecyclerViewAdapter (
    private val context: Context
    ) : RecyclerView.Adapter<DownloadRecyclerViewAdapter.ViewHolder>() {

    private val downloadManager : DownloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    private val dreamerAsyncDiffUtil = object : DreamerAsyncDiffUtil<ChapterDownload>(){}
    private val differ = AsyncListDiffer(this,dreamerAsyncDiffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutDownloadBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    fun submitList (list: List<ChapterDownload>) { differ.submitList(list) }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chapterDownload = differ.currentList[position]
        holder.bindTo(chapterDownload)
    }

    override fun getItemCount(): Int = differ.currentList.size

    inner class ViewHolder(val binding: LayoutDownloadBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(chapterDownload: ChapterDownload) {
            binding.txvTitle.text = chapterDownload.title
            binding.txvLetter.text = chapterDownload.title[0].uppercase()
            binding.txvChapterNumber.text = context.getString(R.string.chapter_number_short,chapterDownload.number.toString())
            val percent = ((chapterDownload.current * 100f) / chapterDownload.total).round(2).toString()
            binding.txvState.text = context.getString(R.string.current_percent,percent)
            DreamerLayout.setClickEffect(binding.root,context)
            DreamerLayout.setClickEffect(binding.imvCancel,context)
            when(chapterDownload.state) {
                DownloadManager.STATUS_SUCCESSFUL -> binding.txvState.setTextColor(context.getColor(R.color.green))
                DownloadManager.STATUS_FAILED -> binding.txvState.setTextColor(context.getColor(R.color.red))
                else -> binding.txvState.setTextColor(context.getColor(R.color.blue_light))
            }
            binding.root.setOnClickListener {}
            binding.imvCancel.setVisibility(chapterDownload.isInProgress())
            binding.progressBar.setVisibilityReverse(chapterDownload.isInProgress())
            binding.imvCancel.setOnClickListener { downloadManager.remove(chapterDownload.idDownload) }
        }
    }
}
