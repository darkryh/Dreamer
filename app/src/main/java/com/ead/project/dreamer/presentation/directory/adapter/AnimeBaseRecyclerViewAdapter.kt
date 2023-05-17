package com.ead.project.dreamer.presentation.directory.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.ead.commons.lib.views.addSelectableItemEffect
import com.ead.project.dreamer.app.data.util.system.toPixels
import com.ead.project.dreamer.data.database.model.AnimeBase
import com.ead.project.dreamer.databinding.LayoutAnimeBaseGridBinding
import com.ead.project.dreamer.databinding.LayoutAnimeBaseLinearBinding
import com.ead.project.dreamer.presentation.profile.AnimeProfileActivity

class AnimeBaseRecyclerViewAdapter(
    private var animeBaseList: List<AnimeBase>,
    private val context: Context,
    private val isSmallDevice : Boolean = false
) : RecyclerView.Adapter<AnimeBaseRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (isSmallDevice)
            return ViewHolder(LayoutAnimeBaseLinearBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,false))

        return ViewHolder(LayoutAnimeBaseGridBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val animeBase = animeBaseList[position]
        holder.bindTo(animeBase)
    }

    override fun getItemCount(): Int = animeBaseList.size

    inner class ViewHolder(val binding: androidx.viewbinding.ViewBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(animeBase: AnimeBase) {
            when(binding) {
                is LayoutAnimeBaseLinearBinding -> bindToLinear(binding,animeBase)
                is LayoutAnimeBaseGridBinding -> bindToGrid(binding,animeBase)
            }
        }

        private fun bindToLinear(binding: LayoutAnimeBaseLinearBinding, animeBase: AnimeBase) {
            binding.apply {
                txvTitleBase.text = animeBase.title
                txvTypeBase.text = animeBase.type
                txvYearBase.text = animeBase.year.toString()
                txvTypeBase.visibility = View.VISIBLE
                txvYearBase.visibility = View.VISIBLE
                root.addSelectableItemEffect()
                imvCoverBase.load(animeBase.cover) {
                    crossfade(true)
                    crossfade(500)
                    transformations(RoundedCornersTransformation(13f, 0f, 13f, 0f))
                }

                root.setOnClickListener {
                    context.startActivity(
                        Intent(
                            context,
                            AnimeProfileActivity::class.java
                        ).apply {
                            putExtra(AnimeProfileActivity.PREFERENCE_ID_BASE, animeBase.id)
                            putExtra(AnimeProfileActivity.PREFERENCE_LINK, animeBase.reference)
                        })
                }
            }
        }

        private fun bindToGrid(binding: LayoutAnimeBaseGridBinding, animeBase: AnimeBase) {
            binding.apply {
                txvTitleBase.text = animeBase.title
                txvTypeBase.text = animeBase.type
                txvYearBase.text = animeBase.year.toString()
                root.addSelectableItemEffect()
                imvCoverBase.load(animeBase.cover) {
                    crossfade(true)
                    crossfade(500)
                    transformations(RoundedCornersTransformation(5f.toPixels()))
                }

                root.setOnClickListener {
                    context.startActivity(
                        Intent(
                            context,
                            AnimeProfileActivity::class.java
                        ).apply {
                            putExtra(AnimeProfileActivity.PREFERENCE_ID_BASE, animeBase.id)
                            putExtra(AnimeProfileActivity.PREFERENCE_LINK, animeBase.reference)
                        })
                }
            }
        }
    }
}