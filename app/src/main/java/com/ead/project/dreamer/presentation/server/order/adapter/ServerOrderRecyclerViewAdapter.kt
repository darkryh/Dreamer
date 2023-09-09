package com.ead.project.dreamer.presentation.server.order.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.ead.project.dreamer.data.utils.ui.mechanism.DreamerAsyncDiffUtil
import com.ead.project.dreamer.databinding.LayoutServerOrderBinding

class ServerOrderRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dreamerAsyncDiffUtil = object : DreamerAsyncDiffUtil<String>(){}
    private val differ = AsyncListDiffer(this,dreamerAsyncDiffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutServerOrderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val server = differ.currentList[position] as String
        val viewHolder = holder as ViewHolder
        viewHolder.bindTo(server)
    }

    fun submitList (list: List<String>) = differ.submitList(list)

    override fun getItemCount(): Int = differ.currentList.size

    inner class ViewHolder(val binding: LayoutServerOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindTo (server : String) {
            settingsLayouts(server)
        }

        private fun settingsLayouts(title : String) {
            binding.txvServer.text = title
        }
    }
}