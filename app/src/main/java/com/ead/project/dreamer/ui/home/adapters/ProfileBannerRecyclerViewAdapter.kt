package com.ead.project.dreamer.ui.home.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.ead.project.dreamer.app.model.Publicity
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.commons.Tools.Companion.justifyInterWord
import com.ead.project.dreamer.data.database.model.AnimeProfile
import com.ead.project.dreamer.data.utils.DreamerAsyncDiffUtil
import com.ead.project.dreamer.databinding.AdAppProfileBinding
import com.ead.project.dreamer.databinding.LayoutProfileRecommendationsBinding
import com.ead.project.dreamer.ui.profile.AnimeProfileActivity


class ProfileBannerRecyclerViewAdapter  (private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val CUSTOM_AD = 1
        const val NOT_AD = 0
    }

    private val dreamerAsyncDiffUtil = object : DreamerAsyncDiffUtil<Any>() {}

    private val differ = AsyncListDiffer(this, dreamerAsyncDiffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == CUSTOM_AD) {
            return ViewHolderAppAd(
                AdAppProfileBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
        return ViewHolder(
            LayoutProfileRecommendationsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == ChapterHomeRecyclerViewAdapter.NOT_AD) {
            val profile = differ.currentList[position] as AnimeProfile
            val profileHolder = holder as ViewHolder
            profileHolder.bindTo(profile)
        }
        else {
            val profile = differ.currentList[position] as Publicity
            val profileHolder = holder as ViewHolderAppAd
            profileHolder.bindTo(profile)
        }
    }

    fun submitList(list: List<Any>) {
        differ.submitList(list)
    }



    override fun getItemCount(): Int = differ.currentList.size

    override fun getItemViewType(position: Int): Int {
        if (differ.currentList[position] !is Publicity)
            return NOT_AD

        return CUSTOM_AD
    }

    inner class ViewHolder(val binding: LayoutProfileRecommendationsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val stringBuilder = StringBuilder()

        fun bindTo(animeProfile: AnimeProfile) {
            settingImage(animeProfile)
            settingContent(animeProfile)
            settingGenres(animeProfile)
            functionality(animeProfile)
        }

        private fun settingImage(animeProfile: AnimeProfile) {
            binding.imvCoverProfile.load(animeProfile.coverPhoto)
            binding.imvProfile.load(animeProfile.profilePhoto)  {
                transformations(CircleCropTransformation())
            }
        }

        private fun settingContent(animeProfile: AnimeProfile) {
            binding.txvTitle.text = animeProfile.title
            binding.txvDescription.text = animeProfile.description
            binding.txvDescription.justifyInterWord()

            when(animeProfile.description.length) {
                in 0..250-> {
                    binding.txvDescription.maxLines = 4
                }
            }

        }

        private fun settingGenres(animeProfile: AnimeProfile) {
            for (genre in animeProfile.genres) {
                stringBuilder.append(" · $genre  ")
            }
            binding.txvGenres.text = stringBuilder.toString()
            stringBuilder.clear()
        }

        private fun functionality(animeProfile: AnimeProfile) {
            binding.root.setOnClickListener {
                it.context.startActivity(
                    Intent(context, AnimeProfileActivity::class.java).apply {
                        putExtra(Constants.PREFERENCE_ID_BASE, animeProfile.id)
                        putExtra(Constants.PREFERENCE_LINK, animeProfile.reference)
                    }
                )
            }
        }
    }

    inner class ViewHolderAppAd(val binding: AdAppProfileBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val stringBuilder = StringBuilder()

        fun bindTo (publicity: Publicity) {
            settingImages(publicity)
            settingContent(publicity)
            settingGenres(publicity)
            functionality(publicity)
        }

        private fun settingImages(publicity: Publicity) {
            binding.imvCoverProfile.load(publicity.cover)
            if (publicity.icon != null) {
                binding.imvIcon.load(publicity.icon) {
                    transformations(CircleCropTransformation())
                }
            }
        }

        private fun settingContent(publicity: Publicity) {
            binding.txvTitle.text = publicity.title
            binding.txvDescription.text = publicity.content

            when(publicity.content.length) {
                in 0..250-> {
                    binding.txvDescription.maxLines = 4
                }
            }
        }

        private fun settingGenres(publicity: Publicity) {
            for (tag in publicity.tags) {
                stringBuilder.append(" · $tag  ")
            }
            binding.txvTags.text = stringBuilder.toString()
            stringBuilder.clear()
        }

        private fun functionality(publicity: Publicity) {
            binding.root.setOnClickListener {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(publicity.web_page)))
            }
        }
    }
}