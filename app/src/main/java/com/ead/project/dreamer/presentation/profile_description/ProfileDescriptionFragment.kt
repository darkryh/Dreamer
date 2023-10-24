package com.ead.project.dreamer.presentation.profile_description

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ead.commons.lib.views.addSelectableItemEffect
import com.ead.commons.lib.views.justifyInterWord
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.database.model.AnimeProfile
import com.ead.project.dreamer.databinding.FragmentProfileDescriptionBinding
import com.ead.project.dreamer.presentation.profile.AnimeProfileViewModel
import com.ead.project.dreamer.presentation.profile.adapters.GenreRecyclerViewAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileDescriptionFragment : Fragment() {

    private val viewModel : AnimeProfileViewModel by viewModels()

    private val minLettersCharacter = 240
    private var descriptionOverloaded = false
    private var wrappedDescription = false

    var profileId : Int = -1

    private var _binding : FragmentProfileDescriptionBinding?=null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileDescriptionBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    private var count = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            recyclerViewGenres.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false)
            }
            textDescriptionContent.justifyInterWord()
            textDescriptionContent.addSelectableItemEffect()

        }

        viewModel.getAnimeProfile(profileId).observe(viewLifecycleOwner){ animeProfile ->

            if (animeProfile != null) {
                if (++count == 1) {
                    loadAnimeProfileDetails(animeProfile)
                }
            }

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}