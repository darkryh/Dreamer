package com.ead.project.dreamer.presentation.download.adapter

import android.annotation.SuppressLint
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
import com.ead.project.dreamer.app.data.util.Apk
import com.ead.project.dreamer.app.data.util.system.round
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.models.Download
import com.ead.project.dreamer.data.utils.ui.mechanism.DreamerAsyncDiffUtil
import com.ead.project.dreamer.databinding.LayoutDownloadBinding
import com.ead.project.dreamer.domain.ChapterUseCase
import com.ead.project.dreamer.domain.servers.HandleChapter
import kotlinx.coroutines.runBlocking
import java.io.File

class DownloadRecyclerViewAdapter(
    private val context: Context,
    private val chapterUseCase: ChapterUseCase,
    private val handleChapter: HandleChapter
) : RecyclerView.Adapter<DownloadRecyclerViewAdapter.ViewHolder>() {

    private val downloadManager : DownloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    private val dreamerAsyncDiffUtil = object : DreamerAsyncDiffUtil<Download>(){}
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

    @SuppressLint("NotifyDataSetChanged")
    fun submitList (list: List<Download>) {
        differ.submitList(list)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chapterDownload = differ.currentList[position]
        holder.bindTo(chapterDownload)
    }

    override fun getItemCount(): Int = differ.currentList.size

    inner class ViewHolder(val binding: LayoutDownloadBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(download: Download) {
            binding.textTitle.text = download.title
            binding.textLetter.text = download.title[0].uppercase()
            binding.textChapterNumber.text =
                if (download.number != -1) context.getString(R.string.chapter_number_short,download.number.toString()) else context.getString(R.string.update)
            val percent = ((download.current * 100f) / download.total).round(2).toString()
            binding.textState.text = context.getString(R.string.current_percent,percent)
            binding.root.addSelectableItemEffect()
            binding.imageCancel.addSelectableItemEffect()

            when(download.state) {
                DownloadManager.STATUS_SUCCESSFUL -> binding.textState.setTextColor(context.getColor(R.color.green))
                DownloadManager.STATUS_FAILED -> binding.textState.setTextColor(context.getColor(R.color.red))
                else -> binding.textState.setTextColor(context.getColor(R.color.orange_peel_dark))
            }

            binding.imageCancel.setVisibility(download.isInProgress())
            binding.progressBar.setVisibilityReverse(download.isInProgress())
            binding.imageCancel.setOnClickListener { downloadManager.remove(download.id) }

            binding.root.setOnClickListener {
                if (download.state == DownloadManager.STATUS_SUCCESSFUL) {
                    when(download.type) {
                        Download.DOWNLOAD_TYPE_CHAPTER -> {
                            runBlocking {
                                val chapter : Chapter? =
                                    chapterUseCase.getChapter.fromTitleAndNumber(download.title,download.number)

                                if (chapter != null) handleChapter(context,chapter)
                                else activity.showShortToast(context.getString(R.string.error_chapter_not_founded))
                            }
                        }
                        Download.DOWNLOAD_TYPE_UPDATE -> {
                            val downloadFile : File = download.toApkFile()
                            if (downloadFile.exists()) Apk.install(context,downloadFile)
                            else activity.showShortToast(context.getString(R.string.error_file_not_founded))
                        }
                    }
                }
                else activity.showShortToast(context.getString(R.string.warning_chapter_status_in_progress))
            }
        }
    }
}
