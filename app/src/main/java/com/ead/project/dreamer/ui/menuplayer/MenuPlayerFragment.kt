package com.ead.project.dreamer.ui.menuplayer

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Parcelable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import coil.load
import coil.transform.RoundedCornersTransformation
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.DreamerApp
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.commons.Tools
import com.ead.project.dreamer.data.database.model.*
import com.ead.project.dreamer.data.network.DreamerWebClient
import com.ead.project.dreamer.data.retrofit.model.discord.User
import com.ead.project.dreamer.data.utils.DataStore
import com.ead.project.dreamer.data.utils.ui.DreamerLayout
import com.ead.project.dreamer.data.utils.ThreadUtil
import com.ead.project.dreamer.data.utils.media.CastManager
import com.ead.project.dreamer.databinding.BottomModalMenuPlayerBinding
import com.ead.project.dreamer.ui.chapterchecker.ChapterCheckerFragment
import com.ead.project.dreamer.ui.player.InterstitialAdActivity
import com.ead.project.dreamer.ui.player.PlayerActivity
import com.ead.project.dreamer.ui.player.PlayerExternalActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MenuPlayerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MenuPlayerFragment : BottomSheetDialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var chapter: Chapter
    private lateinit var rawServers : List<String>
    private var embedServers : MutableList<String> = ArrayList()
    private var serverList : List<Server> = ArrayList()
    private lateinit var videoChecker : VideoChecker
    private var optionsHeight = 0
    private var isFromContent = false
    private var user = User.get()

    private lateinit var menuPlayerViewModel : MenuPlayerViewModel
    private lateinit var serverBase : LinearLayout
    private var webView : WebView?= null
    private val castManager = CastManager(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            chapter = it.getParcelable(Constants.REQUESTED_CHAPTER)!!
            isFromContent = it.getBoolean(Constants.IS_FROM_CONTENT_PLAYER)
        }
    }

    override fun onStart() {
        super.onStart()
        val behavior = BottomSheetBehavior.from(requireView().parent as View)
        behavior.maxWidth = ViewGroup.LayoutParams.MATCH_PARENT
        if (!DataStore.readBoolean(Constants.PREFERENCE_RANK_AUTOMATIC_PLAYER)) {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private var _binding : BottomModalMenuPlayerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = BottomModalMenuPlayerBinding
            .inflate(
                inflater,
                container,
                false)
        webView = WebView(requireContext())
        settingCast()
        loadingLayouts()
        settingJavaScript()
        return binding.root
    }

    private fun settingCast() {
        if (castManager.castIsConnected()) {
            castManager.setPreviousCast()
        }
    }

    private fun settingJavaScript() {
        webView?.webViewClient = object : DreamerWebClient(webView!!,chapter.reference) {

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                try {
                    ThreadUtil.runInMs({
                        if (_binding != null && timeout) {
                            webView!!.loadUrl(BLANK_BROWSER)
                            DreamerApp
                                .showLongToast(requireContext().getString(R.string.timeout_message))
                            dismiss()
                        }
                    }, TIMEOUT_MS)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                timeout = false
                try {
                    if (_binding != null) {
                        webView!!.evaluateJavascript(server_Script) {
                            rawServers = Tools.stringRawArrayToList(it)
                            embeddingServers()
                        }
                    }
                } catch (e : InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun embeddingServers() {
        if (_binding != null) {
            for (server in rawServers) {
                embedServers.add(Tools.embedLink(server))
            }
            configuringLayout()
            employingServers()
        }
    }

    private fun configuringLayout() {
        optionsHeight = if (embedServers.size <= 8) {
            resources
                .getDimensionPixelSize(R.dimen.text_view_padding_player_option)
        } else {
            resources
                .getDimensionPixelSize(R.dimen.text_view_padding_player_option_mini)
        }
    }

    private fun employingServers() {
        if (!DataStore.readBoolean(Constants.PREFERENCE_RANK_AUTOMATIC_PLAYER))
            for (pos in embedServers.indices) {
                val embeddedVideo = embedServers[pos]

                serverBase = LinearLayout(requireContext())
                serverBase.id = serverBaseId + pos
                serverBase.tag = Constants.SERVER_VIDEOS
                serverBase.gravity = Gravity.START
                serverBase.background = ContextCompat
                    .getDrawable(requireContext(), R.drawable.bg_horizontal_border)
                serverBase.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1f
                )
                serverBase.orientation = LinearLayout.HORIZONTAL
                DreamerLayout.setClickEffect(serverBase,requireContext())

                val textViewServer = TextView(requireContext())
                textViewServer.text = Server.identify(embeddedVideo)
                textViewServer.textSize = 15f
                textViewServer.setPadding(
                    (resources.getDimensionPixelSize(R.dimen.text_view_padding_player_option) * 2),
                    optionsHeight,
                    0,
                    optionsHeight)

                serverBase.addView(textViewServer)
                binding.linearServer.addView(serverBase)
                hidingOptions()
            }
        serverParse(embedServers)
    }

    private fun serverParse(serverList: MutableList<String>) {
        menuPlayerViewModel = ViewModelProvider(
            this,
            MenuPlayerViewModelFactory(serverList)
        )[MenuPlayerViewModel::class.java]
        factoringServers()
    }

    private fun factoringServers () {
        menuPlayerViewModel.getServerList().observe(this) {
            serverList = it
            this.isCancelable = false
            if (DataStore.readBoolean(Constants.PREFERENCE_RANK_AUTOMATIC_PLAYER)) {
                initVideoChecker()
                ThreadUtil.execute {
                    executingAutomaticPlay()
                }
            } else {
                scriptLayouts()
                showingOptions()
            }
        }
    }

    private fun initVideoChecker() {
        val pairList : MutableList<Pair<String,List<VideoModel>>> = ArrayList()
        for (pos in serverList.indices) {
            if (serverList[pos].videoList.isNotEmpty() && serverList[pos].isDirect) {
                pairList.add(Pair(embedServers[pos], serverList[pos].videoList))
            }
        }
        videoChecker = VideoChecker((pairList))
    }

    private fun executingAutomaticPlay() {
        try {
            val tripleList = videoChecker.updatedTripleList()
            for (pos in tripleList.indices) {
                if (Constants.SERVER_ONEFICHIER !in tripleList[pos].third.last().directLink) {
                    if (VideoChecker.getConnection(tripleList[pos].third.last().directLink)) {
                        intentReferentData(tripleList[pos].third)
                        break
                    }
                }
                else {
                    settingPremiumServer(tripleList[pos].third)
                }
            }
            dismiss()
        }
        catch ( e : InterruptedException) {
            e.printStackTrace()
        }
    }

    private fun scriptLayouts() {
        for (pos in serverList.indices) {
            val row = binding.linearServer[pos]
            row.setOnClickListener {
                if (serverList[pos].videoList.isNotEmpty()) {
                    ThreadUtil.runInMs({ executingBase(pos) }, Constants.MS_CLICK_EFFECT_MEDIUM)
                }
                else {
                    DreamerApp.showLongToast("error de servidor o video eliminado")
                }
            }
        }
    }

    private fun executingBase(pos: Int) {
        try {
            if (Constants.SERVER_ONEFICHIER !in serverList[pos].videoList.last().directLink) {
                intentReferentData(serverList[pos].videoList)
                dismiss()
            }
            else {
                settingPremiumServer(serverList[pos].videoList)
            }
        }
        catch (e : Exception) {
            DreamerApp.showLongToast( "error de servidor o video eliminado $e")
        }
    }

    private fun intentReferentData(playList: List<VideoModel>) {
        if (_binding != null) {
            try {
                val isExternalPlayerMode = Constants.isExternalPlayerMode()
                Chapter.set(chapter)
                if (chapter.id != 0) {
                    if (Constants.isInQuantityAdLimit() && !User.isVip()) {
                        startActivity(
                            Intent(activity, InterstitialAdActivity::class.java).apply {
                                putExtra(Constants.REQUESTED_CHAPTER, chapter)
                                putParcelableArrayListExtra(
                                    Constants.PLAY_VIDEO_LIST,
                                    playList as java.util.ArrayList<out Parcelable>
                                )
                            }
                        )
                        DataStore.writeIntAsync(Constants.PREFERENCE_CURRENT_WATCHED_VIDEOS, 0)
                    } else {
                        if (!isExternalPlayerMode) {
                            startActivity(
                                Intent(activity, PlayerActivity::class.java).apply {
                                    putExtra(Constants.REQUESTED_CHAPTER, chapter)
                                    putParcelableArrayListExtra(
                                        Constants.PLAY_VIDEO_LIST,
                                        playList as java.util.ArrayList<out Parcelable>)
                                }
                            )
                        } else {
                            startActivity(
                                Intent(activity, PlayerExternalActivity::class.java).apply {
                                    putExtra(Constants.REQUESTED_CHAPTER, chapter)
                                    putParcelableArrayListExtra(
                                        Constants.PLAY_VIDEO_LIST,
                                        playList as java.util.ArrayList<out Parcelable>)
                                }
                            )
                        }
                    }

                    if (isFromContent && isExternalPlayerMode) {
                        activity?.finish()
                    }
                } else {
                    val fragmentManager: FragmentManager = (context
                            as FragmentActivity).supportFragmentManager
                    val data = Bundle()
                    data.apply {
                        putParcelable(Constants.REQUESTED_CHAPTER, chapter)
                        putParcelableArrayList(
                            Constants.PLAY_VIDEO_LIST, playList as java.util.ArrayList<out Parcelable>
                        )
                    }
                    val chapterChecker = ChapterCheckerFragment()
                    chapterChecker.apply {
                        arguments = data
                        show(fragmentManager, Constants.CHAPTER_CHECKER_FRAGMENT)
                    }
                }
                dismiss()

            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    private fun loadingLayouts() {
        binding.lavLoadingServer.visibility = View.VISIBLE
        binding.imvChapterMenu.load(chapter.chapterCover){
            transformations(RoundedCornersTransformation(10f))
        }
        binding.txvTitleMenu.text = getString(R.string.welcome_player,
            chapter.title,
            chapter.chapterNumber)
    }

    private fun showingOptions() {
        for (pos in 0 until binding.linearServer.childCount) {
            binding.linearServer[pos].visibility = View.VISIBLE
        }
        binding.lavLoadingServer.visibility = View.GONE
        this.isCancelable = true
    }

    private fun hidingOptions() {
        for (pos in 0 until binding.linearServer.childCount) {
            binding.linearServer[pos].visibility = View.GONE
        }
    }

    private fun settingPremiumServer (videoList : List<VideoModel>) {
        if (user != null) {
            intentReferentData(videoList)
        }
        else {
            if (!DataStore.readBoolean(Constants.PREFERENCE_RANK_AUTOMATIC_PLAYER))
                DreamerApp.showLongToast("Servidor Premium, no disponible.")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        webView = null
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        DataStore.writeBooleanAsync(Constants.WORK_PREFERENCE_CLICKED_CHAPTER,false)
    }

    companion object {

        const val serverBaseId = 100000

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MenuPlayerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}