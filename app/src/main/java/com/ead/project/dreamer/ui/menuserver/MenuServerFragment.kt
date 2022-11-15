package com.ead.project.dreamer.ui.menuserver

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Parcelable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.allViews
import androidx.core.view.get
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import coil.load
import coil.transform.RoundedCornersTransformation
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.DreamerApp
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.commons.Constants.Companion.BLANK_BROWSER
import com.ead.project.dreamer.data.commons.Constants.Companion.isAdInterstitialTime
import com.ead.project.dreamer.data.commons.Tools
import com.ead.project.dreamer.data.commons.Tools.Companion.observeOnce
import com.ead.project.dreamer.data.commons.Tools.Companion.parcelable
import com.ead.project.dreamer.data.commons.Tools.Companion.launchIntent
import com.ead.project.dreamer.data.commons.Tools.Companion.load
import com.ead.project.dreamer.data.commons.Tools.Companion.onDestroy
import com.ead.project.dreamer.data.database.model.*
import com.ead.project.dreamer.data.models.Server
import com.ead.project.dreamer.data.models.VideoChecker
import com.ead.project.dreamer.data.models.VideoModel
import com.ead.project.dreamer.data.network.DreamerClient
import com.ead.project.dreamer.data.network.DreamerWebView
import com.ead.project.dreamer.data.network.DreamerWebView.Companion.getServerScript
import com.ead.project.dreamer.data.utils.DataStore
import com.ead.project.dreamer.data.utils.ServerManager
import com.ead.project.dreamer.data.utils.ui.DreamerLayout
import com.ead.project.dreamer.data.utils.ThreadUtil
import com.ead.project.dreamer.data.utils.media.CastManager
import com.ead.project.dreamer.databinding.BottomModalMenuPlayerBinding
import com.ead.project.dreamer.ui.chapter.checker.ChapterCheckerFragment
import com.ead.project.dreamer.ui.ads.InterstitialAdActivity
import com.ead.project.dreamer.ui.player.PlayerActivity
import com.ead.project.dreamer.ui.player.PlayerExternalActivity
import com.ead.project.dreamer.ui.player.PlayerWebActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MenuServerFragment : BottomSheetDialogFragment() {

    private lateinit var chapter: Chapter
    private lateinit var rawServers : List<String>
    private var embedServers : MutableList<String> = ArrayList()
    private var serverList : List<Server> = ArrayList()
    private var optionsHeight = 0
    private var isFromContent = false
    private var isDownloadingMode = false

    private lateinit var menuServerViewModel : MenuServerViewModel
    private lateinit var serverBase : LinearLayout
    private var webView : DreamerWebView?= null
    private val castManager = CastManager(true)

    private var _binding : BottomModalMenuPlayerBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        menuServerViewModel = ViewModelProvider(this)[MenuServerViewModel::class.java]
        arguments?.let {
            chapter = it.parcelable(Constants.REQUESTED_CHAPTER)!!
            isFromContent = it.getBoolean(Constants.IS_FROM_CONTENT_PLAYER)
            isDownloadingMode = it.getBoolean(Constants.IS_DATA_FOR_DOWNLOADING_MODE)
        }
    }

    override fun onStart() {
        super.onStart()
        val behavior = BottomSheetBehavior.from(requireView().parent as View)
        behavior.maxWidth = ViewGroup.LayoutParams.MATCH_PARENT
        if (!DataStore.readBoolean(Constants.PREFERENCE_RANK_AUTOMATIC_PLAYER) || isDownloadingMode) {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomModalMenuPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Tools.urlIsValid(chapter.reference)) {
            Constants.setDownloadMode(false)
            webView = DreamerWebView(requireContext())
            settingCast()
            loadingLayouts()
            settingJavaScript()
        }
        else {
            DreamerApp.showLongToast(requireContext().getString(R.string.error_url_message))
            dismiss()
        }
    }

    private fun settingCast() { if (castManager.castIsConnected()) castManager.setPreviousCast() }


    private fun settingJavaScript() {
        webView?.webViewClient = object : DreamerClient() {

            override fun onTimeout(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onTimeout(view, url, favicon)
                safeRun {
                    webView?.loadUrl(BLANK_BROWSER)
                    DreamerApp.showLongToast(requireContext().getString(R.string.timeout_message))
                    dismiss()
                }
            }

            override fun onPageLoaded(view: WebView?, url: String?) {
                super.onPageLoaded(view, url)
                safeRun {
                    webView?.evaluateJavascript(getServerScript()) {
                        webView = null
                        rawServers = Tools.stringRawArrayToList(it)
                        embeddingServers()
                        if (Constants.isAutomaticPlayerMode() && !isDownloadingMode) {
                            parsingServersList()
                        }
                        else {
                            configuringSpaceLayouts()
                            applyingLayoutsServers()
                            serverScript()
                            showingLayoutsServers()
                        }
                    }
                }
            }
        }
        webView?.load(chapter.reference)
    }

    private fun embeddingServers() {
        safeRun {
            for (server in rawServers) {
                embedServers.add(Tools.embedLink(server))
            }
        }
    }

    private fun configuringSpaceLayouts() {
        optionsHeight = if (embedServers.size <= 8) resources.getDimensionPixelSize(R.dimen.dimen_15dp)
         else resources.getDimensionPixelSize(R.dimen.text_view_padding_player_option_mini)
    }

    private fun applyingLayoutsServers() {
        for (pos in embedServers.indices) {
            val embeddedVideo = embedServers[pos]
            val serverName = ServerManager.identify(embeddedVideo)

            serverBase = LinearLayout(requireContext())
            serverBase.id = serverBaseId + pos
            serverBase.tag = Constants.SERVER_VIDEOS
            serverBase.gravity = Gravity.START
            serverBase.background = ContextCompat
                .getDrawable(requireContext(), R.drawable.background_horizontal_border)
            serverBase.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f
            )
            serverBase.orientation = LinearLayout.HORIZONTAL
            DreamerLayout.setClickEffect(serverBase,requireContext())

            if (ServerManager.isRecommended(serverName)) addLogoServer(R.drawable.ic_thumb_up_24)
            if (ServerManager.isWebServer(serverName)) addLogoServer(R.drawable.ic_web_24)
            if (isOtherServer(serverName)) addLogoServer(R.drawable.ic_play_circle_outline_24)

            val textViewServer = TextView(requireContext())
            textViewServer.text = serverName
            textViewServer.textSize = 15f
            textViewServer.setPadding(
                (resources.getDimensionPixelSize(R.dimen.dimen_15dp) * 2),
                optionsHeight,
                0,
                optionsHeight)

            serverBase.addView(textViewServer)
            binding.linearServer.addView(serverBase)
        }
        hidingLayoutsServers()
    }

    private fun isOtherServer(serverName : String) : Boolean
    = !ServerManager.isRecommended(serverName) && !ServerManager.isWebServer(serverName)
            && serverName != "null"

    private fun addLogoServer(imageSource : Int) {
        val imageViewServer = ImageView(requireContext())
        imageViewServer.setImageResource(imageSource)
        imageViewServer.setPadding(
            (resources.getDimensionPixelSize(R.dimen.dimen_15dp)),
            optionsHeight,
            0,
            optionsHeight
        )
        serverBase.addView(imageViewServer)
    }


    private fun parsingServersList() {
        embedServers = VideoChecker.getSorterServerList(embedServers)
        factoringServersPlayers()
    }

    private fun factoringServersPlayers () {
        menuServerViewModel.getServerList(embedServers).observeOnce(this) {
            serverList = it
            this.isCancelable = false
            launchInThread { executingAutomaticPlay() }
        }
    }

    private fun executingAutomaticPlay() {
        safeRun {
            for (server in serverList) {
                if (server.isValidated) {
                    preparingIntent(server.videoList,server.isDirect)
                    break
                }
                else if (server.isConnectionValidated) {
                    preparingIntent(server.videoList, server.isDirect)
                    break
                }
            }
            dismiss()
        }
    }

    private fun serverScript() {
        for (pos in embedServers.indices) {
            val row = binding.linearServer[pos]
            row.setOnClickListener {
                binding.linearServer.allViews.forEach { it.isEnabled = false }
                ThreadUtil.runInMs({ executingBase(pos) }, Constants.MS_CLICK_EFFECT_MEDIUM)
            }
        }
    }

    private fun executingBase(pos : Int) {
        safeRun {
            menuServerViewModel.getServer(embedServers[pos]).observeOnce(this){ server ->
                if (server.videoList.isNotEmpty()) {
                    if (isDownloadingMode) {
                        if (chapter.id != 0) prepareDownload(server)
                        else launchChapterChecker(server.videoList,true)
                    }
                    else preparingIntent(server.videoList,server.isDirect)
                    dismiss()
                }
                else {
                    binding.linearServer.allViews.forEach { it.isEnabled = true }
                    embedServers.removeAt(pos)
                    binding.linearServer.removeViewAt(pos)
                    serverScript()
                    DreamerApp.showShortToast(getString(R.string.server_warning_error))
                }
            }
        }
    }

    private fun prepareDownload(server: Server) {
        val downloadManager = requireContext()
            .getSystemService(Context.DOWNLOAD_SERVICE) as android.app.DownloadManager
        val request =
            Tools.downloadRequest(chapter, server.videoList.last().directLink)
        val idDownload = downloadManager.enqueue(request)
        Chapter.addToDownloadList(Pair(idDownload, chapter.id))
    }

    private fun preparingIntent(playList: List<VideoModel>, isDirect : Boolean) {
        safeRun {
            val isExternalPlayerMode = Constants.isExternalPlayerMode()
            if (chapter.id != 0) {
                if (isAdInterstitialTime(isDirect)) {
                    launchIntent(requireActivity(),chapter,
                        InterstitialAdActivity::class.java,playList,isDirect)
                    Constants.resetCountedAds()
                } else {
                    if (isDirect) {
                        if (!isExternalPlayerMode) launchIntent(requireActivity(),chapter,PlayerActivity::class.java,playList)
                        else launchIntent(requireActivity(),chapter,PlayerExternalActivity::class.java,playList)
                    }
                    else launchIntent(requireActivity(),chapter,PlayerWebActivity::class.java,playList)
                }
                if (isFromContent && isExternalPlayerMode) activity?.finish()
            }
            else launchChapterChecker(playList,isDirect,isExternalPlayerMode)

            dismiss()
        }
    }

    private fun loadingLayouts() {
        binding.lavLoadingServer.visibility = View.VISIBLE
        binding.imvChapterMenu.load(chapter.chapterCover){
            transformations(RoundedCornersTransformation(10f))
        }
        binding.txvTitleMenu.text = getString(R.string.welcome_player, chapter.title, chapter.chapterNumber)
    }

    private fun showingLayoutsServers() {
        for (pos in 0 until binding.linearServer.childCount) {
            binding.linearServer[pos].visibility = View.VISIBLE
        }
        binding.lavLoadingServer.visibility = View.GONE
        this.isCancelable = true
    }

    private fun hidingLayoutsServers() {
        for (pos in 0 until binding.linearServer.childCount) {
            binding.linearServer[pos].visibility = View.GONE
        }
    }

    private fun launchChapterChecker(playList: List<VideoModel>, isDirect: Boolean, isExternalPlayer: Boolean=false) = ChapterCheckerFragment().apply {
        val fragmentManager: FragmentManager =
            (this@MenuServerFragment.context as FragmentActivity).supportFragmentManager
        val data = Bundle()
        data.apply {
            putParcelable(Constants.REQUESTED_CHAPTER, chapter)
            putParcelableArrayList(Constants.PLAY_VIDEO_LIST, playList as java.util.ArrayList<out Parcelable>)
            putBoolean(Constants.REQUESTED_IS_DIRECT,isDirect)
            putBoolean(Constants.PREFERENCE_EXTERNAL_PLAYER,isExternalPlayer)
            putBoolean(Constants.IS_DATA_FOR_DOWNLOADING_MODE,isDownloadingMode)
        }
        arguments = data
        show(fragmentManager, Constants.CHAPTER_CHECKER_FRAGMENT)
    }

    private fun safeRun(task: () -> Unit) { try { task() } catch (e: Exception) { e.printStackTrace() } }
    private fun launchInThread(task: () -> Unit) = ThreadUtil.execute { task() }

    override fun onDestroyView() {
        super.onDestroyView()
        webView?.onDestroy()
        webView = null
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        DataStore.writeBooleanAsync(Constants.WORK_PREFERENCE_CLICKED_CHAPTER,false)
    }

    companion object {
        const val serverBaseId = 100000
    }
}