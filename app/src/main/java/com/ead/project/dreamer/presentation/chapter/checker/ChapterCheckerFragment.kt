package com.ead.project.dreamer.presentation.chapter.checker

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
import com.ead.commons.lib.lifecycle.fragment.showLongToast
import com.ead.commons.lib.lifecycle.parcelable
import com.ead.commons.lib.lifecycle.parcelableArrayList
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.data.preference.AppBuildPreferences
import com.ead.project.dreamer.data.database.model.AnimeBase
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.models.VideoModel
import com.ead.project.dreamer.data.utils.Run
import com.ead.project.dreamer.databinding.FragmentDialogCheckerBinding
import com.ead.project.dreamer.domain.DownloadUseCase
import com.ead.project.dreamer.presentation.server.menu.MenuServerFragment
import com.ead.project.dreamer.presentation.player.PlayerActivity
import com.ead.project.dreamer.presentation.settings.options.SettingsPlayerFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChapterCheckerFragment : DialogFragment() {

    @Inject lateinit var downloadUseCase : DownloadUseCase
    @Inject lateinit var appBuildPreferences: AppBuildPreferences
    private lateinit var viewModel : ChapterCheckerViewModel
    private lateinit var videoList : List<VideoModel>
    private lateinit var chapter : Chapter
    private var previousChapter: Chapter?= null
    private var isDirect = true
    private var isExternalPlayerMode = false
    private var isDownloadingMode = false
    private var isFromContent = false
    private var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[ChapterCheckerViewModel::class.java]
        arguments?.let {
            videoList = it.parcelableArrayList(Chapter.PLAY_VIDEO_LIST)?:return@let
            chapter = it.parcelable(Chapter.REQUESTED)?:return@let
            previousChapter = it.parcelable(Chapter.PREVIOUS_CASTING_MEDIA)
            isDirect = it.getBoolean(Chapter.CONTENT_IS_DIRECT)
            isExternalPlayerMode = it.getBoolean(SettingsPlayerFragment.PREFERENCE_EXTERNAL_PLAYER)
            isDownloadingMode = it.getBoolean(MenuServerFragment.IS_DATA_FOR_DOWNLOADING_MODE)
            isFromContent = it.getBoolean(PlayerActivity.IS_FROM_CONTENT_PLAYER)
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
        viewModel.getChapterData(chapter).observe(viewLifecycleOwner) { mChapter ->
            if (mChapter != null) {
                if (isDownloadingMode) prepareDownload(mChapter)
                else preparingIntent(mChapter)
                dismiss()
            } else gettingAnimeBase()
        }
    }

    private fun gettingAnimeBase() {
        viewModel.getAnimeBase(chapter.title).observe(viewLifecycleOwner) { mAnimeBase ->
            if (mAnimeBase != null) gettingAnimeProfile(mAnimeBase)
        }
    }

    private fun gettingAnimeProfile(animeBase: AnimeBase) {
        viewModel.getAnimeProfile(animeBase.id)
            .observe(viewLifecycleOwner) { mAnimeProfile ->
                if (mAnimeProfile != null) {
                    if (appBuildPreferences.isUnlockedVersion() || mAnimeProfile.checkPolicies()) {
                        if (++count == 1) {
                            viewModel
                                .configureChaptersData(mAnimeProfile.id, animeBase.reference)
                        }
                    }
                    else {
                        showLongToast(getString(R.string.google_policies_message))
                        dismiss()
                    }
                }
                else
                    viewModel
                        .configureProfileData(null,animeBase.id, animeBase.reference)
            }
    }

    private fun prepareDownload(chapter: Chapter) {
        downloadUseCase.add(activity as Context,chapter,videoList.last().directLink)
    }

    private fun preparingIntent(chapter: Chapter) {
        Run.catching {

            viewModel.launchVideo.with(activity as Context,chapter,previousChapter,videoList,isDirect)
            if (isFromContent && viewModel.playerPreferences.isInExternalMode()) {
                activity?.finish()
            }
            dismiss()

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val FRAGMENT = "CHAPTER_CHECKER_FRAGMENT"
    }
}