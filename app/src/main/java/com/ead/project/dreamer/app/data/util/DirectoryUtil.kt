package com.ead.project.dreamer.app.data.util

import android.content.Context
import android.view.View
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.data.util.system.showSnackBar
import com.google.android.material.snackbar.Snackbar
import java.util.Timer
import java.util.TimerTask

object DirectoryUtil {

    var isCompleted = false
    var stateSynchronized = false

    private var warningTimer : Timer?= null
    private var warningCounter = 0

    fun setupState(context: Context, rootView : View) {

        if (isCompleted) {

            showCompletedState(context, rootView)

        }
        else {

            if (warningTimer == null) {

                context.showSnackBar(
                    rootView = rootView,
                    text = context.getString(R.string.requesting_data),
                    color = R.color.red,
                    duration = Snackbar.LENGTH_INDEFINITE
                )
                warningTimer = Timer()

                warningTimer?.schedule(object : TimerTask() {
                    override fun run() {

                        if (!isCompleted) {
                            showAdvices(context, rootView)
                        }
                        else {
                            showAdvices(context, rootView)
                            warningTimer?.cancel()
                            warningTimer = null
                        }

                    }
                }, 5000, 12000)

            }
        }
    }


    fun showCompletedState(context: Context, rootView: View) {
        stateSynchronized = true
        context.showSnackBar(
            rootView = rootView,
            text = context.getString(R.string.successfully_sync),
            color = R.color.green
        )

    }

    private fun showAdvices(context: Context,rootView: View) {

        if (!isCompleted) {
            when(++warningCounter ) {
                1 -> context.showSnackBar(
                    rootView = rootView,
                    text = context.getString(R.string.requesting_data_adv1),
                    color = R.color.red,
                    duration = Snackbar.LENGTH_INDEFINITE
                )
                2 -> context.showSnackBar(
                    rootView = rootView,
                    text = context.getString(R.string.requesting_data_adv2),
                    color = R.color.red,
                    duration = Snackbar.LENGTH_INDEFINITE
                )
                3 -> {
                    context.showSnackBar(
                        rootView = rootView,
                        text = context.getString(R.string.requesting_data_adv3),
                        color = R.color.red,
                        duration = Snackbar.LENGTH_INDEFINITE
                    )
                    warningCounter = 0
                }
            }
        }

    }

}