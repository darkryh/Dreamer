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
import com.ead.project.dreamer.ui.player.PlayerWebActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlin.concurrent.thread

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@AndroidEntryPoint
class ChapterCheckerFragment : DialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            playList = it.getParcelableArrayList(Constants.PLAY_VIDEO_LIST)!!
            chapter = it.getParcelable(Constants.REQUESTED_CHAPTER)!!
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
        if(Constants.isInQuantityAdLimit() && !User.isVip()) {
            launchIntent(InterstitialAdActivity::class.java,playList,chapter,isDirect)
            DataStore.writeIntAsync(Constants.PREFERENCE_CURRENT_WATCHED_VIDEOS,1)
        } else {
            if (isDirect) {
                if (!Constants.isExternalPlayerMode())
                    launchIntent(PlayerActivity::class.java, playList, chapter)
                else
                    launchIntent(PlayerExternalActivity::class.java, playList, chapter)
            }
            else
                launchIntent(PlayerWebActivity::class.java, playList, chapter)
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