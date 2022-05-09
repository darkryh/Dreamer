package com.ead.project.dreamer.ui.player.content.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
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
import com.ead.project.dreamer.databinding.AdUnifiedAnimeProfileBinding
import com.ead.project.dreamer.databinding.LayoutAnimeProfileBinding
import com.ead.project.dreamer.ui.profile.AnimeProfileActivity
import com.google.android.gms.ads.nativead.NativeAd
import java.lang.StringBuilder

class ProfileRecyclerViewAdapter (
    private val context: Context,
    private var isFromContent: Boolean = false
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val IS_AD = 1
        const val NOT_AD = 0
    }

    private val dreamerAsyncDiffUtil = object : DreamerAsyncDiffUtil<Any>(){}

    private val differ = AsyncListDiffer(this,dreamerAsyncDiffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == IS_AD) {
            return ViewHolderAd(
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
            val nativeHolder = holder as ViewHolderAd
            nativeHolder.bindTo(nativeAd)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (differ.currentList[position] !is NativeAd)
            return NOT_AD

        return IS_AD
    }

    fun submitList (list: List<Any>) {
        differ.submitList(list)
    }


    override fun getItemCount(): Int = differ.currentList.size

    inner class ViewHolder(val binding: LayoutAnimeProfileBinding) : RecyclerView.ViewHolder(binding.root) {

        private val stringBuilder = StringBuilder()

        fun bindTo (animeProfile: AnimeProfile) {
            binding.txvTitle.text = animeProfile.title
            binding.txvContent.text = animeProfile.description
            DreamerLayout.setClickEffect(binding.root,context)
            binding.imvCoverBase.load(animeProfile.profilePhoto){
                crossfade(true)
                crossfade(500)
                transformations(RoundedCornersTransformation(35f))
            }

            binding.txvRating.text = context
                .getString(R.string.ratingLayout,animeProfile.rating.toString())

            binding.root.setOnClickListener {
                if (!DataStore.readBoolean(Constants.WORK_PREFERENCE_CLICKED_PROFILE_SUGGESTION)) {
                    DataStore
                        .writeBooleanAsync(Constants.WORK_PREFERENCE_CLICKED_PROFILE_SUGGESTION,true)

                    if (!isFromContent) {
                        it.context.startActivity(
                            Intent(context, AnimeProfileActivity::class.java).apply {
                                putExtra(Constants.PREFERENCE_ID_BASE, animeProfile.id)
                                putExtra(Constants.PREFERENCE_LINK, animeProfile.reference)
                            })
                    }
                    else {
                        DataStore.apply {
                            writeBoolean(Constants.PROFILE_SENDER_VIDEO_PLAYER,true)
                            writeInt(Constants.VALUE_VIDEO_PLAYER_ID_PROFILE,animeProfile.id)
                            writeString(Constants.VALUE_VIDEO_PLAYER_LINK,animeProfile.reference)
                        }
                        (context as Activity).onBackPressed()
                    }
                }
            }

            for (genre in animeProfile.genres) {
                stringBuilder.append("·$genre  ")
            }
            binding.txvGenre.text = stringBuilder.toString()
            stringBuilder.clear()
        }
    }

    inner class ViewHolderAd(val binding: AdUnifiedAnimeProfileBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindTo (nativeAd: NativeAd) {

            if (nativeAd.icon!= null)
                if (nativeAd.icon!!.drawable != null) {
                    binding.adAppIcon.load(nativeAd.icon!!.drawable)
                    binding.nativeView.iconView = binding.adAppIcon
                }

            if (nativeAd.headline != null) {
                binding.adHeadline.text = nativeAd.headline
                binding.nativeView.headlineView = binding.adHeadline
            }
            else {
                binding.adHeadline.visibility = View.GONE
            }

            if (nativeAd.callToAction != null) {
                binding.adCallToAction.text = nativeAd.callToAction
                binding.nativeView.callToActionView = binding.adCallToAction
            }
            else {
                binding.adCallToAction.visibility = View.GONE
            }

            if (nativeAd.store != null) {
                binding.adStore.text = nativeAd.store
            }
            else {
                binding.adStore.visibility = View.GONE
            }

            if (nativeAd.price != null) {
                binding.adPrice.text = nativeAd.price
            }
            else {
                binding.adPrice.visibility = View.GONE
            }

            if (nativeAd.body != null) {
                binding.adBody.text = nativeAd.body
                binding.nativeView.bodyView = binding.adBody
            }
            else {
                binding.adBody.visibility = View.GONE
            }
            binding.nativeView.setNativeAd(nativeAd)
        }
    }
}