package com.ead.project.dreamer.presentation.chapter.checker

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import coil.load
import coil.transform.RoundedCornersTransformation
import com.ead.commons.lib.lifecycle.parcelable
import com.ead.commons.lib.lifecycle.parcelableArrayList
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.data.preference.AppBuildPreferences
import com.ead.project.dreamer.app.data.util.system.toPixels
import com.ead.project.dreamer.data.database.model.AnimeBase
import com.ead.project.dreamer.data.database.model.AnimeProfile
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.models.VideoModel
import com.ead.project.dreamer.data.system.extensions.toast
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

    private var animeBase : AnimeBase? = null
    private var animeProfile : AnimeProfile? = null

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
        binding.imageCoverChecker.load(chapter.cover){
            transformations(RoundedCornersTransformation(20f.toPixels()))
        }
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dialog_checker, container, false)
        requestChapterInformation()
        return view
    }

    private fun requestChapterInformation() {
        viewModel.getChapterData(chapter).observe(viewLifecycleOwner) { chapter ->
            if (chapter == null) {
                gettingAnimeBase()
                return@observe
            }
            if(this.chapter.id == chapter.id) return@observe

            this.chapter = chapter

            if (isDownloadingMode) {
                prepareDownload(chapter)
            }
            else {
                preparingIntent(chapter)
            }
            dismiss()
        }
    }

    private fun gettingAnimeBase() {
        viewModel.getAnimeBase(chapter.title).observe(viewLifecycleOwner) { animeBase ->
            if (animeBase == null || this.animeBase == animeBase) return@observe

            this.animeBase = animeBase

            gettingAnimeProfile(animeBase)
        }
    }

    private fun gettingAnimeProfile(animeBase: AnimeBase) {
        configureProfile(animeBase.id,animeBase.reference)

        viewModel.getAnimeProfile(animeBase.id).observe(viewLifecycleOwner) { animeProfile ->
            if (animeProfile == null || this.animeProfile?.id == animeProfile.id) return@observe

            this.animeProfile = animeProfile

            if (appBuildPreferences.isUnlockedVersion() || animeProfile.isAuthorizedData()) {
                viewModel.configureChaptersData(animeProfile.id, animeBase.reference)
            }
            else {
                toast(getString(R.string.google_policies_message))
                dismiss()
            }
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

    private fun configureProfile(id : Int,reference : String) = viewModel.configureProfileData(id,reference)

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        animeBase = null
        animeProfile = null
    }

    companion object {

        private const val FRAGMENT = "CHAPTER_CHECKER_FRAGMENT"

        fun launch(
            context: Context,
            chapter: Chapter,
            previousChapter : Chapter?,
            playList : List<VideoModel>,
            isDirect : Boolean,
            isExternalPlayer : Boolean = false,
            isDownloadingMode : Boolean,
            isFromContent : Boolean
        ) {
            val fragmentManager: FragmentManager = (context as FragmentActivity).supportFragmentManager
            val chapterCheckerFragment = ChapterCheckerFragment()
            chapterCheckerFragment.apply {
                arguments = Bundle().apply {
                    putParcelable(Chapter.REQUESTED, chapter)
                    putParcelable(Chapter.PREVIOUS_CASTING_MEDIA,previousChapter)
                    putParcelableArrayList(Chapter.PLAY_VIDEO_LIST, playList as ArrayList<out Parcelable>)
                    putBoolean(Chapter.CONTENT_IS_DIRECT,isDirect)
                    putBoolean(SettingsPlayerFragment.PREFERENCE_EXTERNAL_PLAYER,isExternalPlayer)
                    putBoolean(MenuServerFragment.IS_DATA_FOR_DOWNLOADING_MODE,isDownloadingMode)
                    putBoolean(PlayerActivity.IS_FROM_CONTENT_PLAYER,isFromContent)
                }
                show(fragmentManager, FRAGMENT)
            }
        }
    }
}