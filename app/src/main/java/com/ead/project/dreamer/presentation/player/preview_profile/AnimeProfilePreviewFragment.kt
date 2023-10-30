package com.ead.project.dreamer.presentation.player.preview_profile

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import coil.load
import coil.size.Scale
import com.ead.commons.lib.views.setVisibility
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.database.model.AnimeProfile
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.system.extensions.toast
import com.ead.project.dreamer.databinding.FragmentAnimeProfilePreviewBinding
import com.ead.project.dreamer.presentation.profile.AnimeProfileActivity
import com.ead.project.dreamer.presentation.profile.AnimeProfileViewModel
import com.ead.project.dreamer.presentation.profile.adapters.GenreRecyclerViewAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AnimeProfilePreviewFragment : DialogFragment() {

    private lateinit var viewModel: AnimeProfileViewModel

    private var animeProfile : AnimeProfile?=null

    private var profileId = 1
    private var profileReference = "reference"

    private val minLettersCharacter = 240
    private var descriptionOverloaded = false
    private var wrappedDescription = false

    private var _binding: FragmentAnimeProfilePreviewBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[AnimeProfileViewModel::class.java]
        arguments?.apply {
            profileId = getInt(AnimeProfileActivity.PREFERENCE_ID)
            profileReference = getString(AnimeProfileActivity.PREFERENCE_LINK)?:return@apply
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentAnimeProfilePreviewBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(binding.root)
        return builder.create()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_anime_profile_preview, container, false)
        observeProfile()
        observeChapters()
        return view
    }

    private fun observeProfile() {
        viewModel.getAnimeProfile(profileId).observe(viewLifecycleOwner) { animeProfile ->
            if (animeProfile == null || this.animeProfile?.id == animeProfile.id) return@observe

            this.animeProfile = animeProfile

            if (viewModel.appBuildPreferences.isUnlockedVersion() || animeProfile.isAuthorizedData()) {
                configureChapters()
                loadAnimeProfileHeader(animeProfile)
                loadAnimeProfileDetails(animeProfile)
            }
            else {
                toast(getString(R.string.google_policies_message), Toast.LENGTH_SHORT)
                dismiss()
            }
        }
    }

    private fun observeChapters() {
        viewModel.getChaptersFromProfile(profileId).observe(viewLifecycleOwner) { chapters ->
            val isEmpty = chapters.isEmpty()
            binding.progressBarChapter.setVisibility(isEmpty)
            binding.textCurrentChapter.setVisibility(!isEmpty)
            if (isEmpty) return@observe

            val observableChapter = (if (chapters.any { it.totalProgress > 0 }) chapters.maxByOrNull { it.lastDateSeen }
            else chapters.minByOrNull { it.number })

            bindingChapter(observableChapter?:return@observe)
        }
    }

    private fun bindingChapter(chapter: Chapter) {
        binding.apply {

            textCurrentChapter.text = getString(R.string.continue_watching_chapter_number, chapter.number)

            linearCurrentChapter.setOnClickListener {

                viewModel.handleChapter(activity as Context, chapter)
                dismiss()

            }

        }
    }

    private fun loadAnimeProfileHeader(animeProfile: AnimeProfile) {
        binding.apply {
            imageCover.load(animeProfile.coverPhoto) {
                scale(Scale.FIT)
            }
            textTitle.text = animeProfile.title
        }
    }

    private fun loadAnimeProfileDetails(animeProfile: AnimeProfile) {
        binding.apply {

            recyclerViewGenres.adapter = GenreRecyclerViewAdapter(animeProfile.genres,requireContext())
            textState.text = animeProfile.state
            textDate.text = animeProfile.date
            textRating.text = animeProfile.rating.toString()
            ratingBar.rating = animeProfile.rating

            if (animeProfile.description.length > minLettersCharacter) {
                descriptionOverloaded = true
                wrappedDescription(animeProfile,false)
            } else {
                textDescriptionContent.text = animeProfile.description
            }

            textDescriptionContent.setOnClickListener{
                if (descriptionOverloaded) {
                    val params = textDescriptionContent.layoutParams as ViewGroup.LayoutParams
                    if (wrappedDescription) {
                        wrappedDescription(animeProfile,false)
                        params.height = resources.getDimensionPixelSize(R.dimen.dimen_115dp)
                    }
                    else {
                        wrappedDescription(animeProfile,true)
                        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    }
                    textDescriptionContent.layoutParams = params
                    wrappedDescription = !wrappedDescription
                }
            }

        }
    }

    private fun wrappedDescription(animeProfile: AnimeProfile, wrapContent : Boolean) {
        val text : String = animeProfile.description.substring(0,minLettersCharacter)
        binding.apply {

            if(!wrapContent) {
                textDescriptionContent
                    .text = HtmlCompat.fromHtml(
                    "$text<font color='Cyan'> <u>Mostrar más...</u></font>",
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
            }
            else {
                textDescriptionContent
                    .text = HtmlCompat.fromHtml(
                    "${animeProfile.description}<font color='Red'> <u>Mostrar menos...</u></font>",
                    HtmlCompat.FROM_HTML_MODE_LEGACY)
            }

        }
    }

    private fun configureChapters() = viewModel.configureChaptersData(profileId,profileReference)

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        private const val FRAGMENT = "PROFILE_PREVIEW_FRAGMENT"
        fun launch(
            context: Context,
            id : Int,
            reference : String
        ) {
            val fragmentManager: FragmentManager = (context as FragmentActivity).supportFragmentManager
            val animeProfilePreviewFragment = AnimeProfilePreviewFragment()
            animeProfilePreviewFragment.apply {
                arguments = Bundle().apply {
                    putInt(AnimeProfileActivity.PREFERENCE_ID, id)
                    putString(AnimeProfileActivity.PREFERENCE_LINK,reference)
                }
                show(fragmentManager, FRAGMENT)
            }
        }
    }
}