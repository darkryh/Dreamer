package com.ead.project.dreamer.domain.downloads.states

import android.content.Context
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.system.extensions.toast
import javax.inject.Inject

class RunningState @Inject constructor() {

    operator fun invoke(context: Context) {
        context.toast(context.getString(R.string.warning_chapter_status_in_progress))
    }
}