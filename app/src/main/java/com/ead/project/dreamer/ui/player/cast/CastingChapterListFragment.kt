package com.ead.project.dreamer.ui.player.cast

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.databinding.FragmentCastingChapterListBinding
import com.ead.project.dreamer.ui.player.PlayerViewModel
import com.ead.project.dreamer.ui.player.cast.adapters.ChapterCastingRecyclerViewAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CastingChapterListFragment : Fragment() {

    lateinit var chapter: Chapter

    private var _binding : FragmentCastingChapterListBinding?=null
    private val binding get() = _binding!!
    private val playerViewModel : PlayerViewModel by viewModels()
    private lateinit var adapter: ChapterCastingRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCastingChapterListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingsLayout()
    }

    private fun settingsLayout() {
        binding.rcvList.apply {
            layoutManager = LinearLayoutManager(context)
            this@CastingChapterListFragment.adapter = ChapterCastingRecyclerViewAdapter(activity as Context)
            adapter = this@CastingChapterListFragment.adapter
            setupChapters()
        }
    }

    private fun setupChapters() {
        playerViewModel.getChaptersFromProfile(chapter.idProfile).observe(viewLifecycleOwner) {
            adapter.submitList(it)
            binding.rcvList.layoutManager?.scrollToPosition(chapter.chapterNumber-1)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}