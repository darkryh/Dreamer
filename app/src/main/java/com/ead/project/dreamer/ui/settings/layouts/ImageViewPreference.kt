package com.ead.project.dreamer.ui.settings.layouts


import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import coil.load
import coil.transform.CircleCropTransformation
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.retrofit.model.discord.Discord
import com.ead.project.dreamer.data.retrofit.model.discord.User


class ImageViewPreference(context: Context, attrs: AttributeSet?) :
    Preference(context, attrs) {
    private var imageView: ImageView? = null
    private var txvUserName : TextView?= null
    private var txvUserRank : TextView?= null
    private var imageClickListener: View.OnClickListener? = null
    private val user = User.get()

    //onBindViewHolder() will be called after we call setImageClickListener() from SettingsFragment
    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        imageView = holder.findViewById(R.id.image) as ImageView
        txvUserName = holder.findViewById(R.id.txvUserName) as TextView
        txvUserRank = holder.findViewById(R.id.txvRank) as TextView
        imageView!!.setOnClickListener(imageClickListener)
        if (user != null) {
            if (user.avatar != null)
                imageView!!.load(
                    Discord.CDN_ENDPOINT +
                            "/avatars/${user.id}/${user.avatar}") {
                    transformations(CircleCropTransformation())
            }
            txvUserName?.text = user.username
            txvUserRank?.text = user.rank
        }
    }

    @JvmName("setImageClickListener")
    fun setImageClickListener(onClickListener: View.OnClickListener?) {
        imageClickListener = onClickListener
    }
}