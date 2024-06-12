package com.ead.project.dreamer.presentation.player.content.chapterselector

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.data.util.system.hideSystemUI
import com.ead.project.dreamer.app.data.util.system.setStateExpanded
import com.ead.project.dreamer.app.data.util.system.setWidthMatchParent
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.databinding.BottomModalChapterSelectorBinding
import com.ead.project.dreamer.presentation.player.PlayerViewModel
import com.ead.project.dreamer.presentation.profile.adapters.ChapterRecyclerViewAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChapterSelectorFragment : BottomSheetDialogFragment() {

    private val viewModel : PlayerViewModel by viewModels()

    var playerView : PlayerView?= null
    lateinit var chapter : Chapter
    private lateinit var adapterChapters : ChapterRecyclerViewAdapter

    var isHorizontal = false

    private var _binding : BottomModalChapterSelectorBinding?= null
    private val binding get() = _binding!!

    override fun onStart() {
        super.onStart()
        setWidthMatchParent()
        if (isHorizontal) {
            setStateExpanded()
        }
    }

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
        binding.textChapterList.text = getString(R.string.title_series,chapter.title)
    }

    private fun setupData() {
        chapter = viewModel.playerPreferences.getChapter()?:return
        binding.recyclerViewChapters.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapterChapters = ChapterRecyclerViewAdapter(requireActivity() as Context, handleChapter = viewModel.handleChapter, getProfile = viewModel.getProfile)
            adapter = adapterChapters
            setupRecords()
        }
    }

    private fun setupRecords() {
        viewModel.getChaptersFromProfile(chapter.idProfile).observe(viewLifecycleOwner) {
            adapterChapters.submitList(it)
            binding.recyclerViewChapters.layoutManager?.scrollToPosition(chapter.number-1)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.hideSystemUI()
    }

    companion object {

        private const val FRAGMENT = "CHAPTER_SELECTOR_FRAGMENT"

        fun launch(
            context: Context,
            isHorizontal : Boolean,
            playerView: PlayerView,
            chapter: Chapter
        ) {
            val fragmentManager = (context as FragmentActivity).supportFragmentManager
            val chapterSelectorFragment = ChapterSelectorFragment()
            chapterSelectorFragment.apply {
                this.isHorizontal = isHorizontal
                this.playerView = playerView
                this.chapter = chapter
                chapterSelectorFragment.show(fragmentManager, FRAGMENT)
            }
        }
    }
}