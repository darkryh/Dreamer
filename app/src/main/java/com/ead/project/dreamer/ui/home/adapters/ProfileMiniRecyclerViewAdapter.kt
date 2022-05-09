package com.ead.project.dreamer.ui.home.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.database.model.AnimeProfile
import com.ead.project.dreamer.data.utils.DataStore
import com.ead.project.dreamer.data.utils.DreamerAsyncDiffUtil
import com.ead.project.dreamer.data.utils.ui.DreamerLayout
import com.ead.project.dreamer.databinding.LayoutAnimeProfileMiniBinding
import com.ead.project.dreamer.ui.profile.AnimeProfileActivity

class ProfileMiniRecyclerViewAdapter (private val context: Context) :
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
            binding.txvTitle.text = animeProfile.title
            binding.txvState.text = animeProfile.state
            DreamerLayout.setClickEffect(binding.root,context)
            binding.imvCoverBase.load(animeProfile.profilePhoto){
                crossfade(true)
                crossfade(500)
                transformations(RoundedCornersTransformation(11f,11f,0f,0f))
            }

            binding.txvRating.text = context
                .getString(R.string.ratingLayout,animeProfile.rating.toString())

            binding.root.setOnClickListener {
                if (!DataStore.readBoolean(Constants.WORK_PREFERENCE_CLICKED_PROFILE_SUGGESTION)) {
                    DataStore
                        .writeBooleanAsync(Constants.WORK_PREFERENCE_CLICKED_PROFILE_SUGGESTION,true)

                    it.context.startActivity(
                        Intent(context, AnimeProfileActivity::class.java).apply {
                            putExtra(Constants.PREFERENCE_ID_BASE, animeProfile.id)
                            putExtra(Constants.PREFERENCE_LINK, animeProfile.reference)
                        })
                }
            }

        }

    }
}