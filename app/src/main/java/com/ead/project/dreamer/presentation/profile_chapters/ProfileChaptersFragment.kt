package com.ead.project.dreamer.presentation.profile_chapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ead.commons.lib.lifecycle.observeOnce
import com.ead.commons.lib.views.addSelectableItemEffect
import com.ead.commons.lib.views.setVisibility
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.data.util.system.inputMethodManager
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.databinding.FragmentProfileChaptersBinding
import com.ead.project.dreamer.presentation.profile.AnimeProfileViewModel
import com.ead.project.dreamer.presentation.profile.adapters.ChapterRecyclerViewAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileChaptersFragment : Fragment() {

    private val viewModel : AnimeProfileViewModel by viewModels()

    private lateinit var adapter : ChapterRecyclerViewAdapter
    private var backUpChapters : List<Chapter> = listOf()

    private val inputMethodManager : InputMethodManager by lazy { requireContext().inputMethodManager }

    var profileId : Int = -1

    private var _binding : FragmentProfileChaptersBinding?= null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileChaptersBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            recyclerViewChapters.apply {

                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
                this@ProfileChaptersFragment.adapter = ChapterRecyclerViewAdapter(activity as Context, handleChapter = viewModel.handleChapter)
                adapter = this@ProfileChaptersFragment.adapter
                setupChapters()

            }
        }

        setupLayouts()
    }

    private fun setupLayouts() {
        binding.apply {

            currentChapter.addSelectableItemEffect()
            lnCurrentChapter.addSelectableItemEffect()

            imvSearch.setOnClickListener {

                it.setVisibility(false)
                txvCurrentChapter.setVisibility(false)
                edtChapter.setVisibility(true)
                imvClose.setVisibility(true)
                edtChapter.requestFocus()
                inputMethodManager.showSoftInput(edtChapter, InputMethodManager.SHOW_IMPLICIT)

            }

            imvClose.setOnClickListener {

                it.setVisibility(false)
                txvCurrentChapter.setVisibility(true)
                edtChapter.setVisibility(false)
                edtChapter.setText("")
                imvSearch.setVisibility(true)
                inputMethodManager.hideSoftInputFromWindow(root.windowToken,InputMethodManager.HIDE_IMPLICIT_ONLY)

            }
        }
    }

    private fun setupChapters() {
        binding.apply {
            viewModel.getChaptersFromProfile(profileId).observe(viewLifecycleOwner) { chapters ->

                backUpChapters = chapters
                adapter.submitList(chapters)

                val observableChapter = if (chapters.any { it.totalProgress > 0 }) chapters.maxByOrNull { it.lastDateSeen }
                else chapters.minByOrNull { it.number }


                bindingChapter(observableChapter?:return@observe)
                viewModel.updateChapterIfIsConsumed(observableChapter)
            }

            edtChapter.addTextChangedListener { _ ->

                if (backUpChapters.isEmpty()) {
                    return@addTextChangedListener
                }

                if (edtChapter.text.isEmpty()) {
                    adapter.submitList(backUpChapters)
                }
                else {
                    viewModel
                        .getChaptersFromNumber(profileId,getNumber(edtChapter.text.toString()))
                        .observeOnce(viewLifecycleOwner) {
                            adapter.submitList(it)
                        }
                }

            }
        }
    }

    private fun bindingChapter(chapter: Chapter) {
        binding.apply {

            txvCurrentChapter.text = getString(R.string.continue_watching_chapter_number, chapter.number.toString())

            currentChapter.setOnClickListener {

                viewModel.handleChapter(requireActivity(),chapter)

            }

        }
    }

    private fun getNumber(stringNumber : String) : Int {
        return try { stringNumber.toInt() }
        catch (e : Exception) { 0 }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}