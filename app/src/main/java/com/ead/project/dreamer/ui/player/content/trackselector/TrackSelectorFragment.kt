package com.ead.project.dreamer.ui.player.content.trackselector

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.commons.Tools
import com.ead.project.dreamer.data.database.model.VideoModel
import com.ead.project.dreamer.data.utils.ui.DreamerLayout
import com.ead.project.dreamer.data.utils.ThreadUtil
import com.ead.project.dreamer.databinding.BottomModalTrackSelectorBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TrackSelectorFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TrackSelectorFragment : BottomSheetDialogFragment() {
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
        // Inflate the layout for this fragment
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
        Tools.hideSystemUI(requireActivity(),playerView)
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TrackSelectorFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TrackSelectorFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}