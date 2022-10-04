package com.ead.project.dreamer.ui.player.cast

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import coil.load
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.databinding.FragmentCastingChapterBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CastingChapterFragment : Fragment() {

    lateinit var chapter: Chapter

    private var _binding : FragmentCastingChapterBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCastingChapterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingsLayout()
    }

    private fun settingsLayout() { binding.imvCover.load(chapter.chapterCover) }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}