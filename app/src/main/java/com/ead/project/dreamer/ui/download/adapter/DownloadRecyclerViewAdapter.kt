package com.ead.project.dreamer.ui.download.adapter

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.ead.commons.lib.lifecycle.activity.showShortToast
import com.ead.commons.lib.views.addSelectableItemEffect
import com.ead.commons.lib.views.setVisibility
import com.ead.commons.lib.views.setVisibilityReverse
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.commons.Tools
import com.ead.project.dreamer.data.commons.Tools.Companion.round
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.models.DownloadItem
import com.ead.project.dreamer.data.utils.DirectoryManager
import com.ead.project.dreamer.data.utils.DreamerAsyncDiffUtil
import com.ead.project.dreamer.databinding.LayoutDownloadBinding
import com.ead.project.dreamer.domain.ChapterUseCase
import kotlinx.coroutines.runBlocking
import java.io.File

class DownloadRecyclerViewAdapter (
    private val context: Context,
    private val chapterUseCase: ChapterUseCase
    ) : RecyclerView.Adapter<DownloadRecyclerViewAdapter.ViewHolder>() {

    private val downloadManager : DownloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    private val dreamerAsyncDiffUtil = object : DreamerAsyncDiffUtil<DownloadItem>(){}
    private val differ = AsyncListDiffer(this,dreamerAsyncDiffUtil)
    private val activity : Activity = context as Activity

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutDownloadBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    fun submitList (list: List<DownloadItem>) { differ.submitList(list) }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chapterDownload = differ.currentList[position]
        holder.bindTo(chapterDownload)
    }

    override fun getItemCount(): Int = differ.currentList.size

    inner class ViewHolder(val binding: LayoutDownloadBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(downloadItem: DownloadItem) {
            binding.txvTitle.text = downloadItem.title
            binding.txvLetter.text = downloadItem.title[0].uppercase()
            binding.txvChapterNumber.text =
                if (downloadItem.number != -1) context.getString(R.string.chapter_number_short,downloadItem.number.toString()) else context.getString(R.string.update)
            val percent = ((downloadItem.current * 100f) / downloadItem.total).round(2).toString()
            binding.txvState.text = context.getString(R.string.current_percent,percent)
            binding.root.addSelectableItemEffect()
            binding.imvCancel.addSelectableItemEffect()

            when(downloadItem.state) {
                DownloadManager.STATUS_SUCCESSFUL -> binding.txvState.setTextColor(context.getColor(R.color.green))
                DownloadManager.STATUS_FAILED -> binding.txvState.setTextColor(context.getColor(R.color.red))
                else -> binding.txvState.setTextColor(context.getColor(R.color.blue_light))
            }

            binding.imvCancel.setVisibility(downloadItem.isInProgress())
            binding.progressBar.setVisibilityReverse(downloadItem.isInProgress())
            binding.imvCancel.setOnClickListener { downloadManager.remove(downloadItem.id) }

            binding.root.setOnClickListener {
                if (downloadItem.state == DownloadManager.STATUS_SUCCESSFUL) {
                    when(downloadItem.type) {
                        DownloadItem.DOWNLOAD_TYPE_CHAPTER -> {
                            runBlocking {
                                val chapter : Chapter? =
                                    chapterUseCase.getChapter.fromTitleAndNumber(downloadItem.title,downloadItem.number)

                                if (chapter != null) Chapter.manageVideo(context,chapter)
                                else activity.showShortToast(context.getString(R.string.error_chapter_not_founded))
                            }
                        }
                        DownloadItem.DOWNLOAD_TYPE_UPDATE -> {
                            val downloadFile : File = DirectoryManager.getVersionFile(downloadItem.title)
                            if (downloadFile.exists()) Tools.installApk(context,downloadFile)
                            else activity.showShortToast(context.getString(R.string.error_file_not_founded))
                        }
                    }
                }
                else activity.showShortToast(context.getString(R.string.warning_chapter_status_in_progress))
            }
        }
    }
}
