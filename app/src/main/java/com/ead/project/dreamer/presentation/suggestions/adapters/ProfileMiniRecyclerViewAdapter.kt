package com.ead.project.dreamer.presentation.suggestions.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.CachePolicy
import coil.transform.RoundedCornersTransformation
import com.ead.commons.lib.views.addSelectableItemEffect
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.data.util.system.toPixels
import com.ead.project.dreamer.data.database.model.AnimeProfile
import com.ead.project.dreamer.data.utils.ui.mechanism.DreamerAsyncDiffUtil
import com.ead.project.dreamer.databinding.LayoutAnimeProfileMiniBinding
import com.ead.project.dreamer.presentation.profile.AnimeProfileActivity

class ProfileMiniRecyclerViewAdapter(private val context: Context) :
    RecyclerView.Adapter<ProfileMiniRecyclerViewAdapter.ViewHolder>() {

    private val dreamerAsyncDiffUtil = object : DreamerAsyncDiffUtil<AnimeProfile>(){}

    private val differ = AsyncListDiffer(this,dreamerAsyncDiffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutAnimeProfileMiniBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val profile = differ.currentList[position]
        holder.bindTo(profile)
    }

    fun submitList (list: List<AnimeProfile>) {
        differ.submitList(list)
    }

    override fun getItemCount(): Int = differ.currentList.size

    inner class ViewHolder(val binding: LayoutAnimeProfileMiniBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo (animeProfile: AnimeProfile) {
            binding.apply {
                textTitle.text = animeProfile.title
                textState.text = animeProfile.state
                root.addSelectableItemEffect()
                imageCoverBase.load(animeProfile.profilePhoto){
                    crossfade(true)
                    crossfade(500)
                    transformations(RoundedCornersTransformation(8f.toPixels(),8f.toPixels(),0f,0f))
                    memoryCachePolicy(CachePolicy.ENABLED)
                    diskCachePolicy(CachePolicy.ENABLED)
                }

                textRating.text = context
                    .getString(R.string.ratingLayout,animeProfile.rating.toString())

                root.setOnClickListener {
                    context.startActivity(
                        Intent(context, AnimeProfileActivity::class.java).apply {
                            putExtra(AnimeProfileActivity.PREFERENCE_ID_BASE, animeProfile.id)
                            putExtra(AnimeProfileActivity.PREFERENCE_LINK, animeProfile.reference)
                        })
                }
            }
        }

    }
}