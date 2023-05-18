package com.ead.project.dreamer.presentation.menuserver

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.allViews
import androidx.core.view.children
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import coil.load
import coil.transform.RoundedCornersTransformation
import com.ead.commons.lib.lifecycle.observeOnce
import com.ead.commons.lib.lifecycle.parcelable
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.data.util.UrlUtil
import com.ead.project.dreamer.app.data.util.system.setStateExpanded
import com.ead.project.dreamer.app.data.util.system.setWidthMatchParent
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.models.Server
import com.ead.project.dreamer.data.models.VideoModel
import com.ead.project.dreamer.data.system.extensions.toast
import com.ead.project.dreamer.data.utils.Run
import com.ead.project.dreamer.data.utils.Thread
import com.ead.project.dreamer.databinding.BottomModalMenuPlayerBinding
import com.ead.project.dreamer.presentation.chapter.checker.ChapterCheckerFragment
import com.ead.project.dreamer.presentation.player.PlayerActivity
import com.ead.project.dreamer.presentation.settings.options.SettingsPlayerFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MenuServerFragment : BottomSheetDialogFragment() {

    private lateinit var viewModel : MenuServerViewModel
    private val menuServerManager by lazy { MenuServerManager(requireContext()) }

    private lateinit var chapter: Chapter
    private var embeddedUrlServers : MutableList<String> = mutableListOf()

    private var isFromContent = false
    private var isDownloadingMode = false

    private var _binding : BottomModalMenuPlayerBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[MenuServerViewModel::class.java]
        arguments?.let {
            chapter = it.parcelable(Chapter.REQUESTED)?:return@let
            isFromContent = it.getBoolean(PlayerActivity.IS_FROM_CONTENT_PLAYER)
            isDownloadingMode = it.getBoolean(IS_DATA_FOR_DOWNLOADING_MODE)
        }
    }

    override fun onStart() {
        super.onStart()
        setWidthMatchParent()
        if (isAutomaticResolverActivated() || isDownloadingMode) {
            setStateExpanded()
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
        if (UrlUtil.isValid(chapter.reference)) {
            viewModel.setDownloadMode(isDownloadingMode)
            fetchingPreferences()
            initLayouts()
            initServers()
        }
        else {
            toast(requireContext().getString(R.string.error_url_message),Toast.LENGTH_SHORT)
            dismiss()
        }
    }

    private fun initLayouts() {
        menuServerManager.initialize(binding.serverContainer)
        binding.lavLoadingServer.visibility = View.VISIBLE
        binding.imvChapterMenu.load(chapter.cover){
            transformations(RoundedCornersTransformation(10f))
        }
        binding.txvTitleMenu.text = getString(R.string.welcome_player, chapter.title, chapter.number)
    }

    private fun isAutomaticResolverActivated() : Boolean {
        return !com.ead.project.dreamer.app.data.server.Server.isAutomaticResolverActivated()
    }

    private fun isInAutomaticProcess() : Boolean {
        return !isAutomaticResolverActivated() && !isDownloadingMode
    }

    private fun initServers() {
        viewModel.getEmbedServers({timeoutActionTask()},chapter).observeOnce(this) { embeddedServers->

            embeddedUrlServers = embeddedServers.toMutableList()

            if (isInAutomaticProcess()) {

                launchAutomaticProcess()

            }
            else {

                spacingBetweenServer()
                bindingServers()
                generateServersFunctionality()
                showServers()

            }
        }
    }

    private fun timeoutActionTask() {
        if (_binding != null) {
            toast(requireContext().getString(R.string.timeout_message),Toast.LENGTH_SHORT)
        }

        dismiss()
    }

    private fun fetchingPreferences() {
        viewModel.fetchingCastingPreferences()
    }

    private fun spacingBetweenServer() {
        menuServerManager.verticalSpaceBetweenServer =
            if (embeddedUrlServers.size <= 8) { resources.getDimensionPixelSize(R.dimen.dimen_15dp) }
            else { resources.getDimensionPixelSize(R.dimen.text_view_padding_player_option_mini) }
    }

    private fun bindingServers() {
        menuServerManager.bindingServers(embeddedUrlServers)
        hideServers()
    }

    private fun launchAutomaticProcess() {
        embeddedUrlServers = viewModel.getSortedServer(embeddedUrlServers,isDownloadingMode)
        serversFactoringProcess()
    }

    private fun serversFactoringProcess () {
        viewModel.getServers(embeddedUrlServers).observeOnce(this) { serverList ->

            isCancelable = false
            launchAutomaticServer(serverList)

        }
    }

    private fun launchAutomaticServer(serverList : List<Server>) {
        Thread.launch {
            Run.catching {

                for (server in serverList) {
                    if (server.isValidated) {

                        preparingIntent(server.videoList,server.isDirect)
                        break

                    } else if (server.isConnectionValidated) {

                        preparingIntent(server.videoList, server.isDirect)
                        break

                    }
                }

                dismiss()
            }
        }
    }

    private fun generateServersFunctionality() {
        binding.apply {
            serverContainer.children.forEachIndexed { index, serverLayout ->
                serverLayout.setOnClickListener {

                    serverContainer.allViews.forEach { server -> server.isEnabled = false }
                    Thread.runInMs({ getSelectedServer(index) }, 175L)

                }
            }
        }
    }

    private fun getSelectedServer(index : Int) {
        Run.catching {
            viewModel.getServer(embeddedUrlServers[index]).observeOnce(this) { server ->
                if (server.videoList.isNotEmpty()) {

                    if (isDownloadingMode) {
                        if (chapter.id != 0) {
                            prepareDownload(server)
                        }
                        else {
                            launchChapterChecker(server.videoList,true)
                        }
                    } else {
                        preparingIntent(server.videoList,server.isDirect)
                    }
                    dismiss()

                } else {

                    binding.serverContainer.allViews.forEach { serverOption -> serverOption.isEnabled = true }

                    embeddedUrlServers.removeAt(index)
                    binding.serverContainer.removeViewAt(index)

                    generateServersFunctionality()

                    toast(getString(R.string.server_warning_error),Toast.LENGTH_SHORT)
                }
            }
        }
    }

    private fun prepareDownload(server: Server) {
        viewModel.downloadUseCase.createManualDownload(chapter,server.videoList.last().directLink)
    }

    private fun preparingIntent(videoList: List<VideoModel>, isDirect : Boolean) {
        Run.catching {

            viewModel.launchVideo.with(activity as Context,chapter,videoList,isDirect)
            if (isFromContent && viewModel.playerPreferences.isInExternalMode()) {
                activity?.finish()
            }
            dismiss()

        }
    }

    private fun showServers() {
        menuServerManager.showServers()
        binding.lavLoadingServer.visibility = View.GONE
        isCancelable = true
    }

    private fun hideServers() {
        menuServerManager.hideServers()
    }

    private fun launchChapterChecker(playList: List<VideoModel>, isDirect: Boolean, isExternalPlayer: Boolean=false) = ChapterCheckerFragment().apply {
        val fragmentManager: FragmentManager =
            (this@MenuServerFragment.activity as FragmentActivity).supportFragmentManager
        val data = Bundle()
        data.apply {
            putParcelable(Chapter.REQUESTED, chapter)
            putParcelableArrayList(Chapter.PLAY_VIDEO_LIST, playList as ArrayList<out Parcelable>)
            putBoolean(Chapter.CONTENT_IS_DIRECT,isDirect)
            putBoolean(SettingsPlayerFragment.PREFERENCE_EXTERNAL_PLAYER,isExternalPlayer)
            putBoolean(IS_DATA_FOR_DOWNLOADING_MODE,isDownloadingMode)
        }
        arguments = data
        show(fragmentManager, ChapterCheckerFragment.FRAGMENT)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.onDestroy()
        _binding = null
    }

    companion object {
        const val FRAGMENT = "MENU_PLAYER_FRAGMENT"
        const val IS_DATA_FOR_DOWNLOADING_MODE = "IS_DATA_FOR_DOWNLOADING_MODE"
    }
}