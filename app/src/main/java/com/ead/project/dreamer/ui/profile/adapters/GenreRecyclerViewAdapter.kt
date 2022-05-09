package com.ead.project.dreamer.ui.profile.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ead.project.dreamer.R
import com.ead.project.dreamer.databinding.LayoutGenreBinding

class GenreRecyclerViewAdapter (
    private val genreList: List<String>,
    private val context: Context
) :
    RecyclerView.Adapter<GenreRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutGenreBinding.inflate(
                LayoutInflater.from(parent.context), parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val genre = genreList[position]
        holder.binding.txvGenre.text = context.getString(R.string.wordDecorate,genre)
    }

    override fun getItemCount(): Int = genreList.size

    inner class ViewHolder(val binding: LayoutGenreBinding) : RecyclerView.ViewHolder(binding.root)

}