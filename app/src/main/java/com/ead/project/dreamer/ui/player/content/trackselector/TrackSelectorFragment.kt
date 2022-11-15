package com.ead.project.dreamer.ui.player.content.trackselector

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.commons.Tools.Companion.hideSystemUI
import com.ead.project.dreamer.data.models.VideoModel
import com.ead.project.dreamer.data.utils.ui.DreamerLayout
import com.ead.project.dreamer.data.utils.ThreadUtil
import com.ead.project.dreamer.databinding.BottomModalTrackSelectorBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackSelectorFragment : BottomSheetDialogFragment() {

    override fun onStart() {
        super.onStart()
        val behavior = BottomSheetBehavior.from(requireView().parent as View)
        behavior.maxWidth = ViewGroup.LayoutParams.MATCH_PARENT
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }


    private var _binding : BottomModalTrackSelectorBinding?= null
    private val binding get() = _binding!!
    lateinit var videoModelList: List<VideoModel>
    lateinit var player : ExoPlayer
    lateinit var playerView : StyledPlayerView

    /*var videoRendererIndex = 0
    private var trackGroups: TrackGroupArray? = null*/
    lateinit var trackSelector : DefaultTrackSelector


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomModalTrackSelectorBinding
            .inflate(
                layoutInflater,
                container,
                false)

        settingViews()
        /*if (existTracks())
            settingViewsTracks()*/
        return binding.root
    }

    /*private fun settingViewsTracks() {
        for (groupIndex in 0 until  trackGroups!!.length) {
            val group = trackGroups!!.get(groupIndex)
            for (trackIndex in 0 until group.length) {
                val txvTrack  = TextView(requireContext())
                txvTrack.text = group.getFormat(trackIndex).toString()
                txvTrack.textSize = 15f
                txvTrack.setPadding(40)
                txvTrack.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1f
                )
                DreamerLayout.setClickEffect(txvTrack,requireContext())
                binding.linearLayoutQuality.addView(txvTrack)
            }
        }
    }

    private fun existTracks() : Boolean {
        val mappedTrackInfo: MappedTrackInfo = trackSelector.currentMappedTrackInfo!!
        for (i in 0 until mappedTrackInfo.rendererCount) {
            trackGroups = mappedTrackInfo.getTrackGroups(i)
            if (trackGroups!!.length != 0) {
                when (player.getRendererType(i)) {
                    C.TRACK_TYPE_VIDEO -> {
                        videoRendererIndex = i
                        return true
                    }
                }
            }
        }
        return false
    }*/

    private fun settingViews() {
        for (pos in videoModelList.indices) {
            val textVideoModel = TextView(requireContext())
            textVideoModel.text = videoModelList[pos].quality
            textVideoModel.textSize = 15f
            textVideoModel.setPadding(120,40,120,40)
            textVideoModel.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f
            )
            DreamerLayout.setClickEffect(textVideoModel,requireContext())
            textVideoModel.setOnClickListener {
                ThreadUtil.runInMs({
                    player.seekTo(pos,player.currentPosition)
                    dismiss()
                },Constants.MS_CLICK_EFFECT_MEDIUM)
            }
            binding.linearLayoutQuality.addView(textVideoModel)
        }
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