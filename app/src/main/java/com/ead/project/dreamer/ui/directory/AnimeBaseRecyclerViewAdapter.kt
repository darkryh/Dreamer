package com.ead.project.dreamer.ui.directory

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.BlurTransformation
import coil.transform.RoundedCornersTransformation
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.database.model.AnimeBase
import com.ead.project.dreamer.data.utils.DataStore
import com.ead.project.dreamer.data.utils.ui.DreamerLayout
import com.ead.project.dreamer.databinding.LayoutAnimeBaseBinding


import com.ead.project.dreamer.ui.profile.AnimeProfileActivity

class AnimeBaseRecyclerViewAdapter(
    private var animeBaseList: List<AnimeBase>,
    private val context: Context
) : RecyclerView.Adapter<AnimeBaseRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutAnimeBaseBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val animeBase = animeBaseList[position]
        holder.bindTo(animeBase)
    }

    override fun getItemCount(): Int = animeBaseList.size

    inner class ViewHolder(val binding: LayoutAnimeBaseBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(animeBase: AnimeBase) {
            binding.txvTitleBase.text = animeBase.title
            binding.txvTypeBase.text = animeBase.type
            binding.txvYearBase.text = animeBase.year.toString()
            DreamerLayout.setClickEffect(binding.root,context)
            binding.imvCoverBase.load(animeBase.cover) {
                crossfade(true)
                crossfade(500)
                transformations(RoundedCornersTransformation(13f,13f,0f,0f))
                if (Constants.isGooglePolicyActivate()) {
                    if ("Manyuu Hikenchou" in animeBase.title || "Freezing" in animeBase.title ||
                        "Ikkitousen" in animeBase.title) {
                        transformations(
                            RoundedCornersTransformation(11f,11f,0f,0f),
                            BlurTransformation(context, 25f)
                        )
                    }
                }
            }

            binding.root.setOnClickListener {
                if (!DataStore
                        .readBoolean(Constants.WORK_PREFERENCE_CLICKED_PROFILE)) {
                    DataStore
                        .writeBooleanAsync(Constants.WORK_PREFERENCE_CLICKED_PROFILE,true)

                    it.context.startActivity(Intent(context,AnimeProfileActivity::class.java).apply {
                        putExtra(Constants.PREFERENCE_ID_BASE, animeBase.id)
                        putExtra(Constants.PREFERENCE_LINK, animeBase.reference)
                    })
                }
            }
        }
    }
}