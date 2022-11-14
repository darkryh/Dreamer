package com.ead.project.dreamer.ui.settings.layouts


import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import coil.load
import coil.transform.CircleCropTransformation
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.models.discord.Discord
import com.ead.project.dreamer.data.models.discord.User
import com.ead.project.dreamer.data.utils.ui.DreamerLayout


class AccountViewPreference(context: Context, attrs: AttributeSet?) :
    Preference(context, attrs) {
    private var profileView: ImageView? = null
    private var stateView : ImageView?= null
    private var txvUserName : TextView?= null
    private var txvUserRank : TextView?= null
    private var profileClickListener: View.OnClickListener? = null
    private val user = User.get()

    //onBindViewHolder() will be called after we call setImageClickListener() from SettingsFragment
    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        profileView = holder.findViewById(R.id.image) as ImageView
        stateView = holder.findViewById(R.id.state_view) as ImageView
        txvUserName = holder.findViewById(R.id.txvUserName) as TextView
        txvUserRank = holder.findViewById(R.id.txvRank) as TextView

        profileView?.setOnClickListener(profileClickListener)
        if (user != null) {
            if (user.avatar != null)
                profileView?.load(
                    Discord.CDN_ENDPOINT +
                            "/avatars/${user.id}/${user.avatar}") {
                    transformations(CircleCropTransformation())
            }
            txvUserName?.text = user.username
            txvUserRank?.text = user.rank
            val checkDrawable = DreamerLayout.getDrawable(R.drawable.ic_check_24)
            DreamerLayout.setColorFilter(checkDrawable,Color.GREEN)
            stateView?.setImageDrawable(checkDrawable)
        }
    }

    @JvmName("setImageClickListener")
    fun setImageClickListener(onClickListener: View.OnClickListener?) { profileClickListener = onClickListener }
}