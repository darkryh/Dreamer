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
import android.widget.ImageView
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
import com.ead.project.dreamer.data.commons.Constants.Companion.BLANK_BROWSER
import com.ead.project.dreamer.data.commons.Tools
import com.ead.project.dreamer.data.database.model.*
import com.ead.project.dreamer.data.network.DreamerClient
import com.ead.project.dreamer.data.network.DreamerWebView
import com.ead.project.dreamer.data.network.DreamerWebView.Companion.server_Script
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
import com.ead.project.dreamer.ui.player.PlayerWebActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

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
    private var optionsHeight = 0
    private var isFromContent = false

    private lateinit var menuPlayerViewModel : MenuPlayerViewModel
    private lateinit var serverBase : LinearLayout
    private var webView : DreamerWebView?= null
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
        webView = DreamerWebView(requireContext())
        settingCast()
        loadingLayouts()
        settingJavaScript()
        return binding.root
    }

    private fun settingCast() {
        if (castManager.castIsConnected()) castManager.setPreviousCast()
    }

    private fun settingJavaScript() {
        webView?.webViewClient = object : DreamerClient() {

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                try {
                    run {
                        if (_binding != null && timeout) {
                            webView!!.loadUrl(BLANK_BROWSER)
                            DreamerApp
                                .showLongToast(requireContext().getString(R.string.timeout_message))
                            dismiss()
                        }
                    }
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
        webView?.loadUrl(chapter.reference)
    }

    private fun embeddingServers() {
        safeRun {
            for (server in rawServers) {
                embedServers.add(Tools.embedLink(server))
            }
            configuringLayout()
            employingServers()
        }
    }


    private fun configuringLayout() {
        optionsHeight = if (embedServers.size <= 8) resources.getDimensionPixelSize(R.dimen.text_view_padding_player_option)
         else resources.getDimensionPixelSize(R.dimen.text_view_padding_player_option_mini)
    }

    private fun employingServers() {
        if (!DataStore.readBoolean(Constants.PREFERENCE_RANK_AUTOMATIC_PLAYER))
            for (pos in embedServers.indices) {
                val embeddedVideo = embedServers[pos]
                val serverName = Server.identify(embeddedVideo)

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

                if (Server.isRecommended(serverName)) addLogoServer(R.drawable.ic_thumb_up_24)
                if (Server.isWebServer(serverName)) addLogoServer(R.drawable.ic_web_24)
                if (isOtherServer(serverName)) addLogoServer(R.drawable.ic_play_circle_outline_24)

                val textViewServer = TextView(requireContext())
                textViewServer.text = serverName
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
        serverParse()
    }

    private fun isOtherServer(serverName : String) : Boolean
    = !Server.isRecommended(serverName) && !Server.isWebServer(serverName) && serverName != "null"

    private fun addLogoServer(imageSource : Int) {
        val imageViewServer = ImageView(requireContext())
        imageViewServer.setImageResource(imageSource)
        imageViewServer.setPadding(
            (resources.getDimensionPixelSize(R.dimen.text_view_padding_player_option)),
            optionsHeight,
            0,
            optionsHeight
        )
        serverBase.addView(imageViewServer)
    }


    private fun serverParse() {
        if(Constants.isAutomaticPlayerMode()) embedServers = VideoChecker.getSorterServerList(embedServers)
        menuPlayerViewModel = ViewModelProvider(this,
            MenuPlayerViewModelFactory(embedServers))[MenuPlayerViewModel::class.java]
        factoringServers()
    }

    private fun factoringServers () {
        menuPlayerViewModel.getServerList().observe(this) {
            serverList = it
            this.isCancelable = false
            if (Constants.isAutomaticPlayerMode()) {
                ThreadUtil.execute { executingAutomaticPlay() }
            } else {
                scriptLayouts()
                showingOptions()
            }
        }
    }


    private fun executingAutomaticPlay() {
        try {
            for (server in serverList) {
                if (server.videoList.isNotEmpty())
                    if (server.isValidated()) {
                        preparingIntent(server.videoList,server.isDirect)
                        break
                    }
                    else
                        if (server.isConnectionValidated()) {
                            preparingIntent(server.videoList, server.isDirect)
                            break
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
            preparingIntent(serverList[pos].videoList,serverList[pos].isDirect)
            dismiss()
        }
        catch (e : Exception) {
            DreamerApp.showLongToast( "error de servidor o video eliminado $e")
        }
    }

    private fun preparingIntent(playList: List<VideoModel>,isDirect : Boolean) {
        safeRun {

            try {
                val isExternalPlayerMode = Constants.isExternalPlayerMode()
                Chapter.set(chapter)

                if (chapter.id != 0) {
                    if (Constants.isInQuantityAdLimit() && !User.isVip()) {
                        launchIntent(InterstitialAdActivity::class.java,playList,isDirect)
                        DataStore.writeIntAsync(Constants.PREFERENCE_CURRENT_WATCHED_VIDEOS, 0)
                    } else {
                        if (isDirect) {
                            if (!isExternalPlayerMode)
                                launchIntent(PlayerActivity::class.java,playList)
                            else
                                launchIntent(PlayerExternalActivity::class.java,playList)
                        }
                        else {
                            launchIntent(PlayerWebActivity::class.java,playList)
                        }
                    }

                    if (isFromContent && isExternalPlayerMode) activity?.finish()
                }
                else
                    launchChapterChecker(playList,isDirect)

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
        binding.txvTitleMenu.text = getString(R.string.welcome_player, chapter.title, chapter.chapterNumber)
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

    private fun launchIntent(typeClass: Class<*>?, playList: List<VideoModel>,isDirect: Boolean=true) {
        startActivity(Intent(activity,typeClass).apply {
            putExtra(Constants.REQUESTED_CHAPTER, chapter)
            putExtra(Constants.REQUESTED_IS_DIRECT,isDirect)
            putParcelableArrayListExtra(
                Constants.PLAY_VIDEO_LIST,
                playList as java.util.ArrayList<out Parcelable>)
        })
    }

    private fun launchChapterChecker(playList: List<VideoModel>,isDirect: Boolean) = ChapterCheckerFragment().apply {
        val fragmentManager: FragmentManager =
            (this@MenuPlayerFragment.context as FragmentActivity).supportFragmentManager
        val data = Bundle()
        data.apply {
            putParcelable(Constants.REQUESTED_CHAPTER, chapter)
            putParcelableArrayList(Constants.PLAY_VIDEO_LIST, playList as java.util.ArrayList<out Parcelable>)
            putBoolean(Constants.REQUESTED_IS_DIRECT,isDirect)
        }
        arguments = data
        show(fragmentManager, Constants.CHAPTER_CHECKER_FRAGMENT)
    }

    private fun safeRun(task: () -> Unit) { if (_binding != null) task() }

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