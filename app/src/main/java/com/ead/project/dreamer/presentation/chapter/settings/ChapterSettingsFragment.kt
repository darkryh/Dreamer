package com.ead.project.dreamer.presentation.chapter.settings

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
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.databinding.FragmentChapterSettingsBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChapterSettingsFragment : BottomSheetDialogFragment() {

    private lateinit var viewModel: ChapterSettingsViewModel

    private lateinit var chapter: Chapter
    private var isChapter = false
    private var isRecords = false

    private var downloadStatusReverse =-1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[ChapterSettingsViewModel::class.java]
        arguments?.let {
            chapter = it.parcelable(Chapter.REQUESTED)?:return@let
            isChapter = it.getBoolean(IS_INSTANCE_A_CHAPTER)
            isRecords = !isChapter
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
        initVariables()
        initLayouts()
        settingAutomaticDownload()
        settingManualDownload()
        settingRecordsDelete()
        settingFavorites()
        settingChapterMode()
    }

    private fun initVariables() {
        downloadStatusReverse =
            if (chapter.state == Chapter.STATUS_DOWNLOADED) {
                Chapter.STATUS_STREAMING
            }
            else {
                Chapter.STATUS_DOWNLOADED
            }
    }

    private fun initLayouts() {
        viewModel.setDownloadMode(true)
        binding.apply {
            lnDownload.addSelectableItemEffect()
            lnManualDownload.addSelectableItemEffect()
            lnFavorite.addSelectableItemEffect()
            lnDownload.setVisibility(isChapter)
            lnChapterMode.setVisibility(isChapter)
            lnFavorite.setVisibility(isChapter)
            lnRecordDelete.setVisibility(isRecords)
        }
        settingDownloadState()
        settingFavoriteLayout()
    }

    private fun settingDownloadState() {
        binding.apply {
            if (chapter.state == Chapter.STATUS_DOWNLOADED) {
                imvChapterMode.setImageResource(R.drawable.ic_close_24)
                txvChapterMode.text = getString(R.string.chapter_mode_to_dont_downloaded)
            }
            else {
                imvChapterMode.setImageResource(R.drawable.ic_ready_24)
                txvChapterMode.text = getString(R.string.chapter_mode_to_downloaded)
            }
        }
    }

    private fun settingFavoriteLayout() {
        viewModel.getProfileIsFavorite(chapter.idProfile)?.let {
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
            viewModel.downloadUseCase.startDownload(chapter)
            dismiss()
        }
    }

    private fun settingManualDownload() {
        binding.lnManualDownload.setOnClickListener {
            viewModel.downloadUseCase.startManualDownload(chapter, activity as Context)
            dismiss()
        }
    }

    private fun settingRecordsDelete() {
        binding.lnRecordDelete.setOnClickListener {
            viewModel.deleteRecords(chapter.idProfile)
            dismiss()
        }
    }

    private fun settingFavorites() {
        binding.lnFavorite.setOnClickListener {
            viewModel.updateFavoriteProfile(chapter.idProfile)
            dismiss()
        }
    }

    private fun settingChapterMode() {
        binding.lnChapterMode.setOnClickListener {
            viewModel.updateChapter(chapter.copy(state = downloadStatusReverse))
            dismiss()
        }
    }

    companion object {
        const val FRAGMENT = "MENU_CHAPTER_SETTINGS"
        const val IS_INSTANCE_A_CHAPTER = "MENU_CHAPTER_IS_INSTANCE_A_CHAPTER"
    }
}