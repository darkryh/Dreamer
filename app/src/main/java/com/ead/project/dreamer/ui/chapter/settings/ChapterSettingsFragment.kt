package com.ead.project.dreamer.ui.chapter.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.ead.commons.lib.lifecycle.parcelable
import com.ead.commons.lib.views.addSelectableItemEffect
import com.ead.commons.lib.views.setVisibility
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.databinding.FragmentChapterSettingsBinding
import com.ead.project.dreamer.domain.DownloadManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChapterSettingsFragment : BottomSheetDialogFragment() {

    @Inject lateinit var downloadManager : DownloadManager
    private lateinit var chapterSettingsViewModel: ChapterSettingsViewModel
    private lateinit var chapter: Chapter
    private var isChapter = false
    private var isRecords = false
    private var downloadStatusReverse =-1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chapterSettingsViewModel = ViewModelProvider(this)[ChapterSettingsViewModel::class.java]
        arguments?.let {
            chapter = it.parcelable(Constants.REQUESTED_CHAPTER)!!
            isChapter = it.getBoolean(Constants.IS_CORRECT_DATA_FROM_CHAPTER_SETTINGS)
            isRecords = it.getBoolean(Constants.IS_CORRECT_DATA_FROM_RECORDS_SETTINGS)
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
        settingVariables()
        settingLayouts()
        settingAutomaticDownload()
        settingManualDownload()
        settingRecordsDelete()
        settingFavorites()
        settingChapterMode()
    }

    private fun settingVariables() {
        downloadStatusReverse =
            if (chapter.downloadState == Chapter.DOWNLOAD_STATUS_COMPLETED) Chapter.DOWNLOAD_STATUS_INITIALIZED
            else Chapter.DOWNLOAD_STATUS_COMPLETED
    }

    private fun settingLayouts() {
        Constants.setDownloadMode(true)
        binding.lnDownload.addSelectableItemEffect()
        binding.lnManualDownload.addSelectableItemEffect()
        binding.lnFavorite.addSelectableItemEffect()
        binding.lnDownload.setVisibility(isChapter)
        binding.lnChapterMode.setVisibility(isChapter)
        binding.lnFavorite.setVisibility(isChapter)
        binding.lnRecordDelete.setVisibility(isRecords)
        settingDownloadState()
        settingFavoriteLayout()
    }

    private fun settingDownloadState() {
        if (chapter.downloadState == Chapter.DOWNLOAD_STATUS_COMPLETED) {
            binding.imvChapterMode.setImageResource(R.drawable.ic_assignment_24)
            binding.txvChapterMode.text = getString(R.string.chapter_mode_to_dont_downloaded)
        }
        else {
            binding.imvChapterMode.setImageResource(R.drawable.ic_assignment_turned_in_24)
            binding.txvChapterMode.text = getString(R.string.chapter_mode_to_downloaded)
        }
    }

    private fun settingFavoriteLayout() {
        chapterSettingsViewModel.getProfileIsFavorite(chapter.idProfile)?.let {
            if (it.isFavorite) {
                binding.imvFavorites.setImageResource(R.drawable.ic_heart_broken_24)
                binding.txvFavorites.text = getString(R.string.remove_from_favorites)
            }
            else {
                binding.imvFavorites.setImageResource(R.drawable.ic_favorite_24)
                binding.txvFavorites.text = getString(R.string.add_to_favorites)
            }
        }
    }

    private fun settingAutomaticDownload() {
        binding.lnDownload.setOnClickListener {
            downloadManager.startDownload(chapter)
            dismiss()
        }
    }

    private fun settingManualDownload() {
        binding.lnManualDownload.setOnClickListener {
            downloadManager.startManualDownload(chapter, activity as Context)
            dismiss()
        }
    }

    private fun settingRecordsDelete() {
        binding.lnRecordDelete.setOnClickListener {
            chapterSettingsViewModel.deleteRecords(chapter.idProfile)
            dismiss()
        }
    }

    private fun settingFavorites() {
        binding.lnFavorite.setOnClickListener {
            chapterSettingsViewModel.updateFavoriteProfile(chapter.idProfile)
            dismiss()
        }
    }

    private fun settingChapterMode() {
        binding.lnChapterMode.setOnClickListener {
            chapter.downloadState = downloadStatusReverse
            chapterSettingsViewModel.updateChapter(chapter)
            dismiss()
        }
    }
}