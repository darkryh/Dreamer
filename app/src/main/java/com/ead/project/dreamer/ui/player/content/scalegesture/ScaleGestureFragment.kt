package com.ead.project.dreamer.ui.player.content.scalegesture

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.commons.Constants.Companion.MS_CLICK_EFFECT_MEDIUM
import com.ead.project.dreamer.data.commons.Tools
import com.ead.project.dreamer.data.utils.DataStore
import com.ead.project.dreamer.data.utils.ui.DreamerLayout
import com.ead.project.dreamer.data.utils.ThreadUtil
import com.ead.project.dreamer.databinding.BottomModalScaleGestureBinding
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ScaleGestureFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ScaleGestureFragment : BottomSheetDialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
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
        // Inflate the layout for this fragment
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
        Tools.hideSystemUI(requireActivity(),playerView)
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ScaleGestureFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ScaleGestureFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}