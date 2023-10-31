package com.ead.project.dreamer.presentation.settings.layouts

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import coil.load
import coil.transform.CircleCropTransformation
import com.ead.commons.lib.views.addSelectableItemEffect
import com.ead.commons.lib.views.setResourceImageAndColor
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.data.discord.Discord


class AccountViewPreference(context: Context, attrs: AttributeSet?) :
    Preference(context, attrs) {
    private var _profile: ImageView? = null
    val profile get() = _profile!!

    private var _state : ImageView?= null
    val state get() = _state!!

    private var _userName : TextView?= null
    val userName get() = _userName!!

    private var _rank : TextView?= null
    val rank get() = _rank!!

    private var accountClickListener: View.OnClickListener? = null

    private val discordUser = Discord.getUser()

    init { widgetLayoutResource = R.layout.layout_account_view_preference }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        _profile = holder.findViewById(R.id.image_account) as ImageView
        _state = holder.findViewById(R.id.image_state) as ImageView
        _userName = holder.findViewById(R.id.text_user_name) as TextView
        _rank = holder.findViewById(R.id.text_rank) as TextView
        _profile?.setOnClickListener(accountClickListener)
        val root = _profile?.parent as View
        root.addSelectableItemEffect()
        root.setOnClickListener { /*to do*/ }
        bindUser()
    }

    private fun bindUser() {
        discordUser?.let {
            if (it.avatar != null)
                profile.load(
                    Discord.CDN_ENDPOINT + "/avatars/${it.id}/${it.avatar}") {
                    transformations(CircleCropTransformation())
                }
            userName.text = it.username
            rank.text = it.all_ranks
            state.setResourceImageAndColor(R.drawable.ic_check_24,R.color.green)
        }
    }

    @JvmName("setAccountClickListener")
    fun setAccountClickListener(onClickListener: View.OnClickListener?) { accountClickListener = onClickListener }
}