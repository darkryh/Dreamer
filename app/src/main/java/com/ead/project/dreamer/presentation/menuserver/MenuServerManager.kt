package com.ead.project.dreamer.presentation.menuserver

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import com.ead.commons.lib.views.addSelectableItemEffect
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.utils.ServerChecker


class MenuServerManager(private val context: Context) {

    private lateinit var serverContainer: LinearLayout
    private lateinit var serverLayout: LinearLayout

    var verticalSpaceBetweenServer = 0

    fun initialize(serverContainer: LinearLayout) {
        this.serverContainer = serverContainer
    }

    fun bindingServers(servers : List<String>) {
        servers.forEachIndexed { index, server ->
            val serverName = ServerChecker.identify(server)

            serverLayout = LinearLayout(context)
            serverLayout.apply {
                id = SERVER_BASE_ID + index
                gravity = Gravity.START
                background = ContextCompat
                    .getDrawable(context, R.drawable.background_horizontal_border)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1f
                )
                orientation = LinearLayout.HORIZONTAL
                addSelectableItemEffect()
            }

            bindingServerType(serverName)
            bindingServerName(serverName)
            serverContainer.addView(serverLayout)
        }
    }


    fun showServers() {
        serverContainer.forEach { view: View ->
            view.visibility = View.VISIBLE
        }
    }

    fun hideServers() {
        serverContainer.forEach { view: View ->
            view.visibility = View.GONE
        }
    }

    private fun bindingServerType(serverName : String) {
        if (ServerChecker.isRecommended(serverName)) {
            bindingServerLogo(R.drawable.ic_thumb_up_24)
            return
        }

        if (ServerChecker.isWebServer(serverName)) {
            bindingServerLogo(R.drawable.ic_web_24)
            return
        }

        if (ServerChecker.isOtherServer(serverName)) {
            bindingServerLogo(R.drawable.ic_play_circle_outline_24)
            return
        }
    }

    private fun bindingServerName(serverName: String) {
        val nameTextView = TextView(context)
        nameTextView.apply {
            text = serverName
            textSize = 15f
            setPadding(
                (resources.getDimensionPixelSize(R.dimen.dimen_15dp) * 2),
                verticalSpaceBetweenServer,
                0,
                verticalSpaceBetweenServer
            )
        }
        serverLayout.addView(nameTextView)
    }

    private fun bindingServerLogo(imageSource : Int) {
        val logoImageView = ImageView(context)
        logoImageView.apply {
            setImageResource(imageSource)
            setPadding(
                context.resources.getDimensionPixelSize(R.dimen.dimen_15dp),
                verticalSpaceBetweenServer,
                0,
                verticalSpaceBetweenServer
            )
        }
        serverLayout.addView(logoImageView)
    }


    companion object {
        const val SERVER_BASE_ID = 100000
    }
}