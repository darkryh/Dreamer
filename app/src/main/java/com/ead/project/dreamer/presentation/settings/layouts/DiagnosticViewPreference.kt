package com.ead.project.dreamer.presentation.settings.layouts

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.ead.project.dreamer.R


class DiagnosticViewPreference(context: Context, attrs: AttributeSet?) :
    Preference(context, attrs) {
    private var _icon: ImageView? = null
    val icon get() = _icon!!

    private var _title : TextView?= null
    val title get() = _title!!

    private var _log : TextView?= null
    val log get() = _log!!

    private var _buttonTest : Button?= null
    var testClickListener: View.OnClickListener? = null

    init { widgetLayoutResource = R.layout.layout_diagnostic_view_preference }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        _icon = holder.findViewById(R.id.imvDiagnostic) as ImageView
        _title = holder.findViewById(R.id.txvAutomaticDiagnostic) as TextView
        _log = holder.findViewById(R.id.txvLogger) as TextView
        _buttonTest = holder.findViewById(R.id.btnDiagnosticTest) as Button
        _buttonTest?.setOnClickListener(testClickListener)
    }

    @JvmName("setOnTestClickListener")
    fun setOnTestClickListener(onClickListener: View.OnClickListener?) { testClickListener = onClickListener }

    private val stringBuilder : StringBuilder = StringBuilder()

    fun clearStringBuilder() = stringBuilder.clear()

    fun addLog(text : String) {
        stringBuilder.append("$text\n")
        log.text = stringBuilder.toString()
    }
}