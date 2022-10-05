package com.ead.project.dreamer.ui.player.content.scalegesture

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.commons.Constants.Companion.MS_CLICK_EFFECT_MEDIUM
import com.ead.project.dreamer.data.commons.Tools.Companion.hideSystemUI
import com.ead.project.dreamer.data.utils.DataStore
import com.ead.project.dreamer.data.utils.ui.DreamerLayout
import com.ead.project.dreamer.data.utils.ThreadUtil
import com.ead.project.dreamer.databinding.BottomModalScaleGestureBinding
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ScaleGestureFragment : BottomSheetDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onStart() {
        super.onStart()
        val behavior = BottomSheetBehavior.from(requireView().parent as View)
        behavior.maxWidth = ViewGroup.LayoutParams.MATCH_PARENT
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private var _binding : BottomModalScaleGestureBinding? = null
    private val binding get() = _binding!!
    lateinit var playerView : StyledPlayerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomModalScaleGestureBinding
            .inflate(
                inflater,
                container,
                false)

        prepareLayout()
        settingLayout()
        return binding.root
    }

    private fun prepareLayout() {
        DreamerLayout.setClickEffect(binding.lnStretch,requireContext())
        DreamerLayout.setClickEffect(binding.lnZoom,requireContext())
        DreamerLayout.setClickEffect(binding.lnFixedWidth,requireContext())
    }

    private fun settingLayout() {
        binding.lnStretch.setOnClickListener {
            ThreadUtil.runInMs({
                DataStore
                    .writeIntAsync(Constants.PREFERENCE_RESIZING_MODE,
                        AspectRatioFrameLayout.RESIZE_MODE_FILL)
                dismiss()
            },MS_CLICK_EFFECT_MEDIUM)
        }

        binding.lnZoom.setOnClickListener {
            ThreadUtil.runInMs({
                DataStore
                    .writeIntAsync(Constants.PREFERENCE_RESIZING_MODE,
                        AspectRatioFrameLayout.RESIZE_MODE_ZOOM)
                dismiss()
            },MS_CLICK_EFFECT_MEDIUM)
        }

        binding.lnFixedWidth.setOnClickListener {
            ThreadUtil.runInMs({
                DataStore
                    .writeIntAsync(Constants.PREFERENCE_RESIZING_MODE,
                        AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH)
                dismiss()
            },MS_CLICK_EFFECT_MEDIUM)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().hideSystemUI()
    }

}