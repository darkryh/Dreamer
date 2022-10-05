package com.ead.project.dreamer.ui.chapter.checker

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
import com.ead.project.dreamer.data.commons.Tools.Companion.parcelable
import com.ead.project.dreamer.data.commons.Tools.Companion.parcelableArrayList
import com.ead.project.dreamer.data.database.model.AnimeBase
import com.ead.project.dreamer.data.database.model.AnimeProfile
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.database.model.VideoModel
import com.ead.project.dreamer.databinding.FragmentDialogCheckerBinding
import com.ead.project.dreamer.ui.player.InterstitialAdActivity
import com.ead.project.dreamer.ui.player.PlayerActivity
import com.ead.project.dreamer.ui.player.PlayerExternalActivity
import com.ead.project.dreamer.ui.player.PlayerWebActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlin.concurrent.thread

@AndroidEntryPoint
class ChapterCheckerFragment : DialogFragment() {

    private lateinit var chapterCheckerViewModel : ChapterCheckerViewModel
    private lateinit var playList : List<VideoModel>
    private var  animeProfile: AnimeProfile ?= null
    private lateinit var chapter : Chapter
    private var lastChapterNeeded = false
    private var isDirect = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chapterCheckerViewModel = ViewModelProvider(this)[ChapterCheckerViewModel::class.java]
        arguments?.let {
            playList = it.parcelableArrayList(Constants.PLAY_VIDEO_LIST)!!
            chapter = it.parcelable(Constants.REQUESTED_CHAPTER)!!
            isDirect = it.getBoolean(Constants.REQUESTED_IS_DIRECT)
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
                preparingIntent(mChapter)
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
                    chapterCheckerViewModel.cachingChapters(
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

    private fun preparingIntent(chapter: Chapter) {
        Chapter.set(chapter)
        if(Constants.isAdInterstitialTime(isDirect)) {
            launchIntent(InterstitialAdActivity::class.java,playList,chapter,isDirect)
            Constants.resetCountedAds()
        } else {
            if (isDirect) {
                if (!Constants.isExternalPlayerMode())
                    launchIntent(PlayerActivity::class.java, playList, chapter)
                else
                    launchIntent(PlayerExternalActivity::class.java, playList, chapter)
            }
            else launchIntent(PlayerWebActivity::class.java, playList, chapter)
        }
        dismiss()
    }

    private fun launchIntent(typeClass: Class<*>?, playList: List<VideoModel>,chapter: Chapter,isDirect : Boolean=true) {
        startActivity(Intent(activity,typeClass).apply {
            putExtra(Constants.REQUESTED_CHAPTER, chapter)
            putExtra(Constants.REQUESTED_IS_DIRECT,isDirect)
            putParcelableArrayListExtra(
                Constants.PLAY_VIDEO_LIST,
                playList as java.util.ArrayList<out Parcelable>)
        })
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

}