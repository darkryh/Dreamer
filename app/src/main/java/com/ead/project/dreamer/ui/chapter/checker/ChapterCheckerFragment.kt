package com.ead.project.dreamer.ui.chapter.checker

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import coil.load
import coil.transform.RoundedCornersTransformation
import com.ead.commons.lib.lifecycle.fragment.showLongToast
import com.ead.commons.lib.lifecycle.parcelable
import com.ead.commons.lib.lifecycle.parcelableArrayList
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.commons.Tools.Companion.launchIntent
import com.ead.project.dreamer.data.database.model.*
import com.ead.project.dreamer.data.models.VideoModel
import com.ead.project.dreamer.databinding.FragmentDialogCheckerBinding
import com.ead.project.dreamer.domain.DownloadManager
import com.ead.project.dreamer.ui.ads.InterstitialAdActivity
import com.ead.project.dreamer.ui.player.PlayerActivity
import com.ead.project.dreamer.ui.player.PlayerExternalActivity
import com.ead.project.dreamer.ui.player.PlayerWebActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChapterCheckerFragment : DialogFragment() {

    @Inject lateinit var downloadManager : DownloadManager
    private lateinit var chapterCheckerViewModel : ChapterCheckerViewModel
    private lateinit var playList : List<VideoModel>
    private lateinit var chapter : Chapter
    private var isDirect = true
    private var isExternalPlayerMode = false
    private var isDownloadingMode = false
    private var count = 0

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
        binding.imvCoverChecker.load(chapter.cover){
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
        chapterCheckerViewModel.getChapterData(chapter).observe(viewLifecycleOwner) { mChapter ->
            if (mChapter != null) {
                if (isDownloadingMode) prepareDownload(mChapter)
                else preparingIntent(mChapter)
                dismiss()
            } else gettingAnimeBase()
        }
    }

    private fun gettingAnimeBase() {
        chapterCheckerViewModel.getAnimeBase(chapter.title).observe(viewLifecycleOwner) { mAnimeBase ->
            if (mAnimeBase != null) gettingAnimeProfile(mAnimeBase)
        }
    }

    private fun gettingAnimeProfile(animeBase: AnimeBase) {
        chapterCheckerViewModel.getAnimeProfile(animeBase.id)
            .observe(viewLifecycleOwner) { mAnimeProfile ->
                if (mAnimeProfile != null) {
                    if (mAnimeProfile.checkPolicies()) {
                        if (++count == 1)
                            chapterCheckerViewModel
                                .configureChaptersData(mAnimeProfile.id, animeBase.reference)
                    }
                    else {
                        showLongToast(getString(R.string.google_policies_message))
                        dismiss()
                    }
                }
                else
                    chapterCheckerViewModel
                        .configureProfileData(null,animeBase.id, animeBase.reference)
            }
    }

    private fun prepareDownload(chapter: Chapter) =
        downloadManager.launchManualDownload(chapter,playList.last().directLink)

    private fun preparingIntent(chapter: Chapter) {
        if (Constants.isAdInterstitialTime(isDirect)) {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}