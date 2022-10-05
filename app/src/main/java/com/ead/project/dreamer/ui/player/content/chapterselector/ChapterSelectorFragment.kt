package com.ead.project.dreamer.ui.player.content.chapterselector

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.commons.Tools.Companion.hideSystemUI
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.databinding.BottomModalChapterSelectorBinding
import com.ead.project.dreamer.ui.player.PlayerViewModel
import com.ead.project.dreamer.ui.profile.adapters.ChapterRecyclerViewAdapter
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChapterSelectorFragment : BottomSheetDialogFragment() {

    override fun onStart() {
        super.onStart()
        val behavior = BottomSheetBehavior.from(requireView().parent as View)
        behavior.maxWidth = ViewGroup.LayoutParams.MATCH_PARENT
        if (isHorizontal) behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    private var _binding : BottomModalChapterSelectorBinding?= null
    private val binding get() = _binding!!
    private val playerViewModel : PlayerViewModel by viewModels()
    var playerView : StyledPlayerView?= null
    private lateinit var adapterChapters : ChapterRecyclerViewAdapter
    private var chapter = Chapter.get()!!
    var isHorizontal = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomModalChapterSelectorBinding.inflate(inflater,container,false)
        setupLayout()
        setupData()
        return binding.root
    }

    private fun setupLayout() {
        binding.txvChapterList.text = getString(R.string.title_series,chapter.title)
    }

    private fun setupData() {
        binding.rcvChapters.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapterChapters = ChapterRecyclerViewAdapter(activity as Context)
            adapter = adapterChapters
            setupRecords()
        }
    }

    private fun setupRecords() {
        playerViewModel.getChaptersFromProfile(chapter.idProfile).observe(viewLifecycleOwner) {
            adapterChapters.submitList(it)
            binding.rcvChapters.layoutManager?.scrollToPosition(chapter.chapterNumber-1)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().hideSystemUI()
    }

}