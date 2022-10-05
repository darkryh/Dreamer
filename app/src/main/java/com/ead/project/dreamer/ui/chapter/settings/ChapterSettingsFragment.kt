package com.ead.project.dreamer.ui.chapter.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ead.project.dreamer.app.DreamerApp
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.commons.Tools.Companion.parcelableArrayList
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.utils.DownloadManager
import com.ead.project.dreamer.data.utils.ui.DreamerLayout
import com.ead.project.dreamer.databinding.FragmentChapterSettingsBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ChapterSettingsFragment : BottomSheetDialogFragment() {

    private lateinit var chapterList: List<Chapter>
    private var downloadManager : DownloadManager?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { chapterList = it.parcelableArrayList(Constants.REQUESTED_CHAPTER_LIST)!! }
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
        DreamerLayout.setClickEffect(binding.lnFavorite,requireContext())
        binding.lnDownload.setOnClickListener {
            DreamerApp.showLongToast("Empezando Descarga..")
            downloadManager = object : DownloadManager(requireContext(),chapterList.toMutableList()) {
                override fun onCompleted() {
                    super.onCompleted()
                    downloadManager = null
                }
            }
            dismiss()
        }
        binding.lnFavorite.setOnClickListener { dismiss()  }
    }
}