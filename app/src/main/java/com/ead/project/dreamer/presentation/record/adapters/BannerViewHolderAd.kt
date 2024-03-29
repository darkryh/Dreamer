package com.ead.project.dreamer.presentation.record.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ead.commons.lib.views.setVisibility
import com.ead.project.dreamer.app.data.util.system.isNotNullOrNotEmpty
import com.ead.project.dreamer.databinding.AdUnifiedBannerBinding
import com.google.android.gms.ads.nativead.NativeAd

class BannerViewHolderAd(val binding: AdUnifiedBannerBinding) :
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
        binding.adStore.setVisibility(nativeAd.store.isNotNullOrNotEmpty())


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