package com.ead.project.dreamer.presentation.server.menu

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.allViews
import androidx.core.view.children
import androidx.fragment.app.FragmentActivity
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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MenuServerFragment : BottomSheetDialogFragment() {

    private lateinit var viewModel : MenuServerViewModel
    private val menuServerManager by lazy { MenuServerManager(requireContext()) }

    private lateinit var chapter: Chapter
    private var previousChapter : Chapter?= null
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
            previousChapter = viewModel.getIfChapterIsCasting()
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
        binding.lottieLoadingServer.visibility = View.VISIBLE
        if (chapter.cover.isNotBlank()) {
            binding.imageChapterMenu.load(chapter.cover){
                transformations(RoundedCornersTransformation(10f))
            }
        }
        binding.textTitleMenu.text = getString(R.string.welcome_player, chapter.title, chapter.number)
    }

    private fun isAutomaticResolverActivated() : Boolean {
        return !com.ead.project.dreamer.app.data.server.Server.isAutomaticResolverActivated()
    }

    private fun isInAutomaticProcess() : Boolean {
        return !isAutomaticResolverActivated() && !isDownloadingMode
    }

    private fun initServers() {
        viewModel.getEmbedServers(chapter,requireContext()).observeOnce(this) { embeddedServers->

            if (embeddedServers.isEmpty())
                dismissReason(requireContext().getString(R.string.timeout_message))

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

    private fun dismissReason(text  :String) {
        if (_binding != null) {
            toast(text,Toast.LENGTH_SHORT)
        }

        dismiss()
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
        getUntilFindResource()
    }

    private fun getUntilFindResource () {
        viewModel.getServers(embeddedUrlServers).observeOnce(this) { server ->

            isCancelable = false
            launchAutomaticServer(server ?:return@observeOnce dismissReason(requireContext().getString(R.string.no_server_available)))

        }
    }

    private fun launchAutomaticServer(server : Server) {
        Thread.launch {
            Run.catching {

                if (chapter.id == 0) {
                    prepareChecker(server)
                }
                else preparingIntent(server.videoList,server.isDirect)

            }
        }
    }

    private fun prepareChecker(server: Server) {
        if (chapter.id == 0) {
            ChapterCheckerFragment.launch(
                context = requireActivity() as Context,
                chapter = chapter,
                previousChapter = previousChapter,
                playList = server.videoList,
                isDirect = server.isDirect,
                isExternalPlayer = false,
                isDownloadingMode = isDownloadingMode,
                isFromContent = isFromContent
            )
            dismiss()
        }
    }

    private fun generateServersFunctionality() {
        binding.apply {
            serverContainer.children.forEachIndexed { index, serverLayout ->
                serverLayout.setOnClickListener {

                    serverContainer.allViews.forEach { server -> server.isEnabled = false }
                    Thread.onClickEffect { getSelectedServer(index) }

                }
            }
        }
    }

    private fun getSelectedServer(index : Int) {
        Run.catching {
            viewModel.getServer(embeddedUrlServers[index]).observeOnce(this) { server ->
                if (server == null || server.videoList.isEmpty()) {
                    binding.serverContainer.allViews.forEach { serverOption -> serverOption.isEnabled = true }

                    embeddedUrlServers.removeAt(index)
                    binding.serverContainer.removeViewAt(index)

                    generateServersFunctionality()

                    toast(getString(R.string.server_warning_error),Toast.LENGTH_SHORT)
                }
                else
                    if (server.videoList.isNotEmpty()) {

                        if (chapter.id == 0) {

                            ChapterCheckerFragment.launch(
                                context = requireActivity() as Context,
                                chapter = chapter,
                                previousChapter = previousChapter,
                                playList = server.videoList,
                                isDirect = server.isDirect,
                                isExternalPlayer = false,
                                isDownloadingMode = isDownloadingMode,
                                isFromContent = isFromContent
                            )

                            dismiss()
                            return@observeOnce

                        }


                        if (isDownloadingMode) {

                            prepareDownload(server)

                        } else {

                            preparingIntent(server.videoList,server.isDirect)

                        }
                        dismiss()

                    }
            }
        }
    }

    private fun prepareDownload(server: Server) {
        viewModel.downloadUseCase.add(activity as Context,chapter,server.videoList.last().directLink)
    }

    private fun preparingIntent(videoList: List<VideoModel>, isDirect : Boolean) {
        Run.catching {

            viewModel.launchVideo.with(activity as Context,chapter, previousChapter ,videoList,isDirect)
            if (isFromContent && viewModel.playerPreferences.isInExternalMode()) {
                activity?.finish()
            }
            dismiss()

        }
    }

    private fun showServers() {
        menuServerManager.showServers()
        binding.lottieLoadingServer.visibility = View.GONE
        isCancelable = true
    }

    private fun hideServers() {
        menuServerManager.hideServers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val FRAGMENT = "MENU_PLAYER_FRAGMENT"
        const val IS_DATA_FOR_DOWNLOADING_MODE = "IS_DATA_FOR_DOWNLOADING_MODE"

        fun launch(context: Context, chapter: Chapter,isDownloadMode : Boolean) {
            val fragmentManager = (context as FragmentActivity).supportFragmentManager
            val chapterMenu = MenuServerFragment()
            chapterMenu.apply {
                arguments = Bundle().apply {
                    putParcelable(Chapter.REQUESTED, chapter)
                    putBoolean(IS_DATA_FOR_DOWNLOADING_MODE,isDownloadMode)
                }
                show(fragmentManager, FRAGMENT)
            }
        }
    }
}