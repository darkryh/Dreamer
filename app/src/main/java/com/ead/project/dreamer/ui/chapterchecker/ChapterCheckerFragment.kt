package com.ead.project.dreamer.ui.chapterchecker

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import coil.load
import coil.transform.RoundedCornersTransformation
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.DreamerApp
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.database.model.AnimeBase
import com.ead.project.dreamer.data.database.model.AnimeProfile
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.database.model.VideoModel
import com.ead.project.dreamer.data.retrofit.model.discord.User
import com.ead.project.dreamer.data.utils.DataStore
import com.ead.project.dreamer.databinding.FragmentDialogCheckerBinding
import com.ead.project.dreamer.ui.player.InterstitialAdActivity
import com.ead.project.dreamer.ui.player.PlayerActivity
import com.ead.project.dreamer.ui.player.PlayerExternalActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlin.concurrent.thread

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@AndroidEntryPoint
class ChapterCheckerFragment : DialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var chapterCheckerViewModel : ChapterCheckerViewModel
    private lateinit var videoList : List<VideoModel>
    private var  animeProfile: AnimeProfile ?= null
    private lateinit var chapter : Chapter
    private var lastChapterNeeded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chapterCheckerViewModel = ViewModelProvider(this)[ChapterCheckerViewModel::class.java]
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            videoList = it.getParcelableArrayList(Constants.PLAY_VIDEO_LIST)!!
            chapter = it.getParcelable(Constants.REQUESTED_CHAPTER)!!
        }
    }

    private var _binding: FragmentDialogCheckerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentDialogCheckerBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(binding.root)
        binding.imvCoverChecker.load(chapter.chapterCover){
            transformations(RoundedCornersTransformation(8f))
        }
        return builder.create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dialog_checker, container, false)
        gettingData()
        return view
    }

    private fun gettingData() {
        chapterCheckerViewModel.getChapter(chapter).observe(viewLifecycleOwner) { mChapter ->
            if (mChapter != null) {
                intentToPlayer(mChapter)
            } else {
                lastChapterNeeded = true
                gettingAnimeBase()
            }
        }
    }

    private fun gettingAnimeBase() {
        chapterCheckerViewModel.getAnimeBase(chapter.title)
            .observe(viewLifecycleOwner) { mAnimeBase ->
                if (mAnimeBase != null) {
                    gettingAnimeProfile(mAnimeBase)
                }
            }
    }

    private fun gettingAnimeProfile(animeBase: AnimeBase) {
        chapterCheckerViewModel.getAnimeProfile(animeBase.id)
            .observe(viewLifecycleOwner) { mAnimeProfile ->
                if (mAnimeProfile == null) {
                    chapterCheckerViewModel
                        .cachingProfile(animeBase.id, animeBase.reference)
                } else {
                    if (mAnimeProfile.checkPolicies()) {
                        this.animeProfile = mAnimeProfile
                        gettingChapters(mAnimeProfile, animeBase)
                    }
                    else {
                        DreamerApp
                            .showLongToast(getString(R.string.google_policies_message))
                        dismiss()
                    }
                }
            }
    }

    var count = 0
    private fun gettingChapters(animeProfile: AnimeProfile,animeBase: AnimeBase) {
        chapterCheckerViewModel
            .getChaptersFromProfile(animeProfile.id)
            .observe(viewLifecycleOwner) { chapterList ->
                if (++count == 1) {
                    chapterCheckerViewModel
                        .cachingChapters(
                            animeBase.id,
                            animeBase.reference,
                            animeProfile.size - chapterList.size,
                            animeProfile.lastChapterId
                        )
                }

                if (chapterList.isNotEmpty()) {
                    this.animeProfile!!.lastChapterId = chapterList.first().id
                }
            }
    }

    private fun intentToPlayer(chapter: Chapter) {
        Chapter.set(chapter)
        if(Constants.isInQuantityAdLimit() && !User.isVip()) {
            startActivity(
                Intent(requireContext(), InterstitialAdActivity::class.java).apply {
                    putExtra(Constants.REQUESTED_CHAPTER, chapter)
                    putParcelableArrayListExtra(
                        Constants.PLAY_VIDEO_LIST,
                        videoList as java.util.ArrayList<out Parcelable>
                    )
                }
            )
            DataStore.writeIntAsync(Constants.PREFERENCE_CURRENT_WATCHED_VIDEOS,1)
        }
        else {
            if (!Constants.isExternalPlayerMode()) {
                startActivity(
                    Intent(requireContext(), PlayerActivity::class.java).apply {
                        putExtra(Constants.REQUESTED_CHAPTER, chapter)
                        putParcelableArrayListExtra(
                            Constants.PLAY_VIDEO_LIST,
                            videoList as java.util.ArrayList<out Parcelable>
                        )
                    }

                )
            } else {
                startActivity(
                    Intent(requireContext(), PlayerExternalActivity::class.java).apply {
                        putExtra(Constants.REQUESTED_CHAPTER, chapter)
                        putParcelableArrayListExtra(
                            Constants.PLAY_VIDEO_LIST,
                            videoList as java.util.ArrayList<out Parcelable>
                        )
                    }
                )
            }
        }
        dismiss()
    }

    override fun onPause() {
        thread {
            if (animeProfile != null && lastChapterNeeded) {
                chapterCheckerViewModel.updateAnimeProfile(animeProfile!!)
            }
        }
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChapterCheckerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}