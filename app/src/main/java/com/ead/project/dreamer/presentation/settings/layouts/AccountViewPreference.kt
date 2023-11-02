package com.ead.project.dreamer.presentation.settings.layouts

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.ead.commons.lib.views.addSelectableItemEffect
import com.ead.project.dreamer.R


class AccountViewPreference@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int = 0
) : Preference(context, attrs, defStyleAttr) {

    private val _isBinded : MutableLiveData<Boolean> = MutableLiveData(false)
    val isBinded : LiveData<Boolean> = _isBinded

    private var _profile: ImageView? = null
    val profile get() = _profile!!

    private var _state : ImageView?= null
    val state get() = _state!!

    private var _userName : TextView?= null
    val userName get() = _userName!!

    private var _rank : TextView?= null
    val rank get() = _rank!!

    init { widgetLayoutResource = R.layout.layout_account_view_preference }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        with(holder.itemView) {
            _profile = findViewById(R.id.image_account)
            _state = findViewById(R.id.image_state_account)
            _userName = findViewById(R.id.text_user_name_account)
            _rank = findViewById(R.id.text_rank_account)
            val root = _profile?.parent as View

            root.addSelectableItemEffect()
            root.setOnClickListener { /*Clicking effect*/ }
        }
        _isBinded.value = true
    }
}