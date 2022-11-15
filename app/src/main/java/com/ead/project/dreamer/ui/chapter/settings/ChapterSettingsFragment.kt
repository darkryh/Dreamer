package com.ead.project.dreamer.ui.chapter.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.DreamerApp
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.commons.Tools.Companion.parcelable
import com.ead.project.dreamer.data.commons.Tools.Companion.setVisibility
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.utils.DownloadManager
import com.ead.project.dreamer.data.utils.ui.DreamerLayout
import com.ead.project.dreamer.databinding.FragmentChapterSettingsBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChapterSettingsFragment : BottomSheetDialogFragment() {

    private lateinit var chapter: Chapter
    @Inject lateinit var downloadManager : DownloadManager
    @Inject lateinit var mDownloadManager: android.app.DownloadManager
    private var isCorrectData = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            chapter = it.parcelable(Constants.REQUESTED_CHAPTER)!!
            isCorrectData = it.getBoolean(Constants.IS_CORRECT_DATA_FROM_CHAPTER)
        }
    }

    private var _binding : FragmentChapterSettingsBinding?=null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChapterSettingsBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Constants.setDownloadMode(true)
        DreamerLayout.setClickEffect(binding.lnDownload,requireContext())
        DreamerLayout.setClickEffect(binding.lnManualDownload,requireContext())
        DreamerLayout.setClickEffect(binding.lnFavorite,requireContext())
        binding.lnDownload.setVisibility(isCorrectData)
        binding.lnDownload.setOnClickListener {
            downloadManager.init(chapter)
            dismiss()
        }
        binding.lnManualDownload.setOnClickListener {
            when(chapter.downloadState) {
                Chapter.STATUS_INITIALIZED -> {
                    if (downloadManager.isDataNotInProgress(chapter))
                        Chapter.launchServer(activity as Context, chapter, true)
                    else DreamerApp.showShortToast(getString(R.string.warning_chapter_status_in_progress))
                }
                Chapter.STATUS_RUNNING -> { DreamerApp.showShortToast(getString(R.string.warning_chapter_status_in_progress)) }
                Chapter.STATUS_PENDING -> { DreamerApp.showShortToast(getString(R.string.warning_chapter_status_pending)) }
                Chapter.STATUS_PAUSED -> { DreamerApp.showShortToast(getString(R.string.warning_chapter_status_paused)) }
                Chapter.STATUS_FAILED -> {
                    val data = Chapter.getDownloadList().singleOrNull{ it.second == chapter.id }
                    data?.let {
                        mDownloadManager.remove(it.first)
                        Chapter.removeFromDownloadList(it)
                    }
                    Chapter.launchServer(activity as Context, chapter, true)
                }
                Chapter.STATUS_COMPLETED -> { DreamerApp.showShortToast(getString(R.string.warning_chapter_status_completed)) }
            }
            dismiss()
        }
        binding.lnFavorite.setOnClickListener {
            DreamerApp.showLongToast(getString(R.string.in_develop))
            dismiss()
        }
    }
}