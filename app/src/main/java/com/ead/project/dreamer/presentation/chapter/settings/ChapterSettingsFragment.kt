package com.ead.project.dreamer.presentation.chapter.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.ead.commons.lib.lifecycle.parcelable
import com.ead.commons.lib.views.addSelectableItemEffect
import com.ead.commons.lib.views.setVisibility
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.databinding.FragmentChapterSettingsBinding
import com.ead.project.dreamer.presentation.server.menu.MenuServerFragment
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
            //lnDownload.addSelectableItemEffect()
            linearManualDownload.addSelectableItemEffect()
            linearFavorite.addSelectableItemEffect()
            //lnDownload.setVisibility(isChapter)
            linearChapterMode.setVisibility(isChapter)
            linearFavorite.setVisibility(isChapter)
            linearRecordDelete.setVisibility(isRecords)
        }
        settingDownloadState()
        settingFavoriteLayout()
    }

    private fun settingDownloadState() {
        binding.apply {
            if (chapter.state == Chapter.STATUS_DOWNLOADED) {
                imageChapterMode.setImageResource(R.drawable.ic_close_24)
                textChapterMode.text = getString(R.string.chapter_mode_to_dont_downloaded)
            }
            else {
                imageChapterMode.setImageResource(R.drawable.ic_ready_24)
                textChapterMode.text = getString(R.string.chapter_mode_to_downloaded)
            }
        }
    }

    private fun settingFavoriteLayout() {
        viewModel.getProfileIsFavorite(chapter.idProfile)?.let {
            if (it.isFavorite) {
                binding.imageFavorites.setImageResource(R.drawable.ic_heart_broken_24)
                binding.textFavorites.text = getString(R.string.remove_from_favorites)
            }
            else {
                binding.imageFavorites.setImageResource(R.drawable.ic_favorite_24)
                binding.textFavorites.text = getString(R.string.add_to_favorites)
            }
        }
    }

    private fun settingAutomaticDownload() {
        binding.linearDownload.setOnClickListener {
            viewModel.downloadUseCase.add(activity as Context,chapter)
            dismiss()
        }
    }

    private fun settingManualDownload() {
        binding.linearManualDownload.setOnClickListener {
            MenuServerFragment.launch(activity as Context,chapter,true)
            dismiss()
        }
    }

    private fun settingRecordsDelete() {
        binding.linearRecordDelete.setOnClickListener {
            viewModel.deleteRecords(chapter.idProfile)
            dismiss()
        }
    }

    private fun settingFavorites() {
        binding.linearFavorite.setOnClickListener {
            viewModel.updateFavoriteProfile(chapter.idProfile)
            dismiss()
        }
    }

    private fun settingChapterMode() {
        binding.linearChapterMode.setOnClickListener {
            viewModel.updateChapter(chapter.copy(state = downloadStatusReverse))
            dismiss()
        }
    }

    companion object {
        private const val FRAGMENT = "MENU_CHAPTER_SETTINGS"
        const val IS_INSTANCE_A_CHAPTER = "MENU_CHAPTER_IS_INSTANCE_A_CHAPTER"

        fun launch(context: Context, chapter: Chapter,isChapter : Boolean) {
            if (true) return
            val fragmentManager = (context as FragmentActivity).supportFragmentManager
            val chapterSettings = ChapterSettingsFragment()
            chapterSettings.apply {
                arguments = Bundle().apply {
                    putParcelable(Chapter.REQUESTED, chapter)
                    putBoolean(IS_INSTANCE_A_CHAPTER,isChapter)
                }
                show(fragmentManager, FRAGMENT)
            }
        }
    }
}