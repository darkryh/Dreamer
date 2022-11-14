package com.ead.project.dreamer.ui.chapter.checker

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
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
import com.ead.project.dreamer.data.commons.Tools
import com.ead.project.dreamer.data.commons.Tools.Companion.launchIntent
import com.ead.project.dreamer.data.commons.Tools.Companion.parcelable
import com.ead.project.dreamer.data.commons.Tools.Companion.parcelableArrayList
import com.ead.project.dreamer.data.database.model.*
import com.ead.project.dreamer.data.models.VideoModel
import com.ead.project.dreamer.databinding.FragmentDialogCheckerBinding
import com.ead.project.dreamer.ui.ads.InterstitialAdActivity
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
    private var isExternalPlayerMode = false
    private var isDownloadingMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chapterCheckerViewModel = ViewModelProvider(this)[ChapterCheckerViewModel::class.java]
        arguments?.let {
            playList = it.parcelableArrayList(Constants.PLAY_VIDEO_LIST)!!
            chapter = it.parcelable(Constants.REQUESTED_CHAPTER)!!
            isDirect = it.getBoolean(Constants.REQUESTED_IS_DIRECT)
            isExternalPlayerMode = it.getBoolean(Constants.PREFERENCE_EXTERNAL_PLAYER)
            isDownloadingMode = it.getBoolean(Constants.IS_DATA_FOR_DOWNLOADING_MODE)
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
                if (isDownloadingMode) prepareDownload(mChapter)
                else preparingIntent(mChapter)
                dismiss()
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
                    this.animeProfile?.lastChapterId = chapterList.first().id
                }
            }
    }

    private fun prepareDownload(chapter: Chapter) {
        val downloadManager = requireContext()
            .getSystemService(Context.DOWNLOAD_SERVICE) as android.app.DownloadManager
        val request =
            Tools.downloadRequest(chapter, playList.last().directLink)
        val idDownload = downloadManager.enqueue(request)
        Chapter.addToDownloadList(Pair(idDownload, chapter.id))
    }

    private fun preparingIntent(chapter: Chapter) {
        if(Constants.isAdInterstitialTime(isDirect)) {
            launchIntent(requireActivity(),chapter, InterstitialAdActivity::class.java,playList,isDirect)
            Constants.resetCountedAds()
        } else {
            if (isDirect) {
                if (!isExternalPlayerMode)
                    launchIntent(requireActivity(),chapter,PlayerActivity::class.java, playList)
                else
                    launchIntent(requireActivity(),chapter,PlayerExternalActivity::class.java, playList)
            }
            else launchIntent(requireActivity(),chapter,PlayerWebActivity::class.java, playList)
        }
    }

    override fun onPause() {
        thread { animeProfile?.let { if (lastChapterNeeded) chapterCheckerViewModel.updateAnimeProfile(it) } }
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}