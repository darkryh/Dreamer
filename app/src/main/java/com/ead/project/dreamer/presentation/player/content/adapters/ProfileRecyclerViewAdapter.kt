package com.ead.project.dreamer.presentation.player.content.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.CachePolicy
import coil.transform.RoundedCornersTransformation
import com.ead.commons.lib.lifecycle.activity.onBack
import com.ead.commons.lib.views.addSelectableItemEffect
import com.ead.commons.lib.views.justifyInterWord
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.data.util.system.toPixels
import com.ead.project.dreamer.app.model.Requester
import com.ead.project.dreamer.data.database.model.AnimeProfile
import com.ead.project.dreamer.data.utils.ui.mechanism.DreamerAsyncDiffUtil
import com.ead.project.dreamer.databinding.AdUnifiedAnimeProfileBinding
import com.ead.project.dreamer.databinding.LayoutAnimeProfileBinding
import com.ead.project.dreamer.domain.PreferenceUseCase
import com.ead.project.dreamer.presentation.profile.AnimeProfileActivity
import com.google.android.gms.ads.nativead.NativeAd

class ProfileRecyclerViewAdapter(
    private val context: Context,
    private var isFromContent: Boolean = false,
    private var isFavoriteSegment : Boolean = false,
    preferenceUseCase: PreferenceUseCase
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val IS_AD = 1
        const val NOT_AD = 0
    }

    private val playerPreferences = preferenceUseCase.playerPreferences
    private val dreamerAsyncDiffUtil = object : DreamerAsyncDiffUtil<Any>(){}
    private val differ = AsyncListDiffer(this,dreamerAsyncDiffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == IS_AD) {
            return ProfileViewHolderAd(
                AdUnifiedAnimeProfileBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
        return ViewHolder(
            LayoutAnimeProfileBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == NOT_AD) {
            val animeProfile = differ.currentList[position] as AnimeProfile
            val profileHolder = holder as ViewHolder
            profileHolder.bindTo(animeProfile)
        }
        else {
            val nativeAd = differ.currentList[position] as NativeAd
            val nativeHolder = holder as ProfileViewHolderAd
            nativeHolder.bindTo(nativeAd)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (differ.currentList[position] !is NativeAd) return NOT_AD

        return IS_AD
    }

    fun submitList (list: List<Any>) = differ.submitList(list)

    override fun getItemCount(): Int = differ.currentList.size

    inner class ViewHolder(val binding: LayoutAnimeProfileBinding) : RecyclerView.ViewHolder(binding.root) {

        private val stringBuilder = StringBuilder()

        fun bindTo (animeProfile: AnimeProfile) {
            binding.txvTitle.text = animeProfile.title
            binding.txvContent.text = animeProfile.description
            binding.txvContent.justifyInterWord()
            binding.root.addSelectableItemEffect()
            binding.imvCoverBase.load(animeProfile.profilePhoto){
                crossfade(true)
                crossfade(500)
                transformations(RoundedCornersTransformation(15f.toPixels()))
                if (isFavoriteSegment) {
                    memoryCachePolicy(CachePolicy.ENABLED)
                    diskCachePolicy(CachePolicy.ENABLED)
                }
            }

            binding.txvRating.text = context
                .getString(R.string.ratingLayout,animeProfile.rating.toString())

            binding.root.setOnClickListener {
                if (!isFromContent) {
                    it.context.startActivity(
                        Intent(context, AnimeProfileActivity::class.java).apply {
                            putExtra(AnimeProfileActivity.PREFERENCE_ID_BASE, animeProfile.id)
                            putExtra(AnimeProfileActivity.PREFERENCE_LINK, animeProfile.reference)
                        })
                }
                else {
                    playerPreferences.setRequestingProfile(
                        Requester(
                            isRequesting = true,
                            profileId = animeProfile.id,
                            profileReference = animeProfile.reference?:return@setOnClickListener
                        )
                    )
                    (context as AppCompatActivity).onBack()
                }
            }

            for (genre in animeProfile.genres) {
                stringBuilder.append("·$genre  ")
            }
            binding.txvGenre.text = stringBuilder.toString()
            stringBuilder.clear()
        }
    }
}