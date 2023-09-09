package com.ead.project.dreamer.presentation.player.content.scalegesture

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.ead.commons.lib.views.addSelectableItemEffect
import com.ead.project.dreamer.app.data.util.system.hideSystemUI
import com.ead.project.dreamer.app.data.util.system.setStateExpanded
import com.ead.project.dreamer.app.data.util.system.setWidthMatchParent
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.utils.Thread
import com.ead.project.dreamer.databinding.BottomModalScaleGestureBinding
import com.ead.project.dreamer.presentation.player.PlayerViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@AndroidEntryPoint
class ScaleGestureFragment : BottomSheetDialogFragment() {

    private val viewModel : PlayerViewModel by viewModels()

    @Inject lateinit var scope: CoroutineScope
    lateinit var playerView : PlayerView

    override fun onStart() {
        super.onStart()
        setWidthMatchParent()
        setStateExpanded()
    }

    private var _binding : BottomModalScaleGestureBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomModalScaleGestureBinding.inflate(inflater, container, false)

        initLayouts()
        settingLayouts()
        return binding.root
    }

    private fun initLayouts() {
        binding.buttonStretch.addSelectableItemEffect()
        binding.buttonZoom.addSelectableItemEffect()
        binding.buttonFixedWidth.addSelectableItemEffect()
    }

    private fun settingLayouts() {
        binding.buttonStretch.setOnClickListener {
            execute {
                scope.launch {
                    viewModel.preferences.set(
                        Constants.PREFERENCE_RESIZING_MODE,
                        AspectRatioFrameLayout.RESIZE_MODE_FILL
                    )
                }
            }
        }

        binding.buttonZoom.setOnClickListener {
            execute {
                scope.launch {
                    viewModel.preferences.set(
                        Constants.PREFERENCE_RESIZING_MODE,
                        AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    )
                }
            }
        }

        binding.buttonFixedWidth.setOnClickListener {
            execute {
                scope.launch {
                    viewModel.preferences.set(
                        Constants.PREFERENCE_RESIZING_MODE,
                        AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
                    )
                }
            }
        }
    }

    private fun execute(task :  () -> Unit ) {
        Thread.runInMs({
            task()
            dismiss()
        },175L)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.hideSystemUI()
    }

}