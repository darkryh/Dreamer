package com.ead.project.dreamer.ui.player.cast

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ead.project.dreamer.databinding.AdUnifiedChapterCastingBinding
import com.ead.project.dreamer.databinding.AdUnifiedMiniBannerCastBinding
import com.google.android.gms.ads.nativead.NativeAd

class CastViewHolderAd (val binding: androidx.viewbinding.ViewBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bindTo (nativeAd: NativeAd) {
        when(binding) {
            is AdUnifiedMiniBannerCastBinding -> bindToMiniBanner(nativeAd,binding)
            is AdUnifiedChapterCastingBinding -> bindToChapterAd(nativeAd,binding)
        }
    }

    private fun bindToMiniBanner(nativeAd: NativeAd,binding: AdUnifiedMiniBannerCastBinding) {
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

    private fun bindToChapterAd(nativeAd: NativeAd,binding: AdUnifiedChapterCastingBinding) {
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